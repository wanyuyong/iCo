package magic.yuyong.activity;

import java.io.IOException;

import com.umeng.update.UmengUpdateAgent;

import magic.yuyong.R;
import magic.yuyong.app.MagicApplication;
import magic.yuyong.persistence.AccessTokenKeeper;
import magic.yuyong.persistence.Persistence;
import magic.yuyong.service.NotificationService;
import magic.yuyong.util.Debug;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.AccountAPI;
import com.weibo.sdk.android.net.RequestListener;

public class MainActivity extends BaseActivity implements OnClickListener,
		WeiboAuthListener {
	private TextView login_but;
	private View content;
	private View clouds_1, clouds_2, clouds_3, clouds_4, clouds_5;
	private boolean has_check_update = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		actionBar.hide();
		actionBar.setSplitBackgroundDrawable(getResources().getDrawable(R.color.theme_color));
		setContentView(R.layout.main);
		content = findViewById(R.id.content);
		login_but = (TextView) this.findViewById(R.id.login_but);
		login_but.setOnClickListener(this);
		clouds_1 = findViewById(R.id.clouds_1);
		clouds_2 = findViewById(R.id.clouds_2);
		clouds_3 = findViewById(R.id.clouds_3);
		clouds_4 = findViewById(R.id.clouds_4);
		clouds_5 = findViewById(R.id.clouds_5);

		Debug.v("token == null ? "
				+ (MagicApplication.getInstance().getAccessToken() == null));
		checkToken();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		cloudsFlutter();
	}

	@Override
	protected void onStop() {
		super.onStop();
		cloudsStop();
	}
	
	private Animation getAnimation(int duration){
		Animation a = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -0.95f, 
				Animation.RELATIVE_TO_PARENT, 1, 
				Animation.RELATIVE_TO_SELF, 0, 
				Animation.RELATIVE_TO_SELF, 0);
		a.setDuration(duration);
		a.setRepeatCount(Integer.MAX_VALUE);
		return a;
	}

	private void cloudsFlutter(){
		clouds_1.startAnimation(getAnimation(20000));
		clouds_2.startAnimation(getAnimation(37000));
		clouds_3.startAnimation(getAnimation(15000));
		clouds_4.startAnimation(getAnimation(18000));
		clouds_5.startAnimation(getAnimation(30000));
	}
	
	private void cloudsStop(){
		clouds_1.clearAnimation();
		clouds_2.clearAnimation();
		clouds_3.clearAnimation();
		clouds_4.clearAnimation();
		clouds_5.clearAnimation();
	}

	private void checkToken() {
		Debug.e("token == null ? "+(MagicApplication.getInstance().getAccessToken() == null));
		if (MagicApplication.getInstance().getAccessToken() != null
				&& MagicApplication.getInstance().getAccessToken()
						.isSessionValid()) {
			startNotificationService();
			chooseMode();
		} else {
			has_check_update = true;
			UmengUpdateAgent.update(this);
		}
	}

	private void startNotificationService() {
		if (Persistence.isReceiveNotification(getApplicationContext())) {
			Intent service = new Intent(getApplicationContext(),
					NotificationService.class);
			startService(service);
		}
	}

	private void chooseMode() {
		Class activityClass = null;
		if (Persistence.isTimeLineMode(getApplicationContext())) {
			activityClass = TimeLineModeActivity.class;
		} else {
			activityClass = TwitterBoardActivity.class;
		}

		Intent intent = new Intent(getApplicationContext(), activityClass);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("check_update", !has_check_update);
		startActivity(intent);
		finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_but:
			if (MagicApplication.getInstance().getAccessToken() != null
					&& MagicApplication.getInstance().getAccessToken()
							.isSessionValid()) {
				chooseMode();
			} else {
				MagicApplication.getInstance().getWeibo().authorize(this, this);
			}
			break;
		}
	}

	@Override
	public void onCancel() {
	}

	@Override
	public void onComplete(Bundle values) {
		String token = values.getString("access_token");
		String expires_in = values.getString("expires_in");
		Oauth2AccessToken accessToken = new Oauth2AccessToken(token, expires_in);
		MagicApplication.getInstance().setAccessToken(accessToken);
		AccessTokenKeeper.keepAccessToken(getApplicationContext(), accessToken);
		AccountAPI accountAPI = new AccountAPI(accessToken);
		accountAPI.getUid(new RequestListener() {

			@Override
			public void onIOException(IOException arg0) {

			}

			@Override
			public void onError(WeiboException arg0) {

			}

			@Override
			public void onComplete(String response) {
				try {
					JSONObject obj = new JSONObject(response);
					long id = obj.getLong("uid");
					Persistence.setUID(getApplicationContext(), id);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

		startNotificationService();
		chooseMode();
	}

	@Override
	public void onError(WeiboDialogError e) {
		Toast.makeText(getApplicationContext(),
				"Auth error : " + e.getMessage(), Toast.LENGTH_LONG).show();
	}

	@Override
	public void onWeiboException(WeiboException e) {
		Toast.makeText(
				getApplicationContext(),
				"Auth exception : " + e.getStatusCode() + " :  "
						+ e.getMessage(), Toast.LENGTH_LONG).show();
	}
}