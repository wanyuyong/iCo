
package magic.yuyong.swipeback;

import java.util.ArrayList;
import java.util.List;

import com.nineoldandroids.view.ViewHelper;

import magic.yuyong.swipeback.SwipeBackLayout.SwipeListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.View;



public class SwipeBackActivity extends FragmentActivity implements SwipeBackActivityBase {
    private SwipeBackActivityHelper mHelper;
    public static List<SwipeBackActivity> activityStack = new ArrayList<SwipeBackActivity>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHelper = new SwipeBackActivityHelper(this);
        mHelper.onActivityCreate();
        
        if(!activityStack.isEmpty()){
        	SwipeBackActivity preActivity = activityStack.get(activityStack.size()-1);
        	final View preContent = preActivity.getSwipeBackLayout();
        	getSwipeBackLayout().addSwipeListener(new SwipeListener() {
        		
				@Override
				public void onScrollStateChange(int state, float scrollPercent) {
					if(state == ViewDragHelper.STATE_IDLE && scrollPercent == 0){
						ViewHelper.setTranslationX(preContent, -preContent.getWidth()*scrollPercent);
					}
				}
				
				@Override
				public void onScrollOverThreshold() {
				}
				
				@Override
				public void onEdgeTouch(int edgeFlag) {
				}

				@Override
				public void onScroll(float scrollPercent) {
					float x = 7*scrollPercent/20-.25f;
					x = x * preContent.getWidth();
					x = x > 0 ? 0 : x;
					ViewHelper.setTranslationX(preContent, x);
				}
			});
        }
    	activityStack.add(this);
    	
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	activityStack.remove(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mHelper.onPostCreate();
    }

    @Override
    public View findViewById(int id) {
        View v = super.findViewById(id);
        if (v == null && mHelper != null)
            return mHelper.findViewById(id);
        return v;
    }

    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        return mHelper.getSwipeBackLayout();
    }

    @Override
    public void setSwipeBackEnable(boolean enable) {
        getSwipeBackLayout().setEnableGesture(enable);
    }

    @Override
    public void scrollToFinishActivity() {
        getSwipeBackLayout().scrollToFinishActivity();
    }
}
