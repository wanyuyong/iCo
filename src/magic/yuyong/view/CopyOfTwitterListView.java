package magic.yuyong.view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import magic.yuyong.R;
import magic.yuyong.model.Twitter;
import magic.yuyong.persistence.Persistence;
import magic.yuyong.util.DisplayUtil;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.widget.ListView;
import android.widget.TextView;

public class CopyOfTwitterListView extends ListView {

	private Paint paint;
	private Paint paint_edge;
	private final int PAINT_WIDTH = 3;
	private int paintRealWidth;
	private final int PAINT_COLOR = 0X80CCCCCC;
	private final int PAINT_EDGE_COLOR = 0XF0999999;

	private TextView time_indicator;

	public CopyOfTwitterListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public CopyOfTwitterListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CopyOfTwitterListView(Context context) {
		super(context);
		init();
	}

	private void init() {
		paint = new Paint();
		paint.setColor(PAINT_COLOR);
		paintRealWidth = (int) DisplayUtil.dpToPx(getResources(), PAINT_WIDTH);
		paint.setStrokeWidth(paintRealWidth);
		paint.setStyle(Style.STROKE);
		paint.setAntiAlias(true);

		paint_edge = new Paint();
		paint_edge.setColor(PAINT_EDGE_COLOR);
		paint_edge.setStrokeWidth(1);
		paint_edge.setStyle(Style.STROKE);
		paint_edge.setAntiAlias(true);
	}

	@Override
	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
		Twitter twitter = null;
		boolean draw = true;
		if (null != child.getTag()) {
			twitter = (Twitter) child.getTag();
		}
		if (twitter != null && twitter.getId() == 0 && child.getTop() < 0) {
			draw = false;
		}
		return draw ? super.drawChild(canvas, child, drawingTime) : false;
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (getAdapter() == null || getAdapter().isEmpty()) {
			return;
		}
		// timeline
		if (Persistence.getShowTimeLine(getContext())) {
			int x = -1;
			canvas.save();
			for (int i = 0; i < getChildCount(); i++) {
				View child = getChildAt(i);
				Twitter twitter = null;
				if (null != child.getTag()) {
					twitter = (Twitter) child.getTag();
				}
				if (twitter == null) {
					continue;
				}
				if (twitter.getId() == 0) {
					// Rect outRect = new Rect();
					// child.getHitRect(outRect);
					// canvas.clipRect(outRect, Op.DIFFERENCE);
				} else {
					View tagView = child.findViewById(R.id.user_avatar);
					Rect outRect = getOutRectInList(tagView, this);
					canvas.clipRect(outRect, Op.DIFFERENCE);
					if (x == -1 && twitter.getId() != 0) {
						x = outRect.centerX();
					}
				}
			}
			canvas.drawLine(x, 0, x, getHeight(), paint);
			canvas.drawLine(x - (paintRealWidth >> 1), 0, x
					- (paintRealWidth >> 1), getHeight(), paint_edge);
			canvas.drawLine(x + (paintRealWidth >> 1), 0, x
					+ (paintRealWidth >> 1), getHeight(), paint_edge);
			canvas.restore();
		}

		// timeIndicator
		View top_time_indicator = null;
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			Twitter twitter = null;
			if (null != child.getTag()) {
				twitter = (Twitter) child.getTag();
			}
			if (twitter != null && twitter.getId() == 0) {
				if (time_indicator == null) {
					time_indicator = (TextView) child;
				}
				top_time_indicator = child;
				break;
			}
		}
		int top = 0;
		if (top_time_indicator != null && time_indicator != null) {
			if (top_time_indicator.getTop() <= 0) {
				top = 0;
			} else if (top_time_indicator.getTop() <= time_indicator
					.getHeight()) {
				top -= time_indicator.getHeight() - top_time_indicator.getTop();
			}
		}

		int index = getFirstVisiblePosition();
		String timeStr = null;
		for (int i = index; i >= 0; i--) {
			Twitter item = (Twitter) getAdapter().getItem(i);
			if (item != null && item.getId() == 0) {
				timeStr = parseTimeIndicator(item.getCreated_at());
				break;
			}
		}
		if (timeStr != null) {
			time_indicator.setText(timeStr);
		}

		if (time_indicator != null
				&& !(getFirstVisiblePosition() == 0 && getChildAt(0).getTop() >= 0)) {
			canvas.save();
			canvas.translate(getPaddingLeft(), top);
			canvas.translate(getScrollX(), getScrollY());
			time_indicator.draw(canvas);
			canvas.restore();
		}
	}

	public Rect getOutRectInList(View view, View root) {
		Rect outRect = new Rect();
		view.getHitRect(outRect);
		ViewParent parent = view.getParent();
		while (parent != null && parent != root) {
			Rect temp = new Rect();
			((View) parent).getHitRect(temp);
			outRect.top += temp.top;
			outRect.left += temp.left;
			outRect.right += temp.left;
			outRect.bottom += temp.top;
			parent = parent.getParent();
		}
		return outRect;
	}

	public static String parseTimeIndicator(String time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date = null;
		try {
			date = format.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (date != null) {
			format = new SimpleDateFormat("yyyy-MM-dd HH");
			return format.format(date);
		}
		return null;
	}
}
