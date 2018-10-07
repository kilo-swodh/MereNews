package androidnews.kiloproject.system;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;

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
import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.crashreport.CrashReport;
import com.yanzhenjie.permission.PermissionActivity;
import com.zhouyou.http.EasyHttp;

import org.greenrobot.eventbus.EventBus;

import androidnews.kiloproject.widget.AlertWindow;
import androidnews.kiloproject.widget.LauncherView;

import static androidnews.kiloproject.system.AppConfig.CONFIG_NIGHT_MODE;
import static androidnews.kiloproject.system.AppConfig.CONFIG_SWIPE_BACK;
import static androidnews.kiloproject.system.AppConfig.HOST163;
import static androidnews.kiloproject.system.AppConfig.isNightMode;

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
        //检测内存泄露
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        // Normal app init code...

        //网络框架
        EasyHttp.init(this);//默认初始化
        EasyHttp.getInstance()
                .setBaseUrl(HOST163)
//                .debug("网络DEBUG", true);
        ;

        //数据库
//        LitePal.initialize(this);

        //Util工具包
        Utils.init(this);

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
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public void showLauncherView() {
        final AlertWindow alertWindow = new AlertWindow(this);
        LauncherView view = new LauncherView(this);
        view.setCancelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertWindow.dismiss();
            }
        });
        alertWindow.setContentView(view);
        alertWindow.show();
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
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setDrawableSize(20);
            }
        });
    }
}
