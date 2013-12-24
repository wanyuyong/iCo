package magic.yuyong.activity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import magic.yuyong.R;
import magic.yuyong.app.AppConstant;
import magic.yuyong.request.RequestState;
import android.os.Handler;
import android.widget.Toast;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;

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
		public void onComplete4binary(ByteArrayOutputStream responseOS) {
		}

		@Override
		public void onIOException(IOException e) {
			android.os.Message msg = new android.os.Message();
			msg.what = AppConstant.MSG_NETWORK_EXCEPTION;
			handler.sendMessage(msg);
		}

		@Override
		public void onError(WeiboException e) {
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
			}

			requestState.isRefresh = false;
			requestState.isRequest = false;
			requestState.response = null;
		}
	};

	protected abstract void onUpdate(RequestState requestState);

	protected abstract void onError(RequestState requestState);
}
