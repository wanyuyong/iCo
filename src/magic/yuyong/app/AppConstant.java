package magic.yuyong.app;

public class AppConstant {
	public static final int PAGE_NUM = 40;
	public static final int GET_UNREAD_PERIOD = 60*1000;
	
	public static final String DONATE_HOME = "https://me.alipay.com/wanyuyong";
	
	public static final int NORMAL_MODE = 0;
	public static final int EXTEND_MODE = 1;
	
	public static final String SUFFIX_WIDE = "wide";
	public static final String SUFFIX_NARROW = "narrow";
	
	//1：娱乐、2：搞笑、3：美女、4：视频、5：星座、6：各种萌、7：时尚、8：名车、9：美食、10：音乐。
	public static final int TYPE_ENTERTAINMENT = 1;
	public static final int TYPE_FUNNY = 2;
	public static final int TYPE_BEAUTY = 3;
	public static final int TYPE_VIDEO = 4;
	public static final int TYPE_CONSTELLATION = 5;
	public static final int TYPE_MENG = 6;
	public static final int TYPE_FASHION = 7;
	public static final int TYPE_CARS = 8;
	public static final int TYPE_FOOD = 9; 
	public static final int TYPE_MUSIC = 10;
	
	//Post type
	public static final int TYPE_POST_TEXT = 0;
	public static final int TYPE_POST_TEXT_IMG = 1;
	public static final int TYPE_REPOST = 2;
	public static final int TYPE_COMMENT = 3;
	public static final int TYPE_REPLY_COMMENT = 4;
	
	//handler msg
	public static final int MSG_UPDATE_VIEW = 1;
	public static final int MSG_NETWORK_EXCEPTION = 2;
	public static final int MSG_TOKEN_EXPIRED = 3;
	public static final int MSG_SHOW_TWITTER = 4;
	public static final int MSG_FAVORITE_SUCCEED = 5;
	public static final int MSG_FAVORITE_CANCEL_SUCCEED = 6;
	public static final int MSG_SHOW_COMMENTS = 7;
	public static final int MSG_DELETE_SUCCEED = 8;
	public static final int MSG_DELETE_FAILD = 9;
	public static final int MSG_FOLLOW_SUCCEED = 10;
	public static final int MSG_FOLLOW_FAILD = 11;
	public static final int MSG_UPDATA_GROUP = 12;
	public static final int MSG_POST_SUCCEED = 13;
	public static final int MSG_POST_FAILD = 14;
	public static final int MSG_UPDATA_FRIENDS = 15;
	public static final int MSG_UPDATA_SEARCH_FRIENDS = 16;
	public static final int MSG_UNFOLLOW_SUCCEED = 17;
	public static final int MSG_UNFOLLOW_FAILD = 18;
	public static final int MSG_LOGIN_OUT_SUCCEED= 19;
	public static final int MSG_LOGIN_OUT_FAILD= 20;
	public static final int MSG_SAVE_PIC_SUCCEED= 21;
	public static final int MSG_SAVE_PIC_FAILD= 22;
	public static final int MSG_SHOW_GIF= 23;
	public static final int MSG_DOWN_PERCENTAGE= 24;
	public static final int MSG_DOWN_BEGIN= 25;
	public static final int MSG_DOWN_END= 26;
    public static final int MSG_SHOW_REPOSTS= 27;
    public static final int MSG_SHOW_POST_PIC= 28;
	
	//broadcast
	public static final String ACTION_REFRESH_COMMENT = "action_refresh_comment";
	public static final String ACTION_REFRESH_CACHE = "action_refresh_cache";
	public static final String ACTION_UNREAD_STATE_CHANGE_BROADCAST = "action_unread_state_change_broadcast";
	public static final String ACTION_SHUT_DOWN_BROADCAST = "action_shut_down_broadcast";
	
	//Sina
	public static final String CONSUMER_KEY = "577780336";
	public static final String CONSUMER_SECRET = "534de6e04d0ae028f956b21707bc5dda";
	public static final String REDIRECT_URL = "http://weibo.com/u/1069103203";
    public static final String SCOPE =  "all";
	
	//domob
	public static final String DOMOB_KEY = "56OJzndIuMkKkAVNWU";
}
