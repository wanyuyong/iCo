package magic.yuyong.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class ScaleImageView extends AsyncImageView {
	private Matrix suitable;
    private Bitmap bitmap;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public ScaleImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ScaleImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ScaleImageView(Context context) {
		super(context);
	}

	@Override
	public void setImageBitmap(Bitmap bitmap) {
		if(bitmap != null){
			DisplayMetrics dm = new DisplayMetrics();
			dm = getResources().getDisplayMetrics();
			float screenWidth = dm.widthPixels;
			Matrix imgMatrix = getImageMatrix();
			int bW = bitmap.getWidth();
			float scale = screenWidth / bW;
			imgMatrix.postScale(scale, scale, 0, 0);
			setImageMatrix(imgMatrix);
			suitable = new Matrix(imgMatrix);
		}
        this.bitmap = bitmap;
		super.setImageBitmap(bitmap);
	}
	
	public void suitable(){
		if(suitable != null){
			setImageMatrix(suitable);
		}
	}
	
}
