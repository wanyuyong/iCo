package magic.yuyong.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;

public class TwitterImageView extends AsyncImageView {

	private static final int MAX_HEIGHT = 4096;

	private Bitmap bitmap;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            setImageBitmap(bitmap);
        }
    };

	public TwitterImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public TwitterImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TwitterImageView(Context context) {
		super(context);
	}

    @Override
    public void setImageBitmap(Bitmap bm) {
        if(bm.getHeight() > MAX_HEIGHT){
            new HandlerBitmap(bm).start();
        }else{
            bitmap = bm;
            super.setImageBitmap(bm);
        }
    }

    @Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (bitmap != null) {
			float bw = bitmap.getWidth();
			float bh = bitmap.getHeight();
			int w = MeasureSpec.getSize(widthMeasureSpec);
			float scale = w / bw;
			int h = (int) (bh * scale);
			setMeasuredDimension(w, h);
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}

    private class HandlerBitmap extends Thread{
        private Bitmap b;

        private HandlerBitmap(Bitmap bitmap) {
            this.b = bitmap;
        }

        @Override
        public void run() {
            super.run();
            b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), MAX_HEIGHT);
            bitmap = b;
            mHandler.sendEmptyMessage(0);
        }
    }
}
