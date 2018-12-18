package androidnews.kiloproject.activity;

import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;

import java.util.List;

import androidnews.kiloproject.R;
import androidnews.kiloproject.system.AppConfig;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static androidnews.kiloproject.bean.data.CacheNews.CACHE_COLLECTION;
import static androidnews.kiloproject.system.AppConfig.CONFIG_LIST_TYPE;
import static androidnews.kiloproject.system.AppConfig.CONFIG_NIGHT_MODE;
import static androidnews.kiloproject.system.AppConfig.CONFIG_SWIPE_BACK;
import static androidnews.kiloproject.system.AppConfig.CONFIG_TEXT_SIZE;
import static androidnews.kiloproject.system.AppConfig.LIST_TYPE_MULTI;
import static androidnews.kiloproject.system.AppConfig.LIST_TYPE_SINGLE;
import static androidnews.kiloproject.system.AppConfig.type_list;
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1)
                    initShortsCut();

                AppConfig.type_list = spUtils.getInt(CONFIG_LIST_TYPE,-1);
                if (type_list == -1)
                    type_list = ScreenUtils.isTablet() ? LIST_TYPE_MULTI : LIST_TYPE_SINGLE;

                AppConfig.TextSize = spUtils.getInt(CONFIG_TEXT_SIZE, 1);
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
}
