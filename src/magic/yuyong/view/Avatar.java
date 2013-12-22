package magic.yuyong.view;

import magic.yuyong.util.DisplayUtil;
import android.content.Context;
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
import android.util.AttributeSet;

public class Avatar extends AsyncImageView {

	private Paint paint_border;
	private float borderW = 2f;

	public Avatar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public Avatar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public Avatar(Context context) {
		super(context);
		init();
	}

	private void init() {
		borderW = DisplayUtil.dpToPx(getResources(), borderW);
		paint_border = new Paint();
		paint_border.setAntiAlias(true);
		paint_border.setColor(Color.WHITE);
		paint_border.setStyle(Style.STROKE);
		paint_border.setStrokeWidth(borderW);
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		if (bm != null) {
			bm = prepareBitmap(bm);
		}
		super.setImageBitmap(bm);
	}

//	private Bitmap prepareBitmap(Bitmap bitmap) {
//		int w = bitmap.getWidth();
//		float padding = borderW / 2;
//		Bitmap b = Bitmap.createBitmap((int) (w + borderW),
//				(int) (w + borderW), Config.ARGB_8888);
//		Canvas canvas = new Canvas(b);
//		Paint paint = new Paint();
//		paint.setColor(Color.WHITE);
//		paint.setDither(true);
//		paint.setAntiAlias(true);
//		Rect rect = new Rect(0, 0, w, w);
//		RectF rectF = new RectF(padding, padding, padding + w, padding + w);
//		canvas.drawARGB(0, 0, 0, 0);
//		canvas.drawCircle(rectF.centerX(), rectF.centerY(), w / 2.0f, paint);
//		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//		canvas.drawBitmap(bitmap, rect, rectF, paint);
//		canvas.drawCircle(rectF.centerX(), rectF.centerY(), w / 2.0f,
//				paint_border);
//
//		float outBorderW = 1.0f;
//		paint = new Paint();
//		paint.setColor(0XA0999999);
//		paint.setAntiAlias(true);
//		paint.setStyle(Style.STROKE);
//		paint.setStrokeWidth(outBorderW);
//		canvas.drawCircle(rectF.centerX(), rectF.centerY(),
//				(w + borderW - outBorderW) / 2.0f, paint);
//		return b;
//	}
	
	private Bitmap prepareBitmap(Bitmap bitmap) {
		int w = bitmap.getWidth();
		Bitmap b = Bitmap.createBitmap(w, w, Config.ARGB_8888);
		Canvas canvas = new Canvas(b);
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setDither(true);
		paint.setAntiAlias(true);
		Rect rect = new Rect(0, 0, w, w);
		RectF rectF = new RectF(1, 1, w-1, w-1);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawCircle(rectF.centerX(), rectF.centerY(), (w-2)/2.0f, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rectF, paint);
		return b;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		canvas.drawCircle(getWidth()/2, getHeight()/2, (getWidth()-borderW)/2.0f,
				paint_border);

		float outBorderW = 1.0f;
		Paint paint = new Paint();
		paint.setColor(0XA0999999);
		paint.setAntiAlias(true);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(outBorderW);
		canvas.drawCircle(getWidth()/2, getHeight()/2,
				(getWidth() - outBorderW) / 2.0f, paint);
	}

}
