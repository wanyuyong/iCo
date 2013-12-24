package magic.yuyong.extend;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboParameters;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.WeiboAPI;

public class UnReadAPI extends WeiboAPI {
	private static final String SERVER_URL_PRIX = "https://api.weibo.com/2";
	
	public static final int TYPE_FOLLOWER = 0;
	public static final int TYPE_CMT = 1;
	public static final int TYPE_MENTION_STATUS = 2;
	public static final int TYPE_MENTION_CMT = 3;

	public UnReadAPI(Oauth2AccessToken accessToken) {
		super(accessToken);
	}
	
	public void unRead(RequestListener listener) {
		WeiboParameters params = new WeiboParameters();
		request( SERVER_URL_PRIX + "/remind/unread_count.json", params, HTTPMETHOD_GET, listener);
	}
	
	public void clear(RequestListener listener, int type_id) {
		String type = null;
		switch (type_id) {
		case TYPE_FOLLOWER:
			type = "follower";
			break;
		case TYPE_CMT:
			type = "cmt";
			break;
		case TYPE_MENTION_STATUS:
			type = "mention_status";
			break;
		case TYPE_MENTION_CMT:
			type = "mention_cmt";
			break;
		}
		WeiboParameters params = new WeiboParameters();
		params.add("type", type);
		request( SERVER_URL_PRIX + "/remind/set_count.json", params, HTTPMETHOD_GET, listener);
	}
}
