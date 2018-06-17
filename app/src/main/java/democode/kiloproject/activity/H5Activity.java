package democode.kiloproject.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.blankj.utilcode.util.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import democode.kiloproject.R;
import democode.kiloproject.web.MWebChromeClient;
import democode.kiloproject.web.MWebViewClient;
import democode.kiloproject.widget.TitleBar;

/**
 * 自定义实现的H5Activity类，主要用于在页面中展示H5页面，整个Activity只有一个Fragment控件
 */
public class H5Activity extends BaseActivity {

    @BindView(R.id.progress_bar)
    public ProgressBar progressBar;
    @BindView(R.id.webview)
    WebView webview;
    @BindView(R.id.title_bar)
    public TitleBar titlebar;
    @BindView(R.id.iv_error)
    public ImageView ivError;

    public static String WEBVIEW_URL = "web_url";

    private String loadURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h5);
        ButterKnife.bind(this);
        initStateBar(R.color.colorPrimary, false);
    }

    @Override
    void initView() {
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAllowFileAccess(false);
        webSettings.setUseWideViewPort(false);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setDatabaseEnabled(false);
        webSettings.setAppCacheEnabled(false);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webSettings.setBlockNetworkImage(false);
        webview.addJavascriptInterface(this, "myWebBridge");

        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webview.setDownloadListener(new DownloadListener() {
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
        webview.setWebChromeClient(new MWebChromeClient(mActivity));
        webview.setWebViewClient(new MWebViewClient(mActivity));

        titlebar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        titlebar.setTitleColor(Color.WHITE);
        titlebar.setLeftImageResource(R.drawable.ic_arrow_back_white_24dp);
        titlebar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        loadURL = getIntent().getStringExtra(WEBVIEW_URL);
        if (!TextUtils.isEmpty(loadURL)) {
            webview.loadUrl(loadURL);
            titlebar.setTitle("加载中");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
            webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            webview.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        webview.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        webview.onResume();
    }

    @Override
    protected void onDestroy() {
        webview.removeAllViews();
        webview.destroy();
        webview = null;
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

    public void loadWebColor(int color, boolean isBlackFront) {
        initStateBarInt(color, isBlackFront);
        titlebar.setBackgroundColor(color);
        if (isBlackFront) {
            titlebar.setTitleColor(Color.BLACK);
            titlebar.setLeftImageResource(R.drawable.ic_arrow_back_black_24dp);
        } else {
            titlebar.setTitleColor(Color.WHITE);
            titlebar.setLeftImageResource(R.drawable.ic_arrow_back_white_24dp);
        }
    }
}