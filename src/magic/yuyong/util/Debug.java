package magic.yuyong.util;

import android.util.Log;

public class Debug {
	public static final String TAG = "magic";
	
	public static void v(String msg){
		Log.v(TAG, msg);
	}
	
	public static void e(String msg){
		Log.e(TAG, msg);
	}
}
