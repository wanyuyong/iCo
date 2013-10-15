package magic.yuyong.activity;

import android.view.*;
import magic.yuyong.R;
import magic.yuyong.app.AppConstant;
import magic.yuyong.view.AsyncImageView;
import magic.yuyong.view.AsyncImageView.OnImageViewLoadListener;
import magic.yuyong.view.ScaleImageView;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.FloatMath;
import android.view.View.OnTouchListener;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ShowPic extends BaseActivity implements OnTouchListener {
    private ScaleImageView imgview;
    private ProgressBar mProgressBar;

    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();

    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;
    private VelocityTracker mVelocityTracker;

    private String url, originalUrl, current;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case AppConstant.MSG_SAVE_PIC_SUCCEED:
                    Toast.makeText(
                            getApplicationContext(),
                            getResources()
                                    .getString(R.string.text_pic_save_success),
                            Toast.LENGTH_SHORT).show();
                    break;
                case AppConstant.MSG_SAVE_PIC_FAILD:
                    Toast.makeText(
                            getApplicationContext(),
                            getResources()
                                    .getString(R.string.text_pic_save_faild),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.show_pic);
        imgview = (ScaleImageView) findViewById(R.id.image);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);

        url = getIntent().getStringExtra("url");
        originalUrl = getIntent().getStringExtra("originalUrl");
        current = url;

        imgview.setOnImageViewLoadListener(new OnImageViewLoadListener() {

            @Override
            public void onLoadingStarted(AsyncImageView imageView) {
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setProgress(0);
            }

            @Override
            public void onLoadingFailed(AsyncImageView imageView,
                                        Throwable throwable) {
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingEnded(AsyncImageView imageView, Bitmap image) {
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoading(AsyncImageView imageView, float percent) {
                mProgressBar.setProgress((int) (100 * percent));
            }
        });

        imgview.setUrl(current);
        imgview.setOnTouchListener(this);
        imgview.setLongClickable(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.show_pic, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.refresh:
                imgview.setUrl(current);
                break;
            case R.id.suitable:
                if(slipThread != null){
                    slipThread.stopSlip();
                    slipThread = null;
                }
                imgview.suitable();
                break;
            case R.id.save:
                BitmapDrawable bd = (BitmapDrawable) imgview.getDrawable();
                final Bitmap bm = bd.getBitmap();
                if (bm != null) {
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            ContentResolver cr = getContentResolver();
                            MediaStore.Images.Media
                                    .insertImage(cr, bm, current, "");
                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                                    Uri.parse("file://"
                                            + Environment
                                            .getExternalStorageDirectory())));
                            mHandler.sendEmptyMessage(AppConstant.MSG_SAVE_PIC_SUCCEED);
                        }
                    }).start();
                } else {
                    mHandler.sendEmptyMessage(AppConstant.MSG_SAVE_PIC_FAILD);
                }
                break;
            case R.id.see_original:
                current = originalUrl;
                imgview.setImageMatrix(null);
                imgview.setUrl(current);
                break;
        }
        return true;
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (imgview.getBitmap() != null) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    matrix.set(imgview.getImageMatrix());
                    savedMatrix.set(matrix);
                    start.set(event.getX(), event.getY());
                    mode = DRAG;
                    if (mVelocityTracker == null) {
                        mVelocityTracker = VelocityTracker.obtain();
                    }
                    mVelocityTracker.addMovement(event);
                    break;
                case MotionEvent.ACTION_POINTER_DOWN: // 多点触控
                    oldDist = spacing(event);
                    if (oldDist > 10f) {
                        savedMatrix.set(matrix);
                        midPoint(mid, event);
                        mode = ZOOM;
                    }
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    mode = NONE;
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    slip(mVelocityTracker);
                    mode = NONE;
                    if (mVelocityTracker != null) {
                        mVelocityTracker.clear();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mode == DRAG) {
                        matrix.set(savedMatrix);
                        matrix.postTranslate(event.getX() - start.x, event.getY()
                                - start.y);
                        if (mVelocityTracker != null) {
                            mVelocityTracker.addMovement(event);
                        }
                    } else if (mode == ZOOM) {
                        float newDist = spacing(event);
                        if (newDist > 10) {
                            matrix.set(savedMatrix);
                            float scale = newDist / oldDist;
                            matrix.postScale(scale, scale, mid.x, mid.y);
                        }
                    }
                    break;
            }
            ajustMatix();
            imgview.setImageMatrix(matrix);
        }
        return false;
    }

    private class SlipThread extends Thread{
        int vx;
        int vy;
        int tx, ty;
        int ax, ay;
        boolean run;

        private static final int DECAY = 3;

        private SlipThread(int vx, int vy) {
            this.vx = vx;
            this.vy = vy;
            this.tx = Math.abs(vx/DECAY);
            this.ty = Math.abs(vy/DECAY);
            this.ax = vx > 0 ? -DECAY : DECAY;
            this.ay = vy > 0 ? -DECAY : DECAY;
            run = true;
        }

        public void stopSlip(){
            run = false;
        }

        @Override
        public void run() {
            super.run();

            int t = 1;

            while (run && (t < tx || t < ty)) {
                int dx = 0, dy = 0;
                if(t < tx){
                    dx = (int)(vx+.5*ax*(Math.pow(t, 2)-Math.pow(t-1, 2)));
                }
                if(t < ty){
                    dy = (int)(vy+.5*ay*(Math.pow(t, 2)-Math.pow(t-1, 2)));
                }
                t++;

                slipHandler.sendMessage(slipHandler.obtainMessage(0, dx, dy));

                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private SlipThread slipThread;

    private void slip(VelocityTracker velocityTracker) {
        if(slipThread != null){
            slipThread.stopSlip();
            slipThread = null;
        }
        velocityTracker.computeCurrentVelocity(20);
        int vx = (int) velocityTracker.getXVelocity();
        int vy = (int) velocityTracker.getYVelocity();
        slipThread = new SlipThread(vx, vy);
        slipThread.start();
    }

    Handler slipHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int x = msg.arg1;
            int y = msg.arg2;
            matrix.postTranslate(x, y);
            ajustMatix();
            imgview.setImageMatrix(matrix);
        }
    };

    private void ajustMatix() {
        float[] values = new float[9];
        matrix.getValues(values);
        int scaledWidth = (int) (imgview.getBitmap().getWidth() * values[0]);
        if (scaledWidth > imgview.getWidth()) {
            int minXOffset = imgview.getWidth() - scaledWidth;
            values[2] = Math.max(values[2], minXOffset);
            values[2] = Math.min(0, values[2]);
        } else {
            int maxXOffset = imgview.getWidth() - scaledWidth;
            values[2] = Math.min(values[2], maxXOffset);
            values[2] = Math.max(0, values[2]);
        }

        int scaledHeight = (int) (imgview.getBitmap().getHeight() * values[4]);
        if (scaledHeight > imgview.getHeight()) {
            int minYOffset = imgview.getHeight() - scaledHeight;
            values[5] = Math.max(values[5], minYOffset);
            values[5] = Math.min(0, values[5]);
        } else {
            int maxYOffset = imgview.getHeight() - scaledHeight;
            values[5] = Math.min(values[5], maxYOffset);
            values[5] = Math.max(0, values[5]);
        }

        matrix.setValues(values);
    }
}