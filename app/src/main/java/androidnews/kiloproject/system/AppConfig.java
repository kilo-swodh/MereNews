package androidnews.kiloproject.system;

import java.util.HashMap;

/**
 * Created by ct_OS on 2018-1-1.
 */

public class AppConfig {
    //163
    public static final String HOST163 = "http://c.m.163.com";
    public static final String HOST163COMMENT = "http://comment.api.163.com";

    public static final String getNewsDetailA = "nc/article/";
    public static final String getNewsDetailB = "/full.html";

    public static final String getMainDataA = "/nc/article/headline/";
    public static final String getMainDataB = "-20.html";

    public static final String getNewsCommentA = "/api/json/post/list/new/normal/";
    public static final String getNewsCommentB = "/desc/0/20/10/2/2";

    public static final String getVideosA = "/nc/video/list/";
    public static final String getVideosB = "/n/";
    public static final String getVideosC = "-20.html";

    //知乎
    public static final String HOSTzhihu = "http://news-at.zhihu.com/api/4/news";
    public static final String getZhihuRefresh = "/latest";
    public static final String getZhihuLoadMore = "/before/";

    //果壳
    public static final String HOSTguoKr = "http://apis.guokr.com";
    public static final String HOSTguoKrDetail = "http://jingxuan.guokr.com";

    public static final String getGuoKrList = "/handpick/v2/article.json?retrieve_type=by_offset&limit=20&ad=1&offset=";
    public static final String getGuoKrTop = "/flowingboard/item/handpick_carousel.json";
    public static final String getGuoKrDetail = "/pick/v2/";

    //更新
    public static final String CHECK_UPADTE_ADDRESS = "http://osyuohm14.bkt.clouddn.com/update";

    //常量
    public static final String CONFIG_LANGUAGE = "config_language";

    public static final String CONFIG_AUTO_REFRESH = "config_auto_refresh";

    public static final String CONFIG_AUTO_LOADMORE = "config_auto_loadmore";

    public static final String CONFIG_BACK_EXIT = "config_back_exit";

    public static final String CONFIG_SWIPE_BACK = "config_swipe_back";

    public static final String CONFIG_NIGHT_MODE = "config_night_mode";

    public static final String CONFIG_RANDOM_HEADER = "config_random_header";

    public static final String CONFIG_TYPE_ARRAY = "config_type_array";

    public static final String CONFIG_BLOCK_LIST = "config_block_list";

    public static boolean isSwipeBack = false;

    public static boolean isNightMode = false;

}
