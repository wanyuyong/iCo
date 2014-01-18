package magic.yuyong.activity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import magic.yuyong.R;
import magic.yuyong.app.AppConstant;
import magic.yuyong.app.MagicApplication;
import magic.yuyong.drawable.Diagonal;
import magic.yuyong.extend.FriendshipsAPI_E;
import magic.yuyong.model.Group;
import magic.yuyong.model.Twitter;
import magic.yuyong.persistence.Persistence;
import magic.yuyong.request.RequestState;
import magic.yuyong.util.Debug;
import magic.yuyong.util.DisplayUtil;
import magic.yuyong.view.DivideView;
import magic.yuyong.view.TwitterBoard;
import magic.yuyong.view.TwitterBoardScrollView;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.HorizontalScrollView;
import android.widget.OverScroller;
import android.widget.TextView;
import android.widget.Toast;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.StatusesAPI;
import com.sina.weibo.sdk.openapi.legacy.WeiboAPI;
import com.umeng.update.UmengUpdateAgent;

public class TwitterBoardActivity extends GetTwitterActivity implements
		OnClickListener, TwitterBoard.BoundaryListener {
	private TwitterBoard board;
	private TwitterBoardScrollView scrollView;
	private DivideView divide_view;
	private View innerButtonLay;
	private View content;

	private List<Group> groups = new ArrayList<Group>();
	private Long list_id;
	private boolean gettingGroup;

	private static final int STATE_HOME = 0;
	private static final int STATE_GROUP = 1;
	private RequestState current;

	private RequestState homeState, groupState;

	private BroadcastReceiver unReadReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			checkUnRead();
		}
	};

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case AppConstant.MSG_UPDATA_GROUP:
				invalidateOptionsMenu();
				break;
			}
		}
	};

	private void checkUpdate() {
		boolean checkUpdate = getIntent()
				.getBooleanExtra("check_update", false);
		if (checkUpdate) {
			UmengUpdateAgent.update(this);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.twitter_board, menu);
		MenuItem sort = menu.findItem(R.id.sort);
		SubMenu subMenu = null;
		if (sort != null) {
			subMenu = sort.getSubMenu();
		}
		if (groups.size() != 0 && subMenu != null) {
			for (int i = 0; i < groups.size(); i++) {
				Group group = groups.get(i);
				subMenu.addSubMenu(R.id.sort, i, i, group.getName());
			}
			subMenu.addSubMenu(R.id.sort, R.id.menu_group_all, groups.size(),
					getResources().getString(R.string.but_all));
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		super.onCreate(savedInstanceState);

		Drawable actionBarBg = getResources().getDrawable(
				R.drawable.translucence);
		actionBar.setBackgroundDrawable(actionBarBg);
		actionBar.setSplitBackgroundDrawable(actionBarBg);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setCustomView(R.layout.twitter_board_header);

		checkUpdate();

		registerReceiver(unReadReceiver, new IntentFilter(
				AppConstant.ACTION_UNREAD_STATE_CHANGE_BROADCAST));
		setContentView(R.layout.twitter_board);
		content = findViewById(R.id.content);
		board = (TwitterBoard) findViewById(R.id.twitter_board);
		board.setBoundaryListener(this);
		scrollView = (TwitterBoardScrollView) findViewById(R.id.twitter_board_scrollview);
		scrollView.setTwitterBoardScrollListener(board);
		board.setScrollView(scrollView);
		View header = actionBar.getCustomView().findViewById(R.id.header);
		header.setOnClickListener(this);
		divide_view = (DivideView) findViewById(R.id.divide_view);

		homeState = new RequestState(STATE_HOME);
		groupState = new RequestState(STATE_GROUP);
		current = homeState;
		getTwitter(false);

		initInnerButtons();
		checkUnRead();

		getGroup();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(unReadReceiver);
	}

	private void checkUnRead() {
		int follower = Persistence.getFollower(getApplicationContext());
		int cmt = Persistence.getCmt(getApplicationContext());
		int mention_status = Persistence
				.getMention_status(getApplicationContext());
		int mention_cmt = Persistence.getMention_cmt(getApplicationContext());

		TextView view = (TextView) innerButtonLay.findViewById(R.id.unread_at);
		if (mention_status != 0) {
			view.setText(String.valueOf(mention_status));
			view.setVisibility(View.VISIBLE);
		} else {
			view.setVisibility(View.GONE);
		}

		view = (TextView) innerButtonLay.findViewById(R.id.unread_cmt);
		if (cmt != 0) {
			view.setText(String.valueOf(cmt));
			view.setVisibility(View.VISIBLE);
		} else {
			view.setVisibility(View.GONE);
		}
	}

	private void initInnerButtons() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		innerButtonLay = inflater.inflate(R.layout.inner_buttons, null);
		innerButtonLay.setBackgroundDrawable(new Diagonal());
		View postButton = innerButtonLay.findViewById(R.id.new_post);
		postButton.setOnClickListener(this);
		View plazaButton = innerButtonLay.findViewById(R.id.plaza);
		plazaButton.setOnClickListener(this);
		View settingButton = innerButtonLay.findViewById(R.id.setting);
		settingButton.setOnClickListener(this);
		View atButton = innerButtonLay.findViewById(R.id.at);
		atButton.setOnClickListener(this);
		View commentButton = innerButtonLay.findViewById(R.id.comment);
		commentButton.setOnClickListener(this);
	}

	@Override
	public void toTheEnd() {
		getTwitter(false);
	}

	@Override
	public void toTheBeginning() {

	}

	@Override
	protected void onUpdate(RequestState requestState) {
		if (current == requestState) {
			setProgressBarIndeterminateVisibility(false);
			if (requestState.isRefresh) {
				board.refresh();
			}
			List<Twitter> twitters = Twitter
					.parseTwitter(requestState.response);
			if (twitters.size() == 0) {
				requestState.isBottom = true;
			} else {
				board.addData(twitters);
				if (requestState.maxId == 0) {
					requestState.maxId = twitters.get(0).getId();
				}
				requestState.page++;
			}
		}
	}

	@Override
	protected void onError(RequestState requestState) {
		if (current == requestState) {
			setProgressBarIndeterminateVisibility(false);
		}
	}

	private void requestForTwitters(final RequestState requestState) {
		new Thread(new Runnable() {

			@Override
			public void run() {

				if (requestState.requestType == STATE_HOME
						&& requestState.isFirstTime) {
					requestState.isFirstTime = false;
					String data = Persistence
							.getHomeData(getApplicationContext());
					if (!TextUtils.isEmpty(data)) {
						requestState.response = data;
						Message msg = handler
								.obtainMessage(AppConstant.MSG_UPDATE_VIEW);
						msg.obj = requestState;
						handler.sendMessageDelayed(msg, 300);
						return;
					}
				}

				switch (requestState.requestType) {
				case STATE_HOME:
					StatusesAPI homeAPI = new StatusesAPI(MagicApplication
							.getInstance().getAccessToken());
					homeAPI.homeTimeline(0, requestState.maxId,
							AppConstant.PAGE_NUM, requestState.page, false,
							WeiboAPI.FEATURE.ALL, false,
							new TwitterRequestListener(requestState));
					break;

				case STATE_GROUP:
					FriendshipsAPI_E friendshipsApi = new FriendshipsAPI_E(
							MagicApplication.getInstance().getAccessToken());
					friendshipsApi.groupTimeline(list_id, 0,
							requestState.maxId, AppConstant.PAGE_NUM,
							requestState.page, false, WeiboAPI.FEATURE.ALL,
							new TwitterRequestListener(requestState));
					break;
				}
			}
		}).start();
	}

	private void getTwitter(boolean refresh) {
		final RequestState requestState = current;
		if (!requestState.isRequest) {
			if (refresh || !requestState.isBottom) {
				requestState.isRequest = true;
				requestState.isRefresh = refresh;
				if (refresh) {
					requestState.isBottom = false;
					requestState.maxId = 0;
					requestState.page = 1;
				}
				setProgressBarIndeterminateVisibility(true);
				requestForTwitters(requestState);
			}
		}
	}

	private void getGroup() {
		gettingGroup = true;
		FriendshipsAPI_E friendshipsApi = new FriendshipsAPI_E(MagicApplication
				.getInstance().getAccessToken());
		friendshipsApi.group(new RequestListener() {

			@Override
			public void onIOException(IOException arg0) {
				gettingGroup = false;
			}

			@Override
			public void onError(WeiboException arg0) {
				gettingGroup = false;
			}

			@Override
			public void onComplete(String response) {
				gettingGroup = false;
				groups.addAll(Group.parseGroup(response));
				mHandler.sendEmptyMessage(AppConstant.MSG_UPDATA_GROUP);
			}

			@Override
			public void onComplete4binary(ByteArrayOutputStream responseOS) {
			}
		});
	}

	private void stopScroll() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
			return;
		}
		Class<?> classType = HorizontalScrollView.class;
		try {
			Field field = classType.getDeclaredField("mScroller");
			field.setAccessible(true);
			OverScroller scroller = (OverScroller) field.get(scrollView);
			scroller.abortAnimation();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(divide_view.isOpen()){
			divide_view.close();
			return true;
		}
		switch (item.getItemId()) {
		case R.id.sort:
			if (!gettingGroup && groups.size() == 0) {
				getGroup();
				Toast.makeText(getApplicationContext(),
						getResources().getString(R.string.text_loading),
						Toast.LENGTH_SHORT).show();
			}
			break;

		case R.id.refresh:
			getTwitter(true);
			break;

		case R.id.timeline:
			Intent timeLineIntent = new Intent(getApplicationContext(),
					TimeLineModeActivity.class);
			startActivity(timeLineIntent);
			break;

		case R.id.profile:
			Intent profileIntent = new Intent(getApplicationContext(),
					ProfileActivity.class);
			profileIntent.putExtra("uid",
					Persistence.getUID(getApplicationContext()));
			startActivity(profileIntent);
			break;
		}

		if (item.getGroupId() == R.id.sort) {
			int id = item.getItemId();
			if (id == R.id.menu_group_all) {
				if (current != homeState) {
					current = homeState;
					getTwitter(true);
				}
			} else {
				Group g = groups.get(id);
				if (g.getId() != list_id) {
					current = groupState;
					list_id = g.getId();
					getTwitter(true);
				}
			}
		}

		return true;
	}
	
	@Override
	public void onBackPressed() {
		if(divide_view.isOpen()){
			divide_view.close();
			return;
		}
		super.onBackPressed();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.new_post:
			Intent postIntent = new Intent(getApplicationContext(), NewPostActivity.class);
			startActivity(postIntent);
			break;
		case R.id.setting:
			Intent settingIntent = new Intent(getApplicationContext(),
					SettingActivity.class);
			startActivity(settingIntent);
			break;
		case R.id.header:
			if(divide_view.isOpen()){
				divide_view.close();
			}else{
				stopScroll();
				divide_view.setVisibility(View.VISIBLE);
				divide_view.divide(0, getActionBaHeight()
						+ (int) DisplayUtil.dpToPx(getResources(), 5), scrollView,
						innerButtonLay);
			}
			break;
		case R.id.plaza:
			Intent plazaActivity = new Intent(getApplicationContext(),
					PlazaActivity.class);
			startActivity(plazaActivity);
			break;
		case R.id.at:
			Intent atMeActivity = new Intent(getApplicationContext(),
					TimeLineModeActivity.class);
			atMeActivity.putExtra("pos", TimeLineModeActivity.VIEW_AT_ME);
			startActivity(atMeActivity);
			break;
		case R.id.comment:
			Intent commentActivity = new Intent(getApplicationContext(),
					TimeLineModeActivity.class);
			commentActivity.putExtra("pos", TimeLineModeActivity.VIEW_COMMENT);
			startActivity(commentActivity);
			break;
		}
	}

	@Override
	protected void onRequestComplete(RequestState requestState) {
		if (requestState.requestType != STATE_HOME
				|| TextUtils.isEmpty(requestState.response)) {
			return;
		}
		String data = Persistence.getHomeData(getApplicationContext());
		if (TextUtils.isEmpty(data) || requestState.isRefresh) {
			Debug.e("save data.............");
			Persistence.setHomeData(getApplicationContext(),
					requestState.response);
		}
	}

}
