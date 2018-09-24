package androidnews.kiloproject.system;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.View;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.Utils;
import com.squareup.leakcanary.LeakCanary;
import com.yanzhenjie.permission.PermissionActivity;
import com.zhouyou.http.EasyHttp;

import org.greenrobot.eventbus.EventBus;
import androidnews.kiloproject.widget.LauncherView;
import androidnews.kiloproject.widget.AlertWindow;

import static androidnews.kiloproject.system.AppConfig.CONFIG_SWIPE_BACK;
import static androidnews.kiloproject.system.AppConfig.HOST163;

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
                .debug("网络DEBUG", true);;

        //数据库
//        LitePal.initialize(this);

        //Util工具包
        Utils.init(this);

        AppConfig.isSwipeBack = SPUtils.getInstance().getBoolean(CONFIG_SWIPE_BACK);

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
                if (activity instanceof PermissionActivity) //第三方Activity通通跳过EventBus注册
                    return;
                EventBus.getDefault().register(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) { }

            @Override
            public void onActivityResumed(Activity activity) { }

            @Override
            public void onActivityPaused(Activity activity) { }

            @Override
            public void onActivityStopped(Activity activity) { }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) { }

            @Override
            public void onActivityDestroyed(Activity activity) {
                if (activity instanceof PermissionActivity) //第三方Activity通通跳过EventBus注销
                    return;
                EventBus.getDefault().unregister(activity);
            }
        });
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
}
