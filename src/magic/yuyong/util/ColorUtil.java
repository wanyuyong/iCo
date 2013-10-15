package magic.yuyong.util;

import java.util.Random;

public class ColorUtil {
	private static final int[] colors = new int[] { 0XFF33B5E5, 0XFFAA66CC, 
		0XFF99CC00, 0XFFFFBB33, 
		0XFFFF4444, 0XFFFF99CC, 
		0XFF00CCFF };
	
	public static int getRandomColor() {
		Random random = new Random();
		int index = random.nextInt(colors.length);
		return colors[index];
	}

}
