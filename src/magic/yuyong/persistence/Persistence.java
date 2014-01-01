package magic.yuyong.persistence;

import android.content.Context;
import android.content.SharedPreferences;

public class Persistence {
	private final static String persistence_file = "persistence_file";
	private final static String uid = "uid";
	private final static String follower = "follower";
	private final static String cmt = "cmt";
	private final static String mention_status = "mention_status";
	private final static String mention_cmt = "mention_cmt";
	private final static String advertisement = "advertisement";
	private final static String show_time_line = "show_time_line";
	private final static String time_line_mode = "time_line_mode";
	private final static String receive_notification = "receive_notification";
	
	private final static String bilateral_data = "bilateral_data";
	private final static String home_data = "home_data";
	private final static String at_me_data = "at_me_data";
	private final static String comment_data = "comment_data";

	private static SharedPreferences getPreferences(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(
				persistence_file, 0);
		return preferences;
	}

	public static long getUID(Context context) {
		SharedPreferences preferences = getPreferences(context);
		long _uid = preferences.getLong(uid, 0L);
		return _uid;
	}

	public static void setUID(Context context, long _uid) {
		SharedPreferences preferences = getPreferences(context);
		preferences.edit().putLong(uid, _uid).commit();
	}

	public static void setFollower(Context context, int _follower) {
		SharedPreferences preferences = getPreferences(context);
		preferences.edit().putInt(follower, _follower).commit();
	}

	public static int getFollower(Context context) {
		SharedPreferences preferences = getPreferences(context);
		int _follower = preferences.getInt(follower, 0);
		return _follower;
	}

	public static void setCmt(Context context, int _cmt) {
		SharedPreferences preferences = getPreferences(context);
		preferences.edit().putInt(cmt, _cmt).commit();
	}

	public static int getCmt(Context context) {
		SharedPreferences preferences = getPreferences(context);
		int _cmt = preferences.getInt(cmt, 0);
		return _cmt;
	}

	public static void setMention_status(Context context, int _mention_status) {
		SharedPreferences preferences = getPreferences(context);
		preferences.edit().putInt(mention_status, _mention_status).commit();
	}

	public static int getMention_status(Context context) {
		SharedPreferences preferences = getPreferences(context);
		int _mention_status = preferences.getInt(mention_status, 0);
		return _mention_status;
	}

	public static void setMention_cmt(Context context, int _mention_cmt) {
		SharedPreferences preferences = getPreferences(context);
		preferences.edit().putInt(mention_cmt, _mention_cmt).commit();
	}

	public static int getMention_cmt(Context context) {
		SharedPreferences preferences = getPreferences(context);
		int _mention_cmt = preferences.getInt(mention_cmt, 0);
		return _mention_cmt;
	}

	public static boolean getAdvertisement(Context context) {
		SharedPreferences preferences = getPreferences(context);
		boolean _advertisement = preferences.getBoolean(advertisement, false);
		return _advertisement;
	}

	public static void setAdvertisement(Context context, boolean _advertisement) {
		SharedPreferences preferences = getPreferences(context);
		preferences.edit().putBoolean(advertisement, _advertisement).commit();
	}
	
	public static boolean getShowTimeLine(Context context) {
		SharedPreferences preferences = getPreferences(context);
		boolean _showTimeLine = preferences.getBoolean(show_time_line, true);
		return _showTimeLine;
	}

	public static void setShowTimeLine(Context context, boolean _showTimeLine) {
		SharedPreferences preferences = getPreferences(context);
		preferences.edit().putBoolean(show_time_line, _showTimeLine).commit();
	}
	
	public static boolean isTimeLineMode(Context context) {
		SharedPreferences preferences = getPreferences(context);
		boolean isTimeLineMode = preferences.getBoolean(time_line_mode, false);
		return isTimeLineMode;
	}

	public static void setTimeLineMode(Context context, boolean isTimeLineMode) {
		SharedPreferences preferences = getPreferences(context);
		preferences.edit().putBoolean(time_line_mode, isTimeLineMode).commit();
	}
	
	public static boolean isReceiveNotification(Context context) {
		SharedPreferences preferences = getPreferences(context);
		boolean isReceiveNotification = preferences.getBoolean(receive_notification, true);
		return isReceiveNotification;
	}

	public static void setReceiveNotification(Context context, boolean isReceiveNotification) {
		SharedPreferences preferences = getPreferences(context);
		preferences.edit().putBoolean(receive_notification, isReceiveNotification).commit();
	}
	
	public static String getBilateralData(Context context){
		SharedPreferences preferences = getPreferences(context);
		String data = preferences.getString(bilateral_data, "");
		return data;
	}
	
	public static void setBilateralData(Context context, String data){
		SharedPreferences preferences = getPreferences(context);
		preferences.edit().putString(bilateral_data, data).commit();
	}
	
	public static String getHomeData(Context context){
		SharedPreferences preferences = getPreferences(context);
		String data = preferences.getString(home_data, "");
		return data;
	}
	
	public static void setHomeData(Context context, String data){
		SharedPreferences preferences = getPreferences(context);
		preferences.edit().putString(home_data, data).commit();
	}
	
	public static String getAtMeData(Context context){
		SharedPreferences preferences = getPreferences(context);
		String data = preferences.getString(at_me_data, "");
		return data;
	}
	
	public static void setAtMeData(Context context, String data){
		SharedPreferences preferences = getPreferences(context);
		preferences.edit().putString(at_me_data, data).commit();
	}
	
	public static String getCommentData(Context context){
		SharedPreferences preferences = getPreferences(context);
		String data = preferences.getString(comment_data, "");
		return data;
	}
	
	public static void setCommentData(Context context, String data){
		SharedPreferences preferences = getPreferences(context);
		preferences.edit().putString(comment_data, data).commit();
	}
}
