package magic.yuyong.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class SystemUtil{
	public static void openKeyBoard(Context context){
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	public static void closeKeyBoard(View view) {  
	     InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);  
	     imm.hideSoftInputFromWindow(view.getWindowToken(), 0);  
	}  
}