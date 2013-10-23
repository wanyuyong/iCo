/**
 * 
 */
package magic.yuyong.adapter;

import magic.yuyong.R;
import magic.yuyong.view.AsyncSubsamplingScaleImageView;
import magic.yuyong.view.HoloCircularProgressBar;
import magic.yuyong.view.JazzyViewPager;
import magic.yuyong.view.OutlineContainer;
import pl.droidsonroids.gif.AsyncGifImageView;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

/**
 * @title:ShowPicAdapter.java
 * @description:
 * @company: 美丽说（北京）网络科技有限公司
 * @author wanyuyong
 * @version
 * @created Oct 21, 2013
 */
public class ShowPicAdapter extends PagerAdapter {

	private JazzyViewPager mJazzy;

	private String[] pics;

	public JazzyViewPager getJazzy() {
		return mJazzy;
	}

	public void setJazzy(JazzyViewPager mJazzy) {
		this.mJazzy = mJazzy;
	}

	public String[] getPics() {
		return pics;
	}

	public void setPics(String[] pics) {
		this.pics = pics;
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		View view = View.inflate(container.getContext(), R.layout.gallery_pic,
				null);
		container.addView(view, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mJazzy.setObjectForPosition(view, position);
		
		AsyncSubsamplingScaleImageView imgview = (AsyncSubsamplingScaleImageView) view.findViewById(R.id.image);
		AsyncGifImageView gifview = (AsyncGifImageView) view.findViewById(R.id.gif_image);
		
		HoloCircularProgressBar mProgressBar = (HoloCircularProgressBar) view
				.findViewById(R.id.progress);

		String url = pics[position];
		
		if(!TextUtils.isEmpty(url)){
			if(url.endsWith(".gif")){
				imgview.setVisibility(View.GONE);
				gifview.setVisibility(View.VISIBLE);
				prepareGif(gifview, mProgressBar, url);
			}else{
				imgview.setVisibility(View.VISIBLE);
				gifview.setVisibility(View.GONE);
				prepareImg(imgview, mProgressBar, url);
			}
		}

		return view;
	}
	
	private void prepareGif(AsyncGifImageView gifview, final HoloCircularProgressBar mProgressBar, String url){
		gifview.setImageLoadingCallback(new AsyncGifImageView.ImageLoadingCallback() {
			
			@Override
			public void onImageRequestStarted() {
				mProgressBar.setVisibility(View.VISIBLE);
				mProgressBar.setProgress(0);
				mProgressBar.setMarkerProgress(1f);
			}
			
			@Override
			public void onImageRequestLoading(float percent) {
				mProgressBar.setProgress(percent);
			}
			
			@Override
			public void onImageRequestFailed() {
				mProgressBar.setVisibility(View.GONE);
			}
			
			@Override
			public void onImageRequestEnded(String path) {
				mProgressBar.setVisibility(View.GONE);
			}
			
			@Override
			public void onImageRequestCancelled() {
				mProgressBar.setVisibility(View.GONE);
			}
		});
		gifview.setUrl(url);
	}
	
	private void prepareImg(AsyncSubsamplingScaleImageView imgview, final HoloCircularProgressBar mProgressBar, String url){
		imgview.setMaxScale(5);
		
		imgview.setImageLoadingCallback(new AsyncSubsamplingScaleImageView.ImageLoadingCallback() {
			
			@Override
			public void onImageRequestStarted() {
				mProgressBar.setVisibility(View.VISIBLE);
				mProgressBar.setProgress(0);
			}
			
			@Override
			public void onImageRequestLoading(float percent) {
				mProgressBar.setProgress(percent);
			}
			
			@Override
			public void onImageRequestFailed() {
				mProgressBar.setVisibility(View.GONE);
			}
			
			@Override
			public void onImageRequestEnded(String path) {
				mProgressBar.setVisibility(View.GONE);
			}
			
			@Override
			public void onImageRequestCancelled() {
				mProgressBar.setVisibility(View.GONE);
			}
		});
		imgview.setUrl(url);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object obj) {
		container.removeView(mJazzy.findViewFromObject(position));
	}

	@Override
	public int getCount() {
		return pics == null ? 0 : pics.length;
	}

	@Override
	public boolean isViewFromObject(View view, Object obj) {
		if (view instanceof OutlineContainer) {
			return ((OutlineContainer) view).getChildAt(0) == obj;
		} else {
			return view == obj;
		}
	}
}
