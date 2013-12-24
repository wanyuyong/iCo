package magic.yuyong.activity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import magic.yuyong.R;
import magic.yuyong.app.AppConstant;
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

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.AccountAPI;
import com.umeng.update.UmengUpdateAgent;

public class MainActivity extends BaseActivity implements OnClickListener,
		WeiboAuthListener {
	private TextView login_but;
	private View content;
	private View clouds_1, clouds_2, clouds_3, clouds_4, clouds_5;
	private boolean has_check_update = false;
	private SsoHandler mSsoHandler;
	
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
				WeiboAuth mWeiboAuth = new WeiboAuth(this, AppConstant.CONSUMER_KEY, AppConstant.REDIRECT_URL, AppConstant.SCOPE);
				mSsoHandler = new SsoHandler(this, mWeiboAuth);
                mSsoHandler.authorize(this);
			}
			break;
		}
	}

	@Override
	public void onCancel() {
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
        if (mSsoHandler != null) {
        	Debug.v("resultCode : "+resultCode);
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

	@Override
	public void onComplete(Bundle values) {
		 // 从 Bundle 中解析 Token
		Oauth2AccessToken mAccessToken = Oauth2AccessToken.parseAccessToken(values);
        if (mAccessToken.isSessionValid()) {
            // 保存 Token 到 SharedPreferences
            AccessTokenKeeper.writeAccessToken(this, mAccessToken);
            MagicApplication.getInstance().setAccessToken(mAccessToken);
            startNotificationService();
    		chooseMode();
    		
    		AccountAPI accountAPI = new AccountAPI(mAccessToken);
    		accountAPI.getUid(new RequestListener() {
				
				@Override
				public void onIOException(IOException e) {
					
				}
				
				@Override
				public void onError(WeiboException e) {
					
				}
				
				@Override
				public void onComplete4binary(ByteArrayOutputStream responseOS) {
					
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
        } else {
            Toast.makeText(this, R.string.text_token_expired, Toast.LENGTH_LONG).show();
        }
	}

	@Override
	public void onWeiboException(WeiboException e) {
		Toast.makeText(
				getApplicationContext(),
				"Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
	}
}