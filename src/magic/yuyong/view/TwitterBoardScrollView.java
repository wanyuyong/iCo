package magic.yuyong.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

public class TwitterBoardScrollView extends HorizontalScrollView {
	private TwitterBoardScrollListener mListener;
	
	public static interface TwitterBoardScrollListener{
		void onTwitterBoardScrollChanged(int l, int t, int oldl, int oldt, TwitterBoardScrollView parent);
	}

	public TwitterBoardScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public TwitterBoardScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TwitterBoardScrollView(Context context) {
		super(context);
	}

	public TwitterBoardScrollListener getTwitterBoardScrollListener() {
		return mListener;
	}

	public void setTwitterBoardScrollListener(TwitterBoardScrollListener mListener) {
		this.mListener = mListener;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if(mListener != null){
			mListener.onTwitterBoardScrollChanged(l, t, oldl, oldt, this);
		}
	}

}
