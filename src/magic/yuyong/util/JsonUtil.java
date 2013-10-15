package magic.yuyong.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtil {
	
	public static int getInt(JSONObject jsonObj, String attribute) {
		int value = 0;
		if (jsonObj.has(attribute)) {
			try {
				value = jsonObj.getInt(attribute);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return value;
	}

	public static String getString(JSONObject jsonObj, String attribute) {
		String value = "";
		if (jsonObj.has(attribute)) {
			try {
				value = jsonObj.getString(attribute);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return value;
	}

	public static long getLong(JSONObject jsonObj, String attribute) {
		long value = 0;
		if (jsonObj.has(attribute)) {
			try {
				value = jsonObj.getLong(attribute);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return value;
	}
	
	public static boolean getBoolean(JSONObject jsonObj, String attribute) {
		boolean value = false;
		if (jsonObj.has(attribute)) {
			try {
				value = jsonObj.getBoolean(attribute);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return value;
	}
	
	public static JSONObject getJSONObject(JSONObject jsonObj, String attribute) {
		JSONObject value = null;
		if (jsonObj.has(attribute)) {
			try {
				value = jsonObj.getJSONObject(attribute);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return value;
	}
	
	public static JSONArray getJSONArray(JSONObject jsonObj, String attribute) {
		JSONArray value = null;
		if (jsonObj.has(attribute)) {
			try {
				value = jsonObj.getJSONArray(attribute);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return value;
	}
}
