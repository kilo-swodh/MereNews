package androidnews.kiloproject.system.base;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.DecelerateInterpolator;

import com.blankj.swipepanel.SwipePanel;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;

import androidnews.kiloproject.R;
import androidnews.kiloproject.activity.MainActivity;
import androidnews.kiloproject.system.AppConfig;

import static androidnews.kiloproject.system.AppConfig.isNightMode;
import static androidnews.kiloproject.system.AppConfig.isSwipeBack;
import static com.blankj.utilcode.util.AppUtils.relaunchApp;

/**
 * Created by Administrator on 2017/12/9.
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected BaseActivity mActivity;
    boolean isStart = false;
    private ImmersionBar mImmersionBar;
    protected Gson gson = new Gson();
    public SwipePanel swipePanel;

    public static final int SELECT_RESULT = 999;
    public static final int SETTING_RESULT = 998;
    public static final int BLOCK_RESULT = 997;
    public static final int CACHE_RESULT = 996;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ScreenUtils.adaptScreen4VerticalSlide(this, 360);
        mActivity = this;
        isStart = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isStart) {
            //初始化逻辑代码
            initSlowly();
            isStart = false;
        }
        if(isSwipeBack && !(this instanceof MainActivity)) {
            swipePanel = new SwipePanel(this);
            swipePanel.setLeftEdgeSize(SizeUtils.dp2px(110));// 设置左侧触发阈值 110dp
            swipePanel.setLeftDrawable(R.drawable.ic_arrow_back);// 设置左侧 icon
            swipePanel.setRightEdgeSize(SizeUtils.dp2px(110));// 设置左侧触发阈值 110dp
            swipePanel.setRightDrawable(R.drawable.ic_arrow_back_right);// 设置左侧 icon
            swipePanel.wrapView(findViewById(R.id.root_view));// 设置嵌套在 rootLayout 外层
            swipePanel.setOnFullSwipeListener(new SwipePanel.OnFullSwipeListener() {// 设置完全划开松手后的监听
                @Override
                public void onFullSwipe(int direction) {
                    finishWithAnime();
                    swipePanel.close(direction);// 关闭
                }
            });
        }
    }

    //对只需要一次初始化的耗时操作或者界面绘制放在这里
    protected abstract void initSlowly();

    protected void initToolbar(Toolbar toolbar) {
        initToolbar(toolbar, false);
    }

    protected void initToolbar(Toolbar toolbar, boolean isBack) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        if (isBack) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    //状态栏沉浸(颜色资源)
    protected void initBar(int colorRes, boolean isBlackFront) {
//        ScreenUtils.cancelAdaptScreen(this);
        mImmersionBar = ImmersionBar.with(this);
        if (!isNightMode && isBlackFront)
            mImmersionBar.statusBarDarkFont(true, 0.2f)
                    //原理：如果当前设备支持状态栏字体变色，会设置状态栏字体为黑色，
                    // 如果当前设备不支持状态栏字体变色，会使当前状态栏加上透明度，否则不执行透明度
                    .statusBarColor(colorRes)
                    .navigationBarColor(ImmersionBar.isSupportNavigationIconDark() ? R.color.main_background : R.color.divider)
                    .navigationBarDarkIcon(ImmersionBar.isSupportNavigationIconDark())
                    .fitsSystemWindows(true)
                    .keyboardEnable(true)  //解决软键盘与底部输入框冲突问题
                    .init();
        else
            mImmersionBar.keyboardEnable(true)  //解决软键盘与底部输入框冲突问题
                    .statusBarColor(colorRes)
                    .navigationBarColor(colorRes)
                    .fitsSystemWindows(true)
                    .init();   //所有子类都将继承这些相同的属性
    }

    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH;
    }

    public void finishWithAnime() {
        if (isLollipop())
            finishAfterTransition();
        else
            finish();
    }

    protected void restartWithAnime(int bgId, int contentId) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            findViewById(bgId).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            animateRevealShow(findViewById(contentId), true, new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    findViewById(contentId).setVisibility(View.GONE);
                    ActivityUtils.finishAllActivitiesExceptNewest();
                    relaunchApp(true);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
        } else {
            ActivityUtils.finishAllActivitiesExceptNewest();
            relaunchApp(true);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void animateRevealShow(final View view, final boolean isReverse, @Nullable Animator.AnimatorListener listener) {
        if (!isLollipop())
            return;
        int cx = ScreenUtils.getScreenWidth() / 2;
        int cy = ScreenUtils.getScreenHeight() / 2;
        int finalRadius = Math.min(view.getWidth(), view.getHeight());
        Animator anim;
        if (isReverse) {
            //关闭
            anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, finalRadius, 0);
        } else {
            //开屏
            anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
        }
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(650);
        if (listener != null)
            anim.addListener(listener);
        anim.start();
    }
}
