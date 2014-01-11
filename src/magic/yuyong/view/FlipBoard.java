//package magic.yuyong.view;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//
//import magic.yuyong.activity.TwitterShowActivity;
//import magic.yuyong.app.AppConstant;
//import magic.yuyong.model.Twitter;
//import magic.yuyong.util.PicManager;
//import magic.yuyong.util.SDCardUtils;
//import magic.yuyong.view.TwitterBoard.OnFlipListener;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Matrix;
//import android.graphics.Paint;
//import android.graphics.Paint.Style;
//import android.graphics.Rect;
//import android.util.AttributeSet;
//import android.view.GestureDetector;
//import android.view.GestureDetector.OnGestureListener;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Scroller;
//
///**
// * @author wanyuyong
// * 
// */
//public class FlipBoard extends ViewGroup implements OnGestureListener {
//
//	private Scroller scroller;
//	private GestureDetector mGestureDetector;
//
//	public static final int TYPE_WIDE = 0;
//	public static final int TYPE_NARROW = 1;
//
//	private static final int ROWS = 3;
//	private static final int FLING_TIME = 2000;
//
//	private final float scale_wide_w = .8f;
//	private final float scale_h = .24f;
//	private final float scale_top = .18f;
//
//	private int tile_h;
//	private int wide_tile_w;
//	private int narrow_tile_w;
//
//	private int gap_tile = 4;
//	private int gap_page = 40;
//	private int gap_top;
//	private int page_width;
//	private int page_height;
//	private int flip_board_width;
//	private int downX;
//
//	private int page;
//	private boolean refresh = false;
//	private List<Twitter> twitters;
//	private List<TileView> recycleBin = new ArrayList<TileView>();
//
//	private OnFlipListener mOnFlipListener;
//
//	private OnClickListener onTileClickListener = new OnClickListener() {
//
//		@Override
//		public void onClick(View v) {
//			TileView tileView = (TileView) v;
//			Intent intent = new Intent(getContext(), TwitterShowActivity.class);
//			intent.putExtra("twitter", tileView.twitter);
//			intent.putExtra("fromCache", true);
//			getContext().startActivity(intent);
//		}
//	};
//
//	class Extra {
//		public Rect rect;
//		public boolean isAttached;
//	}
//
//	public void setOnFlipListener(OnFlipListener mOnFlipListener) {
//		this.mOnFlipListener = mOnFlipListener;
//	}
//
//	public FlipBoard(Context context) {
//		super(context);
//		init();
//	}
//
//	public FlipBoard(Context context, AttributeSet attrs, int defStyle) {
//		super(context, attrs, defStyle);
//		init();
//	}
//
//	public FlipBoard(Context context, AttributeSet attrs) {
//		super(context, attrs);
//		init();
//	}
//
//	private void init() {
//		scroller = new Scroller(getContext());
//		mGestureDetector = new GestureDetector(this);
//	}
//
//	@Override
//	protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
//		int childCount = getChildCount();
//		for (int index = 0; index < childCount; index++) {
//			TileView child = (TileView) getChildAt(index);
//			Extra extra = (Extra) child.twitter.getExtra();
//			Rect rect = extra.rect;
//			child.layout(rect.left, rect.top, rect.right, rect.bottom);
//		}
//	}
//
//	@Override
//	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//		super.onSizeChanged(w, h, oldw, oldh);
//		notifyChildStartScroll();
//	}
//
//	public void setData(List<Twitter> twitters) {
//		this.twitters = twitters;
//		refresh = true;
//		removeAllViews();
//		requestLayout();
//	}
//
//	@Override
//	public void computeScroll() {
//		if (scroller.computeScrollOffset()) {
//			scrollTo(scroller.getCurrX(), scroller.getCurrY());
//			invalidate();
//		}
//	}
//
//	private void notifyChildStartScroll() {
//		this.postDelayed(new Runnable() {
//
//			@Override
//			public void run() {
//				for (int i = 0; i < getChildCount(); i++) {
//					TileView child = (TileView) getChildAt(i);
//					child.autoScroll();
//				}
//			}
//		}, FLING_TIME);
//	}
//
//	private void notifyChildStopScroll() {
//		for (int i = 0; i < getChildCount(); i++) {
//			TileView child = (TileView) getChildAt(i);
//			child.stopScroll();
//		}
//	}
//
//	@Override
//	public boolean onInterceptTouchEvent(MotionEvent ev) {
//		int x = (int) ev.getX();
//		switch (ev.getAction()) {
//		case MotionEvent.ACTION_DOWN:
//			downX = x;
//			scroller.abortAnimation();
//			break;
//		case MotionEvent.ACTION_MOVE:
//			if (Math.abs(x - downX) > 10) {
//				ev.setAction(MotionEvent.ACTION_DOWN);
//				onTouchEvent(ev);
//				return true;
//			}
//			break;
//		default:
//			break;
//		}
//		return super.onInterceptTouchEvent(ev);
//	}
//
//	@Override
//	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
//		super.onScrollChanged(l, t, oldl, oldt);
//		if (mOnFlipListener != null) {
//			mOnFlipListener.onFlip(flip_board_width, getScrollX());
//		}
//		int childCount = getChildCount();
//		Rect viewRect = getFlipViewRect();
//		for (int index = 0; index < childCount; index++) {
//			TileView child = (TileView) getChildAt(index);
//			Extra extra = (Extra) child.twitter.getExtra();
//			if (!Rect.intersects(viewRect, extra.rect)) {
//				recycleBin.add(child);
//			}
//		}
//		for (TileView child : recycleBin) {
//			removeView(child);
//			child.unAttach();
//		}
//		for (Twitter twitter : twitters) {
//			Extra extra = (Extra) twitter.getExtra();
//			if (!extra.isAttached && Rect.intersects(viewRect, extra.rect)) {
//				TileView child = null;
//				if (recycleBin.size() != 0) {
//					child = recycleBin.remove(0);
//					child.scrollTo(0, 0);
//				} else {
//					child = new TileView(getContext());
//				}
//				child.twitter = twitter;
//				child.attach();
//				addView(child);
//			}
//		}
//	}
//
//	private Rect getFlipViewRect() {
//		return new Rect(getScrollX(), 0, getScrollX() + getWidth(), getHeight());
//	}
//
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		if (MotionEvent.ACTION_UP == event.getAction()) {
//			notifyChildStartScroll();
//		}
//		return mGestureDetector.onTouchEvent(event);
//	}
//
//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//		int width = MeasureSpec.getSize(widthMeasureSpec);
//		int height = MeasureSpec.getSize(heightMeasureSpec);
//		if (width != getWidth() || height != getHeight() || refresh) {
//			refresh = false;
//			page = 0;
//			wide_tile_w = (int) (width * scale_wide_w);
//			tile_h = (int) (height * scale_h);
//			narrow_tile_w = (wide_tile_w - gap_tile) >> 1;
//			gap_top = (int) (height * scale_top);
//			page_width = (wide_tile_w << 1) + gap_tile;
//			page_height = tile_h * ROWS + (ROWS - 1) * gap_tile;
//
//			removeAllViewsInLayout();
//
//			Rect rect = new Rect(getScrollX(), 0, getScrollX() + width, height);
//			for (int index = 0; index < twitters.size(); index++) {
//				Twitter twitter = twitters.get(index);
//				Extra extra = new Extra();
//				twitter.setExtra(extra);
//				extra.rect = getChildRect(index);
//				if (Rect.intersects(rect, extra.rect)) {
//					TileView view = new TileView(getContext());
//					view.twitter = twitter;
//					view.attach();
//					ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
//							ViewGroup.LayoutParams.WRAP_CONTENT,
//							ViewGroup.LayoutParams.WRAP_CONTENT);
//					addViewInLayout(view, -1, lp);
//				}
//			}
//			flip_board_width = getPageRight(page) + gap_page;
//		}
//
//		int childCount = getChildCount();
//		for (int index = 0; index < childCount; index++) {
//			TileView child = (TileView) getChildAt(index);
//			Extra extra = (Extra) child.twitter.getExtra();
//			child.measure(MeasureSpec.makeMeasureSpec(extra.rect.width(),
//					MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
//					extra.rect.height(), MeasureSpec.EXACTLY));
//		}
//
//		setMeasuredDimension(width, height);
//	}
//
//	private int getPageLeft(int page) {
//		return getPageRight(page) - page_width;
//	}
//
//	private int getPageRight(int page) {
//		return (page + 1) * (gap_page + page_width);
//	}
//
//	private Rect getChildRect(int index) {
//		Rect rect = new Rect();
//		if (index == 0) {
//			rect.top = gap_top;
//			rect.bottom = rect.top + tile_h;
//			rect.left = gap_page;
//			rect.right = rect.left + getRandomTileWidth();
//		} else {
//			Extra extra = (Extra) twitters.get(index - 1).getExtra();
//			Rect previous = extra.rect;
//			if (getPageRight(page) - previous.right == 0) {
//				if (page_height + gap_top == previous.bottom) {
//					page++;
//					rect.top = gap_top;
//					rect.left = getPageLeft(page);
//					rect.bottom = gap_top + tile_h;
//					rect.right = rect.left + getRandomTileWidth();
//				} else {
//					rect.top = previous.bottom + gap_tile;
//					rect.left = getPageLeft(page);
//					rect.bottom = rect.top + tile_h;
//					rect.right = rect.left + getRandomTileWidth();
//				}
//			} else {
//				rect.top = previous.top;
//				rect.left = previous.right + gap_tile;
//				rect.bottom = previous.bottom;
//				if (getPageRight(page) - previous.right > wide_tile_w) {
//					rect.right = rect.left + getRandomTileWidth();
//				} else {
//					rect.right = rect.left + narrow_tile_w;
//				}
//			}
//		}
//		return rect;
//	}
//
//	private int getRandomTileWidth() {
//		Random random = new Random();
//		return random.nextInt(10) % 2 == 0 ? wide_tile_w : narrow_tile_w;
//	}
//
//	class GetBitmap implements Runnable {
//		TileView tileView;
//		Twitter twitter;
//		Bitmap bitmap;
//
//		public GetBitmap(TileView tileView, Twitter twitter) {
//			super();
//			this.tileView = tileView;
//			this.twitter = twitter;
//			tileView.mGetBitmap = this;
//		}
//
//		@Override
//		public void run() {
//
//			synchronized (FlipBoard.this) {
//				if (tileView == null)
//					return;
//				String url = twitter.getBmiddle_pic();
//				Extra extra = (Extra) twitter.getExtra();
//				Rect rect = extra.rect;
//				if (rect.width() == narrow_tile_w) {
//					url = url + AppConstant.SUFFIX_NARROW;
//				} else if (rect.width() == wide_tile_w) {
//					url = url + AppConstant.SUFFIX_WIDE;
//				}
//				bitmap = PicManager.getBitmapFormFile(url);
//				if (bitmap == null) {
//					int width = rect.width();
//					int height = rect.height();
//					byte[] data = SDCardUtils.getFile(url);
//					if (data == null || data.length == 0) {
//						return;
//					}
//					Bitmap temp = PicManager.featBitmap(data, width);
//
//					if (width / (float) height > temp.getWidth()
//							/ (float) temp.getHeight()) {
//						float scale = width / (float) temp.getWidth();
//						Matrix matrix = new Matrix();
//						float[] values = new float[] { scale, 0, 0, 0, scale,
//								0, 0, 0, 1, };
//						matrix.setValues(values);
//						bitmap = Bitmap
//								.createBitmap(temp, 0, 0, temp.getWidth(),
//										temp.getHeight(), matrix, true);
//					} else {
//						float scale = height / (float) temp.getHeight();
//						Matrix matrix = new Matrix();
//						float[] values = new float[] { scale, 0, 0, 0, scale,
//								0, 0, 0, 1, };
//						matrix.setValues(values);
//						bitmap = Bitmap
//								.createBitmap(temp, 0, 0, temp.getWidth(),
//										temp.getHeight(), matrix, true);
//					}
//					temp.recycle();
//					if (rect.width() == narrow_tile_w) {
//						temp = Bitmap.createBitmap(bitmap, 0, 0, narrow_tile_w,
//								tile_h);
//						bitmap.recycle();
//						bitmap = temp;
//					} else if (rect.width() == wide_tile_w) {
//						if (bitmap.getHeight() > tile_h + 100) {
//							Random random = new Random();
//							int random_h = random.nextInt(101);
//							random_h += (tile_h + 100);
//							random_h = Math.min(bitmap.getHeight(), random_h);
//							temp = Bitmap.createBitmap(bitmap, 0, 0,
//									wide_tile_w, random_h);
//							bitmap.recycle();
//							bitmap = temp;
//						}
//					}
//					SDCardUtils.saveBitmap(url, bitmap);
//				}
//
//				if (tileView != null) {
//					tileView.bitmap = bitmap;
//					tileView.postInvalidate();
//					tileView.mGetBitmap = null;
//				} else {
//					bitmap.recycle();
//				}
//			}
//		}
//	}
//
//	class TileView extends View {
//		Twitter twitter;
//		Bitmap bitmap;
//		Paint paint;
//		Scroller scroller;
//		GetBitmap mGetBitmap;
//		int time = 10 * 1000;
//		int type;
//
//		@Override
//		public void computeScroll() {
//			if (type == TYPE_WIDE) {
//				if (scroller.computeScrollOffset()) {
//					scrollTo(scroller.getCurrX(), scroller.getCurrY());
//					invalidate();
//				}
//			} else {
//				super.computeScroll();
//			}
//		}
//
//		public TileView(Context context) {
//			super(context);
//			paint = new Paint();
//			paint.setColor(Color.WHITE);
//			paint.setAntiAlias(true);
//			paint.setStrokeWidth(5);
//			paint.setStyle(Style.STROKE);
//			setOnClickListener(onTileClickListener);
//		}
//
//		@Override
//		protected void onAttachedToWindow() {
//			super.onAttachedToWindow();
//			prepareBitmap();
//		}
//
//		private void autoScroll() {
//			Extra extra = (Extra) twitter.getExtra();
//			if (type == TYPE_WIDE && scroller.isFinished() && twitter != null
//					&& extra.isAttached && bitmap != null
//					&& !bitmap.isRecycled() && bitmap.getHeight() > getHeight()) {
//				int distanceY = bitmap.getHeight() - getScrollY() - getHeight();
//				float totalDistanceY = bitmap.getHeight() - getHeight();
//				scroller.startScroll(0, getScrollY(), 0, distanceY, (int) (time
//						* distanceY / totalDistanceY));
//				invalidate();
//			}
//		}
//
//		private void stopScroll() {
//			if (type == TYPE_WIDE && !scroller.isFinished()) {
//				scroller.abortAnimation();
//			}
//		}
//
//		private void prepareBitmap() {
//			mGetBitmap = new GetBitmap(this, twitter);
//			new Thread(mGetBitmap).start();
//		}
//
//		public void unAttach() {
//			stopScroll();
//			if (mGetBitmap != null) {
//				mGetBitmap.tileView = null;
//			}
//			if (null != bitmap) {
//				bitmap.recycle();
//			}
//			bitmap = null;
//			if (null != twitter) {
//				Extra extra = (Extra) twitter.getExtra();
//				extra.isAttached = false;
//			}
//			twitter = null;
//		}
//
//		public void attach() {
//			Extra extra = (Extra) twitter.getExtra();
//			extra.isAttached = true;
//			type = extra.rect.width() == wide_tile_w ? TYPE_WIDE : TYPE_NARROW;
//			if (type == TYPE_WIDE) {
//				scroller = new Scroller(getContext());
//			}
//		}
//
//		@Override
//		protected void onDraw(Canvas canvas) {
//			super.onDraw(canvas);
//			canvas.drawColor(0X50339966);
//			if (null != bitmap && !bitmap.isRecycled()) {
//				canvas.drawBitmap(bitmap, 0, 0, null);
//			}
//			int scrollY = getScrollY();
//			canvas.save();
//			canvas.translate(0, scrollY);
//			canvas.drawRect(new Rect(0, 0, getWidth(), getHeight()), paint);
//			canvas.restore();
//		}
//	}
//
//	@Override
//	public boolean onDown(MotionEvent arg0) {
//		notifyChildStopScroll();
//		return true;
//	}
//
//	private int adjustX(float distanceX) {
//		int scrollX = getScrollX();
//		if (scrollX + distanceX < 0) {
//			distanceX = 0 - scrollX;
//		}
//		if (flip_board_width > getWidth()) {
//			if (scrollX + distanceX > flip_board_width - getWidth()) {
//				distanceX = flip_board_width - getWidth() - scrollX;
//			}
//		} else {
//			scrollX = 0;
//		}
//
//		return (int) distanceX;
//	}
//
//	@Override
//	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
//			float velocityY) {
//		scroller.fling(getScrollX(), 0, -(int) velocityX, 0, 0,
//				flip_board_width - getWidth(), 0, 0);
//		invalidate();
//		return true;
//	}
//
//	@Override
//	public void onLongPress(MotionEvent arg0) {
//
//	}
//
//	@Override
//	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
//			float distanceY) {
//		distanceX = adjustX(distanceX);
//		scrollBy((int) distanceX, 0);
//		return true;
//	}
//
//	@Override
//	public void onShowPress(MotionEvent arg0) {
//	}
//
//	@Override
//	public boolean onSingleTapUp(MotionEvent arg0) {
//		return false;
//	}
//
//}
