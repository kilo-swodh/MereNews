package androidnews.kiloproject.system.base;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.blankj.utilcode.util.SPUtils;
import com.bumptech.glide.RequestManager;
import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;
import com.jude.swipbackhelper.SwipeBackHelper;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Setting;

import java.util.List;
import java.util.Locale;

import androidnews.kiloproject.R;
import androidnews.kiloproject.permission.RuntimeRationale;
import androidnews.kiloproject.system.MyApplication;

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
    protected void initStateBar(int colorRes, boolean isBlackFront) {
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

    /**
     * 权限相关
     *
     * @param grantedAction
     * @param permissions
     */
    protected void requestPermission(Action grantedAction, String... permissions) {
        AndPermission.with(mActivity)
                .runtime()
                .permission(permissions)
                .rationale(new RuntimeRationale())
                .onGranted(grantedAction)
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(@NonNull List<String> permissions) {
                        if (AndPermission.hasAlwaysDeniedPermission(mActivity, permissions)) {
                            showSettingDialog(mActivity, permissions);
                        }
                    }
                })
                .start();
    }

    public void showSettingDialog(Context context, final List<String> permissions) {
        List<String> permissionNames = Permission.transformText(context, permissions);
        String message = context.getString(R.string.message_permission_always_failed, TextUtils.join("\n", permissionNames));

        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(R.string.tip_title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setPermission();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    protected void setPermission() {
        AndPermission.with(this)
                .runtime()
                .setting()
                .onComeback(new Setting.Action() {
                    @Override
                    public void onAction() {
                        Toast.makeText(mActivity, R.string.message_setting_comeback, Toast.LENGTH_SHORT).show();
                    }
                })
                .start();
    }

    /**
     * Request to read and write external storage permissions.
     */
//    protected void requestPermissionForInstallPackage() {
//        AndPermission.with(this)
//                .runtime()
//                .permission(Permission.Group.STORAGE)
//                .rationale(new RuntimeRationale())
//                .onGranted(new Action<List<String>>() {
//                    @Override
//                    public void onAction(List<String> data) {
//                        new WriteApkTask(mActivity, new Runnable() {
//                            @Override
//                            public void run() {
//                                installPackage();
//                            }
//                        }).execute();
//                    }
//                })
//                .onDenied(new Action<List<String>>() {
//                    @Override
//                    public void onAction(List<String> data) {
//                        ToastUtils.showShort(R.string.message_install_failed);
//                    }
//                })
//                .start();
//    }

    /**
     * Install package.
     */
//    protected void installPackage() {
//        AndPermission.with(this)
//                .install()
//                .file(new File(Environment.getExternalStorageDirectory(), getResources().getString(R.string.apk_name)))
//                .rationale(new InstallRationale())
//                .onGranted(new Action<File>() {
//                    @Override
//                    public void onAction(File data) {
//                        // Installing.
//                    }
//                })
//                .onDenied(new Action<File>() {
//                    @Override
//                    public void onAction(File data) {
//                        // The user refused to install.
//                    }
//                })
//                .start();
//    }
//
//    protected void requestPermissionForAlertWindow() {
//        AndPermission.with(this)
//                .overlay()
//                .rationale(new OverlayRationale())
//                .onGranted(new Action<Void>() {
//                    @Override
//                    public void onAction(Void data) {
//                        showAlertWindow();
//                    }
//                })
//                .onDenied(new Action<Void>() {
//                    @Override
//                    public void onAction(Void data) {
//                        ToastUtils.showShort(R.string.message_overlay_failed);
//                    }
//                })
//                .start();
//    }
    protected void showAlertWindow() {
        MyApplication.getInstance().showLauncherView();

        Intent backHome = new Intent(Intent.ACTION_MAIN);
        backHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        backHome.addCategory(Intent.CATEGORY_HOME);
        startActivity(backHome);
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
