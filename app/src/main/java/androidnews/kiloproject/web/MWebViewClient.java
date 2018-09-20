package androidnews.kiloproject.web;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidnews.kiloproject.activity.H5Activity;

/**
 * 自定义实现WebViewClient类
 */
public class MWebViewClient extends WebViewClient {
    /**
     * 在webview加载URL的时候可以截获这个动作, 这里主要说它的返回值的问题：
     *	1、返回: return true;  webview处理url是根据程序来执行的。
     *	2、返回: return false; webview处理url是在webview内部执行。
     */

    H5Activity h5Activity = null;

    public MWebViewClient(Activity activity){
        if (activity instanceof H5Activity){
            h5Activity = (H5Activity) activity;
        }
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        WebView.HitTestResult result = view.getHitTestResult();
        if (!TextUtils.isEmpty(url) && result == null) {
            view.loadUrl(url);
            return true;
        }
        return false;
    }

    /**
     * 在webview开始加载页面的时候回调该方法
     */
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        h5Activity.ivError.setVisibility(View.GONE);
    }

    /**
     * 在webview加载页面结束的时候回调该方法
     */
    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
    }

    /**
     * 加载页面失败的时候回调该方法
     */
    // 该方法为android23中新添加的API，android23中会执行该方法
    @TargetApi(21)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        h5Activity.ivError.setVisibility(View.VISIBLE);
    }

    /**
     * 加载页面失败的时候回调该方法
     */
    /**
     * 在android23中改方法被onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) 替代
     * 因此在android23中执行替代方法
     * 在android23之前执行该方法
     * @param view
     * @param errorCode
     * @param description
     * @param failingUrl
     */
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        h5Activity.ivError.setVisibility(View.VISIBLE);
    }
}