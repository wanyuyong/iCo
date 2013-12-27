package magic.yuyong.model;

import java.util.ArrayList;
import java.util.List;

import magic.yuyong.util.Debug;
import magic.yuyong.util.JsonUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Parcel;

public class User implements android.os.Parcelable {
	private static final String ID = "id";
	private static final String SCREEN_NAME = "screen_name";
	private static final String PROFILE_IMAGE_URL = "profile_image_url";
	private static final String AVATAR_LARGE = "avatar_large";
	private static final String GENDER = "gender";
	private static final String LOCATION = "location";
	private static final String DESCRIPTION = "description";
	private static final String FOLLOWERS_COUNT = "followers_count";
	private static final String FRIENDS_COUNT = "friends_count";
	private static final String STATUSES_COUNT = "statuses_count";
	private static final String FAVOURITES_COUNT = "favourites_count";
	private static final String FOLLOWING = "following";
	private static final String FOLLOW_ME = "follow_me";

	private Long id;
	private String screen_name;
	private String profile_image_url;
	private String avatar_large;
	private String gender;
	private String location;
	private String description;
	private int followers_count;
	private int friends_count;
	private int statuses_count;
	private int favourites_count;
	private boolean following;
	private boolean follow_me;

	private boolean isChoose;

	public User() {
		super();
		this.id = 0L;
		this.screen_name = "";
		this.profile_image_url = "";
		this.avatar_large = "";
		this.gender = "";
		this.location = "";
		this.description = "";
		this.followers_count = 0;
		this.friends_count = 0;
		this.statuses_count = 0;
		this.favourites_count = 0;
		this.following = false;
		this.follow_me = false;
		this.isChoose = false;
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

	public String getProfile_image_url() {
//		return profile_image_url;
		return avatar_large;
	}

	public void setProfile_image_url(String profile_image_url) {
		this.profile_image_url = profile_image_url;
	}

	public String getAvatar_large() {
		return avatar_large;
	}

	public void setAvatar_large(String avatar_large) {
		this.avatar_large = avatar_large;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getFollowers_count() {
		return followers_count;
	}

	public void setFollowers_count(int followers_count) {
		this.followers_count = followers_count;
	}

	public int getFriends_count() {
		return friends_count;
	}

	public void setFriends_count(int friends_count) {
		this.friends_count = friends_count;
	}

	public int getStatuses_count() {
		return statuses_count;
	}

	public void setStatuses_count(int statuses_count) {
		this.statuses_count = statuses_count;
	}

	public int getFavourites_count() {
		return favourites_count;
	}

	public void setFavourites_count(int favourites_count) {
		this.favourites_count = favourites_count;
	}

	public boolean isFollowing() {
		return following;
	}

	public void setFollowing(boolean following) {
		this.following = following;
	}

	public boolean isFollow_me() {
		return follow_me;
	}

	public void setFollow_me(boolean follow_me) {
		this.follow_me = follow_me;
	}

	public boolean isChoose() {
		return isChoose;
	}

	public void setChoose(boolean isChoose) {
		this.isChoose = isChoose;
	}

	public static User parse(JSONObject jsonObj) {
		User user = new User();
		user.setId(JsonUtil.getLong(jsonObj, ID));
		user.setScreen_name(JsonUtil.getString(jsonObj, SCREEN_NAME));
		user.setProfile_image_url(JsonUtil
				.getString(jsonObj, PROFILE_IMAGE_URL));
		user.setAvatar_large(JsonUtil
				.getString(jsonObj, AVATAR_LARGE));
		user.setGender(JsonUtil.getString(jsonObj, GENDER));
		user.setLocation(JsonUtil.getString(jsonObj, LOCATION));
		user.setDescription(JsonUtil.getString(jsonObj, DESCRIPTION));
		user.setFriends_count(JsonUtil.getInt(jsonObj, FRIENDS_COUNT));
		user.setStatuses_count(JsonUtil.getInt(jsonObj, STATUSES_COUNT));
		user.setFollowers_count(JsonUtil.getInt(jsonObj, FOLLOWERS_COUNT));
		user.setFavourites_count(JsonUtil.getInt(jsonObj, FAVOURITES_COUNT));
		user.setFollowing(JsonUtil.getBoolean(jsonObj, FOLLOWING));
		user.setFollow_me(JsonUtil.getBoolean(jsonObj, FOLLOW_ME));
		
		Debug.v("name : "+user.getScreen_name()+"   id : "+user.getId());
		
		return user;
	}

	public static List<User> parseUsers(String json) {
		try {
			List<User> list = new ArrayList<User>();
			JSONObject jsonObj = new JSONObject(json);
			JSONArray jsonArray = JsonUtil.getJSONArray(jsonObj, "users");
			if (jsonArray != null && jsonArray.length() != 0) {
				for (int i = 0; i < jsonArray.length(); i++) {
					jsonObj = ((JSONObject) jsonArray.opt(i));
					User user = parse(jsonObj);
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

	public static final android.os.Parcelable.Creator<User> CREATOR = new android.os.Parcelable.Creator<User>() {

		@Override
		public User createFromParcel(Parcel source) {
			User user = new User();
			user.id = source.readLong();
			user.screen_name = source.readString();
			user.profile_image_url = source.readString();
			user.avatar_large = source.readString();
			user.gender = source.readString();
			user.location = source.readString();
			user.description = source.readString();
			user.friends_count = source.readInt();
			user.statuses_count = source.readInt();
			user.followers_count = source.readInt();
			user.favourites_count = source.readInt();
			user.following = Boolean.valueOf(source.readString());
			user.follow_me = Boolean.valueOf(source.readString());
			return user;
		}

		@Override
		public User[] newArray(int size) {
			return new User[size];
		}

	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(screen_name);
		dest.writeString(profile_image_url);
		dest.writeString(avatar_large);
		dest.writeString(gender);
		dest.writeString(location);
		dest.writeString(description);
		dest.writeInt(friends_count);
		dest.writeInt(statuses_count);
		dest.writeInt(followers_count);
		dest.writeInt(favourites_count);
		dest.writeString(String.valueOf(following));
		dest.writeString(String.valueOf(follow_me));
	}
}
