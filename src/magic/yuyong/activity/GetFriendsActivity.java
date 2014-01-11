package magic.yuyong.activity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import magic.yuyong.R;
import magic.yuyong.adapter.AtFriendsAdapter;
import magic.yuyong.adapter.GetFriendsAdapter;
import magic.yuyong.app.AppConstant;
import magic.yuyong.app.MagicApplication;
import magic.yuyong.model.AtUser;
import magic.yuyong.model.User;
import magic.yuyong.persistence.Persistence;
import magic.yuyong.request.RequestState;
import magic.yuyong.util.StringUtil;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.FriendshipsAPI;
import com.sina.weibo.sdk.openapi.legacy.SearchAPI;
import com.sina.weibo.sdk.openapi.legacy.WeiboAPI.FRIEND_TYPE;
import com.sina.weibo.sdk.openapi.legacy.WeiboAPI.RANGE;

public class GetFriendsActivity extends BaseActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {
    private ListView friendsList, searchList;
    private View friends_footer, search_footer;
    private List<User> friends = new ArrayList<User>();
    private List<AtUser> search_friends = new ArrayList<AtUser>();
    private GetFriendsAdapter friendsAdapter;
    private AtFriendsAdapter searchAdapter;

    private RequestState friendsState, searchState;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case AppConstant.MSG_UPDATA_FRIENDS:
                    if (friendsState.isRefresh) {
                        friends.clear();
                    }
                    List<User> users = User.parseUsers(friendsState.response);
                    if (users.size() == 0) {
                        friendsState.isBottom = true;
                        friends_footer.setVisibility(View.GONE);
                    } else {
                        friends.addAll(users);
                        friendsAdapter.notifyDataSetChanged();
                    }
                    break;
                case AppConstant.MSG_NETWORK_EXCEPTION:
                    Toast.makeText(
                            getApplicationContext(),
                            getResources().getString(
                                    R.string.text_network_exception),
                            Toast.LENGTH_SHORT).show();
                    break;
                case AppConstant.MSG_UPDATA_SEARCH_FRIENDS:
                    search_friends.clear();
                    List<AtUser> searchUsers = AtUser.parseUsers(searchState.response);
                    search_footer.setVisibility(View.GONE);
                    if (searchUsers.size() != 0) {
                        search_friends.addAll(searchUsers);
                        searchAdapter.notifyDataSetChanged();
                    }
                    break;
            }
            RequestState _state = (RequestState) msg.obj;
            _state.isRequest = false;
            _state.isRefresh = false;
            _state.response = "";
            if (_state == searchState) {
                search_footer.setVisibility(View.GONE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.at_friends);
        friendsList = (ListView) findViewById(R.id.friends_list);
        searchList = (ListView) findViewById(R.id.search_list);
        setFooterView();

        friendsAdapter = new GetFriendsAdapter(getApplicationContext());
        friendsList.setAdapter(friendsAdapter);
        friendsAdapter.addData(friends);
        friendsList.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && view.getLastVisiblePosition() == (view.getCount() - 1)) {
                    getFriends(false);
                }
            }

            @Override
            public void onScroll(AbsListView arg0, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });
        searchAdapter = new AtFriendsAdapter(getApplicationContext());
        searchList.setAdapter(searchAdapter);
        searchAdapter.addData(search_friends);
        searchList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String atString = createAtString();
                atString = atString + "@" + search_friends.get(position).getNickname() + " ";
                Intent intent = new Intent(getApplicationContext(),
                        GetFriendsActivity.class);
                intent.putExtra("@", atString);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        friendsState = new RequestState();
        searchState = new RequestState();
        getFriends(true);
    }

    private void search(String searchWord) {
        if (StringUtil.isEmpty(searchWord)) {
            return;
        }
        if (searchList.getVisibility() != View.VISIBLE) {
            searchList.setVisibility(View.VISIBLE);
        }
        if (friendsList.getVisibility() != View.GONE) {
            friendsList.setVisibility(View.GONE);
        }
        if (!searchState.isRequest) {
            searchState.isRequest = true;
            search_footer.setVisibility(View.VISIBLE);
            
            SearchAPI searchAPI = new SearchAPI(
                    MagicApplication.getInstance().getAccessToken());
            searchAPI.atUsers(searchWord, 20, FRIEND_TYPE.ATTENTIONS, RANGE.ALL, new RequestListener() {

                @Override
                public void onIOException(IOException arg0) {
                }

                @Override
                public void onError(WeiboException arg0) {
                    Message msg = new Message();
                    msg.obj = searchState;
                    msg.what = AppConstant.MSG_NETWORK_EXCEPTION;
                    mHandler.sendMessage(msg);
                }

                @Override
                public void onComplete(String response) {
                    searchState.response = response;
                    Message msg = new Message();
                    msg.obj = searchState;
                    msg.what = AppConstant.MSG_UPDATA_SEARCH_FRIENDS;
                    mHandler.sendMessage(msg);
                }

				@Override
				public void onComplete4binary(ByteArrayOutputStream responseOS) {
					
				}
            });
        }
    }

    private void getFriends(boolean refresh) {
        if (!friendsState.isRequest) {
            if (refresh || !friendsState.isBottom) {
                friendsState.isRequest = true;
                friendsState.isRefresh = refresh;
                if (refresh) {
                    friendsState.isBottom = true;
                }
                FriendshipsAPI friendshipsAPI = new FriendshipsAPI(
                        MagicApplication.getInstance().getAccessToken());
                friendshipsAPI.friends(
                        Persistence.getUID(getApplicationContext()), 20,
                        refresh ? 0 : search_friends.size(), true, new RequestListener() {

                    @Override
                    public void onIOException(IOException arg0) {
                    }

                    @Override
                    public void onError(WeiboException arg0) {
                        Message msg = new Message();
                        msg.obj = friendsState;
                        msg.what = AppConstant.MSG_NETWORK_EXCEPTION;
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void onComplete(String response) {
                        friendsState.response = response;
                        Message msg = new Message();
                        msg.obj = friendsState;
                        msg.what = AppConstant.MSG_UPDATA_FRIENDS;
                        mHandler.sendMessage(msg);
                    }

					@Override
					public void onComplete4binary(
							ByteArrayOutputStream responseOS) {
						
					}
                });
            }
        }
    }

    private String createAtString() {
        StringBuffer result = new StringBuffer();
        for (User friend : friends) {
            if (friend.isChoose())
                result.append("@" + friend.getScreen_name() + " ");
        }
        return result.toString();
    }

    private void setFooterView() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        friends_footer = inflater.inflate(R.layout.loadmorelayout, null);
        friendsList.addFooterView(friends_footer);
        friendsList.setFooterDividersEnabled(false);

        search_footer = inflater.inflate(R.layout.loadmorelayout, null);
        searchList.addFooterView(search_footer);
        searchList.setFooterDividersEnabled(false);
    }

    SearchView searchView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.getfriends, menu);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ok:
                Intent intent = new Intent();
                intent.putExtra("@", createAtString());
                setResult(RESULT_OK, intent);
                finish();
                break;
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        search(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        search(newText);
        return true;
    }

    @Override
    public boolean onClose() {
        if (searchList.getVisibility() != View.GONE) {
            searchList.setVisibility(View.GONE);
        }
        if (friendsList.getVisibility() != View.VISIBLE) {
            friendsList.setVisibility(View.VISIBLE);
        }
        return false;
    }

}
