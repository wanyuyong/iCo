package magic.yuyong.activity;

import java.util.ArrayList;
import java.util.List;

import magic.yuyong.R;
import magic.yuyong.app.AppConstant;
import magic.yuyong.app.MagicApplication;
import magic.yuyong.model.Twitter;
import magic.yuyong.view.FlipBoard;
import magic.yuyong.view.FlipBoardBg;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class CacheBoardActivity extends BaseActivity implements OnClickListener{
	private FlipBoard board;
	private FlipBoardBg board_bg;
	private TextView start_but;
	
	private BroadcastReceiver refreshCacheReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context arg0, Intent arg1) {
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		actionBar.hide();
		registerReceiver(refreshCacheReceiver, new IntentFilter(AppConstant.ACTION_REFRESH_CACHE));
		setContentView(R.layout.cache_board);
		board = (FlipBoard) findViewById(R.id.flip_board);
		board_bg = (FlipBoardBg) findViewById(R.id.flip_board_bg);
		board.setOnFlipListener(board_bg);
		List<Twitter> twitters = new ArrayList<Twitter>();
		board.setData(twitters);
		start_but = (TextView) findViewById(R.id.start_but);
		start_but.setOnClickListener(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(refreshCacheReceiver);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.start_but:
			finish();
			break;
		}
	}
}
