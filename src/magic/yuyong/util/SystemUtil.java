package magic.yuyong.util;

import android.content.Context;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;

public class SystemUtil{
	public static void openKeyBoard(Context context){
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	public static void closeKeyBoard(View view) {  
	     InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);  
	     imm.hideSoftInputFromWindow(view.getWindowToken(), 0);  
	}  
	
	public static void stopListViewFling(ListView listView){
		listView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),  SystemClock.uptimeMillis(), MotionEvent.ACTION_CANCEL, 0, 0, 0));
	}
}