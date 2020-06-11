package androidnews.kiloproject.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.blankj.utilcode.util.LogUtils;

import androidnews.kiloproject.R;
import androidnews.kiloproject.system.base.BaseActivity;
import androidnews.kiloproject.web.MWebChromeClient;
import androidnews.kiloproject.web.MWebViewClient;

/**
 * 自定义实现的H5Activity类，主要用于在页面中展示H5页面，整个Activity只有一个Fragment控件
 */
public class AboutActivity extends BaseActivity {

    public ProgressBar progressBar;
    public WebView webView;
    public ImageView ivError;
    public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h5);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        webView = findViewById(R.id.web_view);
        progressBar = findViewById(R.id.progress);
        ivError = findViewById(R.id.iv_error);

        initToolbar(toolbar, true);
        getSupportActionBar().setTitle(R.string.about);
        initBar(R.color.main_background, true);
    }

    @Override
    protected void initSlowly() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAllowFileAccess(false);
        webSettings.setUseWideViewPort(false);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setDatabaseEnabled(false);
        webSettings.setAppCacheEnabled(false);
        if (isLollipop())
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webSettings.setBlockNetworkImage(false);

        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                LogUtils.d("url=" + url);
                LogUtils.d("userAgent=" + userAgent);
                LogUtils.d("contentDisposition=" + contentDisposition);
                LogUtils.d("mimetype=" + mimetype);
                LogUtils.d("contentLength=" + contentLength);
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        webView.setWebChromeClient(new MWebChromeClient(mActivity));
        webView.setWebViewClient(new MWebViewClient(mActivity));

        String url = "file:///android_asset/about.html";
        webView.loadUrl(url);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
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

            try {
                webView.destroy();
            } catch (Throwable ex) {

            }
        }
        webView.removeAllViews();
        webView.destroy();
        webView = null;
        super.onDestroy();
    }

    @JavascriptInterface
    public void dismiss() {
        LogUtils.d("JS回调了dismiss()方法");
        finish();
    }

    @JavascriptInterface
    public void back() {
        LogUtils.d("JS回调了back()方法");
        onBackPressed();
    }
}