package magic.yuyong.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import magic.yuyong.R;
import magic.yuyong.activity.ShowFriendsActivity;
import magic.yuyong.activity.TimeLineModeActivity;
import magic.yuyong.app.AppConstant;
import magic.yuyong.app.MagicApplication;
import magic.yuyong.extend.UnReadAPI;
import magic.yuyong.persistence.Persistence;
import magic.yuyong.util.Debug;
import magic.yuyong.util.JsonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class NotificationService extends Service implements RequestListener {

	private static final int ID_AT = 1;
	private static final int ID_COMMENT = 2;
	private static final int ID_FRIENDS = 3;
	
	private Timer timer;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		getUnReadCount();
		Debug.v("Service is start.....");
	}
	
	@Override
	public void onDestroy() {
		if(timer != null){
			timer.cancel();
		}
		Debug.v("Service is stop.....");
		super.onDestroy();
	}

	private void getUnReadCount() {
		timer = new Timer();
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				UnReadAPI unReadAPI = new UnReadAPI(MagicApplication
						.getInstance().getAccessToken());
				unReadAPI.unRead(NotificationService.this);
			}
		};
		timer.schedule(task, 0, AppConstant.GET_UNREAD_PERIOD);
	}

	@Override
	public void onComplete(String response) {
		Debug.v("Service response....."+response);
		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject(response);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (jsonObj != null) {
			int follower = JsonUtil.getInt(jsonObj, "follower");
			int cmt = JsonUtil.getInt(jsonObj, "cmt");
			int mention_status = JsonUtil.getInt(jsonObj, "mention_status");
			int mention_cmt = JsonUtil.getInt(jsonObj, "mention_cmt");
			
			if (follower == Persistence.getFollower(getApplicationContext())
					&& cmt == Persistence.getCmt(getApplicationContext())
					&& mention_status == Persistence
							.getMention_status(getApplicationContext())
					&& mention_cmt == Persistence
							.getMention_cmt(getApplicationContext())) {
				return;
			}

			if (follower != 0 && follower != Persistence.getFollower(getApplicationContext())) {
				String notificationStr = follower
						+ getResources().getString(R.string.text_new_follower);
				sendNotification(notificationStr, ID_FRIENDS);
			}
			if (mention_status != 0 && mention_status != Persistence.getMention_status(getApplicationContext())) {
				String notificationStr = mention_status
						+ getResources().getString(R.string.text_new_mention_status);
				sendNotification(notificationStr, ID_AT);
			}
			if (cmt != 0 && cmt != Persistence.getCmt(getApplicationContext())) {
				String notificationStr = cmt
						+ getResources().getString(R.string.text_new_comment);
				sendNotification(notificationStr, ID_COMMENT);
			}

			Persistence.setFollower(getApplicationContext(), follower);
			Persistence.setCmt(getApplicationContext(), cmt);
			Persistence.setMention_status(getApplicationContext(),
					mention_status);
			Persistence.setMention_cmt(getApplicationContext(), mention_cmt);

			sendBroadcast(new Intent(
					AppConstant.ACTION_UNREAD_STATE_CHANGE_BROADCAST));
		}
	}

	private void sendNotification(String notificationStr, int id) {
		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(id);
		Notification n = new Notification(R.drawable.notification_icon,
				"Notification From i Co.", System.currentTimeMillis());
		n.flags = Notification.FLAG_AUTO_CANCEL;
		n.defaults = Notification.DEFAULT_SOUND;
		Intent i = new Intent();
		switch (id) {
		case ID_AT:
			i.setClass(getApplicationContext(), TimeLineModeActivity.class);
			i.putExtra("pos", TimeLineModeActivity.VIEW_AT_ME);
			break;
		case ID_COMMENT:
			i.setClass(getApplicationContext(), TimeLineModeActivity.class);
			i.putExtra("pos", TimeLineModeActivity.VIEW_COMMENT);
			break;
		case ID_FRIENDS:
			i.setClass(getApplicationContext(), ShowFriendsActivity.class);
			i.putExtra("uid", Persistence.getUID(getApplicationContext()));
			i.putExtra("pos", ShowFriendsActivity.VIEW_FOLLOWER);
			break;
		}
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent contentIntent = PendingIntent.getActivity(
				getApplicationContext(), id, i,
				PendingIntent.FLAG_UPDATE_CURRENT);

		n.setLatestEventInfo(getApplicationContext(),
				getResources().getText(R.string.title_notification),
				notificationStr, contentIntent);
		nm.notify(id, n);
	}

	@Override
	public void onComplete4binary(ByteArrayOutputStream responseOS) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onIOException(IOException e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(WeiboException e) {
		// TODO Auto-generated method stub
		
	}

}
