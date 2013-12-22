package magic.yuyong.view;

import java.util.ArrayList;
import java.util.List;

import magic.yuyong.util.DisplayUtil;
import magic.yuyong.util.StringUtil;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class TileTextView extends View {

	private int textSize = 15;
	private int textColor;
	private int lineGap = 3;
	private int textHeight;

	private int max_line;

	private String text;
	private Paint mPaint;

	private List<Line> lines = new ArrayList<TileTextView.Line>();

	private class Line {

		public Line(int start, int end, int top) {
			super();
			this.start = start;
			this.end = end;
			this.top = top;
		}

		int start;
		int end;
		int top;
	}

	public TileTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public TileTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public TileTextView(Context context) {
		super(context);
		init();
	}

	private void init() {
		textSize = (int) DisplayUtil.spToPx(getResources(), textSize);
		lineGap = (int) DisplayUtil.dpToPx(getResources(), lineGap);
		textColor = Color.WHITE;

		mPaint = new Paint();
		mPaint.setColor(textColor);
		mPaint.setTextSize(textSize);
		mPaint.setAntiAlias(true);

		FontMetrics fm = mPaint.getFontMetrics();
		textHeight = (int) Math.ceil(fm.descent - fm.ascent);
	}

	public void setText(String text) {
		lines.clear();
		this.text = text;
		requestLayout();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		for (Line line : lines) {
			canvas.drawText(text, line.start, line.end, 0, line.top, mPaint);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (!StringUtil.isEmpty(text) && lines.size() == 0) {
			int width = MeasureSpec.getSize(widthMeasureSpec);
			int height = MeasureSpec.getSize(heightMeasureSpec);
			max_line = height / (textHeight + lineGap);
			int lineLen = width / textSize;
			int start = 0;
			int lineNum = 0;
			while (lineNum < max_line) {
				int end = start + lineLen;
				if (end > text.length()) {
					end = text.length();
				}
				int top = (lineNum + 1) * (lineGap + textHeight);
				Line line = new Line(start, end, top);
				lines.add(line);
				if (end == text.length()) {
					break;
				} else {
					start = end;
				}
				lineNum++;
			}
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

}
