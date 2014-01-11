package magic.yuyong.activity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import magic.yuyong.R;
import magic.yuyong.adapter.TwitterListAdapter;
import magic.yuyong.app.AppConstant;
import magic.yuyong.app.MagicApplication;
import magic.yuyong.extend.StatusesAPI_E;
import magic.yuyong.model.Twitter;
import magic.yuyong.request.RequestState;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.FavoritesAPI;
import com.sina.weibo.sdk.openapi.legacy.WeiboAPI.FEATURE;

public class PlazaActivity extends GetTwitterActivity implements
		OnRefreshListener{

	private ListView list_view;
	private View foot_view;
	private PullToRefreshLayout mPullToRefreshLayout;
	private TwitterListAdapter adapter;
	private RequestState requestState;

	private int type;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case AppConstant.MSG_FAVORITE_SUCCEED:
				Toast.makeText(getApplicationContext(),
						getResources().getString(R.string.text_fav_succeed),
						Toast.LENGTH_SHORT).show();
				break;
			case AppConstant.MSG_FAVORITE_CANCEL_SUCCEED:
				Toast.makeText(
						getApplicationContext(),
						getResources().getString(
								R.string.text_fav_cancel_succeed),
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		actionBar.setListNavigationCallbacks(new TabAdapter(),
				new OnNavigationListener() {

					@Override
					public boolean onNavigationItemSelected(int itemPosition,
							long itemId) {
						if (itemPosition == type) {
							return true;
						}
						type = itemPosition;
						requestState = new RequestState(type);
						if(adapter.getCount() == 0){
							getTwitter(false);
						}else{
							mPullToRefreshLayout.startRefresh();
						}
						return true;
					}
				});

		setContentView(R.layout.timeline_list);

		type = getIntent().getIntExtra("type", AppConstant.TYPE_BEAUTY);

		init();

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
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

	private Intent createShareIntent(String text) {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_TEXT, text);
		return shareIntent;
	}

	private void setFootView(ListView list_view) {
		foot_view = View.inflate(this, R.layout.loadmorelayout, null);
		foot_view.setVisibility(View.VISIBLE);
		list_view.addFooterView(foot_view);
	}

	private void changeFavorite(final boolean favorite, final Twitter twitter) {
		FavoritesAPI favoritesAPI = new FavoritesAPI(MagicApplication
				.getInstance().getAccessToken());
		RequestListener listener = new RequestListener() {

			@Override
			public void onIOException(IOException arg0) {
			}

			@Override
			public void onError(WeiboException arg0) {
			}

			@Override
			public void onComplete(String response) {
				android.os.Message msg = new android.os.Message();
				msg.what = favorite ? AppConstant.MSG_FAVORITE_SUCCEED
						: AppConstant.MSG_FAVORITE_CANCEL_SUCCEED;
				mHandler.sendMessage(msg);
				twitter.setFavorited(favorite);
			}

			@Override
			public void onComplete4binary(ByteArrayOutputStream responseOS) {

			}
		};
		if (!favorite) {
			favoritesAPI.destroy(twitter.getId(), listener);
		} else {
			favoritesAPI.create(twitter.getId(), listener);
		}
	}

	class TabAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return AppConstant.TYPE_MUSIC + 1;
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int pos) {
			return pos;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.plaza_tab_item, null);
			}
			String name = getResources().getStringArray(R.array.type_name)[position];
			TextView tv = (TextView) convertView.findViewById(R.id.type_name);
			tv.setText(name);
			int paddingLeft = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics());
			tv.setPadding(paddingLeft, 0, paddingLeft, 0);

			return convertView;
		}
		
		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.plaza_tab_item, null);
			}
			String name = getResources().getStringArray(R.array.type_name)[position];
			TextView tv = (TextView) convertView.findViewById(R.id.type_name);
			tv.setText(name);

			return convertView;
		}

	}

	private void init() {

		list_view = (ListView) findViewById(R.id.list_view);
		list_view.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				final Twitter twitter = (Twitter) list_view.getAdapter()
						.getItem(position);
				HeaderViewListAdapter headAdapter = (HeaderViewListAdapter) list_view
						.getAdapter();
				final TwitterListAdapter adapter = (TwitterListAdapter) headAdapter
						.getWrappedAdapter();
				adapter.setItemOnSelected(position);

				ActionMode.Callback callback = new ActionMode.Callback() {

					@Override
					public boolean onPrepareActionMode(ActionMode mode,
							Menu menu) {
						MenuItem actionItem = menu.findItem(R.id.favorite);
						if (twitter.isFavorited()) {
							actionItem.setIcon(R.drawable.rating_important);
						} else {
							actionItem.setIcon(R.drawable.rating_not_important);
						}
						return true;
					}

					@Override
					public void onDestroyActionMode(ActionMode mode) {
						adapter.setItemOnSelected(TwitterListAdapter.INVALID_POSTION);
					}

					@Override
					public boolean onCreateActionMode(ActionMode mode, Menu menu) {
						MenuInflater inflater = mode.getMenuInflater();
						inflater.inflate(R.menu.action_mode_timeline, menu);

						MenuItem actionItem = menu.findItem(R.id.share);
						ShareActionProvider actionProvider = (ShareActionProvider) actionItem
								.getActionProvider();
						actionProvider
								.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);

						String text = twitter.getText();
						if (twitter.getOrigin() != null) {
							text = text + "//" + twitter.getOrigin().getText();
						}
						actionProvider.setShareIntent(createShareIntent(text));
						return true;
					}

					@Override
					public boolean onActionItemClicked(ActionMode mode,
							MenuItem item) {
						Twitter twitter = (Twitter) list_view.getAdapter()
								.getItem(position);
						switch (item.getItemId()) {
						case R.id.copy:
							ClipboardManager cmb = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
							String text = twitter.getText();
							if (twitter.getOrigin() != null) {
								text = text + "//"
										+ twitter.getOrigin().getText();
							}
							cmb.setText(text);
							mode.finish();
							Toast.makeText(getApplicationContext(),
									R.string.text_copy_success,
									Toast.LENGTH_SHORT).show();
							break;
						case R.id.comment:
							Intent commentIntent = new Intent(
									getApplicationContext(),
									NewPostActivity.class);
							commentIntent.putExtra("type",
									AppConstant.TYPE_COMMENT);
							commentIntent.putExtra("twitter_id",
									twitter.getId());
							startActivity(commentIntent);
							mode.finish();
							break;
						case R.id.forward:
							Intent repostIntent = new Intent(
									getApplicationContext(),
									NewPostActivity.class);
							repostIntent.putExtra("type",
									AppConstant.TYPE_REPOST);
							repostIntent.putExtra("twitter_id", twitter.getId());
							if (twitter.getOrigin() != null) {
								repostIntent.putExtra("initText", "//@"
										+ twitter.getUser().getScreen_name()
										+ ":" + twitter.getText());
							}
							startActivity(repostIntent);
							mode.finish();
							break;
						case R.id.favorite:
							changeFavorite(!twitter.isFavorited(), twitter);
							mode.finish();
							break;
						}
						return false;
					}
				};
				startActionMode(callback);
				return true;
			}
		});

		setFootView(list_view);
		adapter = new TwitterListAdapter(this);
		list_view.setAdapter(adapter);
		adapter.setData(new ArrayList<Twitter>());
		list_view.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long arg3) {
				TwitterListAdapter.ViewHolder holder = (TwitterListAdapter.ViewHolder) view
						.getTag();
				if (holder != null) {
					Twitter twitter = holder.twitter;
					if (twitter.getId() != 0) {
						Intent intent = new Intent(getApplicationContext(),
								TwitterShowActivity.class);
						intent.putExtra("twitter", twitter);
						startActivity(intent);
					}
				}
			}
		});

		mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);
		ActionBarPullToRefresh.from(this)
        .theseChildrenArePullable(R.id.list_view, android.R.id.empty)
        .listener(this)
        .setup(mPullToRefreshLayout);

		RequestState requestState = new RequestState(type);
		setListScrollListener(list_view, requestState);
	}

	private void setListScrollListener(ListView listView,
			final RequestState requestState) {
		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
						getTwitter(false);
					}
				}
			}

			@Override
			public void onScroll(AbsListView arg0, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
	}

	private void getTwitter(boolean refresh) {
		if (!requestState.isRequest) {
			if (refresh || !requestState.isBottom) {
				requestState.isRequest = true;
				requestState.isRefresh = refresh;

				if (refresh) {
					requestState.isBottom = false;
					requestState.maxId = 0;
					requestState.page = 1;
					list_view.setSelectionAfterHeaderView();
				} else {
					foot_view.setVisibility(View.VISIBLE);
				}

				String uids = null;
				switch (requestState.requestType) {
				case AppConstant.TYPE_BEAUTY:
					uids = AppConstant.UIDS_BEAUTY;
					break;

				case AppConstant.TYPE_TRAVEL:
					uids = AppConstant.UIDS_TRAVEL;
					break;
					
				case AppConstant.TYPE_ENTERTAINMENT:
					uids = AppConstant.UIDS_ENTERTAINMENT;
					break;
					
				case AppConstant.TYPE_FUNNY:
					uids = AppConstant.UIDS_FUNNY;
					break;
					
				case AppConstant.TYPE_CONSTELLATION:
					uids = AppConstant.UIDS_CONSTELLATION;
					break;
					
				case AppConstant.TYPE_EMOTION:
					uids = AppConstant.UIDS_EMOTION;
					break;
					
				case AppConstant.TYPE_CARS:
					uids = AppConstant.UIDS_CARS;
					break;
					
				case AppConstant.TYPE_FOOD:
					uids = AppConstant.UIDS_FOOD;
					break;
					
				case AppConstant.TYPE_MUSIC:
					uids = AppConstant.UIDS_MUSIC;
					break;
				}

				StatusesAPI_E api = new StatusesAPI_E(MagicApplication
						.getInstance().getAccessToken());
				api.timelineBatch(uids, AppConstant.PAGE_NUM,
						requestState.page, false, FEATURE.PICTURE,
						new TwitterRequestListener(requestState));

			}
		}
	}

	@Override
	protected void onUpdate(RequestState requestState) {
		List<Twitter> datas = adapter.getData();
		if (requestState.isRefresh) {
			datas.clear();
		}

		List<Twitter> twitters = Twitter.parseTwitter(requestState.response);
		if (twitters.size() == 0) {
			requestState.isBottom = true;
		} else {
			while (twitters.size() != 0) {
				Twitter twitter = twitters.remove(0);
				if (!twitter.isDeleted()) {
					datas.add(twitter);
				}
			}
			requestState.page++;
		}
		adapter.notifyDataSetChanged();
		mPullToRefreshLayout.setRefreshComplete();
		foot_view.setVisibility(View.INVISIBLE);
	}

	@Override
	protected void onError(RequestState requestState) {
		foot_view.setVisibility(View.INVISIBLE);
		mPullToRefreshLayout.setRefreshComplete();
	}

	@Override
	public void onRefreshStarted(View view) {
		getTwitter(true);
	}

	@Override
	protected void onRequestComplete(RequestState requestState) {
		
	}
}
