//package magic.yuyong.activity;
//
//import java.util.List;
//
//import magic.yuyong.R;
//import magic.yuyong.app.AppConstant;
//import magic.yuyong.app.MagicApplication;
//import magic.yuyong.app.MagicDialog;
//import magic.yuyong.drawable.Diagonal;
//import magic.yuyong.model.Twitter;
//import magic.yuyong.util.SDCardUtils;
//import magic.yuyong.util.WeiBoTool;
//import magic.yuyong.view.LeftSlideView;
//import magic.yuyong.view.PhotoScrollView;
//import magic.yuyong.view.PhotoWallView;
//import magic.yuyong.view.RefreshView;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.umeng.fb.UMFeedbackService;
//import com.weibo.net.Weibo;
//
//public class WeiboWallActivity extends GetTwitterActivity implements
//		PhotoScrollView.Listener, RefreshView.Listener, OnClickListener {
//
//	private int type_id;
//	private String type_name;
//
//	private PhotoWallView wall;
//	private PhotoScrollView scrollView;
//	private RefreshView rf;
//	private TextView title, more_but;
//	private LeftSlideView left_slide_view;
//	private View back_but, channel_but, cache_but, setting_but, about_but, clean_but,
//			feedback_but, exit_but;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		requsetUrl = "suggestions/statuses/hot.json";
//		setContentView(R.layout.weibowall);
//		left_slide_view = (LeftSlideView) findViewById(R.id.left_slide_view);
//
//		rf = (RefreshView) findViewById(R.id.refresh_view);
//		scrollView = (PhotoScrollView) findViewById(R.id.scroll_view);
//		scrollView.setBackgroundDrawable(new Diagonal());
//		wall = new PhotoWallView(getApplicationContext());
//		wall.setScrollView(scrollView);
//		wall.setOnItemClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Twitter twitter = (Twitter) v.getTag();
//				Intent intent = new Intent(getApplicationContext(),
//						TwitterShowActivity.class);
//				intent.putExtra("twitter", twitter);
//				startActivity(intent);
//			}
//		});
//		scrollView.setView(null, wall, null);
//		scrollView.setListener(this);
//		rf.setListener(this);
//		back_but = findViewById(R.id.back_but);
//		back_but.setOnClickListener(this);
//		more_but = (TextView) findViewById(R.id.more_but);
//		more_but.setOnClickListener(this);
//
//		channel_but = (TextView) findViewById(R.id.channel_but);
//		channel_but.setOnClickListener(this);
//		cache_but = (TextView) findViewById(R.id.cache_but);
//		cache_but.setOnClickListener(this);
//		setting_but = (TextView) findViewById(R.id.setting_but);
//		setting_but.setOnClickListener(this);
//
//		clean_but = (TextView) findViewById(R.id.clean_but);
//		clean_but.setOnClickListener(this);
//		about_but = (TextView) findViewById(R.id.about_but);
//		about_but.setOnClickListener(this);
//		feedback_but = (TextView) findViewById(R.id.feedback_but);
//		feedback_but.setOnClickListener(this);
//		exit_but = (TextView) findViewById(R.id.exit_but);
//		exit_but.setOnClickListener(this);
//
//		// clear_smallpic_but = (TextView)
//		// findViewById(R.id.clear_smallpic_but);
//		// clear_smallpic_but.setOnClickListener(this);
//
//		type_id = getIntent().getIntExtra("type_id", AppConstant.TYPE_BEAUTY);
//		String[] type_names = getResources().getStringArray(R.array.type_name);
//		title = (TextView) findViewById(R.id.title);
//		title.setText(type_name = type_names[type_id - 1]);
//		scrollView.showBottomView(true);
//		getTwitter(false);
//	}
//
//	@Override
//	public void onClick(View arg0) {
//		switch (arg0.getId()) {
//		case R.id.back_but:
//			finish();
//			break;
//
//		case R.id.more_but:
//			left_slide_view.slide();
//			break;
//
//		case R.id.channel_but:
//			Intent channel_intent = new Intent(getApplicationContext(),
//					ChannelActivity.class);
//			channel_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			startActivity(channel_intent);
//			finish();
//			break;
//
//		case R.id.cache_but:
//			Intent cache_intent = new Intent(getApplicationContext(),
//					CacheBoardActivity.class);
//			startActivity(cache_intent);
//			break;
//
//		case R.id.clean_but:
//			final MagicDialog clear_dialog = new MagicDialog(this, R.style.magic_dialog);
//			clear_dialog.setMessage(getResources().getString(R.string.title_clean), 
//					getResources().getString(R.string.text_sure_to_clean));
//			clear_dialog.addButton(R.drawable.dustbin, R.string.but_clean, new OnClickListener() {
//				
//				@Override
//				public void onClick(View arg0) {
//					new Thread(new Runnable() {
//						
//						@Override
//						public void run() {
//							MagicApplication.getInstance().getMagicDB().clear();
//							SDCardUtils.deleteCacheDir();							
//						}
//					}).start();
//					Toast.makeText(
//							getApplicationContext(),
//							getResources().getString(
//									R.string.text_clean_cache_success),
//							Toast.LENGTH_SHORT).show();
//					clear_dialog.dismiss();
//				}
//			});
//			clear_dialog.addButton(R.drawable.cancel, R.string.but_cancel, new OnClickListener() {
//				
//				@Override
//				public void onClick(View arg0) {
//					clear_dialog.dismiss();
//				}
//			});
//			clear_dialog.show();
//			break;
//
//		case R.id.exit_but:
//			final MagicDialog exit_dialog = new MagicDialog(this, R.style.magic_dialog);
//			exit_dialog.setMessage(getResources().getString(R.string.title_exit), 
//					getResources().getString(R.string.text_sure_to_eixt));
//			exit_dialog.addButton(R.drawable.logout, R.string.but_exit, new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					WeiBoTool.unAuthorization(getApplicationContext());
//					Toast.makeText(
//							getApplicationContext(),
//							getResources().getString(
//									R.string.text_login_out_success),
//							Toast.LENGTH_SHORT).show();
//					exit_dialog.dismiss();
//					Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
//					mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//					startActivity(mainIntent);
//				}
//			});
//			exit_dialog.addButton(R.drawable.cancel, R.string.but_cancel, new OnClickListener() {
//				
//				@Override
//				public void onClick(View arg0) {
//					exit_dialog.dismiss();
//				}
//			});
//			exit_dialog.show();
//			break;
//
//		case R.id.about_but:
//			Intent aboutIntent = new Intent(getApplicationContext(),
//					AboutActivity.class);
//			startActivity(aboutIntent);
//			break;
//
//		case R.id.feedback_but:
//			UMFeedbackService.setGoBackButtonVisible();
//			UMFeedbackService.openUmengFeedbackSDK(this);
//			break;
//
//		case R.id.setting_but:
//			break;
//
//		// case R.id.clear_smallpic_but:
//		// SDCardUtils.deleteSamllPic();
//		// break;
//		}
//	}
//
//	@Override
//	public void toTheViewTop() {
//	}
//
//	@Override
//	public void toTheViewButtom() {
//		getTwitter(false);
//	}
//
//	@Override
//	public void onRefresh() {
//		getTwitter(true);
//	}
//
//	@Override
//	protected void onUpdate(android.os.Message msg) {
//		super.onUpdate(msg);
//		if (isRefresh) {
//			wall.refresh();
//			rf.close();
//		}
//		wall.setIgnoreClick(false);
//		scrollView.showBottomView(false);
//		List<Twitter> twitters = Twitter.parseHotTwitter(msg.obj.toString());
//		wall.addTiles(twitters, AppConstant.NORMAL_MODE);
//	}
//
//	@Override
//	protected void prepareParameters() {
//		super.prepareParameters();
//		page = isRefresh ? 1 : page;
//		Weibo weibo = Weibo.getInstance();
//		bundle.add("access_token", weibo.getAccessToken()
//				.getToken());
//		bundle.add("count", String.valueOf(AppConstant.PAGE_NUM));
//		bundle.add("page", String.valueOf(page++));
//	}
//	
//	@Override
//	protected void onBeforeRequest() {
//		super.onBeforeRequest();
//		wall.setIgnoreClick(true);
//		scrollView.showBottomView(true);
//		bundle.add("type", String.valueOf(type_id));
//		bundle.add("is_pic", "1");
//	}
//}
