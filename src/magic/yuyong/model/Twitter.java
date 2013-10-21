package magic.yuyong.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import magic.yuyong.util.Debug;
import magic.yuyong.util.JsonUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Parcel;

public class Twitter implements android.os.Parcelable {
	private static final String ID = "id";
	private static final String TEXT = "text";
	private static final String COMMENTS_COUNT = "comments_count";
	private static final String REPOSTS_COUNT = "reposts_count";
	private static final String THUMBNAIL_PIC = "thumbnail_pic";
	private static final String BMIDDLE_PIC = "bmiddle_pic";
	private static final String ORIGINAL_PIC = "original_pic";
	private static final String PWIDTH = "pwidth";
	private static final String PHEIGHT = "pheight";
	private static final String CREATED_AT = "created_at";
	private static final String FAVORITED = "favorited";
	private static final String SOURCE = "source";
	private static final String DELETED = "deleted";
	private static final String PIC_URLS = "pic_urls";

	private Long id;
	private String text;
	private int comments_count;
	private int reposts_count;
	private String thumbnail_pic;
	private String bmiddle_pic;
	private String original_pic;
	private int pic_num = 0;
	private String[] pic_urls = new String[pic_num];
	private int pwidth;
	private int pheight;
	private String created_at;
	private boolean favorited;
	private String source;
	private boolean deleted;
	private User user;
	private Twitter origin;

	private Object extra;

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

	public int getComments_count() {
		return comments_count;
	}

	public void setComments_count(int comments_count) {
		this.comments_count = comments_count;
	}

	public int getReposts_count() {
		return reposts_count;
	}

	public void setReposts_count(int reposts_count) {
		this.reposts_count = reposts_count;
	}


	public int getPic_num() {
		return pic_num;
	}

	public void setPic_num(int pic_num) {
		this.pic_num = pic_num;
	}

	public String[] getPic_urls() {
		return pic_urls;
	}

	public void setPic_urls(String[] pic_urls) {
		this.pic_urls = pic_urls;
	}

	public String getThumbnail_pic() {
		return thumbnail_pic;
	}

	public void setThumbnail_pic(String thumbnail_pic) {
		this.thumbnail_pic = thumbnail_pic;
	}

	public String getBmiddle_pic() {
		return bmiddle_pic;
	}

	public void setBmiddle_pic(String bmiddle_pic) {
		this.bmiddle_pic = bmiddle_pic;
	}

	public String getOriginal_pic() {
		return original_pic;
	}

	public void setOriginal_pic(String original_pic) {
		this.original_pic = original_pic;
	}

	public int getPwidth() {
		return pwidth;
	}

	public void setPwidth(int pwidth) {
		this.pwidth = pwidth;
	}

	public int getPheight() {
		return pheight;
	}

	public void setPheight(int pheight) {
		this.pheight = pheight;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public boolean isFavorited() {
		return favorited;
	}

	public void setFavorited(boolean favorited) {
		this.favorited = favorited;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Twitter getOrigin() {
		return origin;
	}

	public void setOrigin(Twitter origin) {
		this.origin = origin;
	}

	public Object getExtra() {
		return extra;
	}

	public void setExtra(Object extra) {
		this.extra = extra;
	}

	private static String getSourceText(String source) {
		if (source.indexOf(">") == -1 || source.indexOf("<") == -1) {
			return "iCo";
		}
		return source.substring(source.indexOf(">") + 1,
				source.lastIndexOf("<"));
	}

	public static Twitter parseTwitter(JSONObject jsonObj) {
		Twitter twitter = new Twitter();
		twitter.setDeleted(JsonUtil.getString(jsonObj, DELETED).equals("1"));
		twitter.setText(JsonUtil.getString(jsonObj, TEXT));
		twitter.setId(JsonUtil.getLong(jsonObj, ID));
		if (twitter.isDeleted()) {
			return twitter;
		}
		String dataStr = JsonUtil.getString(jsonObj, CREATED_AT);
		Date date = new Date(dataStr);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		dataStr = format.format(date);
		twitter.setCreated_at(dataStr);
		twitter.setComments_count(JsonUtil.getInt(jsonObj, COMMENTS_COUNT));
		twitter.setReposts_count(JsonUtil.getInt(jsonObj, REPOSTS_COUNT));
		String source = JsonUtil.getString(jsonObj, SOURCE);
		twitter.setSource(getSourceText(source));
		JSONArray array = JsonUtil.getJSONArray(jsonObj, PIC_URLS);
		if (array != null) {
			twitter.pic_num = array.length();
			twitter.pic_urls = new String[twitter.pic_num];
			for (int i = 0; i < array.length(); i++) {
				try {
					JSONObject o = array.getJSONObject(i);
					twitter.pic_urls[i] = o.getString(THUMBNAIL_PIC);
				} catch (Exception e) {}
			}
		}
		twitter.setThumbnail_pic(JsonUtil.getString(jsonObj, THUMBNAIL_PIC));
		twitter.setBmiddle_pic(JsonUtil.getString(jsonObj, BMIDDLE_PIC));
		twitter.setOriginal_pic(JsonUtil.getString(jsonObj, ORIGINAL_PIC));
		twitter.setFavorited(JsonUtil.getBoolean(jsonObj, FAVORITED));
		JSONObject userObj = JsonUtil.getJSONObject(jsonObj, "user");
		User user = userObj == null ? new User() : User.parse(userObj);
		twitter.setUser(user);
		JSONObject originObj = JsonUtil.getJSONObject(jsonObj,
				"retweeted_status");
		twitter.origin = originObj == null ? null : parseTwitter(originObj);
		return twitter;
	}

	public static List<Twitter> parseTwitter(String json) {
		List<Twitter> list = new ArrayList<Twitter>();
		try {
			JSONObject jsonObj = new JSONObject(json);
			JSONArray jsonArray = JsonUtil.getJSONArray(jsonObj, "statuses");
			if (jsonArray != null && jsonArray.length() != 0) {
				for (int i = 0; i < jsonArray.length(); i++) {
					jsonObj = ((JSONObject) jsonArray.opt(i));
					Twitter twitter = parseTwitter(jsonObj);
					if (twitter != null)
						list.add(twitter);
				}
			}
		} catch (Exception e) {
			Debug.e("Exception : " + e.getMessage());
		}
		return list;
	}

	public static List<Twitter> parseFavorites(String json) {
		List<Twitter> list = new ArrayList<Twitter>();
		try {
			JSONObject jsonObj = new JSONObject(json);
			JSONArray jsonArray = JsonUtil.getJSONArray(jsonObj, "favorites");
			if (jsonArray != null && jsonArray.length() != 0) {
				for (int i = 0; i < jsonArray.length(); i++) {
					jsonObj = ((JSONObject) jsonArray.opt(i));
					Twitter twitter = parseTwitter(jsonObj
							.getJSONObject("status"));
					if (twitter != null)
						list.add(twitter);
				}
			}
		} catch (Exception e) {
			Debug.e("Exception : " + e.getMessage());
		}
		return list;
	}

	public static final android.os.Parcelable.Creator<Twitter> CREATOR = new android.os.Parcelable.Creator<Twitter>() {

		@Override
		public Twitter createFromParcel(Parcel source) {
			Twitter twitter = new Twitter();
			twitter.deleted = source.readString().equals("1");
			if (twitter.deleted) {
				twitter.id = source.readLong();
				twitter.text = source.readString();
			} else {
				twitter.id = source.readLong();
				twitter.text = source.readString();
				twitter.created_at = source.readString();
				twitter.favorited = Boolean.valueOf(source.readString());
				twitter.source = source.readString();
				twitter.pic_num = source.readInt();
				twitter.pic_urls = new String[twitter.pic_num];
				source.readStringArray(twitter.pic_urls);
				twitter.thumbnail_pic = source.readString();
				twitter.bmiddle_pic = source.readString();
				twitter.original_pic = source.readString();
				twitter.pwidth = source.readInt();
				twitter.pheight = source.readInt();
				twitter.comments_count = source.readInt();
				twitter.reposts_count = source.readInt();
				twitter.user = source.readParcelable(User.class
						.getClassLoader());
				twitter.origin = source.readParcelable(Twitter.class
						.getClassLoader());
			}
			return twitter;
		}

		@Override
		public Twitter[] newArray(int size) {
			return new Twitter[size];
		}

	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		if (deleted) {
			dest.writeString("1");
			dest.writeLong(id);
			dest.writeString(text);
		} else {
			dest.writeString("0");
			dest.writeLong(id);
			dest.writeString(text);
			dest.writeString(created_at);
			dest.writeString(String.valueOf(favorited));
			dest.writeString(source);
			dest.writeInt(pic_num);
			dest.writeStringArray(pic_urls);
			dest.writeString(thumbnail_pic);
			dest.writeString(bmiddle_pic);
			dest.writeString(original_pic);
			dest.writeInt(pwidth);
			dest.writeInt(pheight);
			dest.writeInt(comments_count);
			dest.writeInt(reposts_count);
			dest.writeParcelable(user, 0);
			dest.writeParcelable(origin, 1);
		}
	}
}
