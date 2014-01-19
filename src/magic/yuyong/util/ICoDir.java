package magic.yuyong.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

public class ICoDir {

	public final static String SDCARD_ICO_DIR = Environment
			.getExternalStorageDirectory() + File.separator + "iCo";

	public static boolean hasSDCard() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	public static void createCacheDir() {
		if (!hasDIR()) {
			File file = new File(SDCARD_ICO_DIR);
			file.mkdir();
		}
	}

	public static boolean hasDIR() {
		File file = new File(SDCARD_ICO_DIR);
		if (file.exists()) {
			return true;
		} else {
			return false;
		}
	}

	private static void nioTransferCopy(File source, File target) {
		FileChannel in = null;
		FileChannel out = null;
		FileInputStream inStream = null;
		FileOutputStream outStream = null;
		try {
			inStream = new FileInputStream(source);
			outStream = new FileOutputStream(target);
			in = inStream.getChannel();
			out = outStream.getChannel();
			in.transferTo(0, in.size(), out);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != inStream) {
				try {
					inStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != outStream) {
				try {
					outStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != out) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static String saveToAlbum(String path, String format, Context context) {
		Debug.e("save Path : " + path);
		File f = new File(path);
		File saveDir = new File(SDCARD_ICO_DIR);
		if (!saveDir.exists()) {
			saveDir.mkdir();
		}
		String name = String.valueOf(new Date().getTime());
		String newPath = SDCARD_ICO_DIR + File.separator + name + "." + format;
		File pic = new File(newPath);
		nioTransferCopy(f, pic);

		Intent mediaScanIntent = new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		Uri contentUri = Uri.fromFile(pic);
		mediaScanIntent.setData(contentUri);
		context.sendBroadcast(mediaScanIntent);

		return newPath;
	}

	public static void saveBitmapByPath(String picPath, Bitmap bitmap) {
		if (!hasSDCard()) {
			return;
		}
		createCacheDir();
		OutputStream outputStream = null;
		File file = new File(picPath);
		try {
			file.createNewFile();
			outputStream = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
			outputStream.flush();
		} catch (Exception e) {
			if (file.exists())
				file.delete();
		} finally {
			if (null != outputStream) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void deleteFile(String path){
		File f = new File(path);
		if(f.exists()){
			f.delete();
		}
	}

}
