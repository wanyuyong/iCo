package magic.yuyong.util;

import java.io.File;

import com.sina.weibo.sdk.utils.MD5;

import android.content.Context;

public class Cache {

	public static final String PICASSO_CACHE = "picasso-cache";

	public static String getPicassoCacheFilePath(String mUrl, Context context) {
		return context.getApplicationContext().getCacheDir() + File.separator
				+ PICASSO_CACHE + File.separator + MD5.hexdigest(mUrl)
				+ ".1";
	}

}
