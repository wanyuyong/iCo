package magic.yuyong.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

public class SDCardUtils {

	public final static String SDCARD_DIR = Environment
			.getExternalStorageDirectory().getPath() + "/iCoCache/";
	
	public final static String SDCARD_DIR_SAVE = Environment
			.getExternalStorageDirectory().getPath() + "/iCoSave/";
	
	private final static String NOMEID_FILE = SDCARD_DIR + "nomedia";

	public static boolean hasSDCard() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean hasDIR() {
		File file = new File(SDCARD_DIR);
		if (file.exists()) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean hasNoMedia() {
		File file = new File(NOMEID_FILE);
		if (file.exists()) {
			return true;
		} else {
			return false;
		}
	}

	public static void deleteCacheDir() {
		if (hasDIR()) {
			File file = new File(SDCARD_DIR);
			deleteDir(file);
		}
	}
	
	public static void cleanCacheDir(){
		if (hasDIR()) {
			File file = new File(SDCARD_DIR);
			File[] children = file.listFiles();
			for (File child : children) {
				deleteDir(child);
			}
		}
	}

	public static void deleteSamllPic() {
		if (hasDIR()) {
			File file = new File(SDCARD_DIR);
			deleteSamllPic(file);
		}
	}

	private static void deleteSamllPic(File cache) {
		if (cache.isFile()) {
			if (cache.getName().endsWith("wide")
					|| cache.getName().endsWith("narrow")) {
				cache.delete();
			}
		} else {
			File[] children = cache.listFiles();
			for (File child : children) {
				deleteSamllPic(child);
			}
		}
	}

	/**
	 * delete dir
	 * 
	 * @param f
	 */
	public static void deleteDir(File f) {
		if (f.isFile()) {
			f.delete();
		} else if (f.isDirectory()) {
			File[] children = f.listFiles();
			for (File child : children) {
				deleteDir(child);
			}
			f.delete();
		}
	}
	
	private static void createCacheDir() {
		if (!hasDIR()) {
			File file = new File(SDCARD_DIR);
			file.mkdir();
		}
		if (!hasNoMedia()) {
			File file = new File(NOMEID_FILE);
			file.mkdirs();
		}
	}
	
	public static boolean checkFileExits(String url) {
		if (!hasSDCard()) {
			return false;
		}
		File file = new File(createFilePath(url));
		if (file.exists()) {
			return true;
		} else {
			return false;
		}
	}

	public static byte[] getFile(String url) {
		if (!hasSDCard()) {
			return null;
		}
		File file = new File(createFilePath(url));
		if (!file.exists()) {
			return null;
		}
		InputStream inputStream = null;
		byte b[] = null;
		try {
			inputStream = new FileInputStream(file);
			b = new byte[inputStream.available()];
			inputStream.read(b);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != inputStream) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return b;
	}
	
	public static InputStream getInputStream(String url) {
		if (!hasSDCard()) {
			return null;
		}
		File file = new File(createFilePath(url));
		if (!file.exists()) {
			return null;
		}
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return inputStream;
	}

	public static void saveFile(String url, byte[] b) {
		if (!hasSDCard()) {
			return;
		}
		createCacheDir();
		OutputStream outputStream = null;
		File file = new File(createFilePath(url));
		try {
			file.createNewFile();
			outputStream = new FileOutputStream(file);
			outputStream.write(b);
		} catch (Exception e) {
			Debug.e("SDCardUtil : " + e.getMessage());
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
	
	public static void saveBitmap(String url, Bitmap bitmap) {
		String picPath = createFilePath(url);
		saveBitmapByPath(picPath, bitmap);
	}

	public static String createFilePath(String url) {
		String filename = MD5.toMd5(url.getBytes());
		String path = SDCARD_DIR + filename;
		return path;
	}
	
	public static String createTempPath() {
		return SDCARD_DIR + new Date().getTime();
	}
	
	public static Uri getFileURI(String url){
		File file = new File(createFilePath(url));
		if(file.exists()){
			return Uri.fromFile(file);
		}
		return null;
	}

}
