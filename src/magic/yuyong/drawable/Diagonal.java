package magic.yuyong.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class Diagonal extends Drawable {
	
	Paint paint;

	@Override
	public void draw(Canvas canvas) {
		if(paint == null){
			paint = new Paint();
			paint.setColor(0XFF999999);
		}
		canvas.drawColor(0XFFCCCCCC);
		Rect bounds = getBounds();
		int gap = 5;
		int pointX = 0;
		int pointY = 0;
		while(pointY < (bounds.width() > bounds.height() ? bounds.width()<<1 : bounds.height()<<1)){
			canvas.drawLine(pointX, 0, 0, pointY, paint);
			pointX += gap;
			pointY += gap;
		}
	}

	@Override
	public int getOpacity() {
		return 0;
	}

	@Override
	public void setAlpha(int arg0) {
	}

	@Override
	public void setColorFilter(ColorFilter arg0) {
	}

}
