package magic.yuyong.listener;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public abstract class ListViewScrollListener implements OnTouchListener {

	private GestureDetector mGestureDetector;

	public ListViewScrollListener(Context context) {
		mGestureDetector = new GestureDetector(context,
				new SimpleOnGestureListener() {
			
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						if (velocityY > 0) {
							onFlingDown();
						} else {
							onFlingUp();
						}
						return false;
					};
				});
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		mGestureDetector.onTouchEvent(event);
		return false;
	}

	protected abstract void onFlingUp();

	protected abstract void onFlingDown();

}
