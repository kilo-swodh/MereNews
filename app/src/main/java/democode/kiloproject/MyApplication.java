package democode.kiloproject;

import android.app.Application;

import com.zhouyou.http.EasyHttp;

//import org.litepal.LitePal;

/**
 * Created by Administrator on 2017/12/9.
 */

public class MyApplication extends Application {

    String URL = "http://www.ssqfs.com/api/";

    @Override
    public void onCreate() {
        super.onCreate();
        EasyHttp.init(this);//默认初始化
        EasyHttp.getInstance()
                .setBaseUrl(URL);
//        LitePal.initialize(this);
    }
}
