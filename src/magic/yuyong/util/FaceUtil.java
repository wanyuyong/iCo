package magic.yuyong.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.Paint.FontMetrics;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.widget.EditText;

import magic.yuyong.R;

public class FaceUtil {

	public static String[] faceStrs = new String[] { "嘻嘻", "哈哈", "可爱", "可怜",
			"挖鼻屎", "吃惊", "害羞", "呵呵", "鄙视", "爱你", "泪", "偷笑", "亲亲", "威武", "太开心",
			"懒得理你", "奥特曼", "嘘", "衰", "委屈", "吐", "打哈欠", "抱抱", "怒", "馋嘴", "汗",
			"困", "睡觉", "钱", "失望", "酷", "花心", "哼", "鼓掌", "晕", "抓狂", "疑问", "怒骂",
			"生病", "闭嘴", "太阳", "心", "good", "挤眼", "左哼哼", "右哼哼", "猪头", "囧" };

	private static int[] face_ids = new int[] { R.drawable.xixi,
			R.drawable.haha, R.drawable.keai, R.drawable.kelian,
			R.drawable.wabikong, R.drawable.chijing, R.drawable.haixiu,
			R.drawable.hehe, R.drawable.bishi, R.drawable.aini, R.drawable.lei,
			R.drawable.touxiao, R.drawable.qinqin, R.drawable.weiwu,
			R.drawable.taikaixin, R.drawable.landelini, R.drawable.aoteman,
			R.drawable.xu, R.drawable.shuai2, R.drawable.weiqu, R.drawable.tu,
			R.drawable.dahaqi, R.drawable.baobao, R.drawable.nu,
			R.drawable.chanzui, R.drawable.han, R.drawable.kun,
			R.drawable.shuijiao, R.drawable.qian, R.drawable.shiwang,
			R.drawable.ku, R.drawable.huaxin, R.drawable.heng,
			R.drawable.guzhang, R.drawable.yun, R.drawable.zhuakuang,
			R.drawable.yiwen, R.drawable.numa, R.drawable.shengbing,
			R.drawable.bizui, R.drawable.taiyang, R.drawable.xin,
			R.drawable.good, R.drawable.jiyan, R.drawable.zuohenhen,
			R.drawable.youhenhen, R.drawable.zhutou, R.drawable.jiong };

	public static int getFaceDrawableID(String face) {
		int index = 0;
		for (int i = 0; i < faceStrs.length; i++) {
			if (faceStrs[i].equals(face)) {
				index = i;
				break;
			}
		}
		index = index > face_ids.length - 1 ? 0 : index;
		return face_ids[index];
	}

	public static int getFaceDrawableID(int position) {
		position = position > face_ids.length - 1 ? 0 : position;
		return face_ids[position];
	}
	
	private static Pattern  buildPattern() {
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
	
	public static void refreshTagContent(EditText tagView, String text,  int selection) {
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
				Drawable drawable = tagView.getResources().getDrawable(resId);
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

}
