package magic.yuyong.extend;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboParameters;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.StatusesAPI;

public class StatusesAPI_E extends StatusesAPI {
	
	 private static final String SERVER_URL_PRIX = API_SERVER + "/statuses";

	public StatusesAPI_E(Oauth2AccessToken accessToken) {
		super(accessToken);
	}

	/**
	 * 
	 * @param uids 
	 * @param count
	 * @param page
	 * @param base_app
	 * @param feature
	 * @param listener
	 */
	public void timelineBatch(String uids, int count,
			int page, boolean base_app, FEATURE feature,
			RequestListener listener) {
		WeiboParameters params = new WeiboParameters();
		params.add("uids", uids);
		params.add("count", count);
		params.add("page", page);
		if (base_app) {
			params.add("base_app", 1);
		} else {
			params.add("base_app", 0);
		}
		params.add("feature", feature.ordinal());
		request(SERVER_URL_PRIX + "/timeline_batch.json", params,
				HTTPMETHOD_GET, listener);
	}

}
