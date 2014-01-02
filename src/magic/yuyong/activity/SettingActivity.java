package magic.yuyong.activity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.LogoutAPI;

import magic.yuyong.R;
import magic.yuyong.app.AppConstant;
import magic.yuyong.app.MagicApplication;
import magic.yuyong.app.MagicDialog;
import magic.yuyong.persistence.AccessTokenKeeper;
import magic.yuyong.persistence.Persistence;
import magic.yuyong.request.RequestState;
import magic.yuyong.service.NotificationService;
import magic.yuyong.util.SDCardUtils;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.Toast;

public class SettingActivity extends BaseActivity implements OnClickListener {

	private CheckBox timeline_mode, notification;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case AppConstant.MSG_CLEAN_CACHE_SUCCEED:
				setProgressBarIndeterminateVisibility(false);
				Toast.makeText(getApplicationContext(),
						R.string.text_clean_cache_success, Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		actionBar.setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.setting);
		findViewById(R.id.attention_but).setOnClickListener(this);
		findViewById(R.id.search_but).setOnClickListener(this);
		findViewById(R.id.about_but).setOnClickListener(this);
		findViewById(R.id.feedback_but).setOnClickListener(this);
		findViewById(R.id.exit_but).setOnClickListener(this);
		findViewById(R.id.time_line_but).setOnClickListener(this);
		findViewById(R.id.notification_but).setOnClickListener(this);
		findViewById(R.id.donate_but).setOnClickListener(this);
		findViewById(R.id.clean_cache_but).setOnClickListener(this);
		timeline_mode = (CheckBox) findViewById(R.id.timeline_mode);
		timeline_mode.setChecked(Persistence
				.isTimeLineMode(getApplicationContext()));
		notification = (CheckBox) findViewById(R.id.notification);
		notification.setChecked(Persistence
				.isReceiveNotification(getApplicationContext()));
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

	private void cleanCache() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				SDCardUtils.cleanCacheDir();
				mHandler.sendEmptyMessage(AppConstant.MSG_CLEAN_CACHE_SUCCEED);
			}
		}).start();
	}

	private void cleanPersistenceData() {
		Persistence.setBilateralData(getApplicationContext(), null);
		Persistence.setHomeData(getApplicationContext(), null);
		Persistence.setAtMeData(getApplicationContext(), null);
		Persistence.setCommentData(getApplicationContext(), null);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.donate_but:
			Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");
			Uri uri = Uri.parse(AppConstant.DONATE_HOME);
			intent.setData(uri);
			startActivity(intent);
			break;
		case R.id.attention_but:
			Intent profileIntent = new Intent(getApplicationContext(),
					ProfileActivity.class);
			profileIntent.putExtra("uid", 1069103203l);
			startActivity(profileIntent);
			break;
		case R.id.search_but:
			Toast.makeText(getApplicationContext(), "介个功能还木做...",
					Toast.LENGTH_SHORT).show();
			break;
		case R.id.about_but:
			Intent aboutIntent = new Intent(getApplicationContext(),
					AboutActivity.class);
			startActivity(aboutIntent);
			break;
		case R.id.feedback_but:
			Intent feedbackIntent = new Intent(getApplicationContext(),
					NewPostActivity.class);
			feedbackIntent.putExtra("#",
					getResources().getString(R.string.text_feedback_topic));
			startActivity(feedbackIntent);
			break;
		case R.id.clean_cache_but:
			final MagicDialog clean_cache_dialog = new MagicDialog(this,
					R.style.magic_dialog);
			clean_cache_dialog.setMessage(
					getResources().getString(R.string.title_clean),
					getResources().getString(R.string.text_sure_to_clean));
			clean_cache_dialog.addButton(R.string.but_clean,
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							clean_cache_dialog.dismiss();
							Toast.makeText(getApplicationContext(),
									R.string.text_cleaning, Toast.LENGTH_SHORT);
							setProgressBarIndeterminateVisibility(true);
							cleanCache();
						}
					});
			clean_cache_dialog.addButton(R.string.but_cancel,
					new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							clean_cache_dialog.dismiss();
						}
					});
			clean_cache_dialog.show();
			break;
		case R.id.notification_but:
			boolean receive = !notification.isChecked();
			notification.setChecked(receive);
			Persistence
					.setReceiveNotification(getApplicationContext(), receive);
			Intent service = new Intent(getApplicationContext(),
					NotificationService.class);
			if (receive) {
				startService(service);
			} else {
				stopService(service);
			}
			break;
		case R.id.time_line_but:
			boolean checked = !timeline_mode.isChecked();
			timeline_mode.setChecked(checked);
			Persistence.setTimeLineMode(getApplicationContext(), checked);
			break;
		case R.id.exit_but:
			final MagicDialog exit_dialog = new MagicDialog(this,
					R.style.magic_dialog);
			exit_dialog.setMessage(getResources()
					.getString(R.string.title_exit),
					getResources().getString(R.string.text_sure_to_logout));
			exit_dialog.addButton(R.string.but_logout, new OnClickListener() {

				@Override
				public void onClick(View v) {
					exit_dialog.dismiss();
					setProgressBarIndeterminateVisibility(true);
					new LogoutAPI(MagicApplication.getInstance()
							.getAccessToken()).logout(new RequestListener() {

						@Override
						public void onIOException(IOException e) {
							Toast.makeText(
									getApplicationContext(),
									getResources().getString(
											R.string.text_login_out_faild),
									Toast.LENGTH_SHORT).show();
							setProgressBarIndeterminateVisibility(false);
						}

						@Override
						public void onError(WeiboException e) {
							Toast.makeText(
									getApplicationContext(),
									getResources().getString(
											R.string.text_login_out_faild),
									Toast.LENGTH_SHORT).show();
							setProgressBarIndeterminateVisibility(false);
						}

						@Override
						public void onComplete4binary(
								ByteArrayOutputStream responseOS) {
						}

						@Override
						public void onComplete(String response) {
							if (!TextUtils.isEmpty(response)) {
								try {
									JSONObject obj = new JSONObject(response);
									String value = obj.getString("result");
									if ("true".equalsIgnoreCase(value)) {
										AccessTokenKeeper
												.clear(getApplicationContext());
										MagicApplication.getInstance()
												.setAccessToken(null);
										cleanPersistenceData();
										
										Toast.makeText(
												getApplicationContext(),
												getResources()
														.getString(
																R.string.text_login_out_success),
												Toast.LENGTH_SHORT).show();

										// stop service
										Intent service = new Intent(
												getApplicationContext(),
												NotificationService.class);
										stopService(service);

										startActivity(new Intent(
												getApplicationContext(),
												MainActivity.class));
										shutDown();
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
							setProgressBarIndeterminateVisibility(false);
						}
					});
				}
			});
			exit_dialog.addButton(R.string.but_cancel, new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					exit_dialog.dismiss();
				}
			});
			exit_dialog.show();
			break;
		}
	}
}
