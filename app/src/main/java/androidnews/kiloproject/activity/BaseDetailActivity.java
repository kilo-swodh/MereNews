package androidnews.kiloproject.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebSettings;
import android.widget.ProgressBar;

import com.blankj.utilcode.util.NetworkUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.tobiasrohloff.view.NestedScrollWebView;

import androidnews.kiloproject.R;
import androidnews.kiloproject.system.base.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BaseDetailActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.web_news)
    NestedScrollWebView webView;
//    @BindView(R.id.refreshLayout)
//    SmartRefreshLayout refreshLayout;

    public static final int MAX_HISTORY = 512;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        initView();
        initStateBar(R.color.main_background, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (NetworkUtils.isConnected())
            getMenuInflater().inflate(R.menu.detail_items, menu);//加载menu布局
        return true;
    }

    protected void initWeb() {
        WebSettings webSetting = webView.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSetting.setAppCacheEnabled(true);
        webSetting.setDatabaseEnabled(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            // 如果先调用destroy()方法，则会命中if (isDestroyed()) return;这一行代码，需要先onDetachedFromWindow()，再
            // destory()
            ViewParent parent = webView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(webView);
            }

            webView.stopLoading();
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            webView.getSettings().setJavaScriptEnabled(false);
            webView.clearHistory();
            webView.clearView();
            webView.removeAllViews();
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }

    @Override
    protected void initSlowly() { }

    protected void initView(){}
}
