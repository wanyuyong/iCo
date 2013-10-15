package magic.yuyong.view;

import magic.yuyong.R;
import magic.yuyong.util.PicManager;
import magic.yuyong.view.TwitterBoard.OnFlipListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class FlipBoardBg extends View implements OnFlipListener {

	private Bitmap bitmap;
	private int bg_width;
	public static final int DEFAULT_BG_ID = R.drawable.windowsbg;

	private int bg_id = -1;

	public FlipBoardBg(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public FlipBoardBg(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FlipBoardBg(Context context) {
		super(context);
	}

	private void prepare() {
		bg_id = bg_id == -1 ? DEFAULT_BG_ID : bg_id;
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
		}
		bitmap = PicManager.prepareBitmapForRes(getContext(), bg_id,
				Bitmap.Config.ALPHA_8);
		float scale = getHeight() / (float) bitmap.getHeight();
		bg_width = (int) (scale * bitmap.getWidth());
	}

	public void setBg(int id) {
		bg_id = id;
		requestLayout();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		prepare();
	}

	@Override
	public void onFlip(int flip_board_width, int flip) {
		int _scrollX = flip * (bg_width - getWidth()) / flip_board_width;
		scrollTo(_scrollX, 0);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (bitmap != null) {
			Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
			Rect dst = new Rect(0, 0, bg_width, getHeight());
			canvas.drawBitmap(bitmap, src, dst, null);
		}
		canvas.drawColor(0X40333333);
	}
}
