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
import android.view.WindowManager;

import com.blankj.utilcode.util.SPUtils;

import java.util.ArrayList;
import java.util.List;

import androidnews.kiloproject.R;
import androidnews.kiloproject.system.AppConfig;

import static androidnews.kiloproject.bean.data.CacheNews.CACHE_COLLECTION;
import static androidnews.kiloproject.system.AppConfig.CONFIG_HAVE_CHECK;
import static androidnews.kiloproject.system.AppConfig.CONFIG_NIGHT_MODE;
import static androidnews.kiloproject.system.AppConfig.CONFIG_SWIPE_BACK;
import static androidnews.kiloproject.system.AppConfig.isNightMode;
import static androidnews.kiloproject.util.TransformationUtils.hasCheckEvent173;
import static androidnews.kiloproject.util.TransformationUtils.transferBlockData;
import static androidnews.kiloproject.util.TransformationUtils.transferChannel;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SPUtils spUtils = SPUtils.getInstance();
        AppConfig.isNightMode = spUtils.getBoolean(CONFIG_NIGHT_MODE);
        if (isNightMode)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        AppConfig.isSwipeBack = spUtils.getBoolean(CONFIG_SWIPE_BACK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1)
            initShortsCut();
        if (!hasCheckEvent173()) {
            transferBlockData();
            transferChannel();
            SPUtils.getInstance().put(CONFIG_HAVE_CHECK, true);
        }

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    ShortcutManager mSystemService;

    List<String> mTitle = new ArrayList<>();

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
