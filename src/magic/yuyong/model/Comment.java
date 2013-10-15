package magic.yuyong.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import magic.yuyong.util.Debug;
import magic.yuyong.util.JsonUtil;

import org.json.JSONArray;
import org.json.JSONObject;

public class Comment {
	private static final String ID = "id";
	private static final String TEXT = "text";
	private static final String CREATED_AT = "created_at";
	private static final String STATUS = "status";
	private static final String REPLY_COMMENT = "reply_comment";
    private static final String USER = "user";

	private Long id;
	private String text;
	private String create_at;
	private Comment reply_comment;
	private Twitter twitter;
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

	public Comment getReply_comment() {
		return reply_comment;
	}

	public void setReply_comment(Comment reply_comment) {
		this.reply_comment = reply_comment;
	}

	public Twitter getTwitter() {
		return twitter;
	}

	public void setTwitter(Twitter twitter) {
		this.twitter = twitter;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public static Comment parseComment(JSONObject jsonObj) {
		Comment comment = new Comment();
		comment.setId(JsonUtil.getLong(jsonObj, ID));
		String dataStr = JsonUtil.getString(jsonObj, CREATED_AT);
		Date date = new Date(dataStr);
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm");
		dataStr=format.format(date);
		comment.setCreate_at(dataStr);
		comment.setText(JsonUtil.getString(jsonObj, TEXT));
		JSONObject obj = JsonUtil.getJSONObject(jsonObj, REPLY_COMMENT);
		comment.setReply_comment(obj == null ? null : parseComment(obj));
		obj = JsonUtil.getJSONObject(jsonObj, STATUS);
		comment.setTwitter(obj == null ? null : Twitter.parseTwitter(obj));
		JSONObject userObj = JsonUtil.getJSONObject(jsonObj, USER);
		comment.setUser(userObj == null ? new User() : User.parse(userObj));
		return comment;
	}

	public static List<Comment> parseComment(String json) {
		List<Comment> list = new ArrayList<Comment>();
		try {
			JSONObject jsonObj = new JSONObject(json);
			JSONArray jsonArray = JsonUtil.getJSONArray(jsonObj, "comments");
			if (jsonArray != null && jsonArray.length() != 0) {
				for (int i = 0; i < jsonArray.length(); i++) {
					jsonObj = ((JSONObject) jsonArray.opt(i));
					Comment comment = parseComment(jsonObj);
					if (comment != null)
						list.add(comment);
				}
			}
		} catch (Exception e) {
			Debug.e("Exception : " + e.getMessage());
		}
		return list;
	}
}
