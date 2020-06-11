package androidnews.kiloproject.system;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.Utils;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.tencent.bugly.crashreport.CrashReport;
import com.zhouyou.http.EasyHttp;

import org.litepal.LitePal;

import androidnews.kiloproject.R;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;

import static androidnews.kiloproject.system.AppConfig.CONFIG_AUTO_LOADMORE;
import static androidnews.kiloproject.system.AppConfig.CONFIG_AUTO_NIGHT;
import static androidnews.kiloproject.system.AppConfig.CONFIG_AUTO_REFRESH;
import static androidnews.kiloproject.system.AppConfig.CONFIG_BACK_EXIT;
import static androidnews.kiloproject.system.AppConfig.CONFIG_DISABLE_NOTICE;
import static androidnews.kiloproject.system.AppConfig.CONFIG_EASTER_EGGS;
import static androidnews.kiloproject.system.AppConfig.CONFIG_HAPTIC;
import static androidnews.kiloproject.system.AppConfig.CONFIG_HIGH_RAM;
import static androidnews.kiloproject.system.AppConfig.CONFIG_LIST_TYPE;
import static androidnews.kiloproject.system.AppConfig.CONFIG_NIGHT_MODE;
import static androidnews.kiloproject.system.AppConfig.CONFIG_NO_IMAGE;
import static androidnews.kiloproject.system.AppConfig.CONFIG_PUSH;
import static androidnews.kiloproject.system.AppConfig.CONFIG_PUSH_MODE;
import static androidnews.kiloproject.system.AppConfig.CONFIG_PUSH_SOUND;
import static androidnews.kiloproject.system.AppConfig.CONFIG_PUSH_TIME;
import static androidnews.kiloproject.system.AppConfig.CONFIG_SHOW_SKELETON;
import static androidnews.kiloproject.system.AppConfig.CONFIG_STATUS_BAR;
import static androidnews.kiloproject.system.AppConfig.CONFIG_SWIPE_BACK;
import static androidnews.kiloproject.system.AppConfig.CONFIG_TEXT_SIZE;
import static androidnews.kiloproject.system.AppConfig.HOST_163;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;

//import org.litepal.LitePal;

/**
 * Created by Administrator on 2017/12/9.
 */

public class MyApplication extends Application {
    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        CrashReport.initCrashReport(getApplicationContext(), "e86bab41f6", false);

        //网络框架
        EasyHttp.init(this);//默认初始化
        EasyHttp.getInstance()
                .setBaseUrl(HOST_163)
//                .debug("网络DEBUG", true);
        ;

        //数据库
        LitePal.initialize(this);

        //Util工具包
        Utils.init(this);

        SPUtils spUtils = SPUtils.getInstance();
        AppConfig.isShowSkeleton = spUtils.getBoolean(CONFIG_SHOW_SKELETON,true);
        AppConfig.isAutoNight = spUtils.getBoolean(CONFIG_AUTO_NIGHT);
        AppConfig.listType = spUtils.getInt(CONFIG_LIST_TYPE, -1);
        AppConfig.mTextSize = spUtils.getInt(CONFIG_TEXT_SIZE, 1);
        AppConfig.isNightMode = spUtils.getBoolean(CONFIG_NIGHT_MODE);
        AppConfig.isSwipeBack = spUtils.getBoolean(CONFIG_SWIPE_BACK);
        AppConfig.isAutoRefresh = spUtils.getBoolean(CONFIG_AUTO_REFRESH);
        AppConfig.isAutoLoadMore = spUtils.getBoolean(CONFIG_AUTO_LOADMORE);
        AppConfig.isBackExit = spUtils.getBoolean(CONFIG_BACK_EXIT);
        AppConfig.isStatusBar = spUtils.getBoolean(CONFIG_STATUS_BAR);
        AppConfig.isDisNotice = spUtils.getBoolean(CONFIG_DISABLE_NOTICE);
        AppConfig.isPush = spUtils.getBoolean(CONFIG_PUSH, true);
        AppConfig.isPushSound = spUtils.getBoolean(CONFIG_PUSH_SOUND);
        AppConfig.isPushMode = spUtils.getBoolean(CONFIG_PUSH_MODE);
        AppConfig.pushTime = spUtils.getInt(CONFIG_PUSH_TIME, 1);
        AppConfig.isEasterEggs = spUtils.getBoolean(CONFIG_EASTER_EGGS);
        AppConfig.isHaptic = spUtils.getBoolean(CONFIG_HAPTIC);
        AppConfig.isNoImage = spUtils.getBoolean(CONFIG_NO_IMAGE);
        AppConfig.isHighRam = spUtils.getBoolean(CONFIG_HIGH_RAM);

        int mode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (mode == Configuration.UI_MODE_NIGHT_YES)
            AppConfig.isNightMode = true;

//        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
//            @Override
//            public void onActivityCreated(Activity activity, Bundle bundle) {
//                if (activity instanceof PermissionActivity) //第三方Activity通通跳过EventBus注册
//                    return;
//                EventBus.getDefault().register(activity);
//            }
//
//            @Override
//            public void onActivityStarted(Activity activity) {
//            }
//
//            @Override
//            public void onActivityResumed(Activity activity) {
//            }
//
//            @Override
//            public void onActivityPaused(Activity activity) {
//            }
//
//            @Override
//            public void onActivityStopped(Activity activity) {
//            }
//
//            @Override
//            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
//            }
//
//            @Override
//            public void onActivityDestroyed(Activity activity) {
//                if (activity instanceof PermissionActivity) //第三方Activity通通跳过EventBus注销
//                    return;
//                EventBus.getDefault().unregister(activity);
//            }
//        });
        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                LogUtils.d("onRxJavaErrorHandler ---->: $it");
            }
        });
    }

    public static MyApplication getInstance() {
        return instance;
    }

    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
//                layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);//全局设置主题颜色
                return new MaterialHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
//                指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setDrawableSize(24);
            }
        });
    }
}
