package androidnews.kiloproject.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.appcompat.widget.Toolbar;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.orzangleli.xdanmuku.DanmuContainerView;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import androidnews.kiloproject.R;
import androidnews.kiloproject.adapter.DanmuAdapter;
import androidnews.kiloproject.entity.data.CacheNews;
import androidnews.kiloproject.entity.data.DanmuEntity;
import androidnews.kiloproject.entity.net.CnBetaDetailData;
import androidnews.kiloproject.entity.net.CnbetaCommentData;
import androidnews.kiloproject.system.AppConfig;
import androidnews.kiloproject.util.CNBetaUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static androidnews.kiloproject.entity.data.CacheNews.CACHE_COLLECTION;
import static androidnews.kiloproject.entity.data.CacheNews.CACHE_HISTORY;
import static androidnews.kiloproject.system.AppConfig.GET_CNBETA_EXTRA;
import static androidnews.kiloproject.system.AppConfig.GET_CNBETA_MD5_EXTRA;
import static androidnews.kiloproject.system.AppConfig.HOST_CNBETA_SHARE;
import static androidnews.kiloproject.system.AppConfig.TYPE_CNBETA;
import static androidnews.kiloproject.system.AppConfig.isNightMode;

public class CnBetaDetailActivity extends BaseDetailActivity {

    private ViewStub videoStub;
    private DanmuContainerView danmuView;

    private String html;
    private CnBetaDetailData currentData;
    private boolean isStar;
    private int isCommentReady = 0;
    CnbetaCommentData commentData;
    String sid;

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
                        intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND);//设置分享行为
                        intent.setType("text/plain");//设置分享内容的类型
                        if (!TextUtils.isEmpty(currentData.getResult().getTitle()))
                            intent.putExtra(Intent.EXTRA_SUBJECT, currentData.getResult().getTitle());//添加分享内容标题
                        intent.putExtra(Intent.EXTRA_TEXT, "【" + currentData.getResult().getTitle() + "】"
                                + HOST_CNBETA_SHARE + currentData.getResult().getSid());//添加分享内容
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
                        switch (isCommentReady){
                            case 0:
                                SnackbarUtils.with(toolbar).setMessage(getString(R.string.loading)).show();
                                break;
                            case -1:
                                SnackbarUtils.with(toolbar).setMessage(getString(R.string.no_comment)).show();
                                break;
                            case 1:
                                for (CnbetaCommentData.ResultBean resultBean : commentData.getResult()) {
                                    DanmuEntity danmuEntity = new DanmuEntity();
                                    danmuEntity.setContent(resultBean.getContent());
                                    danmuEntity.setType(0);
                                    danmuEntity.setUserName(resultBean.getUsername());
                                    danmuView.addDanmu(danmuEntity);
                                }
                                break;
                        }
                        break;
                    case R.id.action_link:
                        ClipboardManager cm = (ClipboardManager) Utils.getApp().getSystemService(Context.CLIPBOARD_SERVICE);
                        //noinspection ConstantConditions
                        cm.setPrimaryClip(ClipData.newPlainText("link", HOST_CNBETA_SHARE + currentData.getResult().getSid()));
                        SnackbarUtils.with(toolbar).setMessage(getString(R.string.action_link)
                                + " " + getString(R.string.successful)).show();
                        break;
                    case R.id.action_browser:
                        try {
                            Uri uri = Uri.parse(HOST_CNBETA_SHARE + currentData.getResult().getSid());
                            intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
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
        sid = getIntent().getStringExtra("sid");
        if (!TextUtils.isEmpty(sid)) {
            String dataUrl = AppConfig.GET_CNBETA_DETAIL.replace("{sid}",sid).replace("{timestamp}", System.currentTimeMillis() + "");
            dataUrl = dataUrl + GET_CNBETA_EXTRA + CNBetaUtils.md5(dataUrl + GET_CNBETA_MD5_EXTRA);

            EasyHttp.get(AppConfig.HOST_CNBETA + dataUrl)
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
                                try {
                                    currentData = gson.fromJson(response, CnBetaDetailData.class);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    ToastUtils.showShort(getString(R.string.server_fail) + e.getMessage());
                                    finish();
                                }
                                Observable.create(new ObservableOnSubscribe<Boolean>() {
                                    @Override
                                    public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                                        if (currentData != null && Integer.parseInt(currentData.getResult().getComments()) > 0)
                                            initComment();
                                        else
                                            isCommentReady = -1;
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
                String source = "";
                String body = "";
                if (currentData == null)
                    return;
                try {
                    source = currentData.getResult().getSource();
                    body = currentData.getResult().getBodytext();
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
                        + "<p><h2>" + currentData.getResult().getTitle() + "</h2></p>"
                        + "<p><div><div id=\"from\">" + source +
                        "</div><div id=\"time\">" + currentData.getResult().getTime() + "</div></div></p>"
                        + "<font size=\"4\">"
                        + body + "</font></body>" +
                        "</html>";
                e.onNext(true);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean b) throws Exception {
                        getSupportActionBar().setDisplayShowTitleEnabled(false);
                        if (b && webView != null) {
                            webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", "about:blank");
//                            webView.loadData(html, "text/html; charset=UTF-8", null);
                            saveCacheAsyn(CACHE_HISTORY);
                        }
                    }
                });
    }

    private void saveCacheAsyn(int type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (currentData == null)
                    return;
                List<CacheNews> list = new ArrayList<>();
                try {
                    list = LitePal.where("docid = ?", String.valueOf(currentData.getResult().getSid())).find(CacheNews.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (list != null && list.size() > 0)
                    for (CacheNews cacheNews : list) {
                        if (cacheNews.getType() == type)
                            return;
                    }
                CacheNews cacheNews = new CacheNews(currentData.getResult().getTitle(),
                        AppConfig.HOST_CNBETA_IMG + currentData.getResult().getThumb(),
                        currentData.getResult().getSource(),
                        String.valueOf(currentData.getResult().getSid()),
                        html,
                        type,
                        TYPE_CNBETA);
                cacheNews.setUrl(HOST_CNBETA_SHARE + currentData.getResult().getSid());
                cacheNews.setTimeStr(currentData.getResult().getTime());
                cacheNews.save();
            }
        }).start();
    }

    private boolean checkStar(boolean isClear) {
        List<CacheNews> list = null;
        try {
            list = LitePal.where("docid = ?", String.valueOf(currentData.getResult().getSid())).find(CacheNews.class);
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

    private void initComment(){
        String dataUrl = AppConfig.GET_CNBETA_COMMENT.replace("{sid}",sid).replace("{timestamp}", System.currentTimeMillis() + "");
        dataUrl = dataUrl + GET_CNBETA_EXTRA + CNBetaUtils.md5(dataUrl + GET_CNBETA_MD5_EXTRA);

        EasyHttp.get(AppConfig.HOST_CNBETA + dataUrl)
                .readTimeOut(30 * 1000)//局部定义读超时
                .writeTimeOut(30 * 1000)
                .connectTimeout(30 * 1000)
                .timeStamp(true)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onSuccess(String response) {
                        if (!TextUtils.isEmpty(response) || TextUtils.equals(response, "{}")) {
                            try {
                                commentData = gson.fromJson(response, CnbetaCommentData.class);
                                if (commentData.getResult().size() < 1)
                                    isCommentReady = -1;
                                else {
                                    videoStub = (ViewStub) findViewById(R.id.stub_danmu);
                                    videoStub.setVisibility(View.VISIBLE);
                                    danmuView = findViewById(R.id.view_danmu);
                                    DanmuAdapter adapter = new DanmuAdapter(mActivity);
                                    danmuView.setAdapter(adapter);
                                    danmuView.setSpeed(2);
                                    danmuView.setGravity(DanmuContainerView.GRAVITY_TOP);
                                    isCommentReady = 1;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else
                            isCommentReady = -1;
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        try {
            menu.getItem(1).setVisible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    protected void initWeb() {
        super.initWeb();
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (webView == null)return;
                webView.loadUrl("javascript:document.body.style.paddingBottom=\"" + ConvertUtils.dp2px(16) + "px\"; void 0");
            }
        });
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
}
