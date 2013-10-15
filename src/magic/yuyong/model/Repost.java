package magic.yuyong.model;

import magic.yuyong.util.Debug;
import magic.yuyong.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Repost {
	private static final String ID = "id";
	private static final String TEXT = "text";
	private static final String CREATED_AT = "created_at";
    private static final String USER = "user";

	private Long id;
	private String text;
	private String create_at;
	private User user;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getCreate_at() {
		return create_at;
	}

	public void setCreate_at(String create_at) {
		this.create_at = create_at;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public static Repost parseRepost(JSONObject jsonObj) {
		Repost repost = new Repost();
        repost.setId(JsonUtil.getLong(jsonObj, ID));
		String dataStr = JsonUtil.getString(jsonObj, CREATED_AT);
		Date date = new Date(dataStr);
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm");
		dataStr=format.format(date);
        repost.setCreate_at(dataStr);
        repost.setText(JsonUtil.getString(jsonObj, TEXT));
		JSONObject userObj = JsonUtil.getJSONObject(jsonObj, USER);
        repost.setUser(userObj == null ? new User() : User.parse(userObj));
		return repost;
	}

	public static List<Repost> parseRepost(String json) {
		List<Repost> list = new ArrayList<Repost>();
		try {
			JSONObject jsonObj = new JSONObject(json);
			JSONArray jsonArray = JsonUtil.getJSONArray(jsonObj, "reposts");
			if (jsonArray != null && jsonArray.length() != 0) {
				for (int i = 0; i < jsonArray.length(); i++) {
					jsonObj = ((JSONObject) jsonArray.opt(i));
					Repost repost = parseRepost(jsonObj);
					if (repost != null)
						list.add(repost);
				}
			}
		} catch (Exception e) {
			Debug.e("Exception : " + e.getMessage());
		}
		return list;
	}
}
