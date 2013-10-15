package magic.yuyong.model;

import java.util.ArrayList;
import java.util.List;

import magic.yuyong.util.Debug;
import magic.yuyong.util.JsonUtil;

import org.json.JSONArray;
import org.json.JSONObject;

public class SearchUser {
	private static final String ID = "uid";
	private static final String SCREEN_NAME = "screen_name";
	private static final String FOLLOWERS_COUNT = "followers_count";

	private Long id;
	private String screen_name;
	private int followers_count;

	public SearchUser() {
		super();
		this.id = 0L;
		this.screen_name = "";
		this.followers_count = 0;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getScreen_name() {
		return screen_name;
	}

	public void setScreen_name(String screen_name) {
		this.screen_name = screen_name;
	}

	public int getFollowers_count() {
		return followers_count;
	}

	public void setFollowers_count(int followers_count) {
		this.followers_count = followers_count;
	}

	public static SearchUser parse(JSONObject jsonObj) {
		SearchUser user = new SearchUser();
		user.setId(JsonUtil.getLong(jsonObj, ID));
		user.setScreen_name(JsonUtil.getString(jsonObj, SCREEN_NAME));
		user.setFollowers_count(JsonUtil.getInt(jsonObj, FOLLOWERS_COUNT));
		return user;
	}

	public static List<SearchUser> parseUsers(String json) {
		try {
			List<SearchUser> list = new ArrayList<SearchUser>();
			JSONArray jsonArray = new JSONArray(json);
			if (jsonArray != null && jsonArray.length() != 0) {
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObj = ((JSONObject) jsonArray.opt(i));
					SearchUser user = parse(jsonObj);
					if (user != null)
						list.add(user);
				}
			}
			return list;
		} catch (Exception e) {
			Debug.e("Exception : " + e.getMessage());
		}
		return null;
	}
}
