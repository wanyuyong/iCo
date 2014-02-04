package magic.yuyong.transformation;

import magic.yuyong.util.Blur;
import magic.yuyong.util.BlurTask;
import magic.yuyong.util.Debug;
import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

public class GlassTransformation implements Transformation {

	public static final int DEFAULT_BLUR_RADIUS = BlurTask.DEFAULT_BLUR_RADIUS;
	
	private int radius = DEFAULT_BLUR_RADIUS;
	
	public GlassTransformation() {
		this(DEFAULT_BLUR_RADIUS);
	}

	public GlassTransformation(int radius) {
		this.radius = radius;
	}

	@Override
	public String key() {
		return getClass().getSimpleName();
	}

	@Override
	public Bitmap transform(Bitmap source) {
		Debug.e("source == null ? "+(source == null));
        Bitmap blurred = Blur.apply(source, radius);
        Debug.e("blurred == null ? "+(blurred == null));
        source.recycle();
        return blurred;
	}

}