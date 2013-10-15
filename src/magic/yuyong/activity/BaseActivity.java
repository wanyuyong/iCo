

package magic.yuyong.activity;

import magic.yuyong.app.AppConstant;
import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

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
		registerReceiver(shutDownReceiver, new IntentFilter(AppConstant.ACTION_SHUT_DOWN_BROADCAST));
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
	
	protected void shutDown(){
		sendBroadcast(new Intent(AppConstant.ACTION_SHUT_DOWN_BROADCAST));
	}

}
