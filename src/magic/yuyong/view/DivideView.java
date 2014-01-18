package magic.yuyong.view;

import magic.yuyong.util.DisplayUtil;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

public class DivideView extends ViewGroup implements
		GestureDetector.OnGestureListener {
	private View showView, tagView;
	private DivideBoardBottom bottomView;
	private DivideBoardTop topView;
	private int divideX, divideY;
	private GestureDetector mGestureDetector;
    private Paint paint = new Paint();
    private Bitmap bitmap;
    private boolean isOpen;
    private boolean isAnimationing;
    
	private int divideH = 80;
	private final int animation_time = 380;
	private int shadow_h = 5;
	private GradientDrawable mGradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, 
			new int[]{0XFF000000, 0X00000000});

	public DivideView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public DivideView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DivideView(Context context) {
		super(context);
		init();
	}

	private void init() {
		mGestureDetector = new GestureDetector(this);
		divideH = (int) DisplayUtil.dpToPx(getResources(), divideH);
        shadow_h = (int) DisplayUtil.dpToPx(getResources(), shadow_h);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			Rect rect = (Rect) child.getTag();
			child.layout(rect.left, rect.top, rect.right, rect.bottom);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			Rect rect = (Rect) child.getTag();
			child.measure(MeasureSpec.makeMeasureSpec(rect.width(),
					MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
					rect.height(), MeasureSpec.EXACTLY));
		}

		setMeasuredDimension(width, height);
	}
	
	@Override
	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
		boolean flag =  super.drawChild(canvas, child, drawingTime);
		if(showView == child){
			mGradientDrawable.setBounds(0, divideY, getWidth(), divideY+shadow_h);
			mGradientDrawable.draw(canvas);
		}
		return flag;
	}

	public void divide(int x, int y, View tagView, View showView) {
		if(isAnimationing){
			return;
		}
		isAnimationing = true;
		this.showView = showView;
		this.divideX = x;
		this.divideY = y;
		this.tagView = tagView;
        tagView.setDrawingCacheEnabled(true);
        bitmap = tagView.getDrawingCache();

		Rect rect = new Rect(0, divideY, getWidth(), divideY + divideH);
		showView.setTag(rect);
		addView(showView);
		rect = new Rect(0, 0, getWidth(), divideY+shadow_h);
		topView = new DivideBoardTop(getContext(), rect);
		addView(topView);
		rect = new Rect(0, divideY, getWidth(), getHeight());
		bottomView = new DivideBoardBottom(getContext(), rect);
		addView(bottomView);

		Animation animation = new AlphaAnimation(1, .4f);
		animation.setDuration(animation_time);
		animation.setFillAfter(true);
		animation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation arg0) {
				
			}
			
			@Override
			public void onAnimationRepeat(Animation arg0) {
				
			}
			
			@Override
			public void onAnimationEnd(Animation arg0) {
				isAnimationing = false;
			}
		});
		
		topView.startAnimation(animation);
		bottomView.divide(true);
		
		isOpen = true;
	}
	
	public boolean isOpen(){
		return isOpen;
	}

	public void close() {
		if(isAnimationing){
			return;
		}
		isAnimationing = true;
		bottomView.divide(false);
		Animation animation = new AlphaAnimation(.4f, 1f);
		animation.setDuration(animation_time);
		topView.startAnimation(animation);
		postDelayed(new Runnable() {
			
			@Override
			public void run() {
				removeAllViews();
				setVisibility(View.INVISIBLE);
                bitmap = null;
                tagView.setDrawingCacheEnabled(false);
                isAnimationing = false;
			}
		}, animation_time);
		isOpen = false;
	}

	class DivideBoardBottom extends View {
		Scroller scroller;
		GradientDrawable mGradientDrawable;

		public DivideBoardBottom(Context context, Rect rect) {
			super(context);
			setTag(rect);
			scroller = new Scroller(getContext(), new DecelerateInterpolator());
			mGradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, 
					new int[]{0XFF333333, 0X00FFFFFF});
		}

		@Override
		public void computeScroll() {
			if (scroller.computeScrollOffset()) {
				scrollTo(scroller.getCurrX(), scroller.getCurrY());
				invalidate();
			}
		}

		public void divide(boolean down) {
			if (down) {
				scroller.startScroll(0, 0, 0, -divideH, animation_time);
			} else {
				scroller.startScroll(0, -divideH, 0, divideH, animation_time);
			}
			invalidate();
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

            Rect rect = (Rect) getTag();
            Rect src = new Rect(0, rect.top, bitmap.getWidth(), bitmap.getHeight());
            Rect dst = new Rect(0, 0, getWidth(), getHeight());
            canvas.drawBitmap(bitmap, src, dst, null);

            int alpha = (int) (150*(-getScrollY()/(float)divideH));
			int color = 0X00000000;
			color |= (alpha<<24);
            paint.setColor(color);
			canvas.drawRect(dst, paint);

			mGradientDrawable.setBounds(rect.left, -shadow_h, rect.right, 0);
			mGradientDrawable.draw(canvas);
		}
	}

	class DivideBoardTop extends View {
		
		public DivideBoardTop(Context context, Rect rect) {
			super(context);
			setTag(rect);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			Rect rect = (Rect) getTag();
			canvas.save();
			canvas.translate(0, -rect.top);
			Rect clipRect = new Rect(rect);
			clipRect.bottom -= shadow_h;
			canvas.clipRect(clipRect, Op.INTERSECT);
			tagView.draw(canvas);
			canvas.restore();
		}
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
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		close();
		return true;
	}

}
