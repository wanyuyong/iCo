package magic.yuyong.util;

import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;

public class DisplayUtil {
	public static float dpToPx(Resources res, float dp) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, res.getDisplayMetrics());
	}
	
	public static float spToPx(Resources res, float sp) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, res.getDisplayMetrics());
	}
	
	public static void setBackgroundKeepPadding(View view, int resid) {
		int bottom = view.getPaddingBottom();
		int top = view.getPaddingTop();
		int right = view.getPaddingRight();
		int left = view.getPaddingLeft();
		view.setBackgroundResource(resid);
		view.setPadding(left, top, right, bottom);
	}
}