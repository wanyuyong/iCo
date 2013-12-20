package magic.yuyong.view;

import magic.yuyong.R;
import magic.yuyong.util.DisplayUtil;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Scroller;
import android.widget.TextView;

/**
 * @author wanyuyong
 * 
 */
public class RefreshView extends ViewGroup implements OnGestureListener {
	private View headView;
	private View middleView;
	private View arrowView;
	private TextView textViewTop;
	private ProgressBar progressBarTop;
	private Scroller scroller;
	private GestureDetector mGestureDetector;
	private GradientDrawable shadow_BT;
	private int shadowH = 5;
	private int back_duration_max = 1000;
	private int back_duration_min = 500;
	private int animation_duration = 80;
	private float lastY;
	private int ignore_y = 5;
	private boolean firstDraw = true;
	private static final int NORMAL = 0;
	private static final int WILL_REFRESH = 1;
	private static final int REFRESHING = 2;

	private int state = NORMAL;

	private Listener listener;

	/**
	 * 刷新接口
	 * 
	 */
	public static interface Listener {
		void onRefresh();
	}

	public void setListener(Listener l) {
		this.listener = l;
	}

	public RefreshView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public RefreshView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		Activity activity = (Activity) context;
		headView = activity.getLayoutInflater().inflate(R.layout.refresh_head,
				null);
		addView(headView);
		arrowView = findViewById(R.id.arrow);
		textViewTop = (TextView) findViewById(R.id.text);
		progressBarTop = (ProgressBar) findViewById(R.id.progress_small);
		reSetText();
		scroller = new Scroller(context);
		mGestureDetector = new GestureDetector(this);
		int[] colors = new int[] { 0xF0CCCCCC, 0x00FFFFFF };
		shadow_BT = new GradientDrawable(
				GradientDrawable.Orientation.BOTTOM_TOP, colors);
		shadow_BT.setGradientType(GradientDrawable.LINEAR_GRADIENT);
		shadowH = (int) DisplayUtil.dpToPx(getResources(), shadowH);
	}

	@Override
	public void addView(View child, int index, LayoutParams params) {
		middleView = child;
		super.addView(child, index, params);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int top = 0;
		headView.layout(0, top, headView.getMeasuredWidth(),
				top += headView.getMeasuredHeight());
		middleView.layout(0, top, middleView.getMeasuredWidth(),
				top += middleView.getMeasuredHeight());
		if (firstDraw) {
			scrollTo(0, headView.getHeight());
			firstDraw = false;
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int w = MeasureSpec.getSize(widthMeasureSpec);
		int h = MeasureSpec.getSize(heightMeasureSpec);
		int head_h = MeasureSpec.makeMeasureSpec(200, MeasureSpec.EXACTLY);
		headView.measure(widthMeasureSpec, head_h);
		middleView.measure(widthMeasureSpec,
				MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY));
		setMeasuredDimension(w, h);
	}

	private void reSetText() {
		if (state == NORMAL) {
			textViewTop.setText("下拉可以刷新...");
		} else if (state == WILL_REFRESH) {
			textViewTop.setText("松开即可刷新...");
		}
	}

	private void doAnimation() {
		Animation a = arrowView.getAnimation();
		if (state == NORMAL) {
			a = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
			a.setFillAfter(true);
		} else if (state == WILL_REFRESH) {
			a = new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
		}
		a.setDuration(animation_duration);
		arrowView.startAnimation(a);
	}

	/**
	 * 回弹
	 */
	private void back() {
		int distanceY = getScrollY();
		if (distanceY == headView.getHeight()) {
			return;
		}
		if (state == WILL_REFRESH) {
			state = REFRESHING;
			int dy = (int) (progressBarTop.getTop() - distanceY - DisplayUtil.dpToPx(getResources(), 8));
			int d = (int) (back_duration_max * Math.abs(dy) / (float) headView
					.getHeight());
			scroller.startScroll(0, distanceY, 0, dy, d);
			invalidate();
			textViewTop.setText("加载中...");
			progressBarTop.setVisibility(View.VISIBLE);
			arrowView.clearAnimation();
			arrowView.setVisibility(View.INVISIBLE);
			if(listener != null){
				listener.onRefresh();
			}
		} else if (state == NORMAL) {
			int dy = headView.getHeight() - distanceY;
			scroller.startScroll(0, distanceY, 0, dy, back_duration_min);
			invalidate();
		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		shadow_BT.setBounds(0, headView.getBottom() - shadowH, getWidth(),
				headView.getBottom());
		shadow_BT.draw(canvas);
	}

	/**
	 * 关闭此View
	 */
	public void close() {
		state = NORMAL;
		progressBarTop.setVisibility(View.INVISIBLE);
		arrowView.setVisibility(View.VISIBLE);
		back();
		reSetText();
	}

	@Override
	public void computeScroll() {
		if (scroller.computeScrollOffset()) {
			scrollTo(0, scroller.getCurrY());
			invalidate();
		}
	}

	public void refresh() {
		scrollTo(0, (int) (progressBarTop.getTop() - DisplayUtil.dpToPx(getResources(), 8)));
		state = WILL_REFRESH;
		back();
	}

	private boolean isTop() {
		if (middleView instanceof ListView) {
			ListView view = (ListView) middleView;
			int position = view.getFirstVisiblePosition();
			if (position == 0 && view.getChildCount() != 0) {
				View child = (View) view.getChildAt(0);
				Rect outRect = new Rect();
				child.getHitRect(outRect);
				if (outRect.top == view.getPaddingTop()) {
					return true;
				}
			}
			return false;
		}
		return middleView.getScrollY() == 0;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (state == REFRESHING)
			return false;
		float y = ev.getY();
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			lastY = y;
			break;
		case MotionEvent.ACTION_MOVE:
			if (y > lastY + ignore_y && isTop()) {
				ev.setAction(MotionEvent.ACTION_DOWN);
				onTouchEvent(ev);
				((ViewGroup) getParent())
						.requestDisallowInterceptTouchEvent(true);
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP
				|| event.getAction() == MotionEvent.ACTION_CANCEL) {
			back();
			return true;
		}
		return mGestureDetector.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return true;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		float ratio = getScrollY() / (float) headView.getHeight();
		ratio *= 0.7;
		distanceY = distanceY * ratio;

		if (getScrollY() + distanceY >= headView.getHeight()) {
			distanceY = headView.getHeight() - getScrollY();
		}

		scrollBy(0, (int) distanceY);
		int top = (int) (progressBarTop.getTop() - DisplayUtil.dpToPx(getResources(), 8));
		if (getScrollY() > top && state == WILL_REFRESH) {
			doAnimation();
			state = NORMAL;
			reSetText();
		} else if (getScrollY() < top && state == NORMAL) {
			doAnimation();
			state = WILL_REFRESH;
			reSetText();
		}
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

}
