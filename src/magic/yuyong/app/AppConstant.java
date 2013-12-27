package magic.yuyong.app;

public class AppConstant {
	public static final int PAGE_NUM = 40;
	public static final int GET_UNREAD_PERIOD = 60 * 1000;

	public static final String DONATE_HOME = "https://me.alipay.com/wanyuyong";

	public static final int NORMAL_MODE = 0;
	public static final int EXTEND_MODE = 1;

	public static final String SUFFIX_WIDE = "wide";
	public static final String SUFFIX_NARROW = "narrow";

	// 0：娱乐、1：搞笑、2：美女、3：旅行、4：星座、5：各种萌、6：时尚、7：名车、8：美食、9：音乐。
	public static final int TYPE_ENTERTAINMENT = 0;
	public static final int TYPE_FUNNY = 1;
	public static final int TYPE_BEAUTY = 2;
	public static final int TYPE_TRAVEL = 3;
	public static final int TYPE_CONSTELLATION = 4;
	public static final int TYPE_MENG = 5;
	public static final int TYPE_FASHION = 6;
	public static final int TYPE_CARS = 7;
	public static final int TYPE_FOOD = 8;
	public static final int TYPE_MUSIC = 9;

	/**
	 * 南都娱乐周刊 id : 1216431741 新浪音乐 id : 1266269835 搜狐娱乐 id : 1843633441 韩流图库 id
	 * : 2665043382 韩国思蜜达 id : 2244525523 韩国me2day id : 2482557597 娱乐新闻快报 id :
	 * 1870252767 新浪娱乐 id : 1642591402 欧美音悦Tai id : 1834100153 日韩潮风尚 id :
	 * 2570481764 蔚蓝日本娱乐播报 id : 1237743061
	 */
	public static final String UIDS_ENTERTAINMENT = "1216431741,1266269835,1843633441,2665043382,2244525523,2482557597,1870252767,1642591402,1834100153,2570481764,1237743061";

	/**
	 * 中国美女报告 id : 1666559280 中国美腿 id : 2317382462 美腿团 id : 2716523850 全球美女研究 id
	 * : 1497246097 美女写真照 id : 1669899263 美女热门榜 id : 3858679729 女神阁 id :
	 * 3075537743 美女美图营 id : 3814895924 全球时尚性感美女 id : 2740378344 校园美女大本营 id :
	 * 2393383161 厦门校花 id : 2731172954 JK制服馆 id : 2328441837 美女胸 id : 1763276065
	 * 薇美人__ id : 2757037655
	 */
	public static final String UIDS_BEAUTY = "1666559280,2317382462,2716523850,1497246097,1669899263,3858679729,3075537743,3814895924,2740378344,2393383161,2731172954,2328441837,1763276065,2757037655";

	/**
	 * 雷永辉 id : 1074898101 冰城馨子 id : 1235305362 菜尾蝗-旅游养生禅 id : 1240183873 七月娃娃
	 * id : 1489734185 弹指间行摄 id : 1250590793 简白 id : 1272394180 徐铁人 id :
	 * 1255082647 七色地图 id : 1236937620
	 */
	public static final String UIDS_TRAVEL = "1074898101,1235305362,1240183873,1489734185,1250590793,1272394180,1255082647,1236937620";

	// Post type
	public static final int TYPE_POST_TEXT = 0;
	public static final int TYPE_POST_TEXT_IMG = 1;
	public static final int TYPE_REPOST = 2;
	public static final int TYPE_COMMENT = 3;
	public static final int TYPE_REPLY_COMMENT = 4;

	// handler msg
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
	public static final int MSG_LOGIN_OUT_SUCCEED = 19;
	public static final int MSG_LOGIN_OUT_FAILD = 20;
	public static final int MSG_SAVE_PIC_SUCCEED = 21;
	public static final int MSG_SAVE_PIC_FAILD = 22;
	public static final int MSG_SHOW_GIF = 23;
	public static final int MSG_DOWN_PERCENTAGE = 24;
	public static final int MSG_DOWN_BEGIN = 25;
	public static final int MSG_DOWN_END = 26;
	public static final int MSG_SHOW_REPOSTS = 27;
	public static final int MSG_SHOW_POST_PIC = 28;

	// broadcast
	public static final String ACTION_REFRESH_COMMENT = "action_refresh_comment";
	public static final String ACTION_REFRESH_CACHE = "action_refresh_cache";
	public static final String ACTION_UNREAD_STATE_CHANGE_BROADCAST = "action_unread_state_change_broadcast";
	public static final String ACTION_SHUT_DOWN_BROADCAST = "action_shut_down_broadcast";

	// Sina
	public static final String CONSUMER_KEY = "577780336";
	public static final String CONSUMER_SECRET = "534de6e04d0ae028f956b21707bc5dda";
	public static final String REDIRECT_URL = "http://weibo.com/u/1069103203";
	public static final String SCOPE = "all";

	// domob
	public static final String DOMOB_KEY = "56OJzndIuMkKkAVNWU";
}
