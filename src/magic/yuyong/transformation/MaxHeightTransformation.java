package magic.yuyong.transformation;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

public class MaxHeightTransformation implements Transformation {

	private static final int MAX_HEIGHT = 4096;
	
	@Override
	public String key() {
		return getClass().getSimpleName();
	}

	@Override
	public Bitmap transform(Bitmap source) {
		Bitmap bitmap = source;
		if(source.getHeight() > MAX_HEIGHT){
			bitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), MAX_HEIGHT);
			source.recycle();
		}
		return bitmap;
	}

}
