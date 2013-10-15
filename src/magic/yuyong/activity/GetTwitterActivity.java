package magic.yuyong.activity;

import java.io.IOException;

import magic.yuyong.R;
import magic.yuyong.app.AppConstant;
import magic.yuyong.app.MagicApplication;
import magic.yuyong.persistence.AccessTokenKeeper;
import magic.yuyong.request.RequestState;
import magic.yuyong.util.Debug;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.net.RequestListener;

public abstract class GetTwitterActivity extends BaseActivity {

	protected class TwitterRequestListener implements RequestListener {
		private RequestState requestState;

		public TwitterRequestListener(RequestState requestState) {
			super();
			this.requestState = requestState;
		}

		@Override
		public void onComplete(String response) {
			requestState.response = response;
			android.os.Message msg = new android.os.Message();
			msg.what = AppConstant.MSG_UPDATE_VIEW;
			msg.obj = requestState;
			handler.sendMessage(msg);
		}

		@Override
		public void onIOException(IOException e) {

		}

		@Override
		public void onError(WeiboException e) {
			Debug.e("WeiboException : code = " + e.getStatusCode()
					+ " , msg = " + e.getMessage());
			android.os.Message msg = new android.os.Message();
			msg.obj = requestState;
			switch (e.getStatusCode()) {
			case 40111:
				msg.what = AppConstant.MSG_TOKEN_EXPIRED;
				break;
			default:
				msg.what = AppConstant.MSG_NETWORK_EXCEPTION;
				break;
			}
			handler.sendMessage(msg);
		}

	}

	protected Handler handler = new Handler() {

		@Override
		public void handleMessage(android.os.Message msg) {
			RequestState requestState = (RequestState) msg.obj;
			switch (msg.what) {
			case AppConstant.MSG_UPDATE_VIEW:
				onUpdate(requestState);
				break;
			case AppConstant.MSG_NETWORK_EXCEPTION:
				Toast.makeText(
						getApplicationContext(),
						getResources().getString(
								R.string.text_network_exception),
						Toast.LENGTH_SHORT).show();
				onError(requestState);
				break;
			case AppConstant.MSG_TOKEN_EXPIRED:
				MagicApplication.getInstance().setAccessToken(null);
				AccessTokenKeeper.clear(getApplicationContext());
				Toast.makeText(getApplicationContext(),
						getResources().getString(R.string.text_token_expired),
						Toast.LENGTH_SHORT).show();
				Intent mainIntent = new Intent(getApplicationContext(),
						MainActivity.class);
				mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(mainIntent);
				finish();
				break;
			}

			requestState.isRefresh = false;
			requestState.isRequest = false;
			requestState.response = null;
		}
	};

	protected abstract void onUpdate(RequestState requestState);

	protected abstract void onError(RequestState requestState);
}
