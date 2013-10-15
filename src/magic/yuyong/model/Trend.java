package magic.yuyong.model;

public class Trend {
	private String name;
	private boolean isChoose;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isChoose() {
		return isChoose;
	}

	public void setChoose(boolean isChoose) {
		this.isChoose = isChoose;
	}

	public String makeTopicStr() {
		return "#" + name + "#";
	}
}
