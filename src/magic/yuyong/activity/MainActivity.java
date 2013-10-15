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
	private boolean has_check_update = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		actionBar.hide();
		actionBar.setSplitBackgroundDrawable(getResources().getDrawable(R.color.theme_color));
		setContentView(R.layout.main);
		login_but = (TextView) this.findViewById(R.id.login_but);
		login_but.setOnClickListener(this);

		Debug.v("token == null ? "
				+ (MagicApplication.getInstance().getAccessToken() == null));
		checkToken();
	}

	private void checkToken() {
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