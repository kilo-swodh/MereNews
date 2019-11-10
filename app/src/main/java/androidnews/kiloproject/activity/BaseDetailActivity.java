package androidnews.kiloproject.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.RomUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ObservableWebView;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.DownloadProgressCallBack;
import com.zhouyou.http.exception.ApiException;

import androidnews.kiloproject.R;
import androidnews.kiloproject.system.AppConfig;
import androidnews.kiloproject.system.base.BaseActivity;
import androidnews.kiloproject.util.FileCompatUtils;


public class BaseDetailActivity extends BaseActivity implements ObservableScrollViewCallbacks {

    Toolbar toolbar;
    ObservableWebView webView;
    SkeletonScreen skeletonScreen;
//    SmartRefreshLayout refreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        webView = (ObservableWebView) findViewById(R.id.web_news);
        webView.setBackgroundColor(0);
        webView.getBackground().setAlpha(0);
//        if (ScreenUtils.getScreenWidth() * 2 > ScreenUtils.getScreenHeight())
        webView.setScrollViewCallbacks(this);
//        webView.setDrawingCacheEnabled(true);
//        webView.buildDrawingCache();
//        webView.buildLayer();

        initListener();

        if (!RomUtils.isMeizu() && AppConfig.isShowSkeleton)
            skeletonScreen = Skeleton.bind(webView)
                    .load(R.layout.layout_skeleton_news)
                    .duration(1000)
                    .color(R.color.main_background)
                    .show();
        initView();
        initBar(R.color.main_background, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (NetworkUtils.isConnected())
            getMenuInflater().inflate(R.menu.detail_items, menu);//加载menu布局
        return true;
    }

    private void initListener() {
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                WebView.HitTestResult result = ((WebView) v).getHitTestResult();
                if (null == result)
                    return false;
                int type = result.getType();
                final String url = result.getExtra();
                switch (type) {
                    case WebView.HitTestResult.UNKNOWN_TYPE: //未知
                    case WebView.HitTestResult.EDIT_TEXT_TYPE: // 选中的文字类型
                    case WebView.HitTestResult.PHONE_TYPE: // 处理拨号
                    case WebView.HitTestResult.EMAIL_TYPE: // 处理Email
                    case WebView.HitTestResult.GEO_TYPE: // 　地图类型
                    case WebView.HitTestResult.SRC_ANCHOR_TYPE: // 超链接
                        break;
                    case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE: // 带有链接的图片类型
                    case WebView.HitTestResult.IMAGE_TYPE: // 处理长按图片的菜单项
                        if (!TextUtils.isEmpty(url)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                            builder.setTitle(R.string.download)
                                    .setMessage(R.string.download_img_q)
                                    .setCancelable(true)
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            downloadImg(url);
                                        }
                                    }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                        }
                        return true;
                }
                return false;
            }
        });

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    private void downloadImg(String currentImg) {
        try {
            String fileName = currentImg.substring(currentImg.lastIndexOf('/'), currentImg.length());
            String path = FileCompatUtils.getMediaDir(mActivity);
            EasyHttp.downLoad(currentImg)
                    .savePath(path)
                    .saveName(fileName)//不设置默认名字是时间戳生成的
                    .execute(new DownloadProgressCallBack<String>() {
                        @Override
                        public void update(long bytesRead, long contentLength, boolean done) {
                        }

                        @Override
                        public void onStart() {
                            //开始下载
                        }

                        @Override
                        public void onComplete(String path) {
                            //下载完成，path：下载文件保存的完整路径
                            SnackbarUtils.with(webView)
                                    .setMessage(getString(R.string.download_success))
                                    .show();
                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
                        }

                        @Override
                        public void onError(ApiException e) {
                            //下载失败
                            SnackbarUtils.with(webView)
                                    .setMessage(getString(R.string.download_fail) + e.getMessage())
                                    .showError();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            SnackbarUtils.with(webView)
                    .setMessage(getString(R.string.download_fail))
                    .showError();
        }
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
        webSetting.setPluginState(WebSettings.PluginState.ON);
        if (isLollipop())
            webSetting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        switch (AppConfig.mTextSize) {
            case 0:
                webSetting.setTextZoom(130);
                break;
            case 1:
                webSetting.setTextZoom(100);
                break;
            case 2:
                webSetting.setTextZoom(70);
                break;
            case 3:
                webSetting.setTextZoom(160);
                break;
            case 4:
                webSetting.setTextZoom(50);
                break;
        }
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
    protected void initSlowly() {
    }

    protected void initView() {
    }

    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
    }

    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        Log.e("DEBUG", "onUpOrCancelMotionEvent: " + scrollState);
        if (scrollState == ScrollState.UP) {
            if (toolbarIsShown()) {
                hideToolbar();
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (toolbarIsHidden()) {
                showToolbar();
            }
        }
    }

    private boolean toolbarIsShown() {
        return ViewHelper.getTranslationY(toolbar) == 0;
    }

    private boolean toolbarIsHidden() {
        return ViewHelper.getTranslationY(toolbar) == -toolbar.getHeight();
    }

    private void showToolbar() {
        moveToolbar(0);
    }

    private void hideToolbar() {
        moveToolbar(-toolbar.getHeight());
    }

    private void moveToolbar(float toTranslationY) {
        if (ViewHelper.getTranslationY(toolbar) == toTranslationY) {
            return;
        }
        ValueAnimator animator = ValueAnimator.ofFloat(ViewHelper.getTranslationY(toolbar), toTranslationY).setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (webView == null || toolbar == null) return;
                float translationY = (float) animation.getAnimatedValue();
                ViewHelper.setTranslationY(toolbar, translationY);
                ViewHelper.setTranslationY(webView, translationY);
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) (webView).getLayoutParams();
                lp.height = (int) -translationY + findViewById(android.R.id.content).getHeight() - lp.topMargin;
                (webView).requestLayout();
            }
        });
        animator.start();
    }

    protected void hideSkeleton() {
        if (!RomUtils.isMeizu() && AppConfig.isShowSkeleton)
            skeletonScreen.hide();
    }
}