package magic.yuyong.view;

import magic.yuyong.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;
import magic.yuyong.util.DisplayUtil;

public class LeftSlideView extends ViewGroup {

    private int maxSlideDistance;
    private Scroller scroller;
    public static final int DURATION = 500;
    private GradientDrawable drawable;
    private boolean onLeft = false;
    private int mTouchSlop;
    private int lastY;
    private boolean gesture;

    private Listener l;

    public void setListener(Listener l) {
        this.l = l;
    }

    public static interface Listener {
        void onStateChange(boolean onRight);
    }

    public LeftSlideView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public LeftSlideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LeftSlideView(Context context) {
        super(context);
        init();
    }

    public void setMaxSlideDistance(int distance) {
        maxSlideDistance = distance;
    }

    public void setGesture(boolean gesture) {
        this.gesture = gesture;
    }

    public boolean isOpen() {
        return onLeft;
    }

    private void init() {
        scroller = new Scroller(getContext());
        setBackgroundColor(Color.TRANSPARENT);
        maxSlideDistance = (int) getResources().getDimension(
                R.dimen.right_bar_w);
        int[] colors = new int[]{0X90000000, 0X00000000};
        drawable = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT, colors);
        drawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        final ViewConfiguration configuration = ViewConfiguration
                .get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
    }

    public void slide() {
        if (onLeft) {
            scroller.startScroll(maxSlideDistance, 0, -maxSlideDistance, 0,
                    DURATION);
        } else {
            scroller.startScroll(0, 0, maxSlideDistance, 0, DURATION);
        }
        invalidate();
        onLeft = !onLeft;
        if (null != l) {
            l.onStateChange(onLeft);
        }
    }

    @Override
    public void computeScroll() {
        if (scroller == null) {
            super.computeScroll();
        } else if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), 0);
            invalidate();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        View child = getChildAt(0);
        child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);

        View child = getChildAt(0);
        child.measure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawable.setBounds(getWidth(), 0, getWidth() + (int)DisplayUtil.dpToPx(getResources(), 3), getHeight());
        drawable.draw(canvas);
    }

    VelocityTracker mVelocityTracker;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!gesture) {
            return super.onInterceptTouchEvent(ev);
        }
        int y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                }
                mVelocityTracker.addMovement(ev);
                mVelocityTracker.computeCurrentVelocity(1000);
                float xVelocit = mVelocityTracker.getXVelocity();
                if (Math.abs(lastY - y) < mTouchSlop && xVelocit > 700 && onLeft) {
                    slide();
                    return true;
                } else if (Math.abs(lastY - y) < mTouchSlop && xVelocit < -700 && !onLeft) {
                    slide();
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mVelocityTracker != null) {
                    mVelocityTracker.clear();
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }
}
