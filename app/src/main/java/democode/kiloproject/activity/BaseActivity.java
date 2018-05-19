package democode.kiloproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.gyf.barlibrary.ImmersionBar;

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
    }

    protected void openH5(String url,String title){

    }
}
