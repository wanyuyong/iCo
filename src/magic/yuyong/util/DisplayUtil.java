package magic.yuyong.util;

import android.content.res.Resources;
import android.util.TypedValue;

public class DisplayUtil {
	public static float dpToPx(Resources res, float dp) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, res.getDisplayMetrics());
	}
	
	public static float spToPx(Resources res, float sp) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, res.getDisplayMetrics());
	}
}