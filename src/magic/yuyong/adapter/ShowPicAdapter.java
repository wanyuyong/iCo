/**
 * 
 */
package magic.yuyong.adapter;

import it.sephiroth.android.library.imagezoom.ImageViewTouch.OnImageViewTouchFlingListener;
import it.sephiroth.android.library.imagezoom.ImageViewTouch.OnImageViewTouchScaleListener;
import it.sephiroth.android.library.imagezoom.ImageViewTouch.OnImageViewTouchScrollListener;
import it.sephiroth.android.library.imagezoom.ImageViewTouch.OnImageViewTouchSingleTapListener;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase.DisplayType;
import magic.yuyong.R;
import magic.yuyong.view.AsyncImageViewTouch;
import magic.yuyong.view.HoloCircularProgressBar;
import magic.yuyong.view.JazzyViewPager;
import magic.yuyong.view.OutlineContainer;
import pl.droidsonroids.gif.AsyncGifImageView;
import pl.droidsonroids.gif.AsyncGifImageView.OnSingleTapConfirmedListener;
import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
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
	
	private long lastTouchDate;

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

	public class ViewInfo {
		public int position;
		public String img_path;
		public String img_format;
	}

	private void finishActivity(View view) {
		Context context = view.getContext();
		if (context != null && context instanceof Activity) {
			((Activity) context).finish();
		}
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		View view = View.inflate(container.getContext(), R.layout.gallery_pic,
				null);
		ViewInfo info = new ViewInfo();
		info.position = position;
		view.setTag(info);
		container.addView(view, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		mJazzy.setObjectForPosition(view, position);

		View but_reload = view.findViewById(R.id.but_reload);
		HoloCircularProgressBar mProgressBar = (HoloCircularProgressBar) view
				.findViewById(R.id.progress);
		final AsyncGifImageView gifView = (AsyncGifImageView) view
				.findViewById(R.id.gif_image);
		final AsyncImageViewTouch imgView = (AsyncImageViewTouch) view
				.findViewById(R.id.image);
		imgView.setViewParent(mJazzy);

		final String url = pics[position];

		if (!TextUtils.isEmpty(url)) {
			if (url.endsWith(".gif")) {
				info.img_format = "gif";
				gifView.setVisibility(View.VISIBLE);
				prepareGif(gifView, mProgressBar, but_reload, url, info);
			} else {
				info.img_format = "jpg";
				imgView.setVisibility(View.VISIBLE);
				prepareImg(imgView, mProgressBar, but_reload, url, info);
			}

			but_reload.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (url.endsWith(".gif")) {
						gifView.setUrl(url);
					} else {
						imgView.setUrl(url);
					}
				}
			});
		}

		return view;
	}

	private void prepareGif(final AsyncGifImageView gifview,
			final HoloCircularProgressBar mProgressBar, final View reloadBut,
			String url, final ViewInfo info) {
		gifview.setImageLoadingCallback(new AsyncGifImageView.ImageLoadingCallback() {

			@Override
			public void onImageRequestStarted() {
				if (mProgressBar.getVisibility() != View.VISIBLE) {
					mProgressBar.setVisibility(View.VISIBLE);
				}
				mProgressBar.setProgress(0);
				mProgressBar.setMarkerProgress(1f);
				if (reloadBut.getVisibility() != View.GONE) {
					reloadBut.setVisibility(View.GONE);
				}
			}

			@Override
			public void onImageRequestLoading(float percent) {
				mProgressBar.setProgress(percent);
			}

			@Override
			public void onImageRequestFailed() {
				if (reloadBut.getVisibility() != View.VISIBLE) {
					reloadBut.setVisibility(View.VISIBLE);
				}
				if (mProgressBar.getVisibility() != View.GONE) {
					mProgressBar.setVisibility(View.GONE);
				}
			}

			@Override
			public void onImageRequestEnded(String path) {
				if (mProgressBar.getVisibility() != View.GONE) {
					mProgressBar.setVisibility(View.GONE);
				}
				if (reloadBut.getVisibility() != View.GONE) {
					reloadBut.setVisibility(View.GONE);
				}
				info.img_path = path;
			}

			@Override
			public void onImageRequestCancelled() {
				mProgressBar.setVisibility(View.GONE);
				if (reloadBut.getVisibility() != View.VISIBLE) {
					reloadBut.setVisibility(View.VISIBLE);
				}
			}
		});
		gifview.setOnSingleTapConfirmedListener(new OnSingleTapConfirmedListener() {

			@Override
			public void onSingleTapConfirmed() {
				finishActivity(gifview);
			}
		});
		gifview.setUrl(url);
	}

	private void prepareImg(final AsyncImageViewTouch imgview,
			final HoloCircularProgressBar mProgressBar, final View reloadBut,
			String url, final ViewInfo info) {
		imgview.setDisplayType(DisplayType.FIT_TO_SCREEN);
		imgview.setImageLoadingCallback(new AsyncImageViewTouch.ImageLoadingCallback() {

			@Override
			public void onImageRequestStarted() {
				if (mProgressBar.getVisibility() != View.VISIBLE) {
					mProgressBar.setVisibility(View.VISIBLE);
				}
				mProgressBar.setProgress(0);
				mProgressBar.setMarkerProgress(1f);
				if (reloadBut.getVisibility() != View.GONE) {
					reloadBut.setVisibility(View.GONE);
				}
			}

			@Override
			public void onImageRequestLoading(float percent) {
				mProgressBar.setProgress(percent);
			}

			@Override
			public void onImageRequestFailed() {
				if (reloadBut.getVisibility() != View.VISIBLE) {
					reloadBut.setVisibility(View.VISIBLE);
				}
				if (mProgressBar.getVisibility() != View.GONE) {
					mProgressBar.setVisibility(View.GONE);
				}
			}

			@Override
			public void onImageRequestEnded(String path) {
				if (mProgressBar.getVisibility() != View.GONE) {
					mProgressBar.setVisibility(View.GONE);
				}
				if (reloadBut.getVisibility() != View.GONE) {
					reloadBut.setVisibility(View.GONE);
				}
				info.img_path = path;
			}

			@Override
			public void onImageRequestCancelled() {
				mProgressBar.setVisibility(View.GONE);
				if (reloadBut.getVisibility() != View.VISIBLE) {
					reloadBut.setVisibility(View.VISIBLE);
				}
			}
		});
		imgview.setScrollListener(new OnImageViewTouchScrollListener() {
			
			@Override
			public void onScroll() {
				lastTouchDate = System.currentTimeMillis();
			}
		});
		imgview.setFlingListener(new OnImageViewTouchFlingListener() {
			
			@Override
			public void onFling() {
				lastTouchDate = System.currentTimeMillis();
			}
		});
		imgview.setViewScaleListener(new OnImageViewTouchScaleListener() {
			
			@Override
			public void onScale() {
				lastTouchDate = System.currentTimeMillis();				
			}
		});
		imgview.setSingleTapListener(new OnImageViewTouchSingleTapListener() {

			@Override
			public void onSingleTapConfirmed() {
				if(System.currentTimeMillis() - lastTouchDate > 800){
					finishActivity(imgview);
				}
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
