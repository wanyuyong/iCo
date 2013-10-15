package magic.yuyong.view;

import java.util.ArrayList;
import java.util.List;

import magic.yuyong.R;
import magic.yuyong.util.PicManager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * @author wanyuyong
 *
 */
public class DeskView extends ViewGroup implements OnGestureListener, View.OnClickListener{
	private int desk_width;
	private int desk_height;
	private int desk_num;
	private int current_desk_num;
	private int item_w;
	private int item_h;
	private int downX;
	
	private static int ROW_SPACING;
	private static int COLUMN_SPACING;
	private static final int ITEMS_IN_PER_DESK = 4;
	private static final int COLUMNS = 2;
	private static final int ROWS = (ITEMS_IN_PER_DESK+COLUMNS-1)/COLUMNS;
	public static final float INDICATOR_GAP_SCALE = 1/15f;
	public static final int INDICATOR_GAP = 10;
	private static final float BITMAP_SCALE = .75f;
	private static final int MAX_DURATION = 1000;
	
	private Scroller scroller;
	private GestureDetector mGestureDetector;
	private Paint text_paint;
	private boolean ignorUp;
	private OnDeskChangeListener onDeskChangeListener;
	private OnClickListener onItemClickListener;
	
	private String[] type_names;
	
	public void setOnItemClickListener(OnClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	public OnDeskChangeListener getOnDeskChangeListener() {
		return onDeskChangeListener;
	}

	public int getCurrent_desk_num() {
		return current_desk_num;
	}

	public void setOnDeskChangeListener(OnDeskChangeListener onDeskChangeListener) {
		this.onDeskChangeListener = onDeskChangeListener;
	}

	private List<IcoView> icoViews = new ArrayList<IcoView>();
	
	public static interface OnDeskChangeListener{
		void onDeskChange(int desk_num, int current_desk_num);
	}
	
	public DeskView(Context context) {
		super(context);
		init();
	}

	public DeskView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public DeskView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init(){
		scroller = new Scroller(getContext());
		mGestureDetector = new GestureDetector(this);
		text_paint = new Paint();
		text_paint.setColor(0XFF666666);
		text_paint.setAntiAlias(true);
		ROW_SPACING = getResources().getDisplayMetrics().heightPixels/45;
		COLUMN_SPACING = (int) (ROW_SPACING/3f*2);
		type_names = getResources().getStringArray(R.array.type_name);
	}
	
	public void prepare(){
		int top = 0;
		int left = 0;
		for(int i=0;i<type_names.length;i++){
			int factor = (i%ITEMS_IN_PER_DESK)/COLUMNS;
			top = factor*item_h+(factor+1)*ROW_SPACING;
			left = (i/ITEMS_IN_PER_DESK)*desk_width+(i%ROWS)*item_w+(i%ROWS+1)*COLUMN_SPACING;
			IcoView ico_view = new IcoView(getContext(), (i+1));
			ico_view.rect = new Rect(left, top, left+item_w, top+item_h);
			addView(ico_view);
			ico_view.setOnClickListener(this);
			icoViews.add(ico_view);
		}
		handle();
	}
	
	public int getDeskNum(){
		return (getChildCount()+ITEMS_IN_PER_DESK-1)/ITEMS_IN_PER_DESK;
	}
	
	private void handle(){
		Rect deskViewRect = new Rect(getScrollX(), 0, getScrollX()+desk_width, desk_height);
		int currentTopIndex = current_desk_num*ITEMS_IN_PER_DESK;
		int index = 0;
		for(int i=-ITEMS_IN_PER_DESK;i<ITEMS_IN_PER_DESK<<1;i++){
			index = currentTopIndex + i;
			if(index < 0 || index > icoViews.size()-1){
				continue;
			}
			IcoView ico_view = icoViews.get(index);
			if(ico_view.show_bitmap && !Rect.intersects(deskViewRect, ico_view.rect)){
				ico_view.show_bitmap = false;
				ico_view.handle();
			}
			if(!ico_view.show_bitmap && Rect.intersects(deskViewRect, ico_view.rect)){
				ico_view.show_bitmap = true;
				ico_view.handle();
			}
		}
	}
	
	class IcoView extends View{
		int type_id;
		boolean show_bitmap;
		Bitmap bm;
		Rect rect;
		int bm_w = 0;
		int bm_h = 0;

		public IcoView(Context context, int type_id) {
			super(context);
			this.type_id = type_id;
			setClickable(true);
			setTag(type_id);
		}
		
		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			if(bm != null && !bm.isRecycled()){
				Rect src = new Rect(0, 0, bm_w, bm_h);
				int left = (int) (item_w-bm_w) >> 1;
				int top = (int) (item_h-bm_h) >> 1;
				Rect dst = new Rect(left, top, left+bm_w, top+bm_h);
				canvas.drawBitmap(bm, src, dst, null);
			}
			
			String text = type_names[type_id-1];
			int bottom_gap = (item_h-bm_h) >> 1;
			int paint_size = (int) (item_h*.08);
			paint_size = Math.max(7, paint_size);
			text_paint.setTextSize(paint_size);
			float len = text_paint.measureText(text);
			int x = (int)(item_w-len)>>1;
			int y = item_h-((bottom_gap-paint_size)>>1);
			canvas.drawText(text, x, y, text_paint);
		}
		
		private void handle(){
			if(show_bitmap){
				int ico_id = 0;
				Bitmap temp = PicManager.prepareBitmapForRes(getContext(), ico_id, Bitmap.Config.ALPHA_8);
				if(show_bitmap && temp != null){
					bm_w = (int) (item_w*BITMAP_SCALE);
					bm_h = bm_w*temp.getHeight()/temp.getWidth();
					bm = Bitmap.createScaledBitmap(temp, bm_w, bm_h, true);
					temp.recycle();
					postInvalidate();
				}
			}else{
				if(bm != null){
					bm.recycle();
					bm = null;
				}
				postInvalidate();
			}
		}
	}
	
	@Override
	protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
		int childCount = getChildCount();
		for(int i=0; i<childCount; i++){
			View child = getChildAt(i);
			if(child instanceof IcoView){
				IcoView cpv = (IcoView)child;
				child.layout(cpv.rect.left, cpv.rect.top, cpv.rect.right, cpv.rect.bottom);
			}else if(child instanceof View){
				//other mode...
			}
		}
	}

	@Override
	public void computeScroll() {
		if (scroller.computeScrollOffset()) { 
            scrollTo(scroller.getCurrX(), scroller.getCurrY()); 
            invalidate();
        } 
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		int x = (int)ev.getX();
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downX = x;
			scroller.abortAnimation();
			break;
		case MotionEvent.ACTION_MOVE:
			if(Math.abs(x-downX)>5){
				ev.setAction(MotionEvent.ACTION_DOWN);
				onTouchEvent(ev);
				return true;
			}
			break;
		default:
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}
	
	/**
	 * 回归到正确的位置
	 */
	private void back(){
		int currentX = getScrollX();
		int distance = currentX%desk_width;
		if(distance<desk_width>>1){
			distance = -distance;
		}else{
			distance = desk_width-distance;
		}
		
		scroller.startScroll(currentX, 0, distance, 0, (MAX_DURATION*Math.abs(distance)<<1)/desk_width);
		invalidate();
	}
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		int currentX = getScrollX();
		if((currentX+(desk_width>>1))/desk_width != current_desk_num){
			current_desk_num = (currentX+(desk_width>>1))/desk_width; 
			onDeskChange();
		}
		handle();
	}
	
	private void onDeskChange(){
		if(onDeskChangeListener != null){
			onDeskChangeListener.onDeskChange(desk_num, current_desk_num);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mGestureDetector.onTouchEvent(event);
		if(!ignorUp && event.getAction() == MotionEvent.ACTION_UP){
			back();
		}
		ignorUp = false;
		return true;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		desk_width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		desk_height = (int) (height-INDICATOR_GAP_SCALE*height);
	
		int childCount = getChildCount();
		desk_num = (childCount+ITEMS_IN_PER_DESK-1)/ITEMS_IN_PER_DESK;
		item_w = (desk_width-(COLUMNS+1)*COLUMN_SPACING)/COLUMNS;
		item_h = (desk_height-(ROWS+1)*ROW_SPACING)/ROWS;
		for(int i=0; i<childCount; i++){
			View child = getChildAt(i);
			child.measure(MeasureSpec.makeMeasureSpec(item_w, MeasureSpec.EXACTLY), 
					MeasureSpec.makeMeasureSpec(item_h, MeasureSpec.EXACTLY));
		}
		setMeasuredDimension(desk_width, height);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		prepare();
		onDeskChange();
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		scroller.abortAnimation();
		int currentX = getScrollX();
		int distance = currentX%desk_width;
		
		if(arg2>0){
			distance = -distance;
		}else{
			distance = desk_width-distance;
			if(currentX+distance>desk_width*(desk_num-1)){
				distance = 0;
			}
		}
		scroller.startScroll(currentX, 0, distance, 0, (MAX_DURATION*Math.abs(distance)<<1)/desk_width);
		invalidate();
		ignorUp = true;
		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		int currentX = getScrollX();
		if(currentX+distanceX<0){
			distanceX = 0;
		}else if(currentX+distanceX>(desk_num-1)*desk_width){
			distanceX = (desk_num-1)*desk_width-currentX;
		}
		scrollBy((int)distanceX, 0);
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		return false;
	}

	@Override
	public void onClick(View v) {
		if(null != onItemClickListener){
			onItemClickListener.onClick(v);
		}
	}
	
}
