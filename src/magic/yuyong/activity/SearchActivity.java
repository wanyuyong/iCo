//package magic.yuyong.activity;
//
//import java.io.IOException;
//import java.util.List;
//
//import magic.yuyong.R;
//import magic.yuyong.adapter.SearchUserAdapter;
//import magic.yuyong.app.AppConstant;
//import magic.yuyong.model.SearchUser;
//import magic.yuyong.util.Debug;
//import magic.yuyong.util.WeiBoTool;
//import android.content.Intent;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.os.Handler;
//import android.view.Gravity;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.EditText;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.weibo.net.AsyncWeiboRunner;
//import com.weibo.net.Utility;
//import com.weibo.net.Weibo;
//import com.weibo.net.WeiboException;
//import com.weibo.net.WeiboParameters;
//
//public class SearchActivity extends BaseActivity implements
//		OnClickListener, AsyncWeiboRunner.RequestListener {
//	private ListView list_view;
//	private View ok_but;
//	private EditText search_text;
//	private SearchUserAdapter adapter;
//
//	private boolean isRequestServer;
//
//	private Handler handler = new Handler() {
//
//		@Override
//		public void handleMessage(android.os.Message msg) {
//			switch (msg.what) {
//			case AppConstant.MSG_UPDATE_VIEW:
//				List<SearchUser> users = SearchUser.parseUsers(msg.obj.toString());
//				adapter.changeData(users);
//				if(users.size() == 0){
//					Toast.makeText(getApplicationContext(), 
//							getResources().getText(R.string.text_search_no_data), Toast.LENGTH_SHORT)
//							.show();
//				}
//				adapter.notifyDataSetChanged();
//				break;
//			case AppConstant.MSG_NETWORK_EXCEPTION:
//				Toast.makeText(
//						getApplicationContext(),
//						getResources().getString(
//								R.string.text_network_exception),
//						Toast.LENGTH_SHORT).show();
//				break;
//			case AppConstant.MSG_PLEASE_LOGIN:
//				WeiBoTool.unAuthorization(getApplicationContext());
//				Toast.makeText(getApplicationContext(),
//						getResources().getString(R.string.text_please_login),
//						Toast.LENGTH_SHORT).show();
//				finish();
//				break;
//			case AppConstant.MSG_TOKEN_EXPIRED:
//				WeiBoTool.unAuthorization(getApplicationContext());
//				Toast.makeText(getApplicationContext(),
//						getResources().getString(R.string.text_token_expired),
//						Toast.LENGTH_SHORT).show();
//				finish();
//				break;
//			case AppConstant.MSG_INVALID_ACCESS_TOKEN:
//				WeiBoTool.unAuthorization(getApplicationContext());
//				Toast.makeText(
//						getApplicationContext(),
//						getResources().getString(
//								R.string.text_invalid_access_token),
//						Toast.LENGTH_SHORT).show();
//				finish();
//				break;
//			case AppConstant.MSG_NO_DATA:
//				Toast.makeText(getApplicationContext(),
//						getResources().getString(R.string.text_no_data),
//						Toast.LENGTH_SHORT).show();
//				finish();
//				break;
//			}
//			isRequestServer = false;
//		}
//	};
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.search);
//		search_text = (EditText) findViewById(R.id.search_text);
//		list_view = (ListView) findViewById(R.id.list_view);
//		TextView footerView = new TextView(getApplicationContext());
//		footerView.setText(R.string.text_search_prompt);
//		footerView.setTextColor(0XFF888888);
//		footerView.setTextSize(15);
//		footerView.setGravity(Gravity.CENTER);
//		footerView.setPadding(15, 15, 15, 15);
//		list_view.addFooterView(footerView);
//		ok_but = findViewById(R.id.ok_but);
//		ok_but.setOnClickListener(this);
//
//		adapter = new SearchUserAdapter(getApplicationContext());
//		list_view.setAdapter(adapter);
//		list_view.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position,
//					long id) {
//				Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
//				i.putExtra("uid", String.valueOf(id));
//				startActivity(i);
//			}
//		});
//	}
//
//	class SearchTask extends AsyncTask<Void, Void, Void> {
//
//		@Override
//		protected Void doInBackground(Void... p) {
//			if (!isRequestServer) {
//				isRequestServer = true;
//				Weibo weibo = Weibo.getInstance();
//				String url = Weibo.SERVER + "search/suggestions/users.json";
//				AsyncWeiboRunner weiboRunner = new AsyncWeiboRunner(weibo);
//				WeiboParameters bundle = new WeiboParameters();
//				bundle.add("access_token", weibo.getAccessToken().getToken());
//				bundle.add("count", String.valueOf(50));
//				String q = search_text.getText().toString();
//				bundle.add("q", q);
//				weiboRunner.request(getApplicationContext(), url, bundle,
//						Utility.HTTPMETHOD_GET, SearchActivity.this);
//			}
//			return null;
//		}
//	}
//
//	@Override
//	public void onComplete(String response) {
//		android.os.Message msg = new android.os.Message();
//		msg.what = AppConstant.MSG_UPDATE_VIEW;
//		msg.obj = response;
//		handler.sendMessage(msg);
//	}
//
//	@Override
//	public void onIOException(IOException e) {
//		Debug.e("IOException : " + e.getMessage());
//		handler.sendEmptyMessage(AppConstant.MSG_NETWORK_EXCEPTION);
//	}
//
//	@Override
//	public void onError(WeiboException e) {
//		Debug.e("WeiboException : code = " + e.getStatusCode() + " , msg = "
//				+ e.getMessage());
//		switch (e.getStatusCode()) {
//		case -1:
//			handler.sendEmptyMessage(AppConstant.MSG_NETWORK_EXCEPTION);
//			break;
//		case 10025:
//			handler.sendEmptyMessage(AppConstant.MSG_NO_DATA);
//			break;
//		case 21327:
//			handler.sendEmptyMessage(AppConstant.MSG_TOKEN_EXPIRED);
//			break;
//		case 21332:
//			handler.sendEmptyMessage(AppConstant.MSG_INVALID_ACCESS_TOKEN);
//			break;
//		}
//	}
//
//	@Override
//	public void onClick(View view) {
//		switch (view.getId()) {
//		case R.id.ok_but:
//			new SearchTask().execute();
//			break;
//		}
//	}
//
//}
