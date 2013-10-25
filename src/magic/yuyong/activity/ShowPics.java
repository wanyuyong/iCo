/**
 * 
 */
package magic.yuyong.activity;

import java.io.File;
import java.util.Date;

import magic.yuyong.R;
import magic.yuyong.adapter.ShowPicAdapter;
import magic.yuyong.adapter.ShowPicAdapter.ViewInfo;
import magic.yuyong.app.AppConstant;
import magic.yuyong.util.SDCardUtils;
import magic.yuyong.view.JazzyViewPager;
import magic.yuyong.view.JazzyViewPager.TransitionEffect;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @title:ShowPics.java
 * @description:
 * @company: 美丽说（北京）网络科技有限公司
 * @author wanyuyong
 * @version
 * @created Oct 21, 2013
 */
public class ShowPics extends BaseActivity {

	private JazzyViewPager mJazzy;
	
	private TextView pageIndicator;

	private ShowPicAdapter adapter;
	private String[] pics;
	private String url;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case AppConstant.MSG_SAVE_PIC_SUCCEED:
				String path = (String) msg.obj;
				Toast.makeText(
						getApplicationContext(),
						getResources()
								.getString(R.string.text_pic_save_success)
								+ " : " + path, Toast.LENGTH_SHORT).show();
				break;

			case AppConstant.MSG_SAVE_PIC_FAILD:
				Toast.makeText(getApplicationContext(),
						R.string.text_pic_save_faild, Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see magic.yuyong.activity.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setCustomView(R.layout.page_indicator);
		
		pageIndicator = (TextView) actionBar.getCustomView().findViewById(R.id.page_indicator);

		setContentView(R.layout.show_pics);
		mJazzy = (JazzyViewPager) findViewById(R.id.jazzy_pager);
		mJazzy.setTransitionEffect(TransitionEffect.Tablet);
		mJazzy.setOffscreenPageLimit(2);
		adapter = new ShowPicAdapter();
		adapter.setJazzy(mJazzy);

		pics = getIntent().getStringArrayExtra("pics");
		url = getIntent().getStringExtra("originalUrl");

		for (int i = 0; i < pics.length; i++) {
			pics[i] = url.substring(0, url.lastIndexOf("/"))
					+ pics[i].substring(pics[i].lastIndexOf("/"));
		}

		adapter.setPics(pics);
		mJazzy.setAdapter(adapter);
		mJazzy.setPageMargin(30);
		pageIndicator.setText(1+"/"+adapter.getCount());

		mJazzy.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int pos) {
				pageIndicator.setText((pos+1)+"/"+adapter.getCount());
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.show_pic, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.save:
			int currentIndex = mJazzy.getCurrentItem();
			ViewInfo info = null;
			for (int i = 0; i < mJazzy.getChildCount(); i++) {
				View child = mJazzy.getChildAt(i);
				ViewInfo temp = (ViewInfo) child.getTag();
				if (temp.position == currentIndex) {
					info = temp;
				}
			}

			if (!TextUtils.isEmpty(info.img_path)) {
				final String path = info.img_path;
				final String format = info.img_format;
				new Thread(new Runnable() {

					@Override
					public void run() {
						saveToAlbum(path, format);
					}
				}).start();
			} else {
				mHandler.sendEmptyMessage(AppConstant.MSG_SAVE_PIC_FAILD);
			}
			break;
		}
		return true;
	}

	private void saveToAlbum(String path, String format) {
		File f = new File(path);
		File saveDir = new File(SDCardUtils.SDCARD_DIR_SAVE);
		if (!saveDir.exists()) {
			saveDir.mkdir();
		}
		String name = String.valueOf(new Date().getTime());
		String newPath = SDCardUtils.SDCARD_DIR_SAVE + name + "." + format;
		File pic = new File(newPath);

		f.renameTo(pic);

		Intent mediaScanIntent = new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		Uri contentUri = Uri.fromFile(pic);
		mediaScanIntent.setData(contentUri);
		sendBroadcast(mediaScanIntent);

		mHandler.sendMessage(mHandler.obtainMessage(
				AppConstant.MSG_SAVE_PIC_SUCCEED, newPath));
	}

}
