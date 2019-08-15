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
import androidx.core.content.ContextCompat;
import androidx.core.os.ConfigurationCompat;
import androidx.core.view.ViewCompat;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.Operation;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.facebook.device.yearclass.YearClass;
import com.gyf.immersionbar.ImmersionBar;

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
import static androidnews.kiloproject.system.AppConfig.CONFIG_AUTO_LOADMORE;
import static androidnews.kiloproject.system.AppConfig.CONFIG_AUTO_REFRESH;
import static androidnews.kiloproject.system.AppConfig.CONFIG_BACK_EXIT;
import static androidnews.kiloproject.system.AppConfig.CONFIG_EASTER_EGGS;
import static androidnews.kiloproject.system.AppConfig.CONFIG_HIGH_RAM;
import static androidnews.kiloproject.system.AppConfig.CONFIG_LANGUAGE;
import static androidnews.kiloproject.system.AppConfig.CONFIG_LAST_LAUNCH;
import static androidnews.kiloproject.system.AppConfig.CONFIG_LIST_TYPE;
import static androidnews.kiloproject.system.AppConfig.CONFIG_NIGHT_MODE;
import static androidnews.kiloproject.system.AppConfig.CONFIG_DISABLE_NOTICE;
import static androidnews.kiloproject.system.AppConfig.CONFIG_PUSH;
import static androidnews.kiloproject.system.AppConfig.CONFIG_PUSH_MODE;
import static androidnews.kiloproject.system.AppConfig.CONFIG_PUSH_SOUND;
import static androidnews.kiloproject.system.AppConfig.CONFIG_PUSH_TIME;
import static androidnews.kiloproject.system.AppConfig.CONFIG_SHOW_SKELETON;
import static androidnews.kiloproject.system.AppConfig.CONFIG_STATUS_BAR;
import static androidnews.kiloproject.system.AppConfig.CONFIG_SWIPE_BACK;
import static androidnews.kiloproject.system.AppConfig.CONFIG_TEXT_SIZE;
import static androidnews.kiloproject.system.AppConfig.LIST_TYPE_MULTI;
import static androidnews.kiloproject.system.AppConfig.LIST_TYPE_SINGLE;
import static androidnews.kiloproject.system.AppConfig.PUSH_WORK_NAME;
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

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N)
                    initShortsCut();

                AppConfig.listType = spUtils.getInt(CONFIG_LIST_TYPE, -1);
                if (listType == -1)
                    listType = DeviceUtils.isTablet() ? LIST_TYPE_MULTI : LIST_TYPE_SINGLE;

                long lastLaunchTime = spUtils.getLong(CONFIG_LAST_LAUNCH, -1);
                if (lastLaunchTime == -1) {
                    int year = YearClass.get(getApplicationContext());
                    if (year > 2014) {
                        AppConfig.isHighRam = true;
                        spUtils.put(CONFIG_HIGH_RAM, true);
                    }
                } else
                    AppConfig.isHighRam = spUtils.getBoolean(CONFIG_HIGH_RAM);
                spUtils.put(CONFIG_LAST_LAUNCH, System.currentTimeMillis());

                applyConfig();

                AppConfig.mTextSize = spUtils.getInt(CONFIG_TEXT_SIZE, 1);

                checkPushWork();
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
            WorkManager.getInstance().enqueueUniquePeriodicWork(PUSH_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP,workRequest);
        }
    }
}
