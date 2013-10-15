package magic.yuyong.model;

import java.util.ArrayList;
import java.util.List;

import magic.yuyong.util.Debug;
import magic.yuyong.util.JsonUtil;

import org.json.JSONArray;
import org.json.JSONObject;

public class Group {
	
	private static final String ID = "id";
	private static final String NAME = "name";
	private static final String MEMBER_COUNT = "member_count";

	private Long id;
	private String name;
	private String member_count;
	
	public Group() {
		super();
	}

	public Group(Long id, String name, String member_count) {
		super();
		this.id = id;
		this.name = name;
		this.member_count = member_count;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMember_count() {
		return member_count;
	}

	public void setMember_count(String member_count) {
		this.member_count = member_count;
	}

	public static Group parseGroup(JSONObject jsonObj) {
		Group group = new Group();
		group.setId(JsonUtil.getLong(jsonObj, ID));
		group.setName(JsonUtil.getString(jsonObj, NAME));
		group.setMember_count(JsonUtil.getString(jsonObj, MEMBER_COUNT));
		return group;
	}

	public static List<Group> parseGroup(String json) {
		try {
			List<Group> list = new ArrayList<Group>();
			JSONObject jsonObj = new JSONObject(json);
			JSONArray jsonArray = JsonUtil.getJSONArray(jsonObj, "lists");
			if (jsonArray != null && jsonArray.length() != 0) {
				for (int i = 0; i < jsonArray.length(); i++) {
					jsonObj = ((JSONObject) jsonArray.opt(i));
					Group group = parseGroup(jsonObj);
					if (group != null)
						list.add(group);
				}
			}
			return list;
		} catch (Exception e) {
			Debug.e("Exception : " + e.getMessage());
		}
		return null;
	}
}
