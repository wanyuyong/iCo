/**
 * 
 */
package magic.yuyong.activity;

import android.os.Bundle;
import android.util.Log;
import magic.yuyong.R;
import magic.yuyong.adapter.ShowPicAdapter;
import magic.yuyong.util.Debug;
import magic.yuyong.view.JazzyViewPager;
import magic.yuyong.view.JazzyViewPager.TransitionEffect;

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

		setContentView(R.layout.show_pics);
		mJazzy = (JazzyViewPager) findViewById(R.id.jazzy_pager);
		mJazzy.setTransitionEffect(TransitionEffect.Tablet);
		mJazzy.setOffscreenPageLimit(3);
		adapter = new ShowPicAdapter();
		adapter.setmJazzy(mJazzy);

		pics = getIntent().getStringArrayExtra("pics");
		url = getIntent().getStringExtra("url");

		for (int i = 0; i < pics.length; i++) {
			pics[i] = url.substring(0, url.lastIndexOf("/"))
					+ pics[i].substring(pics[i].lastIndexOf("/"));
			Debug.v("pic url : " + pics[i]);
		}

		adapter.setPics(pics);
		mJazzy.setAdapter(adapter);
		mJazzy.setPageMargin(30);

	}
}
