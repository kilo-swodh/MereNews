package democode.kiloproject.system;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.blankj.utilcode.util.Utils;
import com.squareup.leakcanary.LeakCanary;
import com.zhouyou.http.EasyHttp;

import org.greenrobot.eventbus.EventBus;

//import org.litepal.LitePal;

/**
 * Created by Administrator on 2017/12/9.
 */

public class MyApplication extends Application {

    String URL = "http://www.ssqfs.com/api/";

    @Override
    public void onCreate() {
        super.onCreate();

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
                .setBaseUrl(URL);

        //数据库
//        LitePal.initialize(this);

        //Util工具包
        Utils.init(this);

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
                EventBus.getDefault().register(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                EventBus.getDefault().unregister(activity);
            }
        });
    }
}
