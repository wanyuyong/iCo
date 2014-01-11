package magic.yuyong.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class TwitterImageView extends ImageView {

	public TwitterImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public TwitterImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TwitterImageView(Context context) {
		super(context);
	}

    @Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (getDrawable() != null ) {
			float bw = getDrawable().getIntrinsicWidth();
			float bh = getDrawable().getIntrinsicHeight();
			int w = MeasureSpec.getSize(widthMeasureSpec);
			float scale = w / bw;
			int h = (int) (bh * scale);
			setMeasuredDimension(w, h);
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}

}
