package magic.yuyong.activity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import magic.yuyong.R;
import magic.yuyong.adapter.CommentMeAdapter;
import magic.yuyong.adapter.MyPagerAdapter;
import magic.yuyong.adapter.TwitterListAdapter;
import magic.yuyong.app.AppConstant;
import magic.yuyong.app.MagicApplication;
import magic.yuyong.extend.FriendshipsAPI_E;
import magic.yuyong.extend.UnReadAPI;
import magic.yuyong.model.Comment;
import magic.yuyong.model.Group;
import magic.yuyong.model.Twitter;
import magic.yuyong.persistence.Persistence;
import magic.yuyong.request.RequestState;
import magic.yuyong.view.RefreshView;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.CommentsAPI;
import com.sina.weibo.sdk.openapi.legacy.FavoritesAPI;
import com.sina.weibo.sdk.openapi.legacy.StatusesAPI;
import com.sina.weibo.sdk.openapi.legacy.WeiboAPI;
import com.sina.weibo.sdk.openapi.legacy.WeiboAPI.AUTHOR_FILTER;
import com.sina.weibo.sdk.openapi.legacy.WeiboAPI.FEATURE;
import com.umeng.update.UmengUpdateAgent;

public class TimeLineModeActivity extends GetTwitterActivity implements RefreshView.Listener {

    private ViewPager mPager;
    private List<View> listViews;
    private List<Group> groups = new ArrayList<Group>();
    private boolean gettingGroups;

    private LayoutInflater mInflater;

    public static final int VIEW_BILATERAL = 0;
    public static final int VIEW_HOME = 1;
    public static final int VIEW_AT_ME = 2;
    public static final int VIEW_COMMENT = 3;
    private RequestState current;

    private boolean isHomeRequestAll = true;
    private long groupId;
    private FEATURE bilateralFeature = FEATURE.ALL;
    private AUTHOR_FILTER atMeFilter = AUTHOR_FILTER.ALL;
    private AUTHOR_FILTER commentFilter = AUTHOR_FILTER.ALL;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppConstant.MSG_UPDATA_GROUP:
                    if (current.requestType == VIEW_HOME) {
                        invalidateOptionsMenu();
                    }
                    break;
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

    private void getGroup() {
        gettingGroups = true;
        FriendshipsAPI_E friendshipsApi = new FriendshipsAPI_E(MagicApplication
                .getInstance().getAccessToken());
        friendshipsApi.group(new RequestListener() {

            @Override
            public void onIOException(IOException arg0) {
                gettingGroups = false;
            }

            @Override
            public void onError(WeiboException arg0) {
                gettingGroups = false;
            }

            @Override
            public void onComplete(String response) {
                gettingGroups = false;
                groups.addAll(Group.parseGroup(response));
                mHandler.sendEmptyMessage(AppConstant.MSG_UPDATA_GROUP);
            }

			@Override
			public void onComplete4binary(ByteArrayOutputStream responseOS) {
				
			}
        });
    }

    private void getTwitter(boolean refresh) {
        final RequestState requestState = current;
        if (!requestState.isRequest) {
            if (refresh || !requestState.isBottom) {
                requestState.isRequest = true;
                requestState.isRefresh = refresh;

                View tagView = listViews.get(requestState.requestType);
                ListView listView = (ListView) tagView
                        .findViewById(R.id.list_view);
                View footView = listView.findViewById(R.id.load_more);

                if (refresh) {
                    requestState.isBottom = false;
                    requestState.maxId = 0;
                    requestState.page = 1;
                    listView.setSelectionAfterHeaderView();
                } else {
                    footView.setVisibility(View.VISIBLE);
                }

                switch (requestState.requestType) {
                    case VIEW_BILATERAL:
                        StatusesAPI bilateralAPI = new StatusesAPI(MagicApplication
                                .getInstance().getAccessToken());
                        bilateralAPI.bilateralTimeline(0, requestState.maxId,
                                AppConstant.PAGE_NUM, requestState.page, false,
                                bilateralFeature, false,
                                new TwitterRequestListener(requestState));
                        break;

                    case VIEW_AT_ME:
                        StatusesAPI atMeAPI = new StatusesAPI(MagicApplication
                                .getInstance().getAccessToken());
                        atMeAPI.mentions(0, requestState.maxId,
                                AppConstant.PAGE_NUM, requestState.page,
                                atMeFilter, WeiboAPI.SRC_FILTER.ALL,
                                WeiboAPI.TYPE_FILTER.ALL, false,
                                new TwitterRequestListener(requestState));
                        break;

                    case VIEW_HOME:
                        if (isHomeRequestAll) {
                            StatusesAPI homeAPI = new StatusesAPI(MagicApplication
                                    .getInstance().getAccessToken());
                            homeAPI.homeTimeline(0, requestState.maxId,
                                    AppConstant.PAGE_NUM, requestState.page, false,
                                    WeiboAPI.FEATURE.ALL, false,
                                    new TwitterRequestListener(requestState));
                        } else {
                            FriendshipsAPI_E friendshipsApi = new FriendshipsAPI_E(
                                    MagicApplication.getInstance().getAccessToken());
                            friendshipsApi.groupTimeline(groupId, 0,
                                    requestState.maxId, AppConstant.PAGE_NUM,
                                    requestState.page, false, WeiboAPI.FEATURE.ALL,
                                    new TwitterRequestListener(requestState));
                        }

                        break;

                    case VIEW_COMMENT:
                        CommentsAPI commentsAPI = new CommentsAPI(MagicApplication
                                .getInstance().getAccessToken());
                        commentsAPI.toME(0, requestState.maxId,
                                AppConstant.PAGE_NUM, requestState.page,
                                commentFilter, WeiboAPI.SRC_FILTER.ALL,
                                new TwitterRequestListener(requestState));
                        break;
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.timeline, menu);
        MenuItem sort = menu.findItem(R.id.sort);
        SubMenu subMenu = null;
        if (sort != null) {
            subMenu = sort.getSubMenu();
        }
        if (current != null && subMenu != null) {
            switch (current.requestType) {
                case VIEW_BILATERAL:
                    subMenu.addSubMenu(R.id.sort, FEATURE.ALL.ordinal(),
                            FEATURE.ALL.ordinal(), R.string.but_all);
                    subMenu.addSubMenu(R.id.sort, FEATURE.ORIGINAL.ordinal(),
                            FEATURE.ORIGINAL.ordinal(), R.string.but_original);
                    break;
                case VIEW_HOME:
                    if (groups.size() != 0) {
                        for (int i = 0; i < groups.size(); i++) {
                            Group group = groups.get(i);

                            subMenu.addSubMenu(R.id.sort, i, i, group.getName());

                        }

                        subMenu.addSubMenu(R.id.sort, R.id.menu_group_all,
                                groups.size(),
                                getResources().getString(R.string.but_all));
                    }
                    break;
                case VIEW_AT_ME:
                case VIEW_COMMENT:
                    subMenu.addSubMenu(R.id.sort, AUTHOR_FILTER.ALL.ordinal(),
                            AUTHOR_FILTER.ALL.ordinal(), R.string.but_all);
                    subMenu.addSubMenu(R.id.sort,
                            AUTHOR_FILTER.ATTENTIONS.ordinal(),
                            AUTHOR_FILTER.ATTENTIONS.ordinal(),
                            R.string.but_attentions);
                    break;
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.post:
                Intent postIntent = new Intent(getApplicationContext(),
                        NewPostActivity.class);
                startActivity(postIntent);
                break;
            case R.id.refresh:
                RefreshView rf = (RefreshView) listViews.get(current.requestType);
                rf.refresh();
                break;
            case R.id.profile:
                Intent profileIntent = new Intent(getApplicationContext(),
                        ProfileActivity.class);
                profileIntent.putExtra("uid",
                        Persistence.getUID(getApplicationContext()));
                startActivity(profileIntent);
                break;
            case R.id.sort:
                if (current.requestType == VIEW_HOME && groups.size() == 0) {
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.text_loading),
                            Toast.LENGTH_SHORT).show();
                    if (!gettingGroups) {
                        getGroup();
                    }
                }
                break;
        }

        if (item.getGroupId() == R.id.sort) {
            RefreshView rf = (RefreshView) listViews.get(current.requestType);
            ListView listView = (ListView) rf.findViewById(R.id.list_view);
            if (current.requestType == VIEW_COMMENT) {
                HeaderViewListAdapter headAdapter = (HeaderViewListAdapter) listView
                        .getAdapter();
                CommentMeAdapter adapter = (CommentMeAdapter) headAdapter
                        .getWrappedAdapter();
                adapter.getComments().clear();
                adapter.notifyDataSetChanged();
            } else {
                HeaderViewListAdapter headAdapter = (HeaderViewListAdapter) listView
                        .getAdapter();
                TwitterListAdapter adapter = (TwitterListAdapter) headAdapter
                        .getWrappedAdapter();
                adapter.getData().clear();
                adapter.notifyDataSetChanged();
            }
            switch (current.requestType) {
                case VIEW_BILATERAL:
                    if (item.getItemId() == FEATURE.ALL.ordinal()) {
                        bilateralFeature = FEATURE.ALL;
                    } else if (item.getItemId() == FEATURE.ORIGINAL.ordinal()) {
                        bilateralFeature = FEATURE.ORIGINAL;
                    }
                    break;
                case VIEW_HOME:
                    int id = item.getItemId();
                    if (id == R.id.menu_group_all) {
                        isHomeRequestAll = true;
                    } else {
                        Group group = groups.get(id);
                        groupId = group.getId();
                        isHomeRequestAll = false;
                    }
                    break;
                case VIEW_AT_ME:
                    if (item.getItemId() == AUTHOR_FILTER.ALL.ordinal()) {
                        atMeFilter = AUTHOR_FILTER.ALL;
                    } else if (item.getItemId() == AUTHOR_FILTER.ATTENTIONS
                            .ordinal()) {
                        atMeFilter = AUTHOR_FILTER.ATTENTIONS;
                    }
                    break;
                case VIEW_COMMENT:
                    if (item.getItemId() == AUTHOR_FILTER.ALL.ordinal()) {
                        commentFilter = AUTHOR_FILTER.ALL;
                    } else if (item.getItemId() == AUTHOR_FILTER.ATTENTIONS
                            .ordinal()) {
                        commentFilter = AUTHOR_FILTER.ATTENTIONS;
                    }
                    break;
            }
            rf.refresh();
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkUpdate(){
        boolean checkUpdate = getIntent().getBooleanExtra("check_update", false);
        if(checkUpdate){
            UmengUpdateAgent.update(this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        checkUpdate();

        setContentView(R.layout.timeline_mode);
        mInflater = getLayoutInflater();
        initViewPager();

        Tab tabBilateral = actionBar.newTab().setText(R.string.label_bilateral);
        Tab tabHome = actionBar.newTab().setText(R.string.label_home);
        Tab tabAtme = actionBar.newTab().setText(R.string.label_at_me);
        Tab tabComment = actionBar.newTab().setText(R.string.label_comment);
        tabBilateral.setTabListener(listener);
        tabHome.setTabListener(listener);
        tabAtme.setTabListener(listener);
        tabComment.setTabListener(listener);

        int pos = getIntent().getIntExtra("pos", VIEW_HOME);
        if (pos == VIEW_AT_ME) {
            clearState(UnReadAPI.TYPE_MENTION_STATUS);
        } else if (pos == VIEW_COMMENT) {
            clearState(UnReadAPI.TYPE_CMT);
        }

        actionBar.addTab(tabBilateral, VIEW_BILATERAL, pos == VIEW_BILATERAL);
        actionBar.addTab(tabHome, VIEW_HOME, pos == VIEW_HOME);
        actionBar.addTab(tabAtme, VIEW_AT_ME, pos == VIEW_AT_ME);
        actionBar.addTab(tabComment, VIEW_COMMENT, pos == VIEW_COMMENT);

        getGroup();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        int pos = intent.getIntExtra("pos", VIEW_HOME);
        if (pos == VIEW_AT_ME) {
            clearState(UnReadAPI.TYPE_MENTION_STATUS);
        } else if (pos == VIEW_COMMENT) {
            clearState(UnReadAPI.TYPE_CMT);
        }

        if (current.requestType == pos) {
            RefreshView rf = (RefreshView) listViews.get(pos);
            rf.refresh();
        } else {
            current = (RequestState) listViews.get(pos).getTag();
            if (!current.isFirstTime) {
                RefreshView rf = (RefreshView) listViews.get(pos);
                rf.refresh();
            }
            mPager.setCurrentItem(pos);
        }
    }

    private void clearState(final int type) {
        UnReadAPI unReadAPI = new UnReadAPI(MagicApplication.getInstance().getAccessToken());
        unReadAPI.clear(new RequestListener() {

            @Override
            public void onIOException(IOException arg0) {
            }

            @Override
            public void onError(WeiboException arg0) {
            }

            @Override
            public void onComplete(String arg0) {
                if (type == UnReadAPI.TYPE_CMT) {
                    Persistence.setCmt(getApplicationContext(), 0);
                } else if (type == UnReadAPI.TYPE_MENTION_STATUS) {
                    Persistence.setMention_status(getApplicationContext(), 0);
                }
                sendBroadcast(new Intent(AppConstant.ACTION_UNREAD_STATE_CHANGE_BROADCAST));
            }

			@Override
			public void onComplete4binary(ByteArrayOutputStream responseOS) {
				
			}
        }, type);
    }

    private TabListener listener = new TabListener() {

        @Override
        public void onTabUnselected(Tab tab, FragmentTransaction ft) {

        }

        @Override
        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            int index = actionBar.getSelectedNavigationIndex();
            if(mPager.getCurrentItem() != index){
                mPager.setCurrentItem(index);
            }
            invalidateOptionsMenu();
        }

        @Override
        public void onTabReselected(Tab tab, FragmentTransaction ft) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onUpdate(RequestState requestState) {
        View tagView = listViews.get(requestState.requestType);
        RefreshView rf = (RefreshView) tagView;
        ListView listView = (ListView) tagView.findViewById(R.id.list_view);
        View footView = listView.findViewById(R.id.load_more);
        if (requestState.requestType == VIEW_COMMENT) {
            HeaderViewListAdapter headAdapter = (HeaderViewListAdapter) listView
                    .getAdapter();
            CommentMeAdapter adapter = (CommentMeAdapter) headAdapter
                    .getWrappedAdapter();
            if (requestState.isRefresh) {
                adapter.getComments().clear();
            }
            List<Comment> comments = Comment
                    .parseComment(requestState.response);
            if (comments.size() == 0) {
                requestState.isBottom = true;
                Toast.makeText(this,
                        getResources().getText(R.string.text_nomore_data),
                        Toast.LENGTH_SHORT).show();
            } else {
                adapter.getComments().addAll(comments);
                if (requestState.maxId == 0) {
                    requestState.maxId = comments.get(0).getId();
                }
                requestState.page++;
            }
            adapter.notifyDataSetChanged();
        } else {
            HeaderViewListAdapter headAdapter = (HeaderViewListAdapter) listView
                    .getAdapter();
            TwitterListAdapter adapter = (TwitterListAdapter) headAdapter
                    .getWrappedAdapter();
            List<Twitter> datas = adapter.getData();

            if (requestState.isRefresh) {
                datas.clear();
            }

            List<Twitter> twitters = Twitter
                    .parseTwitter(requestState.response);
            if (twitters.size() == 0) {
                requestState.isBottom = true;
                Toast.makeText(this,
                        getResources().getText(R.string.text_nomore_data),
                        Toast.LENGTH_SHORT).show();
            } else {
                while (twitters.size() != 0) {
                    Twitter twitter = twitters.remove(0);
                    if (!twitter.isDeleted()) {
                        datas.add(twitter);
                    }
                }
                if (requestState.maxId == 0) {
                    requestState.maxId = datas.get(0).getId();
                }
                requestState.page++;
            }
            adapter.notifyDataSetChanged();
        }
        rf.close();
        footView.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onError(RequestState requestState) {
        View tagView = listViews.get(requestState.requestType);
        ListView listView = (ListView) tagView.findViewById(R.id.list_view);
        View footView = listView.findViewById(R.id.load_more);
        RefreshView rf = (RefreshView) tagView;
        rf.close();
        footView.setVisibility(View.INVISIBLE);
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

    private Intent createShareIntent(String text) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");  //纯文本
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        return shareIntent;
    }

    private void prepareView(final View view, int view_type) {
        final ListView list_view = (ListView) view.findViewById(R.id.list_view);
        list_view.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           final int position, long id) {
                final Twitter twitter = (Twitter) list_view.getAdapter().getItem(position);
                HeaderViewListAdapter headAdapter = (HeaderViewListAdapter) list_view
                        .getAdapter();
                final TwitterListAdapter adapter = (TwitterListAdapter) headAdapter.getWrappedAdapter();
                adapter.setItemOnSelected(position);

                ActionMode.Callback callback = new ActionMode.Callback() {

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
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
                        ShareActionProvider actionProvider = (ShareActionProvider) actionItem.getActionProvider();
                        actionProvider.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);

                        String text = twitter.getText();
                        if (twitter.getOrigin() != null) {
                            text = text + "//" + twitter.getOrigin().getText();
                        }
                        actionProvider.setShareIntent(createShareIntent(text));
                        return true;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        Twitter twitter = (Twitter) list_view.getAdapter().getItem(position);
                        switch (item.getItemId()) {
                            case R.id.copy:
                                ClipboardManager cmb = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                String text = twitter.getText();
                                if (twitter.getOrigin() != null) {
                                    text = text + "//" + twitter.getOrigin().getText();
                                }
                                cmb.setText(text);
                                mode.finish();
                                Toast.makeText(getApplicationContext(), R.string.text_copy_success, Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.comment:
                                Intent commentIntent = new Intent(getApplicationContext(),
                                        NewPostActivity.class);
                                commentIntent.putExtra("type", AppConstant.TYPE_COMMENT);
                                commentIntent.putExtra("twitter_id", twitter.getId());
                                startActivity(commentIntent);
                                mode.finish();
                                break;
                            case R.id.forward:
                                Intent repostIntent = new Intent(getApplicationContext(),
                                        NewPostActivity.class);
                                repostIntent.putExtra("type", AppConstant.TYPE_REPOST);
                                repostIntent.putExtra("twitter_id", twitter.getId());
                                if (twitter.getOrigin() != null) {
                                    repostIntent.putExtra("initText",
                                            "//@" + twitter.getUser().getScreen_name() + ":"
                                                    + twitter.getText());
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
        TwitterListAdapter adapter = new TwitterListAdapter(this);
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

        RefreshView rf = (RefreshView) view;
        rf.setListener(this);

        RequestState requestState = new RequestState(view_type);
        view.setTag(requestState);
        setListScrollListener(list_view, requestState);
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

    private void prepareCommentView(final View view) {
        final ListView list_view = (ListView) view.findViewById(R.id.list_view);
        setFootView(list_view);
        CommentMeAdapter adapter = new CommentMeAdapter(this);
        list_view.setAdapter(adapter);
        adapter.addData(new ArrayList<Comment>());

        list_view.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Comment comment = (Comment) list_view.getAdapter().getItem(i);
                HeaderViewListAdapter headAdapter = (HeaderViewListAdapter) list_view
                        .getAdapter();
                final CommentMeAdapter adapter = (CommentMeAdapter) headAdapter.getWrappedAdapter();
                adapter.setItemOnSelected(i);

                ActionMode.Callback callback = new ActionMode.Callback() {

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return true;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
                        adapter.setItemOnSelected(TwitterListAdapter.INVALID_POSTION);
                    }

                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        MenuInflater inflater = mode.getMenuInflater();
                        inflater.inflate(R.menu.action_mode_timeline_comment, menu);
                        return true;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.reply:
                                Intent commentIntent = new Intent(
                                        getApplicationContext(),
                                        NewPostActivity.class);
                                commentIntent.putExtra("twitter_id", comment.getTwitter().getId());
                                commentIntent.putExtra("cid", comment.getId());
                                commentIntent.putExtra("cu", comment.getUser().getScreen_name());
                                commentIntent.putExtra("cc", comment.getText());
                                commentIntent.putExtra("type", AppConstant.TYPE_REPLY_COMMENT);
                                startActivity(commentIntent);
                                break;
                            case R.id.show:
                                Intent intent = new Intent(getApplicationContext(),
                                        TwitterShowActivity.class);
                                Twitter twitter = comment.getTwitter();
                                intent.putExtra("twitter_id", twitter.getId());
                                startActivity(intent);
                                break;
                        }
                        return false;
                    }
                };
                startActionMode(callback);
            }
        });

        RefreshView rf = (RefreshView) view;
        rf.setListener(this);

        RequestState requestState = new RequestState(VIEW_COMMENT);
        view.setTag(requestState);
        setListScrollListener(list_view, requestState);
    }

    private void initViewPager() {
        mPager = (ViewPager) findViewById(R.id.vPager);
        mPager.setBackgroundColor(Color.WHITE);
        mPager.setOffscreenPageLimit(3);
        listViews = new ArrayList<View>();

        View bilateralList = mInflater.inflate(R.layout.timeline_list, null);
        prepareView(bilateralList, VIEW_BILATERAL);
        listViews.add(bilateralList);

        View homeList = mInflater.inflate(R.layout.timeline_list, null);
        prepareView(homeList, VIEW_HOME);
        listViews.add(homeList);

        View mentionsList = mInflater.inflate(R.layout.timeline_list, null);
        prepareView(mentionsList, VIEW_AT_ME);
        listViews.add(mentionsList);

        View commentMeList = mInflater.inflate(R.layout.timeline_list, null);
        prepareCommentView(commentMeList);
        listViews.add(commentMeList);

        mPager.setOnPageChangeListener(new MyOnPageChangeListener());
        mPager.setAdapter(new MyPagerAdapter(listViews));
    }

    public class MyOnPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int pos) {
            View view = listViews.get(pos);
            current = (RequestState) view.getTag();
            if (current.isFirstTime) {
                current.isFirstTime = false;
                getTwitter(false);
            }
            if(actionBar.getSelectedNavigationIndex() != pos){
                actionBar.setSelectedNavigationItem(pos);
            }
        }
    }

    private void setFootView(ListView list_view) {
        View foot_view = mInflater.inflate(R.layout.loadmorelayout, null);
        foot_view.setVisibility(View.VISIBLE);
        list_view.addFooterView(foot_view);
    }

    @Override
    public void onRefresh() {
        getTwitter(true);
    }

}
