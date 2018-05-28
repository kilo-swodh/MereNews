package democode.kiloproject.system;

import android.app.Application;

import com.blankj.utilcode.util.Utils;
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

        //网络框架
        EasyHttp.init(this);//默认初始化
        EasyHttp.getInstance()
                .setBaseUrl(URL);

        //数据库
//        LitePal.initialize(this);

        //Util工具包
        Utils.init(this);
    }
}
