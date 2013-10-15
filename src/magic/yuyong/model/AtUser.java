package magic.yuyong.model;

import java.util.ArrayList;
import java.util.List;

import magic.yuyong.util.Debug;
import magic.yuyong.util.JsonUtil;

import org.json.JSONArray;
import org.json.JSONObject;

public class AtUser {
	private static final String ID = "uid";
	private static final String NICKNAME = "nickname";
	private static final String REMARK = "remark";

	private Long id;
	private String nickname;
	private String remark;
	
	public AtUser() {
		super();
		this.id = 0L;
		this.nickname = "";
		this.remark = "";
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public static AtUser parse(JSONObject jsonObj) {
		AtUser user = new AtUser();
		user.setId(JsonUtil.getLong(jsonObj, ID));
		user.setNickname(JsonUtil.getString(jsonObj, NICKNAME));
		user.setRemark(JsonUtil.getString(jsonObj, REMARK));
		return user;
	}

	public static List<AtUser> parseUsers(String json) {
		List<AtUser> list = new ArrayList<AtUser>();
		try {
			JSONArray jsonArray = new JSONArray(json);
			if (jsonArray != null && jsonArray.length() != 0) {
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObj = ((JSONObject) jsonArray.opt(i));
					AtUser user = parse(jsonObj);
					if (user != null)
						list.add(user);
				}
			}
		} catch (Exception e) {
			Debug.e("Exception : " + e.getMessage());
		}
		return list;
	}
}
