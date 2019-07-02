package androidnews.kiloproject.system;

import java.util.UUID;

/**
 * Created by ct_OS on 2018-1-1.
 */

public class AppConfig {
    public static boolean isHighRam,
            isSwipeBack,
            isNightMode,
            isAutoRefresh,
            isAutoLoadMore,
            isStatusBar,
            isDisNotice,
            isBackExit,
            isPush,
            isPushSound,
            isEasterEggs;
    public static int listType,pushTime;

    public static int mTextSize = 1;

    public static final int TYPE_NETEASE_START = 0;
    public static final int TYPE_NETEASE_END = 49;
    public static final int TYPE_NETEASE_END_USED = 48;

    public static final int TYPE_ZHIHU = 50;
    public static final int TYPE_GUOKR = 51;
    public static final int TYPE_OTHER_END = 69;
    public static final int TYPE_OTHER_END_USED = 51;

    public static final int TYPE_VIDEO_START = 70;
    public static final int TYPE_VIDEO_END = 89;
    public static final int TYPE_VIDEO_END_USED = 73;

    public static final int TYPE_ITHOME_START = 90;
    public static final int TYPE_ITHOME_END = 124;
    public static final int TYPE_ITHOME_END_USED = 116;

    public static final int TYPE_PRESS_START = 125;
    public static final int TYPE_PRESS_END = 199;
    public static final int TYPE_PRESS_END_USED = 176;

    public static final int TYPE_SMARTISAN_START = 200;
    public static final int TYPE_SMARTISAN_END = 208;
    public static final int TYPE_SMARTISAN_END_USED = 214;

    //网易
    public static final String HOST_163 = "http://c.m.163.com";
    public static final String HOST_163_COMMENT = "http://comment.api.163.com";

    public static final String GET_NEWS_DETAIL = "nc/article/{docid}/full.html";

    public static final String GET_MAIN_DATA = "/nc/article/headline/{typeStr}/{currentPage}-20.html";

    public static final String GET_NEWS_COMMENT = "/api/json/post/list/new/normal/{board}/{docid}/desc/0/20/10/2/2";

    public static final String GET_VIDEOS = "/nc/video/list/{typeStr}/n/{currentPage}-20.html";

    public static final String NEWS_PHOTO_URL = "http://pic.news.163.com/photocenter/api/list/0001/00AN0001,00AO0001,00AP0001/0/10/cacheMoreData.json";

    //知乎
    public static final String HOST_ZHIHU = "http://news-at.zhihu.com/api/4/news";
    public static final String GET_ZHIHU_REFRESH = "/latest";
    public static final String GET_ZHIHU_LOAD_MORE = "/before/";

    //果壳
    public static final String HOST_GUO_KR = "http://apis.guokr.com";
    public static final String HOST_GUO_KR_DETAIL = "http://jingxuan.guokr.com";

    public static final String GET_GUO_KR_LIST = "/handpick/v2/article.json?retrieve_type=by_offset&limit=20&ad=1&offset=";
    public static final String GET_GUO_KR_TOP = "/flowingboard/item/handpick_carousel.json";
    public static final String GET_GUO_KR_DETAIL = "/pick/v2/{newsId}/";

    //IT之家
    public static final String HOST_IT_HOME = "http://api.ithome.com";
    public static final String HOST_IT_HOME_URL = "https://www.ithome.com";

    public static final String GET_IT_HOME_REFRESH = "/xml/newslist/{typeStr}.xml";
    public static final String GET_IT_HOME_LOAD_MORE = "/xml/newslist/{typeStr}_{lastItemId}.xml";
    public static final String GET_IT_HOME_DETAIL = "/xml/newscontent/{newsId}.xml";

    //锤子阅读
    public static final String HOST_SMARTISAN = "http://reader.smartisan.com";
    public static final String HOST_SMARTISAN_REFRESH = "/index.php?r=find/GetArticleList&cate_id={cate_id}&art_id=&page_size=20";
    public static final String HOST_SMARTISAN_LOAD_MORE = "/index.php?r=find/GetArticleList&cate_id={cate_id}&art_id={last_id}&page_size=20";

    //更新
    public static final String CHECK_UPADTE_ADDRESS = "https://raw.githubusercontent.com/kilo-swodh/MereNews/master/update.txt";

    public static final String DOWNLOAD_ADDRESS = "https://www.pgyer.com/android_news";

    public static final String BUGLY_KEY = "mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" +
            "dfXHsJjgt5dX_ma5KylHFi60LZmFsuLv";

    //常量
    public static final String CONFIG_LANGUAGE = "config_language";

    public static final String CONFIG_AUTO_REFRESH = "config_auto_refresh";

    public static final String CONFIG_AUTO_LOADMORE = "config_auto_loadmore";

    public static final String CONFIG_BACK_EXIT = "config_back_exit";

    public static final String CONFIG_SWIPE_BACK = "config_swipe_back";

    public static final String CONFIG_HIGH_RAM = "config_high_ram";

    public static final String CONFIG_DISABLE_NOTICE = "config_dis_notice";

    public static final String CONFIG_STATUS_BAR = "config_status_bar";

    public static final String CONFIG_NIGHT_MODE = "config_night_mode";

    public static final String CONFIG_RANDOM_HEADER = "config_random_header_int";

    public static final String CONFIG_TEXT_SIZE = "config_text_size";

    public static final String CONFIG_TYPE_ARRAY = "config_type_array";

    public static final String CONFIG_TYPE_SORT = "config_type_sort";

    public static final String CONFIG_HEADER_COLOR = "config_header_color";

    public static final String CONFIG_LIST_TYPE = "config_list_type";

    public static final String CONFIG_LAST_LAUNCH = "config_last_launch";  //设置重置

    public static final String CONFIG_PUSH = "config_push";

    public static final String CONFIG_PUSH_TIME = "config_push_time";

    public static final String CONFIG_PUSH_SOUND = "config_push_sound";

    public static final String CONFIG_EASTER_EGGS = "config_easter_eggs";

    public static final String CACHE_LAST_PUSH_ID = "cache_push_id";  //当日推送的id统计
    public static final String CACHE_LAST_PUSH_TIME = "cache_push_TIME";  //当日推送的时间统计

    public static final String PUSH_WORK_NAME = "mere_push_work";

    public static final int LIST_TYPE_SINGLE = 0;

    public static final int LIST_TYPE_MULTI = 1;
}
