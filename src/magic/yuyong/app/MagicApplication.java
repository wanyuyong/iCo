package magic.yuyong.app;

import magic.yuyong.activity.MainActivity;
import magic.yuyong.persistence.AccessTokenKeeper;
import magic.yuyong.persistence.Persistence;
import magic.yuyong.service.NotificationService;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;


public class MagicApplication extends Application {
	
    private static MagicApplication instance  = null;
    private Oauth2AccessToken accessToken = null;
    
    @Override
	public void onCreate() {
		super.onCreate();
		instance =  this;
		accessToken = AccessTokenKeeper.readAccessToken(this);
	}

	public static MagicApplication getInstance(){
		return instance ;
	}

	public Oauth2AccessToken getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(Oauth2AccessToken accessToken) {
		this.accessToken = accessToken;
	}
	
	private void cleanPersistenceData() {
		Persistence.setBilateralData(getApplicationContext(), null);
		Persistence.setHomeData(getApplicationContext(), null);
		Persistence.setAtMeData(getApplicationContext(), null);
		Persistence.setCommentData(getApplicationContext(), null);
	}
	
	public void exit(Activity activity){
		AccessTokenKeeper
				.clear(getApplicationContext());
		MagicApplication.getInstance()
				.setAccessToken(null);
		cleanPersistenceData();
		
		// stop service
		Intent service = new Intent(
				getApplicationContext(),
				NotificationService.class);
		stopService(service);

		activity.startActivity(new Intent(
				getApplicationContext(),
				MainActivity.class));
		shutDown();
	}
	
	protected void shutDown() {
		sendBroadcast(new Intent(AppConstant.ACTION_SHUT_DOWN_BROADCAST));
	}

}
