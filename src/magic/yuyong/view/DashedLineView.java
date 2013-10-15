package magic.yuyong.view;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by wanyuyong on 13-5-30.
 */
public class DashedLineView extends View {
    public DashedLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0XF0CCCCCC);
        paint.setStrokeWidth(4);
        Path path = new Path();
        path.moveTo(0, 0);
        path.lineTo(getWidth(), 0);
        PathEffect effects = new DashPathEffect(new float[]{5, 5, 5, 5}, 1);
        paint.setPathEffect(effects);
        canvas.drawPath(path, paint);
    }
}
