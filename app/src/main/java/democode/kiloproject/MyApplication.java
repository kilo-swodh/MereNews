package democode.kiloproject;

import android.app.Application;

import org.litepal.LitePal;

/**
 * Created by Administrator on 2017/12/9.
 */

public class MyApplication extends Application {



    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
    }
}
