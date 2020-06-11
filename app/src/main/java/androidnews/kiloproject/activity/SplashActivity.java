package androidnews.kiloproject.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Icon;
import android.icu.util.Currency;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.ConfigurationCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.util.DisplayMetrics;
import android.webkit.WebView;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.SPUtils;
import com.facebook.device.yearclass.YearClass;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import androidnews.kiloproject.R;
import androidnews.kiloproject.push.NotifyWork;
import androidnews.kiloproject.system.AppConfig;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static androidnews.kiloproject.entity.data.CacheNews.CACHE_COLLECTION;
import static androidnews.kiloproject.system.AppConfig.CONFIG_AUTO_NIGHT;
import static androidnews.kiloproject.system.AppConfig.CONFIG_HIGH_RAM;
import static androidnews.kiloproject.system.AppConfig.CONFIG_LANGUAGE;
import static androidnews.kiloproject.system.AppConfig.CONFIG_LAST_LAUNCH;
import static androidnews.kiloproject.system.AppConfig.CONFIG_NIGHT_MODE;
import static androidnews.kiloproject.system.AppConfig.LIST_TYPE_MULTI;
import static androidnews.kiloproject.system.AppConfig.LIST_TYPE_SINGLE;
import static androidnews.kiloproject.system.AppConfig.PUSH_WORK_NAME;
import static androidnews.kiloproject.system.AppConfig.isAutoNight;
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
                AppConfig.goodTags = getResources().getStringArray(R.array.good_tag);

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N)
                    initShortsCut();

                if (listType == -1)
                    listType = DeviceUtils.isTablet() ? LIST_TYPE_MULTI : LIST_TYPE_SINGLE;

                long lastLaunchTime = spUtils.getLong(CONFIG_LAST_LAUNCH, -1);
                if (lastLaunchTime == -1) {
                    int year = YearClass.get(getApplicationContext());
                    if (year > 2014) {
                        AppConfig.isHighRam = true;
                        spUtils.put(CONFIG_HIGH_RAM, true);
                    }
                }

                spUtils.put(CONFIG_LAST_LAUNCH, System.currentTimeMillis());

                checkPushWork();

                e.onNext(true);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        new WebView(SplashActivity.this);
                        applyConfig();
                        startActivity(new Intent(SplashActivity.this, NewsMainActivity.class));
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
        if (isAutoNight){
            int currentNightMode = getResources().getConfiguration().uiMode
                    & Configuration.UI_MODE_NIGHT_MASK;
            switch (currentNightMode) {
                case Configuration.UI_MODE_NIGHT_NO:
                    // Night mode is not active, we're in day time
                case Configuration.UI_MODE_NIGHT_UNDEFINED:
                    // We don't know what mode we're in, assume notnight
                    isNightMode = false;
                    break;
                case Configuration.UI_MODE_NIGHT_YES:
                    // Night mode is active, we're at night!
                    isNightMode = true;
                    break;
            }
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
            else
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }else {
            AppCompatDelegate.setDefaultNightMode(isNightMode ? AppCompatDelegate.MODE_NIGHT_YES :
                    AppCompatDelegate.MODE_NIGHT_NO);
        }

        int language = SPUtils.getInstance().getInt(CONFIG_LANGUAGE);
        Locale myLocale = null;
        switch (language) {
            case 0:
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1)
                    myLocale = ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration()).get(0);
                else
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

    @SuppressLint("NewApi")
    public void checkPushWork() {
        if (!AppConfig.isPush || AppConfig.isPushMode) return;

//        Constraints myCoustrain = new Constraints.Builder()
//                .setRequiresBatteryNotLow(true) //不在电量不足执行
//                .setRequiresCharging(true) //在充电时执行
//                .setRequiresStorageNotLow(true) //不在存储容量不足时执行
//                .setRequiresDeviceIdle(true) //在待机状态下执行 调用需要API级别最低为23
//                .setRequiredNetworkType(NetworkType.CONNECTED)
//                .build();

        PeriodicWorkRequest.Builder notifyWork = null;
        switch (AppConfig.pushTime) {
            case 0:
                notifyWork = new PeriodicWorkRequest.Builder(NotifyWork.class, 20, TimeUnit.MINUTES);
                break;
            case 1:
                notifyWork = new PeriodicWorkRequest.Builder(NotifyWork.class, 42, TimeUnit.MINUTES);
                break;
            case 2:
                notifyWork = new PeriodicWorkRequest.Builder(NotifyWork.class, 160, TimeUnit.MINUTES);
                break;
            case 3:
                notifyWork = new PeriodicWorkRequest.Builder(NotifyWork.class, 300, TimeUnit.MINUTES);
                break;
            default:
                break;
        }
        if (notifyWork != null) {
//            notifyWork.setConstraints(myCoustrain);
            PeriodicWorkRequest workRequest = notifyWork.build();
            WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork(PUSH_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, workRequest);
        }
    }
}
