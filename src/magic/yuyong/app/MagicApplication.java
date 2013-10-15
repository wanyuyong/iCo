package magic.yuyong.app;

import magic.yuyong.persistence.AccessTokenKeeper;

import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;

public class MagicApplication extends GDApplication {
	
    private static MagicApplication instance  = null;
    private Oauth2AccessToken accessToken = null;
    private Weibo weibo = null;

    @Override
	public void onCreate() {
		super.onCreate();
		instance =  this;
		accessToken = AccessTokenKeeper.readAccessToken(this);
		weibo = Weibo.getInstance(AppConstant.CONSUMER_KEY, AppConstant.REDIRECT_URL);
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

	public Weibo getWeibo() {
		return weibo;
	}
}
