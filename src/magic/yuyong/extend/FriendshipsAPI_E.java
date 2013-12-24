package magic.yuyong.extend;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboParameters;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.FriendshipsAPI;

public class FriendshipsAPI_E extends FriendshipsAPI {
	private static final String SERVER_URL_PRIX = API_SERVER + "/friendships";

	public FriendshipsAPI_E(Oauth2AccessToken accessToken) {
		super(accessToken);
	}
	
	public void group(RequestListener listener) {
		WeiboParameters params = new WeiboParameters();
		request( SERVER_URL_PRIX + "/groups.json", params, HTTPMETHOD_GET, listener);
	}

	public void groupTimeline(long list_id, long since_id, long max_id_group,
			int count, int page, boolean base_app, FEATURE feature, RequestListener listener) {
		WeiboParameters params = new WeiboParameters();
		params.add("list_id", list_id);
		params.add("since_id", since_id);
		params.add("max_id", max_id_group);
		params.add("count", count);
		params.add("page", page);
		params.add("base_app", base_app ? 1 : 0);
		params.add("feature", feature.ordinal());
		request( SERVER_URL_PRIX + "/groups/timeline.json", params, HTTPMETHOD_GET, listener);
	}
	
}
