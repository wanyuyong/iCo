package magic.yuyong.activity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import magic.yuyong.R;
import magic.yuyong.animaiton.Rotate3dAnimation;
import magic.yuyong.app.AppConstant;
import magic.yuyong.app.MagicApplication;
import magic.yuyong.drawable.Diagonal;
import magic.yuyong.extend.FriendshipsAPI_E;
import magic.yuyong.extend.UnReadAPI;
import magic.yuyong.model.Group;
import magic.yuyong.model.Twitter;
import magic.yuyong.persistence.Persistence;
import magic.yuyong.request.RequestState;
import magic.yuyong.util.DisplayUtil;
import magic.yuyong.view.DivideView;
import magic.yuyong.view.LeftSlideView;
import magic.yuyong.view.TwitterBoard;
import magic.yuyong.view.TwitterBoardScrollView;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.OverScroller;
import android.widget.ProgressBar;
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
	private LeftSlideView slideView;
	private View appName;
	private View rightBar;
	private LinearLayout groupLay;
	private TextView all;
	private View timeline, refresh, setting;
	private ProgressBar pb;
	private TextView start;
	private View more;
	private DivideView divide_view;
	private View content;
	private View innerButtonLay;

	private List<Group> groups = new ArrayList<Group>();
	private Long list_id;
	private boolean gettingGroup;

	private static final int STATE_HOME = 0;
	private static final int STATE_AT_ME = 1;
	private static final int STATE_GROUP = 2;
	private RequestState current;

	private RequestState homeState, atMeState, groupState;

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
				for (final Group group : groups) {
					TextView textView = (TextView) getLayoutInflater().inflate(
							R.layout.group_but, null);
					int margin = (int) DisplayUtil.dpToPx(getResources(), 2);

					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT,
							LinearLayout.LayoutParams.WRAP_CONTENT);
					lp.setMargins(margin, margin, margin, margin);
					textView.setLayoutParams(lp);
					textView.setText(group.getName());

					groupLay.addView(textView, 0);
					textView.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							if (list_id == null || list_id != group.getId()) {
								list_id = group.getId();
								current = groupState;
								getTwitter(true);
							}
							slideView.slide();
						}
					});
				}
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
	protected void onCreate(Bundle savedInstanceState) {
		fullScreen();
		super.onCreate(savedInstanceState);

		checkUpdate();

		registerReceiver(unReadReceiver, new IntentFilter(
				AppConstant.ACTION_UNREAD_STATE_CHANGE_BROADCAST));
		setContentView(R.layout.twitter_board);
		board = (TwitterBoard) findViewById(R.id.twitter_board);
		pb = (ProgressBar) findViewById(R.id.progress);
		board.setBoundaryListener(this);
		scrollView = (TwitterBoardScrollView) findViewById(R.id.twitter_board_scrollview);
		scrollView.setTwitterBoardScrollListener(board);
		board.setScrollView(scrollView);
		pb.setVisibility(View.VISIBLE);
		appName = findViewById(R.id.app_name);
		more = findViewById(R.id.more_but);
		more.setOnClickListener(this);
		start = (TextView) findViewById(R.id.start_but);
		start.setOnClickListener(this);
		divide_view = (DivideView) findViewById(R.id.divide_view);
		content = findViewById(R.id.content);
		slideView = (LeftSlideView) findViewById(R.id.slide_view);
		slideView.setGesture(false);
		slideView.setListener(new LeftSlideView.Listener() {
			@Override
			public void onStateChange(boolean onRight) {
				doAnimation(onRight);
			}
		});
		rightBar = findViewById(R.id.right_bar);
		groupLay = (LinearLayout) rightBar.findViewById(R.id.group_lay);
		all = (TextView) rightBar.findViewById(R.id.all_but);
		all.setOnClickListener(this);
		refresh = rightBar.findViewById(R.id.refresh_but);
		refresh.setOnClickListener(this);
		setting = rightBar.findViewById(R.id.setting_but);
		setting.setOnClickListener(this);
		timeline = rightBar.findViewById(R.id.timeline_but);
		timeline.setOnClickListener(this);

		homeState = new RequestState(STATE_HOME);
		atMeState = new RequestState(STATE_AT_ME);
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
		View homeButton = innerButtonLay.findViewById(R.id.home);
		homeButton.setOnClickListener(this);
		View postButton = innerButtonLay.findViewById(R.id.new_post);
		postButton.setOnClickListener(this);
		View atButton = innerButtonLay.findViewById(R.id.at);
		atButton.setOnClickListener(this);
		View commentButton = innerButtonLay.findViewById(R.id.comment);
		commentButton.setOnClickListener(this);
		View profileButton = innerButtonLay.findViewById(R.id.profile);
		profileButton.setOnClickListener(this);
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
			pb.setVisibility(View.GONE);
			if (requestState.isRefresh) {
				board.refresh();
			}
			List<Twitter> twitters = Twitter
					.parseTwitter(requestState.response);
			if (twitters.size() == 0) {
				requestState.isBottom = true;
				Toast.makeText(getApplicationContext(),
						getResources().getText(R.string.text_nomore_data),
						Toast.LENGTH_SHORT).show();
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
			pb.setVisibility(View.GONE);
		}
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
				pb.setVisibility(View.VISIBLE);

				switch (requestState.requestType) {
				case STATE_HOME:
					StatusesAPI homeAPI = new StatusesAPI(MagicApplication
							.getInstance().getAccessToken());
					homeAPI.homeTimeline(0, requestState.maxId,
							AppConstant.PAGE_NUM, requestState.page, false,
							WeiboAPI.FEATURE.ALL, false,
							new TwitterRequestListener(requestState));
					break;

				case STATE_AT_ME:
					StatusesAPI atMeAPI = new StatusesAPI(MagicApplication
							.getInstance().getAccessToken());
					atMeAPI.mentions(0, requestState.maxId,
							AppConstant.PAGE_NUM, requestState.page,
							WeiboAPI.AUTHOR_FILTER.ALL,
							WeiboAPI.SRC_FILTER.ALL, WeiboAPI.TYPE_FILTER.ALL,
							false, new TwitterRequestListener(requestState));
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

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		Rect outRect = new Rect();
		rightBar.getGlobalVisibleRect(outRect);
		if (slideView.isOpen()
				&& !outRect.contains((int) ev.getX(), (int) ev.getY())) {
			slideView.slide();
			return true;
		}
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && slideView.isOpen()) {
			slideView.slide();
			return true;
		}
		return super.onKeyDown(keyCode, event);
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

	private void doAnimation(boolean open) {
		float fromDegrees = 0;
		float toDegrees = 0;
		float fromX = 0;
		float toX = 0;
		float fromAlpha = 0;
		float toAlpha = 0;
		if (open) {
			fromDegrees = 10;
			toDegrees = 0;
			fromX = rightBar.getWidth() / 3;
			toX = 0;
			fromAlpha = .3f;
			toAlpha = 1;
		} else {
			fromDegrees = 0;
			toDegrees = 10;
			fromX = 0;
			toX = rightBar.getWidth() / 3;
			fromAlpha = 1;
			toAlpha = .3f;
		}
		Animation animaiont = new Rotate3dAnimation(fromDegrees, toDegrees,
				fromX, toX, 0, 0, fromAlpha, toAlpha, 0,
				rightBar.getHeight() / 2);
		animaiont.setDuration(LeftSlideView.DURATION / 2);
		rightBar.startAnimation(animaiont);
	}

	private void pandaAnimation() {
		Animation a = new AlphaAnimation(0, 1);
		a.setDuration(1000);
		appName.startAnimation(a);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.more_but:
			slideView.slide();
			pandaAnimation();
			if (!gettingGroup && groups.size() == 0) {
				getGroup();
			}
			break;
		case R.id.refresh_but:
			getTwitter(true);
			slideView.slide();
			break;
		case R.id.setting_but:
			Intent settingIntent = new Intent(getApplicationContext(),
					SettingActivity.class);
			startActivity(settingIntent);
			break;
		case R.id.start_but:
			stopScroll();
			divide_view.setVisibility(View.VISIBLE);
			divide_view.divide(0, start.getHeight() + start.getTop()
					+ (int) DisplayUtil.dpToPx(getResources(), 5), content,
					innerButtonLay);
			break;
		case R.id.home:
			divide_view.close();
			current = homeState;
			getTwitter(true);
			break;
		case R.id.new_post:
			Intent newPostIntent = new Intent(getApplicationContext(),
					NewPostActivity.class);
			startActivity(newPostIntent);
			break;
		case R.id.at:
			divide_view.close();
			current = atMeState;
			getTwitter(true);
			if (Persistence.getMention_status(getApplicationContext()) != 0) {
				UnReadAPI unReadAPI = new UnReadAPI(MagicApplication
						.getInstance().getAccessToken());
				unReadAPI.clear(new RequestListener() {

					@Override
					public void onIOException(IOException arg0) {
					}

					@Override
					public void onError(WeiboException arg0) {
					}

					@Override
					public void onComplete(String arg0) {
						Persistence.setMention_status(getApplicationContext(),
								0);
						sendBroadcast(new Intent(
								AppConstant.ACTION_UNREAD_STATE_CHANGE_BROADCAST));
					}

					@Override
					public void onComplete4binary(
							ByteArrayOutputStream responseOS) {
					}
				}, UnReadAPI.TYPE_MENTION_STATUS);
			}
			break;
		case R.id.comment:
			Intent commentActivity = new Intent(getApplicationContext(),
					TimeLineModeActivity.class);
			commentActivity.putExtra("pos", TimeLineModeActivity.VIEW_COMMENT);
			startActivity(commentActivity);
			break;
		case R.id.timeline_but:
			Intent timeLineIntent = new Intent(getApplicationContext(),
					TimeLineModeActivity.class);
			startActivity(timeLineIntent);
			break;
		case R.id.all_but:
			if (current != homeState) {
				current = homeState;
				getTwitter(true);
			}
			slideView.slide();
			break;
		case R.id.profile:
			Intent profileIntent = new Intent(getApplicationContext(),
					ProfileActivity.class);
			profileIntent.putExtra("uid",
					Persistence.getUID(getApplicationContext()));
			startActivity(profileIntent);
			break;
		default:
			break;
		}
	}

}
