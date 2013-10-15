/*
 * Copyright (C) 2010 Cyril Mottier (http://www.cyrilmottier.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package magic.yuyong.image;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import magic.yuyong.util.Debug;
import magic.yuyong.util.GDUtils;
import magic.yuyong.util.PicManager;
import magic.yuyong.util.SDCardUtils;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;
import android.util.DisplayMetrics;

/**
 * An ImageLoader asynchronously loads image from a given url. Client may be
 * notified from the current image loading state using the
 * {@link ImageLoaderCallback}.
 * <p>
 * <em><strong>Note: </strong>You normally don't need to use the {@link ImageLoader}
 * class directly in your application. You'll generally prefer using an
 * {@link ImageRequest} that takes care of the entire loading process.</em>
 * </p>
 * 
 * @author Cyril Mottier
 */
public class ImageLoader {

	private static final String LOG_TAG = ImageLoader.class.getSimpleName();

	/**
	 * @author Cyril Mottier
	 */
	public static interface ImageLoaderCallback {

		void onImageLoadingStarted(ImageLoader loader);

		void onImageLoadingEnded(ImageLoader loader, Bitmap bitmap);

		void onImageLoadingFailed(ImageLoader loader, Throwable exception);

		void onImageLoading(ImageLoader loader, float percent);
	}

	private static final int ON_START = 0x100;
	private static final int ON_FAIL = 0x101;
	private static final int ON_END = 0x102;
	private static final int ON_LOADING = 0x103;

	private static ImageCache sImageCache;
	private static ExecutorService sExecutor;
	private static BitmapFactory.Options sDefaultOptions;
	private static AssetManager sAssetManager;

	public ImageLoader(Context context) {
		if (sImageCache == null) {
			sImageCache = GDUtils.getImageCache(context);
		}
		if (sExecutor == null) {
			sExecutor = GDUtils.getExecutor(context);
		}
		if (sDefaultOptions == null) {
			sDefaultOptions = new BitmapFactory.Options();
			sDefaultOptions.inDither = true;
			sDefaultOptions.inScaled = true;
			sDefaultOptions.inDensity = DisplayMetrics.DENSITY_MEDIUM;
			sDefaultOptions.inTargetDensity = context.getResources()
					.getDisplayMetrics().densityDpi;
		}
		sAssetManager = context.getAssets();
	}

	public Future<?> loadImage(String url, ImageLoaderCallback callback) {
		return loadImage(url, callback, null);
	}

	public Future<?> loadImage(String url, ImageLoaderCallback callback,
			ImageProcessor bitmapProcessor) {
		return loadImage(url, callback, bitmapProcessor, null);
	}

	public Future<?> loadImage(String url, ImageLoaderCallback callback,
			ImageProcessor bitmapProcessor, BitmapFactory.Options options) {
		return sExecutor.submit(new ImageFetcher(url, callback,
				bitmapProcessor, options));
	}

	private class ImageFetcher implements Runnable {

		private String mUrl;
		private ImageHandler mHandler;
		private ImageProcessor mBitmapProcessor;
		private BitmapFactory.Options mOptions;

		public ImageFetcher(String url, ImageLoaderCallback callback,
				ImageProcessor bitmapProcessor, BitmapFactory.Options options) {
			mUrl = url;
			mHandler = new ImageHandler(url, callback);
			mBitmapProcessor = bitmapProcessor;
			mOptions = options;
		}

		public void run() {

			Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

			final Handler h = mHandler;
			Bitmap bitmap = null;
			Throwable throwable = null;

			h.sendMessage(Message.obtain(h, ON_START));

			try {

				if (TextUtils.isEmpty(mUrl)) {
					throw new Exception("The given URL cannot be null or empty");
				}

				if (mUrl.startsWith("file:///android_asset/")) {
					InputStream inputStream = sAssetManager.open(mUrl
							.replaceFirst("file:///android_asset/", ""));
					bitmap = BitmapFactory.decodeStream(inputStream, null,
							(mOptions == null) ? sDefaultOptions : mOptions);
				} else {
					byte[] data = SDCardUtils.getFile(mUrl);
					if (data == null || data.length == 0) {
						data = downLoadPic(mUrl, h);
					}
					if (data != null && data.length != 0) {
						bitmap = BitmapFactory
								.decodeByteArray(data, 0, data.length,
										(mOptions == null) ? sDefaultOptions
												: mOptions);
						SDCardUtils.saveFile(mUrl, data);
						data = null;
					}
				}

				if (mBitmapProcessor != null && bitmap != null) {
					final Bitmap processedBitmap = mBitmapProcessor
							.processImage(bitmap);
					if (processedBitmap != null) {
						bitmap = processedBitmap;
					}
				}

			} catch (Exception e) {
				// An error occured while retrieving the image
				throwable = e;
			}

			if (bitmap == null) {
				if (throwable == null) {
					// Skia returned a null bitmap ... that's usually because
					// the given url wasn't pointing to a valid image
					throwable = new Exception("Skia image decoding failed");
				}
				h.sendMessage(Message.obtain(h, ON_FAIL, throwable));
			} else {
				h.sendMessage(Message.obtain(h, ON_END, bitmap));
			}
		}
	}

	private byte[] downLoadPic(String mUrl, Handler h) {

		InputStream inputStream = null;
		ByteArrayOutputStream bos = null;
		try {
			URL url = new URL(mUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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
				Message msg = Message.obtain(h, ON_LOADING);
				msg.obj = sum / size;
				h.sendMessage(msg);
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

	private class ImageHandler extends Handler {

		private String mUrl;
		private ImageLoaderCallback mCallback;

		private ImageHandler(String url, ImageLoaderCallback callback) {
			mUrl = url;
			mCallback = callback;
		}

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {

			case ON_START:
				if (mCallback != null) {
					mCallback.onImageLoadingStarted(ImageLoader.this);
				}
				break;

			case ON_FAIL:
				if (mCallback != null) {
					mCallback.onImageLoadingFailed(ImageLoader.this,
							(Throwable) msg.obj);
				}
				break;

			case ON_END:

				final Bitmap bitmap = (Bitmap) msg.obj;
				sImageCache.put(mUrl, bitmap);

				if (mCallback != null) {
					mCallback.onImageLoadingEnded(ImageLoader.this, bitmap);
				}
				break;

			case ON_LOADING:
				if (mCallback != null) {
					mCallback.onImageLoading(ImageLoader.this, (Float) msg.obj);
				}
				break;

			default:
				super.handleMessage(msg);
				break;
			}
		};
	}

}
