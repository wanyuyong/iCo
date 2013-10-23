/**
 * 
 */
package magic.yuyong.activity;

import magic.yuyong.R;
import magic.yuyong.adapter.ShowPicAdapter;
import magic.yuyong.view.JazzyViewPager;
import magic.yuyong.view.JazzyViewPager.TransitionEffect;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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

	private ShowPicAdapter adapter;
	private String[] pics;
	private String url;

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

		setContentView(R.layout.show_pics);
		mJazzy = (JazzyViewPager) findViewById(R.id.jazzy_pager);
		mJazzy.setTransitionEffect(TransitionEffect.Tablet);
		mJazzy.setOffscreenPageLimit(1);
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
	            case R.id.refresh:
	                break;
	            case R.id.suitable:
	                break;
	            case R.id.save:
//	                BitmapDrawable bd = (BitmapDrawable) imgview.getDrawable();
//	                final Bitmap bm = bd.getBitmap();
//	                if (bm != null) {
//	                    new Thread(new Runnable() {
//
//	                        @Override
//	                        public void run() {
//	                            ContentResolver cr = getContentResolver();
//	                            MediaStore.Images.Media
//	                                    .insertImage(cr, bm, current, "");
//	                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
//	                                    Uri.parse("file://"
//	                                            + Environment
//	                                            .getExternalStorageDirectory())));
//	                            mHandler.sendEmptyMessage(AppConstant.MSG_SAVE_PIC_SUCCEED);
//	                        }
//	                    }).start();
//	                } else {
//	                    mHandler.sendEmptyMessage(AppConstant.MSG_SAVE_PIC_FAILD);
//	                }
	                break;
	            case R.id.see_original:
	                break;
	        }
	        return true;
	    }

}
