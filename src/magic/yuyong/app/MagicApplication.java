package magic.yuyong.app;

import magic.yuyong.persistence.AccessTokenKeeper;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;


public class MagicApplication extends GDApplication {
	
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

}
