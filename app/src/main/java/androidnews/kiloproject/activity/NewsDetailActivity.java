package androidnews.kiloproject.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

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
import androidnews.kiloproject.bean.data.CacheNews;
import androidnews.kiloproject.bean.net.NewsDetailData;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static androidnews.kiloproject.bean.data.CacheNews.CACHE_COLLECTION;
import static androidnews.kiloproject.bean.data.CacheNews.CACHE_HISTORY;
import static androidnews.kiloproject.system.AppConfig.GET_NEWS_DETAIL;
import static androidnews.kiloproject.system.AppConfig.TYPE_NETEASE_START;
import static androidnews.kiloproject.system.AppConfig.isNightMode;

public class NewsDetailActivity extends BaseDetailActivity {
    private String html;
    private NewsDetailData currentData;
    private boolean isStar = false;

    private int type = 0;
    public static final int TPYE_AUDIO = 1024;

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
                        ClipboardManager cm = (ClipboardManager) Utils.getApp().getSystemService(Context.CLIPBOARD_SERVICE);
                        //noinspection ConstantConditions
                        cm.setPrimaryClip(ClipData.newPlainText("link", currentData.getShareLink()));
                        SnackbarUtils.with(toolbar).setMessage(getString(R.string.action_link)
                                + " " + getString(R.string.successful)).show();
                        break;
                    case R.id.action_browser:
                        try {
                            Uri uri = Uri.parse(currentData.getShareLink());
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
                            progress.setVisibility(View.GONE);
//                            refreshLayout.finishRefresh();
                        }

                        @Override
                        public void onSuccess(String response) {
                            progress.setVisibility(View.GONE);
                            if (!TextUtils.isEmpty(response) || TextUtils.equals(response, "{}")) {
                                String jsonNoHeader = response.substring(20, response.length());
                                String jsonFine = jsonNoHeader.substring(0, jsonNoHeader.length() - 1);

                                if (response.contains("点这里升级")) {
                                    ToastUtils.showShort(getString(R.string.server_fail));
                                    finish();
                                    return;
                                }
                                try {
                                    currentData = gson.fromJson(jsonFine, NewsDetailData.class);
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
                            progress.setVisibility(View.GONE);
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
                        String mediaUrl = videoBean.getMp4_url();
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
                                            "\" controls=\"controls\" poster=\"" + videoBean.getCover() + "\"></video>");
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
    protected void onPause() {
        super.onPause();
        if (type != TPYE_AUDIO)
            webView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (type != TPYE_AUDIO)
            webView.onResume();
    }
}
