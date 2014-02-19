package magic.yuyong.activity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import magic.yuyong.R;
import magic.yuyong.adapter.MyPagerAdapter;
import magic.yuyong.adapter.ShowFriendsAdapter;
import magic.yuyong.app.AppConstant;
import magic.yuyong.app.MagicApplication;
import magic.yuyong.extend.UnReadAPI;
import magic.yuyong.model.User;
import magic.yuyong.persistence.Persistence;
import magic.yuyong.request.RequestState;
import magic.yuyong.util.JsonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.FriendshipsAPI;

public class ShowFriendsActivity extends BaseActivity implements
		View.OnClickListener, OnRefreshListener {

	private ViewPager mPager;
	private List<View> listViews;

	public static final int VIEW_FOLLOWER = 0;
	public static final int VIEW_FOLLOWING = 1;
	private RequestState current;

	private long uid;
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			RequestState requestState = (RequestState) msg.obj;
			switch (msg.what) {
			case AppConstant.MSG_UPDATE_VIEW:
				onUpdate(requestState);
				break;
			case AppConstant.MSG_NETWORK_EXCEPTION:
				onError(requestState);
				break;
			}
			requestState.isRefresh = false;
			requestState.isRequest = false;
		}
	};
	
	private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int index) {
			View view = listViews.get(index);
			current = (RequestState) view.getTag();
			
			if (current.isFirstTime) {
				current.isFirstTime = false;
				getFriends(false);
			}
			
			if(current.requestType == VIEW_FOLLOWING){
				actionBar.setTitle(R.string.label_following);
			}else if(current.requestType == VIEW_FOLLOWER){
				actionBar.setTitle(R.string.label_follower);
			}
			
		}
		
		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		}
		
		@Override
		public void onPageScrollStateChanged(int state) {
		}
	};

	private void getFriends(boolean refresh) {
		final RequestState requestState = current;
		if (!requestState.isRequest) {
			if (refresh || !requestState.isBottom) {
				requestState.isRequest = true;
				requestState.isRefresh = refresh;

				View tagView = listViews.get(requestState.requestType);
				ListView listView = (ListView) tagView
						.findViewById(R.id.friends_list);
				View footView = listView.findViewById(R.id.load_more);

				if (refresh) {
					requestState.isBottom = false;
					requestState.next_cursor = 0;
					requestState.previous_cursor = 0;
				} else {
					footView.setVisibility(View.VISIBLE);
				}
				FriendshipsAPI friendshipsAPI = new FriendshipsAPI(
						MagicApplication.getInstance().getAccessToken());

				switch (requestState.requestType) {
				case VIEW_FOLLOWER:
					friendshipsAPI.followers(uid, 50, requestState.next_cursor, false,
							new RequestListener() {

								@Override
								public void onIOException(IOException arg0) {
								}

								@Override
								public void onError(WeiboException arg0) {
									Message msg = new Message();
									msg.obj = requestState;
									msg.what = AppConstant.MSG_NETWORK_EXCEPTION;
									mHandler.sendMessage(msg);
								}

								@Override
								public void onComplete(String response) {
									requestState.response = response;
									Message msg = new Message();
									msg.obj = requestState;
									msg.what = AppConstant.MSG_UPDATE_VIEW;
									mHandler.sendMessage(msg);
								}

								@Override
								public void onComplete4binary(
										ByteArrayOutputStream responseOS) {
									
								}
							});
					break;
				case VIEW_FOLLOWING:
					friendshipsAPI.friends(uid, 50, requestState.next_cursor, false,
							new RequestListener() {

								@Override
								public void onIOException(IOException arg0) {
								}

								@Override
								public void onError(WeiboException arg0) {
									Message msg = new Message();
									msg.obj = requestState;
									msg.what = AppConstant.MSG_NETWORK_EXCEPTION;
									mHandler.sendMessage(msg);
								}

								@Override
								public void onComplete(String response) {
									requestState.response = response;
									Message msg = new Message();
									msg.obj = requestState;
									msg.what = AppConstant.MSG_UPDATE_VIEW;
									mHandler.sendMessage(msg);
								}

								@Override
								public void onComplete4binary(
										ByteArrayOutputStream responseOS) {
									
								}
							});
					break;
				}
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		setContentView(R.layout.show_friends);
		uid = getIntent().getLongExtra("uid", -1L);
		initViewPager();
		
		int pos = getIntent().getIntExtra("pos", VIEW_FOLLOWER);
		if(pos == VIEW_FOLLOWER){
			clearFollowerState();
		}
		
		mPager.setCurrentItem(pos);
		mOnPageChangeListener.onPageSelected(pos);
		
	}
	
	private void clearFollowerState(){
		UnReadAPI unReadAPI = new UnReadAPI(MagicApplication.getInstance().getAccessToken());
		unReadAPI.clear(new RequestListener() {
			
			@Override
			public void onIOException(IOException arg0) {
			}
			
			@Override
			public void onError(WeiboException arg0) {
			}
			
			@Override
			public void onComplete(String arg0) {
				Persistence.setFollower(getApplicationContext(), 0);
				sendBroadcast(new Intent(AppConstant.ACTION_UNREAD_STATE_CHANGE_BROADCAST));
			}

			@Override
			public void onComplete4binary(ByteArrayOutputStream responseOS) {
				
			}
		}, UnReadAPI.TYPE_FOLLOWER);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.friends, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
			
		case R.id.follower:
			mPager.setCurrentItem(0);
			break;
			
		case R.id.following:
			mPager.setCurrentItem(1);
			break;
		}
		
		return true;
	}

	private void onUpdate(RequestState requestState) {
		View tagView = listViews.get(requestState.requestType);
		PullToRefreshLayout mPullToRefreshLayout = (PullToRefreshLayout) tagView;
		ListView listView = (ListView) tagView.findViewById(R.id.friends_list);
		View footView = listView.findViewById(R.id.load_more);
		HeaderViewListAdapter headAdapter = (HeaderViewListAdapter) listView
				.getAdapter();
		ShowFriendsAdapter adapter = (ShowFriendsAdapter) headAdapter
				.getWrappedAdapter();
		List<User> datas = adapter.getData();

		if (requestState.isRefresh) {
			datas.clear();
		}

		List<User> users = User.parseUsers(requestState.response);
		try {
			JSONObject jsonObj = new JSONObject(requestState.response);
			requestState.next_cursor = JsonUtil.getInt(jsonObj, "next_cursor");
			requestState.previous_cursor = JsonUtil.getInt(jsonObj, "previous_cursor");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (requestState.next_cursor == 0) {
			requestState.isBottom = true;
		}
		datas.addAll(users);
		adapter.notifyDataSetChanged();
		footView.setVisibility(View.INVISIBLE);
		mPullToRefreshLayout.setRefreshComplete();
	}

	private void onError(RequestState requestState) {
		View tagView = listViews.get(requestState.requestType);
		ListView listView = (ListView) tagView.findViewById(R.id.friends_list);
		View footView = listView.findViewById(R.id.load_more);
		PullToRefreshLayout mPullToRefreshLayout = (PullToRefreshLayout) tagView;
		mPullToRefreshLayout.setRefreshComplete();
		footView.setVisibility(View.INVISIBLE);
		Toast.makeText(
				getApplicationContext(),
				getResources().getString(
						R.string.text_network_exception),
				Toast.LENGTH_SHORT).show();
	}

	private void setListScrollListener(ListView listView,
			final RequestState requestState) {
		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
						getFriends(false);
					}
				}
			}

			@Override
			public void onScroll(AbsListView arg0, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
	}

	private void prepareView(final View view, int view_type) {
		ListView list_view = (ListView) view.findViewById(R.id.friends_list);
		setFootView(list_view);
		ShowFriendsAdapter adapter = new ShowFriendsAdapter(this);
		list_view.setAdapter(adapter);
		adapter.addData(new ArrayList<User>());

		PullToRefreshLayout mPullToRefreshLayout = (PullToRefreshLayout)view;
		ActionBarPullToRefresh.from(this)
        .theseChildrenArePullable(R.id.friends_list, android.R.id.empty)
        .listener(this)
        .setup(mPullToRefreshLayout);

		RequestState requestState = new RequestState(view_type);
		view.setTag(requestState);
		setListScrollListener(list_view, requestState);
	}

	private void initViewPager() {
		mPager = (ViewPager) findViewById(R.id.vPager);
		listViews = new ArrayList<View>();

		View followerList = getLayoutInflater().inflate(R.layout.friends, null);
		prepareView(followerList, VIEW_FOLLOWER);
		listViews.add(followerList);

		View followingList = getLayoutInflater()
				.inflate(R.layout.friends, null);
		prepareView(followingList, VIEW_FOLLOWING);
		listViews.add(followingList);
		
		MyPagerAdapter adapter = new MyPagerAdapter(listViews);
		mPager.setAdapter(adapter);
		
		mPager.setOnPageChangeListener(mOnPageChangeListener);
	}

	private void setFootView(ListView list_view) {
		View foot_view = getLayoutInflater().inflate(R.layout.loadmorelayout,
				null);
		foot_view.setVisibility(View.INVISIBLE);
		list_view.addFooterView(foot_view);
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public void onRefreshStarted(View view) {
		getFriends(true);
	}

}
