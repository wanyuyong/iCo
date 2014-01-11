package magic.yuyong.transformation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;

import com.squareup.picasso.Transformation;

public class CircleTransformation implements Transformation {

	private Paint border_paint;
	private Paint out_border_paint;
	private float border_width;
	private float out_border_width;
	private int border_color;
	private int out_border_color;
	private float diameter;

	private static final float DEFAULT_BORDER_WIDTH = 5f;
	private static final float DEFAULT_OUT_BORDER_WIDTH = 1f;
	private static final int DEFAULT__BORDER_COLOR = Color.WHITE;
	private static final int DEFAULT__OUT_BORDER_COLOR = Color.GRAY;

	public CircleTransformation() {
		border_color = DEFAULT__BORDER_COLOR;
		border_width = DEFAULT_BORDER_WIDTH;
		out_border_color = DEFAULT__OUT_BORDER_COLOR;
		out_border_width = DEFAULT_OUT_BORDER_WIDTH;
		init();
	}

	public CircleTransformation(float border_width, float out_border_width,
			int border_color, int out_border_color) {
		this.border_width = border_width;
		this.out_border_width = out_border_width;
		this.border_color = border_color;
		this.out_border_color = out_border_color;
		init();
	}

	public void init() {
		border_paint = new Paint();
		border_paint.setAntiAlias(true);
		border_paint.setColor(border_color);
		border_paint.setStyle(Style.STROKE);
		border_paint.setStrokeWidth(border_width);

		out_border_paint = new Paint();
		out_border_paint.setAntiAlias(true);
		out_border_paint.setColor(out_border_color);
		out_border_paint.setStyle(Style.STROKE);
		out_border_paint.setStrokeWidth(out_border_width);
	}

	@Override
	public String key() {
		return getClass().getSimpleName();
	}

	@Override
	public Bitmap transform(Bitmap source) {
		int w = source.getWidth();
		int h = source.getHeight();

		int diameter = w < h ? w : h;

		Bitmap bitmap = Bitmap.createBitmap(diameter, diameter,
				Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);

		Paint paint = new Paint();
		paint.setColor(Color.BLUE);
		paint.setDither(true);
		paint.setAntiAlias(true);

		int l = (w - diameter) / 2;
		int t = (h - diameter) / 2;
		Rect rect = new Rect(l, t, l + diameter, t + diameter);
		RectF rectF = new RectF(1, 1, diameter - 1, diameter - 1);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawCircle(rectF.centerX(), rectF.centerY(),
				(diameter - 2) / 2.0f, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(source, rect, rectF, paint);

		canvas.drawCircle(diameter / 2f, diameter / 2f,
				(diameter - border_width) / 2.0f, border_paint);

		canvas.drawCircle(diameter / 2f, diameter / 2f,
				(diameter - out_border_width) / 2.0f, out_border_paint);

		source.recycle();

		return bitmap;
	}

}