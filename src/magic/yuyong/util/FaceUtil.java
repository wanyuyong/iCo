package magic.yuyong.util;

import java.util.regex.Pattern;

import magic.yuyong.R;

public class FaceUtil {

	private static Pattern pattern;

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

	public Pattern getPattern() {
		if (pattern == null) {
			StringBuilder patternString = new StringBuilder();
			patternString.append('(');
			for (String s : FaceUtil.faceStrs) {
				patternString.append(Pattern.quote("[" + s + "]"));
				patternString.append('|');
			}
			patternString.replace(patternString.length() - 1,
					patternString.length(), ")");
			pattern = Pattern.compile(patternString.toString());
		}
		return pattern;
	}
}
