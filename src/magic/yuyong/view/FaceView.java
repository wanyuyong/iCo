package magic.yuyong.view;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import magic.yuyong.R;
import magic.yuyong.util.DisplayUtil;
import magic.yuyong.util.FaceUtil;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.FontMetrics;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
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

	public void refreshTagContent() {
		if (tagView != null) {
			refreshTagContent(tagView.getText().toString(), tagView.getText()
					.length());
		}
	}

	public void refreshTagContent(String text, int selection) {
		if (tagView != null) {
			if(text.length() > 140){
				text = text.substring(0, 140);
			}
			FontMetrics fm = tagView.getPaint().getFontMetrics();
			int height = (int) (fm.descent - fm.ascent);
			SpannableStringBuilder builder = new SpannableStringBuilder(text);
			Pattern pattern = buildPattern();
			Matcher matcher = pattern.matcher(text);
			while (matcher.find()) {
				String temp = matcher.group();
				int resId = FaceUtil.getFaceDrawableID(temp.substring(1,
						temp.length() - 1));
				Drawable drawable = getResources().getDrawable(resId);
				int width = height * drawable.getIntrinsicWidth()
						/ drawable.getIntrinsicHeight();
				drawable.setBounds(0, 0, width, height);
				ImageSpan span = new ImageSpan(drawable,
						ImageSpan.ALIGN_BASELINE);
				builder.setSpan(span, matcher.start(), matcher.end(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			tagView.setText(builder);
			selection = selection > 140 ? 140 : selection;
			tagView.setSelection(selection);
		}
	}

	public void addFace(int position) {
		if (tagView != null) {
			String text = tagView.getText().toString();
			int start = tagView.getSelectionStart();
			int end = tagView.getSelectionEnd();
			String str = "";
			str = "[" + (String) getAdapter().getItem(position) + "]";
			text = text.substring(0, start) + str + text.substring(end);
			refreshTagContent(text, start + str.length());
		}
	}

	private Pattern buildPattern() {
		StringBuilder patternString = new StringBuilder();
		patternString.append('(');
		for (String s : FaceUtil.faceStrs) {
			patternString.append(Pattern.quote("[" + s + "]"));
			patternString.append('|');
		}
		patternString.replace(patternString.length() - 1,
				patternString.length(), ")");
		return Pattern.compile(patternString.toString());
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
				int size = DisplayUtil.dip2px(50, getResources()
						.getDisplayMetrics().density);
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
