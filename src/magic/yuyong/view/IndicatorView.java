package magic.yuyong.view;

import magic.yuyong.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * DeskView的指示器
 * 
 * @author wanyuyong
 * 
 */
public class IndicatorView extends ViewGroup implements
		DeskView.OnDeskChangeListener {
	private int num;
	private static final int DOT_W = 12;

	public IndicatorView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public IndicatorView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public IndicatorView(Context context) {
		super(context);
	}

	public void init(int num, int currentNum) {
		this.num = num;
		for (int i = 0; i < num; i++) {
			View dot = new View(getContext());
			dot.setBackgroundResource(currentNum == i ? R.drawable.dot_active
					: R.drawable.dot_normal);
			addView(dot);
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int left = (getMeasuredWidth() - (num - 1) * DeskView.INDICATOR_GAP - num
				* DOT_W) / 2;
		int top = (getMeasuredHeight() - DOT_W) / 2;
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			child.layout(left, top, left + child.getMeasuredWidth(), top
					+ child.getMeasuredHeight());
			left += DeskView.INDICATOR_GAP + DOT_W;
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int w = MeasureSpec.makeMeasureSpec(DOT_W, MeasureSpec.EXACTLY);
		int h = MeasureSpec.makeMeasureSpec(DOT_W, MeasureSpec.EXACTLY);
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			child.measure(w, h);
		}
		w = MeasureSpec.getSize(widthMeasureSpec);
		h = MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(w, (int) (h * DeskView.INDICATOR_GAP_SCALE));
	}

	@Override
	public void onDeskChange(int desk_num, int current_desk_num) {
		for (int i = 0; i < getChildCount(); i++) {
			View dot = getChildAt(i);
			dot.setBackgroundResource(current_desk_num == i ? R.drawable.dot_active
					: R.drawable.dot_normal);
		}
		postInvalidate();
	}
}