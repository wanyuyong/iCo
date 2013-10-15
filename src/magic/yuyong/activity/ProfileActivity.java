package magic.yuyong.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.weibo.sdk.android.api.FavoritesAPI;
import magic.yuyong.R;
import magic.yuyong.adapter.TwitterListAdapter;
import magic.yuyong.app.AppConstant;
import magic.yuyong.app.MagicApplication;
import magic.yuyong.app.MagicDialog;
import magic.yuyong.model.Twitter;
import magic.yuyong.model.User;
import magic.yuyong.persistence.Persistence;
import magic.yuyong.request.RequestState;
import magic.yuyong.util.Debug;
import magic.yuyong.view.AsyncImageView;
import magic.yuyong.view.RefreshView;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.FriendshipsAPI;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.api.UsersAPI;
import com.weibo.sdk.android.api.WeiboAPI.FEATURE;
import com.weibo.sdk.android.net.RequestListener;

public class ProfileActivity extends GetTwitterActivity implements OnClickListener, RefreshView.Listener {

    private User user;
    private View head;
    private View footer;
    private RefreshView rv;
    private AsyncImageView avatar;
    private TextView user_name;
    private ImageView user_gender;
    private TextView location;
    private TextView description;
    private TextView following;
    private TextView weibo;
    private TextView follower;
    private TextView favourit;
    private LinearLayout following_lay;
    private LinearLayout weibo_lay;
    private LinearLayout follower_lay;
    private LinearLayout favourit_lay;
    private ListView listView;

    private TwitterListAdapter adapter;
    private List<Twitter> myTwitters = new ArrayList<Twitter>();
    private List<Twitter> myFavouriteTwitters = new ArrayList<Twitter>();
    private RequestState myTwittersRequest, myFavouriteRequest, current;
    private long uid;
    private boolean requestProfile;
    private String screen_name;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(android.os.Message msg) {
            setProgressBarIndeterminateVisibility(false);
            switch (msg.what) {
                case AppConstant.MSG_UPDATE_VIEW:
                    String response = msg.obj.toString();
                    JSONObject jsonObj = null;
                    try {
                        jsonObj = new JSONObject(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (jsonObj != null) {
                        user = User.parse(jsonObj);
                    }
                    if (Persistence.getUID(getApplicationContext()) == user.getId()) {
                        favourit_lay.setVisibility(View.VISIBLE);
                    }
                    invalidateOptionsMenu();
                    user_name.setText(user.getScreen_name());
                    user_gender
                            .setImageResource(user.getGender().equals("m") ? R.drawable.male
                                    : R.drawable.female);
                    location.setText(user.getLocation());
                    description.setText(user.getDescription());
                    following.setText(String.valueOf(user.getFriends_count()));
                    weibo.setText(String.valueOf(user.getStatuses_count()));
                    follower.setText(String.valueOf(user.getFollowers_count()));
                    favourit.setText(String.valueOf(user.getFavourites_count()));
                    avatar.setUrl(user.getAvatar_large());
                    break;
                case AppConstant.MSG_FOLLOW_SUCCEED:
                    invalidateOptionsMenu();
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.text_follow_succeed), Toast.LENGTH_SHORT).show();
                    break;
                case AppConstant.MSG_FOLLOW_FAILD:
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.text_follow_fail), Toast.LENGTH_SHORT).show();
                    break;
                case AppConstant.MSG_UNFOLLOW_SUCCEED:
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.text_unfollow_succeed), Toast.LENGTH_SHORT).show();
                    invalidateOptionsMenu();
                    break;
                case AppConstant.MSG_UNFOLLOW_FAILD:
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.text_unfollow_fail), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.profile);
        head = getLayoutInflater().inflate(R.layout.profile_head, null);

        avatar = (AsyncImageView) head.findViewById(R.id.user_avatar);
        avatar.setDefaultImageResource(R.drawable.avatar);
        user_name = (TextView) head.findViewById(R.id.name);
        user_gender = (ImageView) head.findViewById(R.id.gender);
        location = (TextView) head.findViewById(R.id.location);
        description = (TextView) head.findViewById(R.id.description);
        following = (TextView) head.findViewById(R.id.following);
        following_lay = (LinearLayout) head.findViewById(R.id.following_lay);
        following_lay.setOnClickListener(this);
        weibo = (TextView) head.findViewById(R.id.weibo);
        weibo_lay = (LinearLayout) head.findViewById(R.id.weibo_lay);
        weibo_lay.setOnClickListener(this);
        follower = (TextView) head.findViewById(R.id.follower);
        follower_lay = (LinearLayout) head.findViewById(R.id.follower_lay);
        follower_lay.setOnClickListener(this);
        favourit = (TextView) head.findViewById(R.id.favourit);
        favourit_lay = (LinearLayout) head.findViewById(R.id.favourit_lay);
        favourit_lay.setOnClickListener(this);

        rv = (RefreshView) findViewById(R.id.refresh_view);
        rv.setListener(this);

        adapter = new TwitterListAdapter(this);

        listView = (ListView) findViewById(R.id.list_view);
        listView.addHeaderView(head);
        footer = getLayoutInflater().inflate(R.layout.loadmorelayout, null);
        footer.setVisibility(View.INVISIBLE);
        listView.addFooterView(footer);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int arg2,
                                    long arg3) {
                TwitterListAdapter.ViewHolder holder = (TwitterListAdapter.ViewHolder) view
                        .getTag();
                if (holder != null) {
                    Twitter twitter = holder.twitter;
                    if (twitter.getId() != 0) {
                        Intent intent = new Intent(getApplicationContext(),
                                TwitterShowActivity.class);
                        intent.putExtra("twitter", twitter);
                        startActivity(intent);
                    }
                }
            }
        });
        listView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                    if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                        getTwitter(false);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView arg0, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });

        uid = getIntent().getLongExtra("uid", 0L);
        screen_name = getIntent().getStringExtra("screen_name");
        myTwittersRequest = new RequestState();
        myFavouriteRequest = new RequestState();

        getProfile();
        changeType(myTwittersRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile, menu);
        MenuItem settingItem = menu.findItem(R.id.setting);
        MenuItem followItem = menu.findItem(R.id.follow);
        MenuItem at_taItem = menu.findItem(R.id.at_ta);

        if (Persistence.getUID(getApplicationContext()) != uid) {
            settingItem.setVisible(false);
        }
        if (user != null) {
            if (Persistence.getUID(getApplicationContext()) != uid) {
                followItem.setVisible(true);
                at_taItem.setVisible(true);
                if (user.isFollowing()) {
                    followItem.setTitle(R.string.but_unfollow);
                } else {
                    followItem.setTitle(R.string.but_follow);
                }
            }
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.follow:
                boolean isFollowing = user.isFollowing();
                final MagicDialog follow_dialog = new MagicDialog(this,
                        R.style.magic_dialog);
                follow_dialog.setMessage(
                        getResources().getString(
                                isFollowing ? R.string.title_unfollow
                                        : R.string.title_follow),
                        getResources().getString(
                                isFollowing ? R.string.text_unfollow
                                        : R.string.text_follow));
                follow_dialog.addButton(isFollowing ? R.string.but_unfollow
                        : R.string.but_follow, new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        followTask();
                        follow_dialog.dismiss();
                    }
                });
                follow_dialog.addButton(R.string.but_cancel,
                        new OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                follow_dialog.dismiss();
                            }
                        });
                follow_dialog.show();
                break;
            case R.id.setting:
                Intent settingIntent = new Intent(getApplicationContext(),
                        SettingActivity.class);
                startActivity(settingIntent);
                break;
            case R.id.at_ta:
                Intent postIntent = new Intent(getApplicationContext(),
                        NewPostActivity.class);
                postIntent.putExtra("@", "@" + user.getScreen_name());
                startActivity(postIntent);
                break;
        }
        return true;
    }

    private void followTask() {
        setProgressBarIndeterminateVisibility(true);
        FriendshipsAPI friendshipsAPI = new FriendshipsAPI(MagicApplication
                .getInstance().getAccessToken());
        if (user.isFollowing()) {
            friendshipsAPI.destroy(user.getId(), user.getScreen_name(),
                    new RequestListener() {

                        @Override
                        public void onIOException(IOException arg0) {
                        }

                        @Override
                        public void onError(WeiboException arg0) {
                            handler.sendEmptyMessage(AppConstant.MSG_UNFOLLOW_FAILD);
                        }

                        @Override
                        public void onComplete(String arg0) {
                            user.setFollowing(false);
                            handler.sendEmptyMessage(AppConstant.MSG_UNFOLLOW_SUCCEED);
                        }
                    });
        } else {
            friendshipsAPI.create(user.getId(), user.getScreen_name(),
                    new RequestListener() {

                        @Override
                        public void onIOException(IOException arg0) {
                        }

                        @Override
                        public void onError(WeiboException arg0) {
                            handler.sendEmptyMessage(AppConstant.MSG_FOLLOW_FAILD);
                        }

                        @Override
                        public void onComplete(String arg0) {
                            user.setFollowing(true);
                            handler.sendEmptyMessage(AppConstant.MSG_FOLLOW_SUCCEED);
                        }
                    });
        }
    }

    private void getProfile() {
        requestProfile = true;
        UsersAPI usersAPI = new UsersAPI(MagicApplication.getInstance()
                .getAccessToken());
        RequestListener listener = new RequestListener() {

            @Override
            public void onIOException(IOException arg0) {
                requestProfile = false;
            }

            @Override
            public void onError(WeiboException arg0) {
                requestProfile = false;
                handler.sendEmptyMessage(AppConstant.MSG_NETWORK_EXCEPTION);
            }

            @Override
            public void onComplete(String response) {
                requestProfile = false;
                android.os.Message msg = new android.os.Message();
                msg.what = AppConstant.MSG_UPDATE_VIEW;
                msg.obj = response;
                handler.sendMessage(msg);
            }
        };
        if (uid != 0L) {
            usersAPI.show(uid, listener);
        } else {
            usersAPI.show(screen_name, listener);
        }
    }

    private void getTwitter(boolean refresh) {
        if (!current.isRequest) {
            if (refresh || !current.isBottom) {
                current.isRequest = true;
                current.isRefresh = refresh;

                if (refresh) {
                    current.isBottom = false;
                    current.maxId = 0;
                    current.page = 1;
                } else {
                    footer.setVisibility(View.VISIBLE);
                }

                if (current == myTwittersRequest) {
                    StatusesAPI statusesAPI = new StatusesAPI(MagicApplication
                            .getInstance().getAccessToken());
                    Debug.v("maxID : "+current.maxId+" : "+current.page);
                    if (uid != 0L) {
                        statusesAPI.userTimeline(uid, 0, current.maxId,
                                AppConstant.PAGE_NUM, current.page, false,
                                FEATURE.ALL, false, new TwitterRequestListener(
                                current));
                    } else {
                        statusesAPI.userTimeline(screen_name, 0,
                                current.maxId, AppConstant.PAGE_NUM,
                                current.page, false, FEATURE.ALL, false,
                                new TwitterRequestListener(current));
                    }
                } else if (current == myFavouriteRequest) {
                    FavoritesAPI favoritesAPI = new FavoritesAPI(MagicApplication
                            .getInstance().getAccessToken());
                    favoritesAPI.favorites(AppConstant.PAGE_NUM, current.page, new TwitterRequestListener(current));
                }

            }
        }
    }

    private void changeType(RequestState state) {
        if (state != current) {
            current = state;
            if (current == myTwittersRequest) {
                adapter.setData(myTwitters);
                if (myTwitters.size() == 0) {
                    getTwitter(false);
                }
            } else if (current == myFavouriteRequest) {
                adapter.setData(myFavouriteTwitters);
                if (myFavouriteTwitters.size() == 0) {
                    getTwitter(false);
                }
            }
        }
    }

    @Override
    protected void onUpdate(RequestState requestState) {
        if (current == requestState) {
            List<Twitter> datas = adapter.getData();

            if (requestState.isRefresh) {
                datas.clear();
            }

            List<Twitter> twitters = null;
            if(requestState == myTwittersRequest){
                twitters = Twitter.parseTwitter(requestState.response);
            }else if(requestState == myFavouriteRequest){
                twitters = Twitter.parseFavorites(requestState.response);
            }
            if (twitters.size() == 0) {
                requestState.isBottom = true;
                Toast.makeText(this,
                        getResources().getText(R.string.text_nomore_data),
                        Toast.LENGTH_SHORT).show();
            } else {
                while (twitters.size() != 0) {
                    Twitter twitter = twitters.remove(0);
                    if (!twitter.isDeleted()) {
                        datas.add(twitter);
                    }
                }
                if (requestState.maxId == 0) {
                    requestState.maxId = datas.get(0).getId();
                }
                requestState.page++;
            }
            adapter.notifyDataSetChanged();
        }
        footer.setVisibility(View.INVISIBLE);
        rv.close();
    }

    @Override
    protected void onError(RequestState requestState) {
        footer.setVisibility(View.INVISIBLE);
        rv.close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.follower_lay:
                Intent followerIntent = new Intent(getApplicationContext(), ShowFriendsActivity.class);
                followerIntent.putExtra("uid", uid);
                followerIntent.putExtra("pos", ShowFriendsActivity.VIEW_FOLLOWER);
                startActivity(followerIntent);
                break;

            case R.id.following_lay:
                Intent followingIntent = new Intent(getApplicationContext(), ShowFriendsActivity.class);
                followingIntent.putExtra("uid", uid);
                followingIntent.putExtra("pos", ShowFriendsActivity.VIEW_FOLLOWING);
                startActivity(followingIntent);
                break;

            case R.id.weibo_lay:
                changeType(myTwittersRequest);
                break;
            case R.id.favourit_lay:
                changeType(myFavouriteRequest);
                break;
        }
    }

    @Override
    public void onRefresh() {
        if (!requestProfile && user == null) {
            getProfile();
        }
        getTwitter(true);
    }

}
