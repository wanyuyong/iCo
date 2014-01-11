package magic.yuyong.view;

import magic.yuyong.util.DisplayUtil;
import magic.yuyong.util.FaceUtil;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class FaceView extends GridView {
	private EditText tagView;

	public FaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public FaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public FaceView(Context context) {
		super(context);
		init();
	}

	private void init() {
		FaceAdapter adapter = new FaceAdapter();
		setAdapter(adapter);
		setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				addFace(position);
			}
		});
	}

	public void addFace(int position) {
		if (tagView != null) {
			String text = tagView.getText().toString();
			int start = tagView.getSelectionStart();
			int end = tagView.getSelectionEnd();
			String str = "";
			str = "[" + (String) getAdapter().getItem(position) + "]";
			text = text.substring(0, start) + str + text.substring(end);
			FaceUtil.refreshTagContent(tagView, text, start + str.length());
		}
	}

	public void setTagView(EditText tagView) {
		this.tagView = tagView;
	}

	class FaceAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return FaceUtil.faceStrs.length;
		}

		@Override
		public Object getItem(int postion) {
			return FaceUtil.faceStrs[postion];
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				ImageView view = new ImageView(getContext());
				int size = (int) DisplayUtil.dpToPx(getResources(), 50);
				view.setLayoutParams(new LayoutParams(size, size));
				view.setScaleType(ScaleType.CENTER);
				convertView = view;
			}
			((ImageView) convertView).setImageResource(FaceUtil
					.getFaceDrawableID(position));
			return convertView;
		}
	}

}
