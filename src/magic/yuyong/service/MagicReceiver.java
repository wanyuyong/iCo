package magic.yuyong.service;

import magic.yuyong.app.MagicApplication;
import magic.yuyong.persistence.Persistence;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class MagicReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent service = new Intent(context, NotificationService.class);
		if (!isNetworkAvailable(context)) {
			context.stopService(service);
		} else{
			if (MagicApplication.getInstance().getAccessToken() != null
					&& MagicApplication.getInstance().getAccessToken()
							.isSessionValid()
					&& Persistence.isReceiveNotification(context)) {
				context.startService(service);
			}
		}
	}

	private static boolean isNetworkAvailable(Context context) {
		ConnectivityManager mgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] info = mgr.getAllNetworkInfo();
		if (info != null) {
			for (int i = 0; i < info.length; i++) {
				if (info[i].getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}
		}
		return false;
	}

}
