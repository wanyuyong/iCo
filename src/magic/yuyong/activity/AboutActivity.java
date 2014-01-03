package magic.yuyong.activity;

import magic.yuyong.R;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class AboutActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		actionBar.setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.about);

		String versionName = "";
		try {
			versionName = getPackageManager().getPackageInfo(getPackageName(),
					PackageManager.GET_CONFIGURATIONS).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		TextView versionText = (TextView) findViewById(R.id.version);
		versionText.setText(getResources().getString(R.string.version) + " "
				+ versionName);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return true;
	}

}
