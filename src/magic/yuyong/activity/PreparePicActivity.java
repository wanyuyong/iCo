package magic.yuyong.activity;

import java.io.File;
import java.io.FileOutputStream;

import magic.yuyong.R;
import magic.yuyong.util.Debug;
import magic.yuyong.util.PicManager;
import magic.yuyong.util.SDCardUtils;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

public class PreparePicActivity extends BaseActivity {
	private String imgPath;
	private Bitmap bitmap;
	private ImageView img;
	private int screenWidth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		actionBar.setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.prepare_pic);

		img = (ImageView) findViewById(R.id.pic);
		imgPath = getIntent().getStringExtra("path");
		screenWidth = getWindowManager().getDefaultDisplay().getWidth();
		initPic();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.prepare_pic, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.left:
			rotate(-90);
			break;

		case R.id.right:
			rotate(90);
			break;

		case R.id.ok:
			new Thread(new Runnable() {

				@Override
				public void run() {
					String fileTemp = SDCardUtils.SDCARD_DIR + "ico_temp.jpeg";
					File file = new File(fileTemp);
					if (file.exists()) {
						file.delete();
					}
					FileOutputStream fos = null;
					try {
						file.createNewFile();
						fos = new FileOutputStream(file);
						bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
						fos.flush();
						fos.close();
						imgPath = fileTemp;
					} catch (Exception e) {
						Debug.v(e.toString());
					}

					Intent data = new Intent();
					data.putExtra("path", imgPath);
					setResult(RESULT_OK, data);
					finish();
				}
			}).start();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void initPic() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				bitmap = PicManager.featBitmap(imgPath, screenWidth);
				changePic();
			}
		}).start();
	}

	private void changePic() {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				img.setImageBitmap(bitmap);
			}
		});

	}

	private void rotate(final int degrees) {
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				if (degrees != 0 && bitmap != null) {
					Matrix m = new Matrix();
					m.setRotate(degrees, (float) bitmap.getWidth() / 2,
							(float) bitmap.getHeight() / 2);
					try {
						Bitmap temp = Bitmap
								.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
										bitmap.getHeight(), m, false);
						bitmap.recycle();
						bitmap = temp;
						changePic();
					} catch (OutOfMemoryError ex) {
						System.gc();
					}
				}
			}
		};
		new Thread(runnable).start();
	}
}
