package magic.yuyong.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class MagicLinear extends LinearLayout {
	private int shadow_h = 4;
	
	private int fromColor = 0XB0333333;
	private int toColor = 0X00FFFFFF;
	
	private GradientDrawable mGradientDrawableTB = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, 
			new int[]{fromColor, toColor});

	public MagicLinear(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MagicLinear(Context context) {
		super(context);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		View headView = getChildAt(0);
		Rect outRect = new Rect();
		headView.getHitRect(outRect);
		mGradientDrawableTB.setBounds(outRect.left, outRect.bottom, outRect.right, outRect.bottom+shadow_h);
		mGradientDrawableTB.draw(canvas);
	}
}
