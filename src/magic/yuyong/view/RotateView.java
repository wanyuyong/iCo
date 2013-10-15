package magic.yuyong.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 
 * @author wanyuyong
 *
 */
public class RotateView extends SurfaceView implements SurfaceHolder.Callback,
		Runnable {
	private SurfaceHolder holder;
	private int width, height;
	private Point[] points = new Point[5];
	private Point handlerPoint = null;
	private int handlerIndex = -1;

	private static final int BITMAP_NUM = 4;
	private static final int SENSITIVE = 60;
	private Bitmap[] bitmaps = new Bitmap[BITMAP_NUM];

	private int[] ids = new int[] {};

	private Paint paintPoint, paintBorder, paintLine;

	public RotateView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public RotateView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RotateView(Context context) {
		super(context);
		init();
	}

	private void init() {
		holder = getHolder();
		holder.addCallback(this);
	}

	private void initPaint() {
		paintPoint = new Paint();
		paintPoint.setAntiAlias(true);
		paintPoint.setColor(0XFF333333);

		paintBorder = new Paint();
		paintBorder.setAntiAlias(true);
		paintBorder.setColor(Color.WHITE);
		paintBorder.setStyle(Style.STROKE);
		paintBorder.setStrokeWidth(4);

		paintLine = new Paint();
		paintLine.setStrokeWidth(5);
		paintLine.setAntiAlias(true);
		paintLine.setColor(Color.WHITE);
	}
	
	private Bitmap prepareBitmap(Point one, Point two, Point three, int id){
		Bitmap temp = BitmapFactory.decodeResource(getResources(), id);
		Bitmap bitmap = null;
		Rect rect = getBitmapRect(one, two, three);
		int width = rect.width();
		int height = rect.height();

		if (width / (float)height > temp.getWidth() / (float)temp.getHeight()) {
			float scale = width/(float)temp.getWidth();
			Matrix matrix = new Matrix();
			matrix.setScale(scale, scale);
			bitmap = Bitmap.createBitmap(temp, 0, 0, temp.getWidth(), 
					temp.getHeight(), matrix, true);
		} else {
			float scale = height/(float)temp.getHeight();
			Matrix matrix = new Matrix();
			matrix.setScale(scale, scale);
			bitmap = Bitmap.createBitmap(temp, 0, 0, temp.getWidth(), 
					temp.getHeight(), matrix, true);
		}
		temp.recycle();
		return bitmap;
	}
	
	private void preparePicture() {
		bitmaps[0] = prepareBitmap(points[0], points[1], points[3], ids[0]);
		bitmaps[1] = prepareBitmap(points[0], points[1], points[4], ids[1]);
		bitmaps[2] = prepareBitmap(points[0], points[2], points[3], ids[2]);
		bitmaps[3] = prepareBitmap(points[0], points[2], points[4], ids[3]);
	}

	private Rect getBitmapRect(Point one, Point two, Point three) {
		int left = Math.min(Math.min(one.x, two.x), three.x);
		int right = Math.max(Math.max(one.x, two.x), three.x);
		int top = Math.min(Math.min(one.y, two.y), three.y);
		int bottom = Math.max(Math.max(one.y, two.y), three.y);
		Rect rect = new Rect(left, top, right, bottom);
		
		if(left == 0 && right == width){
			if(rect.centerY()<(height>>1)){
				rect.top = Math.min(top, 0);
			}else{
				rect.bottom = Math.max(bottom, height);
			}
		}
		
		return rect;
	}

	/**
	 * 与直线one-two垂直的直线和屏幕边缘的交点
	 * 
	 * y = k*x+Cy-k*Cx ; x = (y+k*Cx-Cy)/k
	 * 
	 * @param one
	 * @param two
	 * @return
	 */
	private List<Point> getTwoPoint(Point one, Point two) {
		List<Point> list = new ArrayList<Point>();
		float k = (two.y - one.y) / (float) (two.x - one.x);
		k = -1 / k;

		Point temp = new Point();
		temp.x = 0;
		temp.y = (int) ((height >> 1) - k * (width >> 1));
		list.add(temp);

		temp = new Point();
		temp.x = width;
		temp.y = (int) (k * temp.x + (height >> 1) - k * (width >> 1));
		list.add(temp);

		temp = new Point();
		temp.y = 0;
		temp.x = (int) ((k * (width >> 1) - (height >> 1)) / k);
		list.add(temp);

		temp = new Point();
		temp.y = height;
		temp.x = (int) ((temp.y + k * (width >> 1) - (height >> 1)) / k);
		list.add(temp);

		int i = 0;
		while (i < list.size()) {
			Point p = list.get(i);
			if (p.x > width || p.x < 0 || p.y > height || p.y < 0) {
				list.remove(i);
			} else {
				i++;
			}
		}

		return list;
	}

	private void preparePoint(Point point, int order) {
		if (points[0] == null) {
			points[0] = new Point(width >> 1, height >> 1);
		}
		int x = point.x;
		int y = point.y;
		Point tempP = null;
		List<Point> list = null;

		switch (order) {
		case 1:
			points[1] = point;
			if (x == 0) {
				points[2] = new Point(width, height - y);
			} else if (x == width) {
				points[2] = new Point(0, height - y);
			} else if (y == 0) {
				points[2] = new Point(width - x, height);
			} else if (y == height) {
				points[2] = new Point(width - x, 0);
			}
			list = getTwoPoint(points[1], points[2]);
			tempP = list.remove(0);
			if ((points[2].x - points[1].x) * (tempP.y - points[2].y)
					- (points[2].y - points[1].y) * (tempP.x - points[2].x) < 0) {
				points[4] = tempP;
				points[3] = list.remove(0);
			} else {
				points[3] = tempP;
				points[4] = list.remove(0);
			}

			break;
		case 2:
			points[2] = point;
			if (x == 0) {
				points[1] = new Point(width, height - y);
			} else if (x == width) {
				points[1] = new Point(0, height - y);
			} else if (y == 0) {
				points[1] = new Point(width - x, height);
			} else if (y == height) {
				points[1] = new Point(width - x, 0);
			}
			list = getTwoPoint(points[1], points[2]);
			tempP = list.remove(0);
			if ((points[2].x - points[1].x) * (tempP.y - points[2].y)
					- (points[2].y - points[1].y) * (tempP.x - points[2].x) < 0) {
				points[4] = tempP;
				points[3] = list.remove(0);
			} else {
				points[3] = tempP;
				points[4] = list.remove(0);
			}

			break;
		case 3:
			points[3] = point;
			if (x == 0) {
				points[4] = new Point(width, height - y);
			} else if (x == width) {
				points[4] = new Point(0, height - y);
			} else if (y == 0) {
				points[4] = new Point(width - x, height);
			} else if (y == height) {
				points[4] = new Point(width - x, 0);
			}
			list = getTwoPoint(points[3], points[4]);
			tempP = list.remove(0);
			if ((points[4].x - points[3].x) * (tempP.y - points[4].y)
					- (points[4].y - points[3].y) * (tempP.x - points[4].x) < 0) {
				points[1] = tempP;
				points[2] = list.remove(0);
			} else {
				points[2] = tempP;
				points[1] = list.remove(0);
			}
			break;
		case 4:
			points[4] = point;
			if (x == 0) {
				points[3] = new Point(width, height - y);
			} else if (x == width) {
				points[3] = new Point(0, height - y);
			} else if (y == 0) {
				points[3] = new Point(width - x, height);
			} else if (y == height) {
				points[3] = new Point(width - x, 0);
			}
			list = getTwoPoint(points[3], points[4]);
			tempP = list.remove(0);
			if ((points[4].x - points[3].x) * (tempP.y - points[4].y)
					- (points[4].y - points[3].y) * (tempP.x - points[4].x) < 0) {
				points[1] = tempP;
				points[2] = list.remove(0);
			} else {
				points[2] = tempP;
				points[1] = list.remove(0);
			}
			break;
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		this.width = width;
		this.height = height;
		preparePoint(new Point(80, 0), 1);
		new Thread(this).start();
	}
	
	private void drawPic(Canvas canvas, Point one, Point two, Point three, Bitmap bitmap){
		Rect dst = getBitmapRect(one, two, three);
		Path path = new Path();
		path.moveTo(one.x, one.y);
		path.lineTo(two.x, two.y);
		if(Math.abs(two.x-three.x) == width){
			if(dst.centerY() > (height>>1)){
				if(two.x == 0){
					path.lineTo(0, height);
					path.lineTo(width, height);
				}else{
					path.lineTo(width, height);
					path.lineTo(0, height);
				}
			}else{
				if(two.x == 0){
					path.lineTo(0, 0);
					path.lineTo(width, 0);
				}else{
					path.lineTo(width, 0);
					path.lineTo(0, 0);
				}
			}
		}else if(two.x == 0 && three.y == height
				|| three.x == 0 && two.y == height){
			path.lineTo(0, height);
		}else if(two.x == 0 && three.y == 0
				|| three.x == 0 && two.y == 0){
			path.lineTo(0, 0);
		}else if(two.y == 0 && three.x == width
				|| three.y == 0 && two.x == width){
			path.lineTo(width, 0);
		}else if(two.x == width && three.y == height
				|| three.x == width && two.y == height){
			path.lineTo(width, height);
		}
		path.lineTo(three.x, three.y);
		path.close();
		canvas.save();
		canvas.clipPath(path);
		Rect src = new Rect(0, 0, dst.width(), dst.height());
		canvas.drawBitmap(bitmap, src, dst, null);
		canvas.restore();
	}

	private void refreshDraw() {
		Canvas canvas = holder.lockCanvas();
		canvas.drawColor(Color.GRAY);
		preparePicture();
		
		drawPic(canvas, points[0], points[1], points[3], bitmaps[0]);
		drawPic(canvas, points[0], points[1], points[4], bitmaps[1]);
		drawPic(canvas, points[0], points[2], points[3], bitmaps[2]);
		drawPic(canvas, points[0], points[2], points[4], bitmaps[3]);

		canvas.drawLine(points[1].x, points[1].y, points[2].x, points[2].y,
				paintLine);
		canvas.drawLine(points[3].x, points[3].y, points[4].x, points[4].y,
				paintLine);

		for (int i = 0; i < points.length; i++) {
			Point p = points[i];
			canvas.drawCircle(p.x, p.y, 10, paintPoint);
			canvas.drawCircle(p.x, p.y, 8, paintBorder);
		}
		holder.unlockCanvasAndPost(canvas);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		initPaint();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			for (int i = 1; i < points.length; i++) {
				Point p = points[i];
				if (x > p.x - SENSITIVE && x < p.x + SENSITIVE
						&& y > p.y - SENSITIVE && y < p.y + SENSITIVE) {
					handlerPoint = p;
					handlerIndex = i;
					break;
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			handlerPoint = null;
			handlerIndex = -1;
			break;
		case MotionEvent.ACTION_MOVE:

			if (handlerPoint != null) {
				if (handlerPoint.x == 0 || handlerPoint.x == width) {
					handlerPoint.y = y;
					handlerPoint.y = Math.max(0, handlerPoint.y);
					handlerPoint.y = Math.min(height, handlerPoint.y);
					if (handlerPoint.y < 30) {
						handlerPoint.y = 0;
						handlerPoint.x = x;
					} else if (handlerPoint.y > height - 30) {
						handlerPoint.y = height;
						handlerPoint.x = x;
					}
					handlerPoint.x = Math.max(0, handlerPoint.x);
					handlerPoint.x = Math.min(width, handlerPoint.x);
				}
				if (handlerPoint.y == 0 || handlerPoint.y == height) {
					handlerPoint.x = x;
					handlerPoint.x = Math.max(0, handlerPoint.x);
					handlerPoint.x = Math.min(width, handlerPoint.x);
					if (handlerPoint.x < 30) {
						handlerPoint.x = 0;
						handlerPoint.y = y;
					} else if (handlerPoint.x > width - 30) {
						handlerPoint.x = width;
						handlerPoint.y = y;
					}
					handlerPoint.y = Math.max(0, handlerPoint.y);
					handlerPoint.y = Math.min(height, handlerPoint.y);
				}
				preparePoint(handlerPoint, handlerIndex);
				new Thread(this).start();
			}
			break;

		default:
			break;
		}

		return true;
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	}

	@Override
	public void run() {
		refreshDraw();
	}
}
