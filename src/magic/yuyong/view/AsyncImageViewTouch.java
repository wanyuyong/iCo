/**
 * 
 */
package magic.yuyong.view;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Future;

import magic.yuyong.util.Cache;
import magic.yuyong.util.DecodeUtils;
import magic.yuyong.util.MagicExecutorService;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewParent;

/**
 * @description:
 * @company: 美丽说（北京）网络科技有限公司
 * @author wanyuyong
 * @version
 * @created Oct 22, 2013
 */
public class AsyncImageViewTouch extends ImageViewTouch {

	public static interface ImageLoadingCallback {

		void onImageRequestStarted();

		void onImageRequestFailed();

		void onImageRequestEnded(String path);

		void onImageRequestCancelled();

		void onImageRequestLoading(float percent);
	}

	private class Tag {
		Bitmap bitmap;
		String path;
	}

	private static final int ON_START = 0x100;
	private static final int ON_FAIL = 0x101;
	private static final int ON_END = 0x102;
	private static final int ON_LOADING = 0x103;

	private String mUrl;

	private Future<?> mFuture;

	private ImageLoadingCallback mCallBack;

	public ImageLoadingCallback getImageLoadingCallback() {
		return mCallBack;
	}

	public void setImageLoadingCallback(ImageLoadingCallback mCallBack) {
		this.mCallBack = mCallBack;
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {

			case ON_START:
				if (mCallBack != null) {
					mCallBack.onImageRequestStarted();
				}
				break;

			case ON_FAIL:
				if (mCallBack != null) {
					mCallBack.onImageRequestFailed();
				}
				break;

			case ON_END:
				Tag tag = (Tag) msg.obj;
				setImageBitmap(tag.bitmap, new Matrix(), ZOOM_INVALID,
						ZOOM_INVALID);
				if (mCallBack != null) {
					mCallBack.onImageRequestEnded(tag.path);
				}
				break;

			case ON_LOADING:
				float percent = (Float) msg.obj;
				if (mCallBack != null) {
					mCallBack.onImageRequestLoading(percent);
				}
				break;

			default:
				super.handleMessage(msg);
				break;
			}
		}
	};

	private float lastX;
	private ViewParent viewParent;

	public void setViewParent(ViewParent viewParent) {
		this.viewParent = viewParent;
	}
	
	public boolean onTouchEvent(android.view.MotionEvent event) {
		float x = event.getX();
		boolean toRight = x > lastX;
		lastX = x;
		RectF rect = getBitmapRect();
		if(rect == null){
			return super.onTouchEvent(event);
		}
		boolean needParentIntercept = false;
		if (!toRight && rect.right <= getWidth() + 1) {
			needParentIntercept = true;
		}
		if (toRight && rect.left >= -1) {
			needParentIntercept = true;
		}
		if (event.getPointerCount() > 1) {
			needParentIntercept = false;
		}
		if (needParentIntercept) {
			if (viewParent != null) {
				viewParent.requestDisallowInterceptTouchEvent(false);
			}
		} else {
			if (viewParent != null) {
				viewParent.requestDisallowInterceptTouchEvent(true);
			}
		}
		boolean flag = super.onTouchEvent(event);
		return flag;
	};

	public AsyncImageViewTouch(Context context, AttributeSet attr) {
		super(context, attr);
	}

	public AsyncImageViewTouch(Context context) {
		super(context);
	}

	public void cancel() {
		if (mFuture != null && !mFuture.isCancelled()) {
			mFuture.cancel(false);
			if (mCallBack != null) {
				mCallBack.onImageRequestCancelled();
			}
		}
		mUrl = null;
	}

	public void setUrl(String url) {

		if (url != null && url.equals(mUrl)) {
			return;
		}

		cancel();

		mUrl = url;

		if (!TextUtils.isEmpty(mUrl)) {
			mFuture = MagicExecutorService.getExecutor().submit(
					new ImageFetcher());
		}
	}

	private class ImageFetcher implements Runnable {

		public void run() {
			Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
			String path = "";
			mHandler.sendMessage(Message.obtain(mHandler, ON_START));
			String cacheFilePath = Cache.getPicassoCacheFilePath(mUrl,
					getContext());
			File cacheFile = new File(cacheFilePath);
			if (cacheFile.exists()) {
				path = cacheFilePath;
			} else if (!TextUtils.isEmpty(mUrl)) {
				path = downLoadPic(mUrl, mHandler);
			}
			if (TextUtils.isEmpty(path)) {
				mHandler.sendMessage(Message.obtain(mHandler, ON_FAIL));
				mUrl = null;
			} else {
				Bitmap bitmap = DecodeUtils.decode(getContext(),
						Uri.fromFile(new File(path)), 4096, 4096);
				Tag tag = new Tag();
				tag.bitmap = bitmap;
				tag.path = path;
				mHandler.sendMessage(Message.obtain(mHandler, ON_END, tag));
			}
		}
	}

	private String downLoadPic(String mUrl, Handler h) {
		InputStream inputStream = null;
		HttpURLConnection conn = null;
		try {
			URL url = new URL(mUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5 * 1000);
			conn.setReadTimeout(10 * 1000);
			inputStream = conn.getInputStream();
			float size = conn.getContentLength();
			byte[] buffer = new byte[1024];
			int len = 0;
			int sum = 0;
			while ((len = inputStream.read(buffer)) != -1) {
				sum += len;
				Message msg = Message.obtain(h, ON_LOADING);
				msg.obj = sum / size;
				h.sendMessage(msg);
			}
			String path = Cache.getPicassoCacheFilePath(mUrl, getContext());
			return path;
		} catch (IOException e) {

		} finally {
			if (null != inputStream) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != conn) {
				conn.disconnect();
			}
		}
		return null;
	}

}
