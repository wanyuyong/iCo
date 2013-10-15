package magic.yuyong.model;

import magic.yuyong.persistence.Persistence;
import magic.yuyong.util.Debug;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class Config {
	private boolean advertisement;

	public boolean getAdvertisement() {
		return advertisement;
	}

	public void setAdvertisement(boolean advertisement) {
		this.advertisement = advertisement;
	}

	public void parsa(JSONObject json) {
		String _advertisement = null;

		try {
			_advertisement = json.getString("advertisement");
		} catch (JSONException e) {
			Debug.e("JSONException : " + e.getMessage());
		}

		if (_advertisement != null) {
			advertisement = Boolean.valueOf(_advertisement);
		}
	}

	public static Config obtainConfig(Context context) {
		Config config = new Config();
		config.advertisement = Persistence.getAdvertisement(context);
		return config;
	}

	public void save(Context context) {
		Persistence.setAdvertisement(context, advertisement);
	}
}
