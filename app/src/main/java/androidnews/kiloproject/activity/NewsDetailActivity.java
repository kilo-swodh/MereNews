package androidnews.kiloproject.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.net.Uri;

import androidx.appcompat.widget.Toolbar;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.BounceInterpolator;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.bumptech.glide.Glide;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.MoveType;
import com.yhao.floatwindow.PermissionListener;
import com.yhao.floatwindow.Screen;
import com.yhao.floatwindow.ViewStateListener;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.litepal.LitePal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import androidnews.kiloproject.R;
import androidnews.kiloproject.entity.data.CacheNews;
import androidnews.kiloproject.entity.net.NewsDetailData;
import androidnews.kiloproject.util.GlideUtils;
import androidnews.kiloproject.widget.MyJzvdStd;
import cn.jzvd.Jzvd;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static androidnews.kiloproject.entity.data.CacheNews.CACHE_COLLECTION;
import static androidnews.kiloproject.entity.data.CacheNews.CACHE_HISTORY;
import static androidnews.kiloproject.system.AppConfig.GET_NEWS_DETAIL;
import static androidnews.kiloproject.system.AppConfig.TYPE_NETEASE_START;
import static androidnews.kiloproject.system.AppConfig.isNightMode;

public class NewsDetailActivity extends BaseDetailActivity {
    private String html;
    private NewsDetailData currentData;
    private boolean isStar = false;

    private boolean isLand = false;
    private ViewStub videoStub;
    private FrameLayout videoLayout;

    private int type = 0;
    public static final int TPYE_AUDIO = 1024;

    private List<NewsDetailData.VideoBean> videoList = new ArrayList();
    private int videoIndex = 0;

    @Override
    protected void initView() {
        initToolbar(toolbar, true);
        getSupportActionBar().setTitle(getString(R.string.loading));
        //menu item点击事件监听
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.action_share:
                        if (currentData == null || currentData.getShareLink() == null)
                            break;
                        String title = "";
                        try {
                            title = currentData.getTitle();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND);//设置分享行为
                        intent.setType("text/plain");//设置分享内容的类型
                        if (!TextUtils.isEmpty(title))
                            intent.putExtra(Intent.EXTRA_SUBJECT, title);//添加分享内容标题
                        intent.putExtra(Intent.EXTRA_TEXT, "【" + title + "】"
                                + currentData.getShareLink());//添加分享内容
                        //创建分享的Dialog
                        intent = Intent.createChooser(intent, getString(R.string.action_share));
                        startActivity(intent);
                        break;
                    case R.id.action_star:
                        if (isStar) {
                            Observable.create(new ObservableOnSubscribe<Boolean>() {
                                @Override
                                public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                                    e.onNext(checkStar(true));
                                    e.onComplete();
                                }
                            }).subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<Boolean>() {
                                        @Override
                                        public void accept(Boolean aBoolean) throws Exception {
                                            if (aBoolean) {
                                                item.setIcon(R.drawable.ic_star_no);
                                                SnackbarUtils.with(toolbar).setMessage(getString(R.string.star_no)).show();
                                            } else
                                                SnackbarUtils.with(toolbar).setMessage(getString(R.string.fail)).showError();
                                        }
                                    });
                            isStar = false;
                        } else {
                            item.setIcon(R.drawable.ic_star_ok);
                            saveCacheAsyn(CACHE_COLLECTION);
                            SnackbarUtils.with(toolbar).setMessage(getString(R.string.star_yes)).show();
                            isStar = true;
                        }
                        break;
                    case R.id.action_comment:
//                        ObjectAnimator animator = ObjectAnimator.ofInt(webView,"scrollY",webView.getScrollY(),0);
//                        animator.setDuration(300).start();
                        if (currentData == null || TextUtils.isEmpty(currentData.getReplyBoard()) || TextUtils.isEmpty(currentData.getDocid())) {
                            SnackbarUtils.with(toolbar).setMessage(getString(R.string.no_comment)).showError();
                            break;
                        }
                        intent = new Intent(mActivity, CommentActivity.class);
                        intent.putExtra("board", currentData.getReplyBoard());
                        intent.putExtra("docid", currentData.getDocid());
                        startActivity(intent);
                        break;
                    case R.id.action_link:
                        if (currentData != null) {
                            ClipboardManager cm = (ClipboardManager) Utils.getApp().getSystemService(Context.CLIPBOARD_SERVICE);
                            //noinspection ConstantConditions
                            cm.setPrimaryClip(ClipData.newPlainText("link", currentData.getShareLink()));
                            SnackbarUtils.with(toolbar).setMessage(getString(R.string.action_link)
                                    + " " + getString(R.string.successful)).show();
                        }
                        break;
                    case R.id.action_browser:
                        try {
                            Uri uri = Uri.parse(currentData.getShareLink());
                            intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case R.id.action_video:
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                            if (videoList.size() > 0) {
                                View view = getLayoutInflater().inflate(R.layout.layout_pip_video,null);
                                MyJzvdStd videoView = view.findViewById(R.id.item_card_vid);
                                loadVideo(videoView);

                                view.findViewById(R.id.btn_exit).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        FloatWindow.destroy("Video");
                                        Jzvd.releaseAllVideos();
                                    }
                                });
                                view.findViewById(R.id.btn_previous).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (videoIndex == 0){
                                            SnackbarUtils.with(toolbar).setMessage(getString(R.string.no_video)).show();
                                        }else {
                                            videoIndex--;
                                            loadVideo(videoView);
                                        }
                                    }
                                });
                                view.findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (videoIndex == (videoList.size() - 1)){
                                            SnackbarUtils.with(toolbar).setMessage(getString(R.string.no_video)).show();
                                        }else {
                                            videoIndex++;
                                            loadVideo(videoView);
                                        }
                                    }
                                });

                                FloatWindow
                                        .with(mActivity.getApplicationContext())
                                        .setView(view)
                                        .setWidth(Screen.width, 0.8f) //设置悬浮控件宽高
                                        .setHeight(Screen.width, 0.7f)
                                        .setX(0)
                                        .setY(0)
                                        .setMoveType(MoveType.slide, 50, 50)
                                        .setMoveStyle(500, new BounceInterpolator())
                                        .setViewStateListener(mViewStateListener)
                                        .setPermissionListener(mPermissionListener)
                                        .setDesktopShow(true)
                                        .setTag("Video")
                                        .build();
                                FloatWindow.get("Video").show();
                            } else
                                SnackbarUtils.with(toolbar).setMessage(getString(R.string.no_video)).show();
                        } else
                            SnackbarUtils.with(toolbar).setMessage(getString(R.string.not_support)).show();
                        break;
                }
                return false;
            }
        });
//        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
//            @Override
//            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
//                initSlowly();
//            }
//        });
    }

    @Override
    protected void initSlowly() {
        String docid = getIntent().getStringExtra("docid");
        if (!TextUtils.isEmpty(docid)) {
            EasyHttp.get(GET_NEWS_DETAIL.replace("{docid}", docid))
                    .readTimeOut(30 * 1000)//局部定义读超时
                    .writeTimeOut(30 * 1000)
                    .connectTimeout(30 * 1000)
                    .timeStamp(true)
                    .execute(new SimpleCallBack<String>() {
                        @Override
                        public void onError(ApiException e) {
                            SnackbarUtils.with(toolbar).setMessage(getString(R.string.load_fail) + e.getMessage()).showError();
                            hideSkeleton();
//                            refreshLayout.finishRefresh();
                        }

                        @Override
                        public void onSuccess(String response) {
                            hideSkeleton();
                            if (!TextUtils.isEmpty(response) || TextUtils.equals(response, "{}")) {
                                String jsonNoHeader = response.substring(20, response.length());
                                String jsonFine = jsonNoHeader.substring(0, jsonNoHeader.length() - 1);
                                if (response.contains("点这里升级")) {
                                    loadError();
                                    return;
                                }
                                try {
                                    currentData = gson.fromJson(jsonFine, NewsDetailData.class);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    ToastUtils.showShort(getString(R.string.server_fail) + e.getMessage());
                                    finish();
                                }
                                if (currentData == null || TextUtils.isEmpty(currentData.getBody())) {
                                    loadError();
                                    return;
                                }
                                Observable.create(new ObservableOnSubscribe<Boolean>() {
                                    @Override
                                    public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                                        currentData.setBody(deleteAd(currentData.getBody()));
                                        e.onNext(checkStar(false));
                                        e.onComplete();
                                    }
                                }).subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Consumer<Boolean>() {
                                            @Override
                                            public void accept(Boolean aBoolean) throws Exception {
                                                if (aBoolean) {
                                                    isStar = true;
                                                    try {
                                                        toolbar.getMenu().findItem(R.id.action_star).setIcon(R.drawable.ic_star_ok);
                                                    } catch (Exception e) {
                                                    }
                                                }
//                                                refreshLayout.finishRefresh();
                                            }
                                        });
                                if (webView != null) {
                                    initWeb();
                                    loadUrl();
                                }
                            } else {
//                                refreshLayout.finishRefresh();
                                SnackbarUtils.with(toolbar).setMessage(getString(R.string.load_fail)).showError();
                            }
                        }
                    });
        } else {
            Observable.create(new ObservableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                    html = getIntent().getStringExtra("htmlText");
                    if (isNightMode)
                        html.replace("<body text=\"#333\">", "<body bgcolor=\"#212121\" body text=\"#ccc\">");
                    if (!StringUtils.isEmpty(html)) {
                        e.onNext(true);
                    } else {
                        e.onNext(false);
                    }
                    e.onComplete();
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            hideSkeleton();
                            if (aBoolean) {
                                initWeb();
                                getSupportActionBar().setTitle(R.string.news);
                                webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", "about:blank");
//                                webView.loadData(html, "text/html; charset=UTF-8", null);
                            } else
                                SnackbarUtils.with(toolbar).setMessage(getString(R.string.load_fail)).showError();
                        }
                    });
        }
    }

    private void loadUrl() {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                String title = "";
                String source = "";
                String pTime = "";
                String body = "";
                if (currentData == null)
                    return;
                try {
                    title = currentData.getTitle();
                    pTime = currentData.getPtime();
                    body = currentData.getBody();
                    if (!TextUtils.isEmpty(currentData.getSource()) && !currentData.getSource().equals("null"))
                        source = currentData.getSource();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                String colorBody = isNightMode ? "<body bgcolor=\"#212121\" body text=\"#ccc\">" : "<body text=\"#333\">";
                html = "<!DOCTYPE html>" +
                        "<html lang=\"zh\">" +
                        "<head>" +
                        "<meta charset=\"UTF-8\" />" +
                        "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />" +
                        "<meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\" />" +
                        "<title>Document</title>" +
                        "<style type=\"text/css\">" +
                        "body{\n" +
                        "margin-left:18px;\n" +
                        "margin-right:18px;\n" +
                        "}" +
                        "p {line-height:36px;}" +
                        "body img{" +
                        "width: 100%;" +
                        "height: 100%;" +
                        "}" +
                        "body video{" +
                        "width: 100%;" +
                        "height: 100%;" +
                        "}" +
                        "p{margin: 25px auto}" +
                        "div{width:100%;height:30px;} #from{width:auto;float:left;color:gray;} #time{width:auto;float:right;color:gray;}" +
                        "</style>" +
                        "</head>" +
                        colorBody
                        + "<p><h2>" + title + "</h2></p>"
                        + "<p><div><div id=\"from\">" + source +
                        "</div><div id=\"time\">" + pTime + "</div></div></p>"
                        + "<font size=\"4\">"
                        + body + "</font></body>" +
                        "</html>";
                if (currentData.getVideo() != null) {
                    for (NewsDetailData.VideoBean videoBean : currentData.getVideo()) {
                        String mediaUrl = videoBean.getUrl_mp4();
                        if (TextUtils.isEmpty(mediaUrl)) {
                            mediaUrl = videoBean.getUrl_mp4();
                        }

                        if (mediaUrl.endsWith(".mp3")) {       //音频
                            html = html.replace(videoBean.getRef(),
                                    "<audio  src=\"" + mediaUrl +
                                            "\" controls=\"controls\" src=\"" + videoBean.getCover() + "\"></audio >");
                            type = TPYE_AUDIO;
                        } else {
                            html = html.replace(videoBean.getRef(),
                                    "<video src=\"" + mediaUrl +
                                            "\" controls=\"controls\" poster=\"" + videoBean.getCover() + "\"type=\"video/mp4\"></video>");
                            videoList.add(videoBean);
                        }
                    }
                }
                if (currentData.getImg() != null) {
                    for (NewsDetailData.ImgBean imgBean : currentData.getImg()) {
                        html = html.replace(imgBean.getRef(), "<img src=\"" + imgBean.getSrc() + "\"/>");
                    }
                }
                e.onNext(true);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean b) throws Exception {
                        if (videoList.size() > 0) {
                            videoStub = (ViewStub) findViewById(R.id.stub_video);
                            videoStub.setVisibility(View.VISIBLE);
                            videoLayout = findViewById(R.id.fl_video_full);
                            videoLayout.setKeepScreenOn(true);
                        }
                        getSupportActionBar().setDisplayShowTitleEnabled(false);
                        if (b && webView != null) {
                            webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", "about:blank");
//                            webView.loadData(html, "text/html; charset=UTF-8", null);
                            saveCacheAsyn(CACHE_HISTORY);
                        }
                    }
                });
    }

    private String deleteAd(String data) {
        if (data.contains("<!--SPINFO")) {
            int index = data.indexOf("<!--SPINFO");
            int imgHead = data.indexOf("<!--IMG#", index);
            if (imgHead != -1) {
                int imgEnd = data.indexOf("-->", imgHead);
                String resultStart = data.substring(0, imgHead - 1);
                String resultEnd = data.substring(imgEnd + 3, data.length() - 1);
                data = resultStart + resultEnd;
            }
        }
        if (data.contains("<p>原标题：")) {
            int imgHead = data.indexOf("<p>原标题：");
            int imgEnd = data.indexOf("</p>", imgHead);
            String resultStart = data.substring(0, imgHead - 1);
            String resultEnd = data.substring(imgEnd + 4, data.length() - 1);
            data = resultStart + resultEnd;
        }
        return data;
    }

    private PermissionListener mPermissionListener = new PermissionListener() {
        @Override
        public void onSuccess() {
            LogUtils.d("permission success");
        }

        @Override
        public void onFail() {
            SnackbarUtils.with(toolbar).setMessage(getString(R.string.message_permission_rationale)).showError();
            LogUtils.d("permission fail");
        }
    };

    private ViewStateListener mViewStateListener = new ViewStateListener() {
        @Override
        public void onPositionUpdate(int x, int y) {
            Log.d("FloatWindow", "onPositionUpdate: x=" + x + " y=" + y);
        }

        @Override
        public void onShow() {
            Log.d("FloatWindow", "onShow");
        }

        @Override
        public void onHide() {
            Log.d("FloatWindow", "onHide");
        }

        @Override
        public void onDismiss() {
            Log.d("FloatWindow", "onDismiss");
        }

        @Override
        public void onMoveAnimStart() {
            Log.d("FloatWindow", "onMoveAnimStart");
        }

        @Override
        public void onMoveAnimEnd() {
            Log.d("FloatWindow", "onMoveAnimEnd");
        }

        @Override
        public void onBackToDesktop() {
            Log.d("FloatWindow", "onBackToDesktop");
        }
    };

    private void saveCacheAsyn(int type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (currentData == null)
                    return;
                List<CacheNews> list = new ArrayList<>();
                try {
                    list = LitePal.where("docid = ?", currentData.getDocid()).find(CacheNews.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (list != null && list.size() > 0)
                    for (CacheNews cacheNews : list) {
                        if (cacheNews.getType() == type)
                            return;
                    }
                CacheNews cacheNews = new CacheNews(currentData.getTitle(),
                        currentData.getRecImgsrc(),
                        currentData.getSource(),
                        currentData.getDocid(),
                        html,
                        type,
                        TYPE_NETEASE_START);
                cacheNews.save();
            }
        }).start();
    }

    private boolean checkStar(boolean isClear) {
        List<CacheNews> list = null;
        try {
            list = LitePal.where("docid = ?", currentData.getDocid()).find(CacheNews.class);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        if (list != null && list.size() > 0) {
            for (CacheNews cacheNews : list) {
                if (cacheNews.getType() == CACHE_COLLECTION) {
                    if (isClear) {
                        LitePal.delete(CacheNews.class, cacheNews.getId());
                        setResult(RESULT_OK);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void initWeb() {
        super.initWeb();
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (webView == null) return;
                webView.loadUrl("javascript:document.body.style.paddingBottom=\"" + ConvertUtils.dp2px(16) + "px\"; void 0");
            }

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                ScreenUtils.getScreenRotation(mActivity);
                fullScreen(true);
                ScreenUtils.setFullScreen(mActivity);
                webView.setVisibility(View.GONE);
                videoLayout.setVisibility(View.VISIBLE);
                videoLayout.addView(view);
                super.onShowCustomView(view, callback);
            }

            @Override
            public void onHideCustomView() {
                fullScreen(false);
                ScreenUtils.setNonFullScreen(mActivity);
                webView.setVisibility(View.VISIBLE);
                videoLayout.setVisibility(View.GONE);
                videoLayout.removeAllViews();
                super.onHideCustomView();

            }
        });
    }

    private void fullScreen(boolean isStart) {
        if (isStart) {
            toolbar.setVisibility(View.GONE);
            isLand = ScreenUtils.isLandscape();
            try {
                if (currentData != null && currentData.getVideo().get(0).getVideoRatio() > 1) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            toolbar.setVisibility(View.VISIBLE);
            if (isLand)
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);
            else
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
        }
    }

    private void loadVideo(MyJzvdStd videoView){
        NewsDetailData.VideoBean videoItem = videoList.get(videoIndex);
        videoView.setUp(videoItem.getUrl_mp4(), videoItem.getAlt(), Jzvd.SCREEN_WINDOW_NORMAL);
        if (!TextUtils.isEmpty(videoItem.getCover()) && GlideUtils.isValidContextForGlide(mActivity))
            Glide.with(mActivity)
                    .load(videoItem.getCover())
                    .into(videoView.thumbImageView);
    }

    private void loadError(){
        ToastUtils.showShort(getString(R.string.server_fail));
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (type != TPYE_AUDIO)
            webView.onPause();
        if (videoList.size() > 0)
            Jzvd.releaseAllVideos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (type != TPYE_AUDIO)
            webView.onResume();
    }

    @Override
    public void onBackPressed() {
        if (videoList.size() > 0 && Jzvd.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTranslucentOrFloating()) {
            LogUtils.d("avoid calling setRequestedOrientation when Oreo.");
            return;
        }
        super.setRequestedOrientation(requestedOrientation);
    }

    private boolean isTranslucentOrFloating(){
        boolean isTranslucentOrFloating = false;
        try {
            int [] styleableRes = (int[]) Class.forName("com.android.internal.R$styleable").getField("Window").get(null);
            final TypedArray ta = obtainStyledAttributes(styleableRes);
            Method m = ActivityInfo.class.getMethod("isTranslucentOrFloating", TypedArray.class);
            m.setAccessible(true);
            isTranslucentOrFloating = (boolean)m.invoke(null, ta);
            m.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isTranslucentOrFloating;
    }

    private boolean fixOrientation(){
        try {
            Field field = Activity.class.getDeclaredField("mActivityInfo");
            field.setAccessible(true);
            ActivityInfo o = (ActivityInfo)field.get(this);
            o.screenOrientation = -1;
            field.setAccessible(false);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
