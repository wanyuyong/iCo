package magic.yuyong.activity;

import magic.yuyong.R;
import magic.yuyong.view.DeskView;
import magic.yuyong.view.IndicatorView;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;

public class ChannelActivity extends BaseActivity implements OnClickListener{

	private DeskView desk;
	private IndicatorView indicator;
	private View back_but;
	
	private OnClickListener onItemClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
//			int type_id = (Integer) v.getTag();
//			Intent intent = new Intent(getApplicationContext(), WeiboWallActivity.class);
//			intent.putExtra("type_id", type_id);
//			startActivity(intent);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.channel);

		desk = (DeskView) findViewById(R.id.desk_view);
		indicator = (IndicatorView) findViewById(R.id.indicator);
		
		desk.setOnDeskChangeListener(indicator);
		desk.setOnItemClickListener(onItemClickListener);
		
		back_but = findViewById(R.id.back_but);
		back_but.setOnClickListener(this);
		
		Animation animation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 1,
				Animation.RELATIVE_TO_SELF, 0);
		animation.setDuration(800);
		animation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation arg0) {}
			
			@Override
			public void onAnimationRepeat(Animation arg0) {}
			
			@Override
			public void onAnimationEnd(Animation a) {
				indicator.init(desk.getDeskNum(), desk.getCurrent_desk_num());
			}
		});
		indicator.startAnimation(animation);
		
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_but:
			finish();
			break;

		default:
			break;
		}
	}
}
