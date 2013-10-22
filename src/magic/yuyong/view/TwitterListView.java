package magic.yuyong.view;

import java.util.ArrayList;
import java.util.List;

import magic.yuyong.R;
import magic.yuyong.persistence.Persistence;
import magic.yuyong.util.DisplayUtil;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.widget.ListView;

public class TwitterListView extends ListView {

	private Paint timeLinePaint;
	private Paint paint_edge;
	private int timeLineWidth = 3;
	private final int PAINT_COLOR = 0X80CCCCCC;
	private final int PAINT_EDGE_COLOR = 0XF0999999;

	public TwitterListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public TwitterListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public TwitterListView(Context context) {
		super(context);
		init();
	}

	private void init() {
		timeLinePaint = new Paint();
		timeLinePaint.setColor(PAINT_COLOR);
		timeLineWidth = (int) DisplayUtil.dpToPx(getResources(), timeLineWidth);
		timeLinePaint.setStrokeWidth(timeLineWidth);
		timeLinePaint.setStyle(Style.STROKE);
		timeLinePaint.setAntiAlias(true);

		paint_edge = new Paint();
		paint_edge.setColor(PAINT_EDGE_COLOR);
		paint_edge.setStrokeWidth(1);
		paint_edge.setStyle(Style.STROKE);
		paint_edge.setAntiAlias(true);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (getAdapter() == null || getAdapter().isEmpty()) {
			return;
		}
		List<Integer> lines = new ArrayList<Integer>();
		lines.add(0);
		int x = -1;
		if (Persistence.getShowTimeLine(getContext())) {
			for (int i = 0; i < getChildCount(); i++) {
				View child = getChildAt(i);
				View tagView = child.findViewById(R.id.user_avatar);
				if(tagView == null){
					continue;
				}
				Rect outRect = getOutRectInList(tagView, this);
				if(x == -1){
					x = outRect.centerX();
				}
				lines.add(outRect.top);
				lines.add(outRect.bottom);
			}
			lines.add(getHeight());
			
			for(int i = 0;i<lines.size()-1;i+=2){
				int lineTop = lines.get(i);
				int lineBottom = lines.get(i+1);
				canvas.drawLine(x, lineTop, x, lineBottom, timeLinePaint);
				canvas.drawLine(x - (timeLineWidth >> 1), lineTop, x
						- (timeLineWidth >> 1), lineBottom, paint_edge);
				canvas.drawLine(x + (timeLineWidth >> 1), lineTop, x
						+ (timeLineWidth >> 1), lineBottom, paint_edge);
			}
		}
	}

	private Rect getOutRectInList(View view, View root) {
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
}
