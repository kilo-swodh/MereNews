package androidnews.kiloproject.system.base;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;

import com.blankj.utilcode.util.SPUtils;
import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;
import com.jude.swipbackhelper.SwipeBackHelper;

import java.util.Locale;

import androidnews.kiloproject.R;

import static androidnews.kiloproject.system.AppConfig.CONFIG_LANGUAGE;
import static androidnews.kiloproject.system.AppConfig.isNightMode;
import static androidnews.kiloproject.system.AppConfig.isSwipeBack;

/**
 * Created by Administrator on 2017/12/9.
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected BaseActivity mActivity;
    boolean isStart = false;
    private ImmersionBar mImmersionBar;
    protected Gson gson = new Gson();

    public static final int SELECT_RESULT = 999;
    public static final int SETTING_RESULT = 998;
    public static final int BLOCK_RESULT = 997;
    public static final int CACHE_RESULT = 996;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (isNightMode)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
//        ScreenUtils.adaptScreen4VerticalSlide(this, 360);
        mActivity = this;
        isStart = true;
        applyAppLanguage();
        SwipeBackHelper.onCreate(this);
        SwipeBackHelper.getCurrentPage(this).setSwipeBackEnable(isSwipeBack);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isStart) {
            //初始化逻辑代码
            initSlowly();
            isStart = false;
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        SwipeBackHelper.onPostCreate(this);
    }

    //对只需要一次初始化的耗时操作或者界面绘制放在这里
    protected abstract void initSlowly();

    protected void initToolbar(Toolbar toolbar) {
        initToolbar(toolbar, false);
    }

    protected void initToolbar(Toolbar toolbar, boolean isBack) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        if (isBack) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    //状态栏沉浸(颜色资源)
    protected void initStatusBar(int colorRes, boolean isBlackFront) {
//        ScreenUtils.cancelAdaptScreen(this);
        mImmersionBar = ImmersionBar.with(this);
        if (isNightMode) {
            mImmersionBar.keyboardEnable(true)  //解决软键盘与底部输入框冲突问题
                    .statusBarColor(colorRes)
                    .navigationBarColor(colorRes)
                    .fitsSystemWindows(true)
                    .init();   //所有子类都将继承这些相同的属性
        } else {
            if (isBlackFront) {
                if (ImmersionBar.isSupportNavigationIconDark()){
                    mImmersionBar.navigationBarColor(R.color.main_background)
                            .navigationBarDarkIcon(true);
                }else {
                    mImmersionBar.navigationBarColor(R.color.divider);
                }
                mImmersionBar.statusBarDarkFont(true, 0.2f)
                        //原理：如果当前设备支持状态栏字体变色，会设置状态栏字体为黑色，
                        // 如果当前设备不支持状态栏字体变色，会使当前状态栏加上透明度，否则不执行透明度
                        .statusBarColor(colorRes)
                        .fitsSystemWindows(true)
                        .keyboardEnable(true)  //解决软键盘与底部输入框冲突问题
                        .init();
            } else {
                mImmersionBar.keyboardEnable(true)  //解决软键盘与底部输入框冲突问题
                        .statusBarColor(colorRes)
                        .navigationBarColor(colorRes)
                        .fitsSystemWindows(true)
                        .init();   //所有子类都将继承这些相同的属性
            }
        }
//        ScreenUtils.restoreAdaptScreen();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // 如果你的app可以横竖屏切换，并且适配4.4或者emui3手机请务必在onConfigurationChanged方法里添加这句话
        ImmersionBar.with(this).init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //必须调用该方法，防止内存泄漏，不调用该方法，如果界面bar发生改变，
        // 在不关闭app的情况下，退出此界面再进入将记忆最后一次bar改变的状态
        ImmersionBar.with(mActivity).destroy();
        SwipeBackHelper.onDestroy(this);
    }

    private void applyAppLanguage() {
        int type = SPUtils.getInstance().getInt(CONFIG_LANGUAGE);
        Locale myLocale = null;
        switch (type) {
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

    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public void finishWithAnime() {
        if (isLollipop())
            finishAfterTransition();
        else
            finish();
    }
}
