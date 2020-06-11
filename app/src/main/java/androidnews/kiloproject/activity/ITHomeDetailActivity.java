package androidnews.kiloproject.activity;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.widget.Toolbar;

import android.os.Build;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import androidnews.kiloproject.R;
import androidnews.kiloproject.entity.data.CacheNews;
import androidnews.kiloproject.entity.net.IThomeDetailData;
import androidnews.kiloproject.util.XmlParseUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static androidnews.kiloproject.entity.data.CacheNews.CACHE_COLLECTION;
import static androidnews.kiloproject.entity.data.CacheNews.CACHE_HISTORY;
import static androidnews.kiloproject.system.AppConfig.GET_IT_HOME_DETAIL;
import static androidnews.kiloproject.system.AppConfig.HOST_IT_HOME;
import static androidnews.kiloproject.system.AppConfig.TYPE_ITHOME_START;
import static androidnews.kiloproject.system.AppConfig.isNightMode;

public class ITHomeDetailActivity extends BaseDetailActivity {
    private String html;
    private IThomeDetailData currentData;
    private boolean isStar = false;
    private String currentUrl;

    private String newsId;
    private String title;
    private String pTime;
    private String img;

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
                        if (!TextUtils.isEmpty(currentData.getDetail())) {    //HTML
                            intent = new Intent();
                            intent.setAction(Intent.ACTION_SEND);//设置分享行为
                            intent.setType("text/plain");//设置分享内容的类型
                            if (!TextUtils.isEmpty(title))
                                intent.putExtra(Intent.EXTRA_SUBJECT, title);//添加分享内容标题
                            intent.putExtra(Intent.EXTRA_TEXT, "【" + title + "】"
                                    + currentUrl);//添加分享内容
                            //创建分享的Dialog
                            intent = Intent.createChooser(intent, getString(R.string.action_share));
                            startActivity(intent);
                        }else { //URL
                            String title = "";
                            try {
                                title = currentData.getNewssource();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            intent = new Intent();
                            intent.setAction(Intent.ACTION_SEND);//设置分享行为
                            intent.setType("text/plain");//设置分享内容的类型
                            if (!TextUtils.isEmpty(title))
                                intent.putExtra(Intent.EXTRA_SUBJECT, title);//添加分享内容标题
                            intent.putExtra(Intent.EXTRA_TEXT, "【" + title + "】"
                                    + currentUrl);//添加分享内容
                            //创建分享的Dialog
                            intent = Intent.createChooser(intent, getString(R.string.action_share));
                            startActivity(intent);
                        }
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
                    case R.id.action_link:
                        if (!TextUtils.isEmpty(currentData.getDetail())) {    //HTML
                            ClipboardManager cm = (ClipboardManager) Utils.getApp().getSystemService(Context.CLIPBOARD_SERVICE);
                            //noinspection ConstantConditions
                            cm.setPrimaryClip(ClipData.newPlainText("link", currentUrl));
                            SnackbarUtils.with(toolbar).setMessage(getString(R.string.action_link)
                                    + " " + getString(R.string.successful)).show();
                        }else { //URL
                            ClipboardManager cm = (ClipboardManager) Utils.getApp().getSystemService(Context.CLIPBOARD_SERVICE);
                            //noinspection ConstantConditions
                            cm.setPrimaryClip(ClipData.newPlainText("link", currentUrl));
                            SnackbarUtils.with(toolbar).setMessage(getString(R.string.action_link)
                                    + " " + getString(R.string.successful)).show();
                        }
                        break;
                    case R.id.action_browser:
                        try {
                            Uri uri = Uri.parse(currentUrl);
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
        Intent intent = getIntent();
        newsId = intent.getStringExtra("id");
        currentUrl = intent.getStringExtra("url");
        title = intent.getStringExtra("title");
        pTime = intent.getStringExtra("time");
        img = intent.getStringExtra("img");
        if (!TextUtils.isEmpty(newsId) && newsId.length() > 3) {
            StringBuilder sb = new StringBuilder(newsId);
            sb.insert(3,"%2F");
            String detailId = sb.toString();
            EasyHttp.get(HOST_IT_HOME + GET_IT_HOME_DETAIL.replace("{newsId}",detailId))
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
                            if (!TextUtils.isEmpty(response)) {
                                currentData = new IThomeDetailData();
                                if(response.contains("detail")){    //HTML
                                    try {
                                        currentData.setNewssource(XmlParseUtils.getXmlElement("newssource",response));
                                        currentData.setNewsauthor(XmlParseUtils.getXmlElement("newsauthor",response));
                                        currentData.setDetail(XmlParseUtils.getXmlElement("detail",response)
                                                .replace("&amp;","&")
                                                .replace("&lt;","<")
                                                .replace("&gt;",">")
                                                .replace("&nbsp;"," ")
                                                .replace("&#8226;","•")
                                        );
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        ToastUtils.showShort(getString(R.string.server_fail) + e.getMessage());
                                        finish();
                                    }
                                    Observable.create(new ObservableOnSubscribe<Boolean>() {
                                        @Override
                                        public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
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
                                }else { //URL
                                    try{
                                        currentData.setNewssource(XmlParseUtils.getXmlElement("newssource",response));
                                        currentData.setNewsauthor(XmlParseUtils.getXmlElement("newsauthor",response));

                                        currentUrl = XmlParseUtils.getXmlElement("otherlink",response);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        ToastUtils.showShort(getString(R.string.server_fail) + e.getMessage());
                                        finish();
                                    }
                                }

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
                    if (TextUtils.isEmpty(html))
                        return;
                    if (isNightMode)
                        html.replace("<body text=\"#333\">", "<body bgcolor=\"#212121\" body text=\"#ccc\">");
                    e.onNext(!StringUtils.isEmpty(html));
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
        if (!TextUtils.isEmpty(currentData.getDetail())) {    //HTML
            Observable.create(new ObservableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                    String source = "";
                    String body = "";
                    if (currentData == null)
                        return;
                    try {
                        source = currentData.getNewsauthor();
                        body = currentData.getDetail();
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
                    html = checkVideoWidth(html);
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
        }else {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            webView.loadUrl(currentUrl);
            saveCacheAsyn(CACHE_HISTORY);
        }
    }

    private void saveCacheAsyn(int type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (currentData == null)
                    return;
                List<CacheNews> list = new ArrayList<>();
                try {
                    list = LitePal.where("docid = ?", String.valueOf(newsId)).find(CacheNews.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (list != null && list.size() > 0)
                    for (CacheNews cacheNews : list) {
                        if (cacheNews.getType() == type)
                            return;
                    }
                CacheNews cacheNews = new CacheNews(title,
                        img,
                        currentData.getNewsauthor(),
                        String.valueOf(newsId),
                        html,
                        type,
                        TYPE_ITHOME_START);
                cacheNews.setUrl(currentUrl);
                cacheNews.setTimeStr(pTime);
                cacheNews.save();
            }
        }).start();
    }

    private boolean checkStar(boolean isClear) {
        List<CacheNews> list = null;
        try {
            list = LitePal.where("docid = ?", String.valueOf(newsId)).find(CacheNews.class);
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

  private String checkVideoWidth(String html){
        int index = html.indexOf("<iframe");
        if (index != -1){
            int wStart = html.indexOf("width=\"", index) + 7;
            int wEnd = html.indexOf("\"", wStart);
            html = html.substring(0,wStart) + "100%" + html.substring(wEnd,html.length());

            int hStart = html.indexOf("height=\"", wEnd);
            int hEnd = html.indexOf("\"", hStart + 8);
            html = html.substring(0,hStart) + html.substring(hEnd,html.length());
        }
        return html;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        try {
            menu.getItem(0).setVisible(false);
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
                webView.loadUrl("javascript:function setTop(){document.querySelector('.ft').style.display=\"none\";}setTop();");
                webView.loadUrl("javascript:document.body.style.paddingBottom=\"" + ConvertUtils.dp2px(16) + "px\"; void 0");
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            // 拦截页面加载，返回true表示宿主app拦截并处理了该url，否则返回false由当前WebView处理
            // 此方法在API24被废弃，不处理POST请求
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
                WebView.HitTestResult result = view.getHitTestResult();
                if (url.startsWith("mailto:")) {
                    //Handle mail Urls
                    startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse(url)));
                    return true;
                } else if (url.startsWith("tel:")) {
                    //Handle telephony Urls
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(url)));
                    return true;
                }else if (!TextUtils.isEmpty(url) && result == null) {
                    view.loadUrl(url);
                    return true;
                }
                return false;
            }

            // 拦截页面加载，返回true表示宿主app拦截并处理了该url，否则返回false由当前WebView处理
            // 此方法添加于API24，不处理POST请求，可拦截处理子frame的非http请求
            @TargetApi(Build.VERSION_CODES.N)
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return shouldOverrideUrlLoading(view, request.getUrl().toString());
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
