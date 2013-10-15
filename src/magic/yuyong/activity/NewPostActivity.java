package magic.yuyong.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import magic.yuyong.R;
import magic.yuyong.app.AppConstant;
import magic.yuyong.app.MagicApplication;
import magic.yuyong.app.MagicDialog;
import magic.yuyong.util.PicManager;
import magic.yuyong.util.SDCardUtils;
import magic.yuyong.util.StringUtil;
import magic.yuyong.util.SystemUtil;
import magic.yuyong.view.FaceView;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.CommentsAPI;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.api.WeiboAPI;
import com.weibo.sdk.android.net.RequestListener;

public class NewPostActivity extends BaseActivity implements OnClickListener,
		RequestListener {

	private static final int GET_FRIENDS_REQUEST = 0;
	private static final int GET_PIC = 1;
	public static final int PHOTOGRAPH = 2;
	public static final int PREPARE_PIC = 3;

	private EditText post_text;
	private FaceView faceView;
	private ImageView pic;
	private TextView text_num;
	private CheckBox check_box;

	private long twitter_id;
	private long cid;
	private String cu;
	private String cc;
	private Uri uri;
	private String picPath, cameraPicPath;
	private boolean resizeFaceView = true;

	private int type;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case AppConstant.MSG_POST_SUCCEED:
				Toast.makeText(getApplicationContext(),
						getResources().getText(R.string.text_post_success),
						Toast.LENGTH_SHORT).show();
				break;

			case AppConstant.MSG_POST_FAILD:
				Toast.makeText(getApplicationContext(),
						getResources().getText(R.string.text_post_faild),
						Toast.LENGTH_SHORT).show();
				break;
			case AppConstant.MSG_SHOW_POST_PIC:
				Bitmap bitmap = (Bitmap) msg.obj;
				pic.setImageBitmap(bitmap);
				pic.setVisibility(View.VISIBLE);
				break;
			}

			setProgressBarIndeterminateVisibility(false);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		actionBar.setDisplayHomeAsUpEnabled(true);
		setProgressBarIndeterminateVisibility(false);

		setContentView(R.layout.new_post);
		text_num = (TextView) findViewById(R.id.text_num);
		post_text = (EditText) findViewById(R.id.post_text);
		faceView = (FaceView) findViewById(R.id.face_view);
		faceView.setTagView(post_text);
		post_text.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (faceView.getVisibility() != View.GONE) {
					faceView.setVisibility(View.GONE);
				}
				return false;
			}
		});
		post_text.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				text_num.setText(post_text.getText().length() + "X");
			}
		});

		pic = (ImageView) findViewById(R.id.pic);
		pic.setOnClickListener(this);
		check_box = (CheckBox) findViewById(R.id.check_box);

		String at = getIntent().getStringExtra("@");
		if (at != null && !"".equals(at)) {
			post_text.setText(at + " ");
			post_text.setSelection(post_text.getText().length());
		}
		String topic = getIntent().getStringExtra("#");
		if (topic != null && !"".equals(topic)) {
			post_text.setText(topic + " ");
			post_text.setSelection(post_text.getText().length());
		}
		String initText = getIntent().getStringExtra("initText");
		if (initText != null && !"".equals(initText)) {
			post_text.setText(initText);
			faceView.refreshTagContent();
			post_text.setSelection(0);
		}
		twitter_id = getIntent().getLongExtra("twitter_id", 0l);
		cid = getIntent().getLongExtra("cid", 0l);
		cu = getIntent().getStringExtra("cu");
		cc = getIntent().getStringExtra("cc");
		type = getIntent().getIntExtra("type", AppConstant.TYPE_POST_TEXT);
		if (type == AppConstant.TYPE_REPOST) {
			actionBar.setTitle(R.string.title_repost);
			check_box.setVisibility(View.VISIBLE);
			check_box.setText(R.string.text_also_comment);
		} else if (type == AppConstant.TYPE_COMMENT) {
			actionBar.setTitle(R.string.title_comment);
			check_box.setVisibility(View.VISIBLE);
			check_box.setText(R.string.text_also_repost);
		} else if (type == AppConstant.TYPE_REPLY_COMMENT) {
			actionBar.setTitle(R.string.title_reply_comment);
			check_box.setVisibility(View.VISIBLE);
			check_box.setText(R.string.text_also_repost);
		} else {
			actionBar.setTitle(R.string.title_new_post);
		}

		resizeFaceView = true;
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		picPath = savedInstanceState.getString("picPath");
		cameraPicPath = savedInstanceState.getString("cameraPicPath");
		type = savedInstanceState.getInt("type");
		String text = savedInstanceState.getString("text");
		if (!StringUtil.isEmpty(text)) {
			post_text.setText(text);
			faceView.refreshTagContent();
		}
		if (!StringUtil.isEmpty(picPath)) {
			showPic();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("picPath", picPath);
		outState.putString("cameraPicPath", cameraPicPath);
		outState.putInt("type", type);
		outState.putString("text", post_text.getText().toString());
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		resizeFaceView = true;
		if (faceView.getVisibility() != View.GONE) {
			faceView.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.post, menu);
		if (type != AppConstant.TYPE_POST_TEXT
				&& type != AppConstant.TYPE_POST_TEXT_IMG) {
			menu.findItem(R.id.get_pic).setVisible(false);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onBackPressed() {
		if (faceView.getVisibility() != View.GONE) {
			faceView.setVisibility(View.GONE);
			SystemUtil.openKeyBoard(getApplicationContext());
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.clear:
			post_text.setText("");
			break;
		case R.id.topic:
			String text = post_text.getText().toString();
			int start = post_text.getSelectionStart();
			text = text.substring(0, start) + "##" + text.substring(start);
			faceView.refreshTagContent(text, start + 1);
			break;
		case R.id.img:
			Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
			pickIntent.setDataAndType(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
			startActivityForResult(pickIntent, GET_PIC);
			break;
		case R.id.camera:
			Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			cameraPicPath = makePicPath();
			uri = Uri.fromFile(new File(cameraPicPath));
			captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			startActivityForResult(captureIntent, PHOTOGRAPH);
			break;
		case R.id.face:
			if (faceView.getVisibility() != View.GONE) {
				faceView.setVisibility(View.GONE);
				SystemUtil.openKeyBoard(getApplicationContext());
			} else {
				if (resizeFaceView) {
					Rect outRect1 = new Rect();
					getWindow().getDecorView().getWindowVisibleDisplayFrame(
							outRect1);
					int statusBarHeight = outRect1.top;

					Rect outRect2 = new Rect();
					getWindow().getWindowManager().getDefaultDisplay()
							.getRectSize(outRect2);
					boolean isPortrait = outRect2.width() < outRect2.height();
					int faceViewHeight = 0;
					if (isPortrait) {
						faceViewHeight = outRect2.height() - statusBarHeight
								- outRect1.height();
					} else {
						faceViewHeight = outRect1.height() / 2;
					}
					faceView.getLayoutParams().height = faceViewHeight;
					resizeFaceView = false;
				}
				SystemUtil.closeKeyBoard(post_text);
				faceView.postDelayed(new Runnable() {

					@Override
					public void run() {
						faceView.setVisibility(View.VISIBLE);
					}
				}, 200);
			}
			break;
		case R.id.at:
			Intent getFriendsIntent = new Intent(getApplicationContext(),
					GetFriendsActivity.class);
			startActivityForResult(getFriendsIntent, GET_FRIENDS_REQUEST);
			break;
		case R.id.send:
			post();
			break;
		}
		return true;
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (faceView.getVisibility() != View.GONE) {
			faceView.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pic:
			popDel();
			break;
		}

	}

	private String makePicPath() {
		Date now = new Date();
		return Environment.getExternalStorageDirectory().getPath() + "/"
				+ now.getTime() + ".jpeg";
	}

	private void popDel() {
		final MagicDialog dialog = new MagicDialog(this, R.style.magic_dialog);
		dialog.setMessage(getResources().getString(R.string.title_del_img),
				getResources().getString(R.string.text_del_img));
		dialog.addButton(R.string.but_del, new OnClickListener() {

			@Override
			public void onClick(View v) {
				pic.setImageBitmap(null);
				pic.setVisibility(View.GONE);
				type = AppConstant.TYPE_POST_TEXT;
				dialog.dismiss();
			}
		});
		dialog.addButton(R.string.but_cancel, new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == GET_PIC) {
				uri = data.getData();
				String path = getAbsoluteImagePath(uri);
				preparePic(path);
			} else if (requestCode == PHOTOGRAPH) {
				savePic(cameraPicPath);
				preparePic(cameraPicPath);
			} else if (requestCode == PREPARE_PIC) {
				// uri = data.getData();
				// picPath = uri.getPath();
				// new Thread(new Runnable() {
				//
				// @Override
				// public void run() {
				// showPic();
				// savePic(picPath);
				// }
				// }).start();
				// Bundle extra = data.getExtras();
				// if (null != extra) {
				// // image was changed by the user?
				// boolean changed = extra
				// .getBoolean(Constants.EXTRA_OUT_BITMAP_CHANGED);
				// }
			} else if (requestCode == GET_FRIENDS_REQUEST) {
				String choose = data.getStringExtra("@");
				String text = post_text.getText().toString();
				int start = post_text.getSelectionStart();
				text = text.substring(0, start) + choose
						+ text.substring(start);
				faceView.refreshTagContent(text, start + choose.length());
			}
		}
	}

	private void preparePic(final String path) {
		setProgressBarIndeterminateVisibility(false);

		new Thread(new Runnable() {

			@Override
			public void run() {
				int degrees = 0;
				switch (PicManager.getPictureDegree(path)) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degrees = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degrees = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degrees = 270;
					break;
				}
				if (degrees != 0) {
					Bitmap bitmap = PicManager.featBitmap(path, 800);
					Bitmap temp = PicManager.rotate(bitmap, degrees);
					bitmap.recycle();

					picPath = makePicPath();
					SDCardUtils.saveBitmapByPath(picPath, temp);
					temp.recycle();
				} else {
					picPath = path;
				}

				showPic();
			}
		}).start();

	}

	private void showPic() {
		Bitmap bitmap = PicManager.getRectBitmap(picPath, (int) getResources()
				.getDimension(R.dimen.avatar));
		mHandler.sendMessage(mHandler.obtainMessage(
				AppConstant.MSG_SHOW_POST_PIC, bitmap));
		type = AppConstant.TYPE_POST_TEXT_IMG;
	}

	// private void feather() {
	// Intent newIntent = new Intent(this, FeatherActivity.class);
	// newIntent.setData(uri);
	// newIntent.putExtra(Constants.EXTRA_OUTPUT,
	// Uri.parse("file://" + makePicPath()));
	// newIntent.putExtra(Constants.EXTRA_OUTPUT_FORMAT,
	// Bitmap.CompressFormat.JPEG.name());
	// newIntent.putExtra(Constants.EXTRA_OUTPUT_QUALITY, 90);
	// newIntent.putExtra(Constants.EXTRA_TOOLS_LIST, new String[] {
	// "SHARPNESS", "BRIGHTNESS", "CONTRAST", "SATURATION", "EFFECTS",
	// "RED_EYE", "CROP", "WHITEN", "DRAWING", "TEXT", "BLEMISH",
	// "MEME", "ADJUST", "ENHANCE", "COLORTEMP", "COLOR_SPLASH",
	// "TILT_SHIFT" });
	// newIntent.putExtra(Constants.EXTRA_EFFECTS_ENABLE_FAST_PREVIEW, true);
	// newIntent.putExtra(Constants.EXTRA_HIDE_EXIT_UNSAVE_CONFIRMATION, true);
	// newIntent.putExtra(Constants.EXTRA_TOOLS_DISABLE_VIBRATION, true);
	// newIntent.putExtra(Constants.EXTRA_IN_SAVE_ON_NO_CHANGES, false);
	// newIntent
	// .putExtra(Constants.EXTRA_EFFECTS_ENABLE_EXTERNAL_PACKS, false);
	// newIntent.putExtra(Constants.EXTRA_FRAMES_ENABLE_EXTERNAL_PACKS, false);
	// startActivityForResult(newIntent, PREPARE_PIC);
	// }

	private void savePic(final String path) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					MediaStore.Images.Media.insertImage(getContentResolver(),
							path, "", "");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private String getAbsoluteImagePath(Uri uri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, proj, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	private void post() {
		setProgressBarIndeterminateVisibility(true);

		String content = post_text.getText().toString();
		switch (type) {

		case AppConstant.TYPE_POST_TEXT:
			StatusesAPI postAPI = new StatusesAPI(MagicApplication
					.getInstance().getAccessToken());
			postAPI.update(content, null, null, this);
			break;

		case AppConstant.TYPE_POST_TEXT_IMG:
			StatusesAPI postImgAPI = new StatusesAPI(MagicApplication
					.getInstance().getAccessToken());
			postImgAPI.upload(content, picPath, null, null, this);
			break;

		case AppConstant.TYPE_REPOST:
			StatusesAPI repostAPI = new StatusesAPI(MagicApplication
					.getInstance().getAccessToken());
			repostAPI.repost(twitter_id, content,
					check_box.isChecked() ? WeiboAPI.COMMENTS_TYPE.CUR_STATUSES
							: WeiboAPI.COMMENTS_TYPE.NONE, this);
			break;

		case AppConstant.TYPE_COMMENT:
			if (check_box.isChecked()) {
				// comment & repost
				StatusesAPI comment_repost_API = new StatusesAPI(
						MagicApplication.getInstance().getAccessToken());
				comment_repost_API.repost(twitter_id, content,
						WeiboAPI.COMMENTS_TYPE.CUR_STATUSES, this);
			} else {
				// only comment
				CommentsAPI commentAPI = new CommentsAPI(MagicApplication
						.getInstance().getAccessToken());
				commentAPI.create(content, twitter_id, true, this);
			}
			break;

		case AppConstant.TYPE_REPLY_COMMENT:

			CommentsAPI replyCommentAPI = new CommentsAPI(MagicApplication
					.getInstance().getAccessToken());
			replyCommentAPI.reply(cid, twitter_id, content, false, false, this);

			if (check_box.isChecked()) {
				content = "回复@" + cu + ":" + content + "//@" + cu + ":" + cc;
				StatusesAPI comment_repost_API = new StatusesAPI(
						MagicApplication.getInstance().getAccessToken());
				comment_repost_API.repost(twitter_id, content,
						WeiboAPI.COMMENTS_TYPE.NONE, new RequestListener() {
							@Override
							public void onComplete(String s) {

							}

							@Override
							public void onIOException(IOException e) {

							}

							@Override
							public void onError(WeiboException e) {

							}
						});
			}

			break;
		}
	}

	@Override
	public void onComplete(String arg0) {
		mHandler.sendEmptyMessage(AppConstant.MSG_POST_SUCCEED);
		finish();
	}

	@Override
	public void onError(WeiboException e) {
		mHandler.sendEmptyMessage(AppConstant.MSG_POST_FAILD);
	}

	@Override
	public void onIOException(IOException arg0) {
	}
}
