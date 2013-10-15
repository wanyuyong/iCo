package magic.yuyong.activity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import magic.yuyong.R;
import magic.yuyong.app.AppConstant;
import magic.yuyong.gif.GifView;
import magic.yuyong.gif.GifView.GifImageType;
import magic.yuyong.util.PicManager;
import magic.yuyong.util.SDCardUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;


public class ShowGif extends BaseActivity {
	private GifView gifView;
	private ProgressBar mProgressBar;

	private String url;
	private int[] size;
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case AppConstant.MSG_DOWN_BEGIN:
				mProgressBar.setVisibility(View.VISIBLE);
				mProgressBar.setProgress(0);
				break;

			case AppConstant.MSG_DOWN_PERCENTAGE:
				 float percent = (Float) msg.obj;
				mProgressBar.setProgress((int) (100 * percent));
				break;
				
			case AppConstant.MSG_DOWN_END:
				mProgressBar.setVisibility(View.GONE);
				break;
				
			case AppConstant.MSG_SHOW_GIF:
				byte[] data = (byte[]) msg.obj;
				int width = getResources().getDisplayMetrics().widthPixels;
				int height = size[1]*width/size[0];
				LayoutParams lp = gifView.getLayoutParams();
				lp.width = width;
				lp.height = height;
				gifView.setGifImage(data);
				
				break;
			}
		}};
		

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		actionBar.setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.show_gif);
		gifView = (GifView) findViewById(R.id.image);
		gifView.setGifImageType(GifImageType.SYNC_DECODER);
		
		mProgressBar = (ProgressBar) findViewById(R.id.progress);

		url = getIntent().getStringExtra("url");

		prepareGif();

	}
	
	@Override
	protected void onDestroy() {
		gifView.destroy();
		super.onDestroy();
	}

	private void prepareGif(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				byte[] data = SDCardUtils.getFile(url);
				if(data == null || data.length == 0){
					mHandler.sendEmptyMessage(AppConstant.MSG_DOWN_BEGIN);
					data = downLoadPic();
					if(data != null && data.length != 0){
						SDCardUtils.saveFile(url, data);
					}
					mHandler.sendEmptyMessage(AppConstant.MSG_DOWN_END);
				}
				if(data != null && data.length != 0){
					size = PicManager.sizeOfBitmap(data);
					Message msg = mHandler.obtainMessage(AppConstant.MSG_SHOW_GIF, data);
					mHandler.sendMessage(msg);
				}
			}
		}).start();
	}
	
	private byte[] downLoadPic() {
		InputStream inputStream = null;
		ByteArrayOutputStream bos = null;
		try {
			URL _url = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) _url.openConnection();
			conn.setConnectTimeout(5 * 1000);
			conn.setReadTimeout(10*1000);
			inputStream = conn.getInputStream();
			float size = conn.getContentLength();
			byte[] buffer = new byte[1024];
			int len = 0;
			int sum = 0;
			bos = new ByteArrayOutputStream();
			while ((len = inputStream.read(buffer)) != -1) {
				bos.write(buffer, 0, len);
				sum += len;
				Message msg = mHandler.obtainMessage(AppConstant.MSG_DOWN_PERCENTAGE, sum / size);
				mHandler.sendMessage(msg);
			}
			bos.flush();
			return bos.toByteArray();
		} catch (IOException e) {

		} finally {
			if (null != inputStream) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		MenuInflater inflater = getMenuInflater();
//		inflater.inflate(R.menu.show_pic, menu);
//		return super.onCreateOptionsMenu(menu);
//	}

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