package magic.yuyong.util;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.BitmapFactory.Options;
import android.graphics.Rect;
import android.media.ExifInterface;

public class PicManager {

	public static Bitmap featBitmap(byte[] data, int width) {
		Bitmap bitmap = null;
		try {
			Options options = new Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeByteArray(data, 0, data.length, options);
			options.inJustDecodeBounds = false;
			options.inSampleSize = 1;
			options.inPreferredConfig = Bitmap.Config.ALPHA_8;
			options.inPurgeable = true;
			options.inInputShareable = true;
			int bitmap_w = options.outWidth;
			while (bitmap_w / (float) width > 2) {
				options.inSampleSize <<= 1;
				bitmap_w >>= 1;
			}
			return BitmapFactory.decodeByteArray(data, 0, data.length, options);
		} catch (Exception e) {
			Debug.e(e.getMessage());
		}
		return bitmap;
	}

	public static Bitmap getRectBitmap(String fileName, int width) {
		Bitmap bitmap = null;
		try {
			Options options = new Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(fileName, options);
			options.inJustDecodeBounds = false;
			options.inSampleSize = 1;
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			options.inPurgeable = true;
			options.inInputShareable = true;
			int bitmap_w = options.outWidth;
			int bitmap_h = options.outHeight;
			int w = bitmap_w > bitmap_h ? bitmap_h : bitmap_w;
			while (w / (float) width > 2) {
				options.inSampleSize <<= 1;
				w >>= 1;
			}
			Debug.v("inSampleSize : " + options.inSampleSize);
			Bitmap temp = BitmapFactory.decodeFile(fileName, options);
			bitmap = Bitmap.createBitmap(w, w, Bitmap.Config.RGB_565);
			Canvas canvas = new Canvas(bitmap);
			int left = (temp.getWidth() - w) / 2;
			int top = (temp.getHeight() - w) / 2;
			Rect src = new Rect(left, top, left + w, top + w);
			Rect dst = new Rect(0, 0, w, w);
			canvas.drawBitmap(temp, src, dst, null);
		} catch (Exception e) {
			Debug.e(e.getMessage());
		}
		return bitmap;
	}

	public static Bitmap featBitmap(String fileName, int width) {
		Bitmap bitmap = null;
		try {
			Options options = new Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(fileName, options);
			options.inJustDecodeBounds = false;
			options.inSampleSize = 1;
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			options.inPurgeable = true;
			options.inInputShareable = true;
			int bitmap_w = options.outWidth;
			while (bitmap_w / (float) width > 2) {
				options.inSampleSize <<= 1;
				bitmap_w >>= 1;
			}
			Debug.v("inSampleSize : " + options.inSampleSize);
			return BitmapFactory.decodeFile(fileName, options);
		} catch (Exception e) {
			Debug.e(e.getMessage());
		}
		return bitmap;
	}

	public static int[] sizeOfBitmap(byte[] data) {
		Options options = new Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, 0, data.length, options);
		return new int[] { options.outWidth, options.outHeight };
	}

	public static int[] sizeOfBitmap(InputStream in) {
		Options options = new Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, options);
		return new int[] { options.outWidth, options.outHeight };
	}
	
	public static int[] sizeOfBitmap(String path){
		InputStream in = null;
		int[] size = new int[]{0, 0};
		try {
			in = new FileInputStream(path);
			size = sizeOfBitmap(in);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}finally{
			if(in != null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return size;
	}

	public static Bitmap prepareBitmap(byte[] data, Bitmap.Config config) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = config;
		options.inPurgeable = true;
		options.inInputShareable = true;
		Bitmap bm = BitmapFactory.decodeStream(new ByteArrayInputStream(data),
				null, options);
		return bm;
	}

	public static Bitmap prepareBitmapForRes(Context context, int id,
			Bitmap.Config config) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = config;
		options.inPurgeable = true;
		options.inInputShareable = true;
		InputStream is = context.getResources().openRawResource(id);
		return BitmapFactory.decodeStream(is, null, options);
	}

	public static int getPictureDegree(String path) {
		int degree = ExifInterface.ORIENTATION_NORMAL;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			degree = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	public static Bitmap rotate(Bitmap bitmap, float degrees) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Bitmap rotateBitmap = bitmap;
		Matrix m = new Matrix();
		m.setRotate(degrees, w / 2f, h / 2f);
		try {
			rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, m, false);
		} catch (OutOfMemoryError ex) {
			System.gc();
		}
		return rotateBitmap;
	}

}
