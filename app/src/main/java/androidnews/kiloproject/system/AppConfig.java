package androidnews.kiloproject.system;

import java.util.HashMap;

/**
 * Created by ct_OS on 2018-1-1.
 */

public class AppConfig {
    public static final String HOST163 = "http://c.m.163.com";
    public static final String HOST163COMMENT = "http://comment.api.163.com";

    public static final String getNewsDetailA = "nc/article/";
    public static final String getNewsDetailB = "/full.html";

    public static final String getNewsCommentA = "/api/json/post/list/new/normal/";
    public static final String getNewsCommentB = "/desc/0/20/10/2/2";


    public static final String getMainDataA = "/nc/article/headline/";
    public static final String getMainDataB = "-20.html";


    public static final String CONFIG_LANGUAGE = "config_language";

    public static final String CONFIG_AUTO_REFRESH = "config_auto_refresh";

    public static final String CONFIG_STATUSBAR = "config_statusbar";

    public static final String CONFIG_SWIPE_BACK = "config_swipe_back";

    public static final String CONFIG_TYPE_ARRAY = "config_type_array";

    public static boolean isSwipeBack = false;


}
