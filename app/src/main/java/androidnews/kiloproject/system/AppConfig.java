package androidnews.kiloproject.system;

import java.util.List;
import java.util.UUID;

import androidnews.kiloproject.entity.data.BlockItem;

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
            isPushMode,
            isHaptic,
            isNoImage,
            isEasterEggs,
            isShowSkeleton,
            isAutoNight;;
    public static int listType,pushTime;

    public static String[] goodTags;
    public static List<BlockItem> blockList;

    public static int mTextSize = 1;

    public static final int TYPE_NETEASE_START = 0;
    public static final int TYPE_NETEASE_END = 49;
    public static final int TYPE_NETEASE_END_USED = 44;

    public static final int TYPE_ZHIHU = 50;
    public static final int TYPE_GUOKR = 51;
    public static final int TYPE_CNBETA = 52;
    public static final int TYPE_OTHER_END = 69;
    public static final int TYPE_OTHER_END_USED = 52;       //加其他记得改

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
    public static final String GET_SMARTISAN_REFRESH = "/index.php?r=find/GetArticleList&cate_id={cate_id}&art_id=&page_size=20";
    public static final String GET_SMARTISAN_LOAD_MORE = "/index.php?r=find/GetArticleList&cate_id={cate_id}&art_id={last_id}&page_size=20";

    //CnBeta
    public static final String HOST_CNBETA = "http://api.cnbeta.com/capi?";
    public static final String GET_CNBETA_REFRESH = "app_key=10000&end_sid=2147483647&format=json&method=Article.Lists&timestamp={timestamp}&v=2.8.5";
    public static final String GET_CNBETA_LOADMORE = "app_key=10000&end_sid={end_sid}&format=json&method=Article.Lists&timestamp={timestamp}&v=2.8.5";
    public static final String GET_CNBETA_DETAIL = "app_key=10000&format=json&method=Article.NewsContent&sid={sid}&timestamp={timestamp}&v=2.8.5";
    public static final String GET_CNBETA_COMMENT = "app_key=10000&format=json&method=Article.Comment&page=1&pageSize=20&sid={sid}&timestamp={timestamp}&v=2.8.5";
    public static final String GET_CNBETA_EXTRA = "&sign=";
    public static final String GET_CNBETA_MD5_EXTRA = "&mpuffgvbvbttn3Rc";
    public static final String HOST_CNBETA_SHARE = "http://www.cnbeta.com/articles/";
    public static final String HOST_CNBETA_IMG = "https://static.cnbetacdn.com";

    //更新
    public static final String CHECK_UPDATE_ADDRESS = "https://gitee.com/kiloswodh/android_news/raw/master/mere_update.json";

    public static final String DOWNLOAD_ADDRESS = "https://www.lanzous.com/b00t809ah";

    public static final String DOWNLOAD_EXPLORER_ADDRESS = "https://www.lanzous.com/b00t809da";

    public static final String QQ_KEY = "mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" +
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

    public static final String CONFIG_PUSH_MODE = "config_push_mode";

    public static final String CONFIG_SHOW_SKELETON = "config_show_skeleton";

    public static final String CONFIG_AUTO_NIGHT = "config_auto_night";

    public static final String CONFIG_EASTER_EGGS = "config_easter_eggs";

    public static final String CONFIG_SHOW_EXPLORER = "config_show_explorer";

    public static final String CONFIG_HAPTIC = "config_haptic";

    public static final String CONFIG_NO_IMAGE = "config_no_image";

    public static final String CACHE_LAST_PUSH_ID = "cache_push_id";  //当日推送的id统计

    public static final String PUSH_WORK_NAME = "mere_push_work";

    public static final int LIST_TYPE_SINGLE = 0;

    public static final int LIST_TYPE_MULTI = 1;
}
