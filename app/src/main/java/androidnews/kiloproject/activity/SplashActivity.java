package androidnews.kiloproject.activity;

import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.DisplayMetrics;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.facebook.device.yearclass.YearClass;

import java.util.List;
import java.util.Locale;

import androidnews.kiloproject.R;
import androidnews.kiloproject.system.AppConfig;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static androidnews.kiloproject.entity.data.CacheNews.CACHE_COLLECTION;
import static androidnews.kiloproject.system.AppConfig.CONFIG_AUTO_LOADMORE;
import static androidnews.kiloproject.system.AppConfig.CONFIG_AUTO_REFRESH;
import static androidnews.kiloproject.system.AppConfig.CONFIG_BACK_EXIT;
import static androidnews.kiloproject.system.AppConfig.CONFIG_HIGH_RAM;
import static androidnews.kiloproject.system.AppConfig.CONFIG_LANGUAGE;
import static androidnews.kiloproject.system.AppConfig.CONFIG_LAST_LAUNCH;
import static androidnews.kiloproject.system.AppConfig.CONFIG_LIST_TYPE;
import static androidnews.kiloproject.system.AppConfig.CONFIG_NIGHT_MODE;
import static androidnews.kiloproject.system.AppConfig.CONFIG_DISABLE_NOTICE;
import static androidnews.kiloproject.system.AppConfig.CONFIG_STATUS_BAR;
import static androidnews.kiloproject.system.AppConfig.CONFIG_SWIPE_BACK;
import static androidnews.kiloproject.system.AppConfig.CONFIG_TEXT_SIZE;
import static androidnews.kiloproject.system.AppConfig.LIST_TYPE_MULTI;
import static androidnews.kiloproject.system.AppConfig.LIST_TYPE_SINGLE;
import static androidnews.kiloproject.system.AppConfig.listType;
import static androidnews.kiloproject.system.AppConfig.isNightMode;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                SPUtils spUtils = SPUtils.getInstance();
                AppConfig.isNightMode = spUtils.getBoolean(CONFIG_NIGHT_MODE);
                AppConfig.isSwipeBack = spUtils.getBoolean(CONFIG_SWIPE_BACK);
                AppConfig.isAutoRefresh = spUtils.getBoolean(CONFIG_AUTO_REFRESH);
                AppConfig.isAutoLoadMore = spUtils.getBoolean(CONFIG_AUTO_LOADMORE);
                AppConfig.isBackExit = spUtils.getBoolean(CONFIG_BACK_EXIT);
                AppConfig.isStatusBar = spUtils.getBoolean(CONFIG_STATUS_BAR);
                AppConfig.isDisNotice = spUtils.getBoolean(CONFIG_DISABLE_NOTICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1)
                    initShortsCut();

                AppConfig.listType = spUtils.getInt(CONFIG_LIST_TYPE,-1);
                if (listType == -1)
                    listType = ScreenUtils.isTablet() ? LIST_TYPE_MULTI : LIST_TYPE_SINGLE;

                long lastLaunchTime = spUtils.getLong(CONFIG_LAST_LAUNCH,-1);
                if (lastLaunchTime == -1) {
                    int year = YearClass.get(getApplicationContext());
                    if (year > 2014){
                        AppConfig.isHighRam = true;
                        spUtils.put(CONFIG_HIGH_RAM,true);
                    }
                }else
                    AppConfig.isHighRam = spUtils.getBoolean(CONFIG_HIGH_RAM);
                spUtils.put(CONFIG_LAST_LAUNCH,System.currentTimeMillis());

                applyConfig();

                AppConfig.mTextSize = spUtils.getInt(CONFIG_TEXT_SIZE, 1);
                e.onNext(true);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (isNightMode)
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                    }
                });
    }

    ShortcutManager mSystemService;

    @RequiresApi(25)
    private void initShortsCut() {
        mSystemService = getSystemService(ShortcutManager.class);

        List<ShortcutInfo> dynamicShortcuts = mSystemService.getDynamicShortcuts();

        //mSystemService.getMaxShortcutCountPerActivity() 可以获得shortcut显示的最大值
        if (dynamicShortcuts != null && dynamicShortcuts.size() < 1) {

            Intent intent = new Intent(this, CacheActivity.class);
            intent.setAction(Intent.ACTION_VIEW);
            intent.putExtra("type", CACHE_COLLECTION);
            ShortcutInfo info = new ShortcutInfo.Builder(this, "shortcut_col")//设置id
                    .setShortLabel(getResources().getString(R.string.shortcut_star))//设置短标题
                    .setDisabledMessage(getResources().getString(R.string.shortcut_disabled))
                    .setLongLabel(getResources().getString(R.string.action_star))//设置长标题
                    .setIcon(Icon.createWithResource(this, R.drawable.ic_shortcut_star))//设置图标
                    .setIntent(intent)//设置intent
                    .build();
            dynamicShortcuts.add(info);//将新建好的shortcut添加到集合

            mSystemService.setDynamicShortcuts(dynamicShortcuts);//设置动态shortcut
        }
    }

    private void applyConfig() {
        if (isNightMode)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        int language = SPUtils.getInstance().getInt(CONFIG_LANGUAGE);
        Locale myLocale = null;
        switch (language) {
            case 0:
                myLocale = Locale.getDefault();
                break;
            case 1:
                myLocale = new Locale("en");
                break;
            case 2:
                myLocale = new Locale("zh");
                break;
        }
        if (myLocale == null)
            return;
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }
}
