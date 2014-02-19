package magic.yuyong.activity;

import magic.yuyong.app.AppConstant;
import magic.yuyong.app.MagicApplication;
import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.sina.weibo.sdk.exception.WeiboException;
import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends Activity {
	protected Menu mOptionsMenu;
	protected ActionBar actionBar;

	private BroadcastReceiver shutDownReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			finish();
		}
	};

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		actionBar = getActionBar();

		registerReceiver(shutDownReceiver, new IntentFilter(
				AppConstant.ACTION_SHUT_DOWN_BROADCAST));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(shutDownReceiver);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		mOptionsMenu = menu;
		return true;
	}

	protected void fullScreen() {
		/* set it to be no title */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		/* set it to be full screen */
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	protected int getActionBaHeight() {
		int actionBarHeight = 0;
		TypedValue tv = new TypedValue();
		if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
			actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,
					getResources().getDisplayMetrics());
		}
		return actionBarHeight;
	}

	private static boolean isMeizu() {
		if (Build.MANUFACTURER.equalsIgnoreCase("meizu")) {
			return true;
		}
		return false;
	}

	protected void hideSmartBar() {
		if (isMeizu()) {
			ViewGroup mDecorView = (ViewGroup) getWindow().getDecorView();
			ViewGroup vg = (ViewGroup) mDecorView.getChildAt(0);
			vg.getChildAt(vg.getChildCount() - 1).setVisibility(View.GONE);
		}
	}

	protected int getScreenWidth() {
		DisplayMetrics dm = getResources().getDisplayMetrics();
		return dm.widthPixels;
	}

	protected int getScreenHeight() {
		DisplayMetrics dm = getResources().getDisplayMetrics();
		return dm.heightPixels;
	}

	protected boolean checkTokenInvalid(WeiboException exception) {
		magic.yuyong.model.Error error = magic.yuyong.model.Error
				.parseError(exception.getMessage());
		if (error != null && error.getError_code() == 10006) {
			return true;
		}
		return false;
	}

}
