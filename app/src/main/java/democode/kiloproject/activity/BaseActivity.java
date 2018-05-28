package democode.kiloproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.gyf.barlibrary.ImmersionBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import democode.kiloproject.receiver.MessageEvent;

/**
 * Created by Administrator on 2017/12/9.
 */

public abstract class BaseActivity extends AppCompatActivity {
    BaseActivity mActivity;
    boolean isStart = false;
    private ImmersionBar mImmersionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mActivity = this;
        isStart = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isStart) {
            //初始化逻辑代码
            initView();
            isStart = false;
        }
    }

    //对只需要一次初始化的耗时操作或者界面绘制放在这里
    abstract void initView();


    //状态栏沉浸(透明版)
    protected void initStateBar(boolean isBlackFront) {
        mImmersionBar = ImmersionBar.with(this);
        if (isBlackFront) {
            mImmersionBar.statusBarDarkFont(true, 0.2f)
                    //原理：如果当前设备支持状态栏字体变色，会设置状态栏字体为黑色，
                    // 如果当前设备不支持状态栏字体变色，会使当前状态栏加上透明度，否则不执行透明度
                    .keyboardEnable(true)  //解决软键盘与底部输入框冲突问题
                    .transparentStatusBar()  //透明状态栏，不写默认透明色
                    .transparentNavigationBar()
                    .init();
        } else {
            mImmersionBar.keyboardEnable(true)  //解决软键盘与底部输入框冲突问题
                    .transparentStatusBar()  //透明状态栏，不写默认透明色
                    .transparentNavigationBar()
                    .init();   //所有子类都将继承这些相同的属性
        }
    }

    //状态栏沉浸
    protected void initStateBar(int colorRes, boolean isBlackFront) {
        mImmersionBar = ImmersionBar.with(this);
        if (isBlackFront) {
            mImmersionBar.statusBarDarkFont(true, 0.2f)
                    //原理：如果当前设备支持状态栏字体变色，会设置状态栏字体为黑色，
                    // 如果当前设备不支持状态栏字体变色，会使当前状态栏加上透明度，否则不执行透明度
                    .statusBarColor(colorRes)
                    .navigationBarColor(colorRes)
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //必须调用该方法，防止内存泄漏，不调用该方法，如果界面bar发生改变，在不关闭app的情况下，退出此界面再进入将记忆最后一次bar改变的状态
        if (mImmersionBar != null)
            mImmersionBar.destroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * POSTING (默认) 表示事件处理函数的线程跟发布事件的线程在同一个线程。
     MAIN 表示事件处理函数的线程在主线程(UI)线程，因此在这里不能进行耗时操作。
     BACKGROUND 表示事件处理函数的线程在后台线程，因此不能进行UI操作。如果发布事件的线程是主线程(UI线程)，那么事件处理函数将会开启一个后台线程，如果果发布事件的线程是在后台线程，那么事件处理函数就使用该线程。
     ASYNC 表示无论事件发布的线程是哪一个，事件处理函数始终会新建一个子线程运行，同样不能进行UI操作。
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {/* Do something */}
    // 使用 EventBus.getDefault().post(messageEvent); 发送事件
}
