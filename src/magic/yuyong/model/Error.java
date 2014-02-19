package magic.yuyong.model;

import magic.yuyong.util.JsonUtil;

import org.json.JSONObject;

public class Error {

	private static final String ERROR = "error";
	private static final String ERROR_CODE = "error_code";
	private static final String REQUEST = "request";

	private String error;
	private int error_code;
	private String request;

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public int getError_code() {
		return error_code;
	}

	public void setError_code(int error_code) {
		this.error_code = error_code;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public static Error parseError(String json) {
		Error error = null;
		try {
			JSONObject jsonObj = new JSONObject(json);
			error = new Error();
			error.error = JsonUtil.getString(jsonObj, ERROR);
			error.error_code = JsonUtil.getInt(jsonObj, ERROR_CODE);
			error.request = JsonUtil.getString(jsonObj, REQUEST);
		} catch (Exception e) {
		}
		return error;
	}

}
