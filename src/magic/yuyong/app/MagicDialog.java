package magic.yuyong.app;

import java.util.ArrayList;
import java.util.List;

import magic.yuyong.R;
import magic.yuyong.util.DisplayUtil;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MagicDialog extends Dialog{
	private List<ButtonInfo> buttonInfos = new ArrayList<MagicDialog.ButtonInfo>();
	private String title, msg;

	public MagicDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public MagicDialog(Context context, int theme) {
		super(context, theme);
	}

	public MagicDialog(Context context) {
		super(context);
	}

	public void addButton(int text, android.view.View.OnClickListener onClickListener){
		buttonInfos.add(new ButtonInfo(text, onClickListener));
	}
	
	public void setMessage(String title, String msg){
		this.title = title;
		this.msg = msg;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog);
		LinearLayout buttonsLay = (LinearLayout)findViewById(R.id.buttons);
		((TextView)findViewById(R.id.title)).setText(title);
		((TextView)findViewById(R.id.text)).setText(msg);
		for(ButtonInfo info : buttonInfos){
			TextView button = new TextView(getContext());
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
					LinearLayout.LayoutParams.MATCH_PARENT);
            lp.weight = 1;
			int margin = DisplayUtil.dip2px(10, getContext().getResources().getDisplayMetrics().density);
			lp.leftMargin = margin;
			lp.rightMargin = margin;
			button.setText(info.text);
			button.setTextColor(0XFF555555);
			button.setGravity(Gravity.CENTER);
			button.setBackgroundResource(R.drawable.round_rect_but_bg);
			button.setOnClickListener(info.onClickListener);
			buttonsLay.addView(button, lp);
		}
	}
	
	class ButtonInfo{
		int text;
		android.view.View.OnClickListener onClickListener;
		
		public ButtonInfo(int text, android.view.View.OnClickListener onClickListener) {
			super();
			this.text = text;
			this.onClickListener = onClickListener;
		}
	}

}
