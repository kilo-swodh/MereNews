package androidnews.kiloproject.activity;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.blankj.utilcode.util.SnackbarUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.litepal.LitePal;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import androidnews.kiloproject.R;
import androidnews.kiloproject.entity.data.CacheNews;
import androidnews.kiloproject.entity.net.ZhihuDetailData;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static androidnews.kiloproject.entity.data.CacheNews.CACHE_COLLECTION;
import static androidnews.kiloproject.entity.data.CacheNews.CACHE_HISTORY;
import static androidnews.kiloproject.system.AppConfig.HOST_ZHIHU;
import static androidnews.kiloproject.system.AppConfig.TYPE_ZHIHU;
import static androidnews.kiloproject.system.AppConfig.isNightMode;

public class ZhiHuDetailActivity extends BaseDetailActivity {
    private ZhihuDetailData currentData;
    private boolean isStar = false;

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
                        if (currentData == null || currentData.getShare_url() == null)
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
                                + currentData.getShare_url());//添加分享内容
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
                        break;
                    case R.id.action_link:
                        if (currentData != null) {
                            ClipboardManager cm = (ClipboardManager) Utils.getApp().getSystemService(Context.CLIPBOARD_SERVICE);
                            //noinspection ConstantConditions
                            cm.setPrimaryClip(ClipData.newPlainText("link", currentData.getShare_url()));
                            SnackbarUtils.with(toolbar).setMessage(getString(R.string.action_link)
                                    + " " + getString(R.string.successful)).show();
                        }
                        break;
                    case R.id.action_browser:
                        try {
                            Uri uri = Uri.parse(currentData.getShare_url());
                            intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        } catch (Exception e) {
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
        int id = getIntent().getIntExtra("id", 0);
        if (id != 0) {
            EasyHttp.get(HOST_ZHIHU + "/" + id)
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
                                    currentData = gson.fromJson(response, ZhihuDetailData.class);
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
                                                    }catch (Exception e){
                                                        e.printStackTrace();
                                                    }
                                                }
//                                                refreshLayout.finishRefresh();
                                            }
                                        });
                                if (webView != null && currentData != null) {
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
//            refreshLayout.finishRefresh();
            hideSkeleton();
            SnackbarUtils.with(toolbar).setMessage(getString(R.string.load_fail)).showError();
        }
    }

    private void loadUrl() {
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        webView.loadUrl(currentData.getShare_url());
        saveCacheAsyn(CACHE_HISTORY);
    }

    private void saveCacheAsyn(int type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(currentData == null)
                    return;
                List<CacheNews> list = new ArrayList<>();
                try {
                    list = LitePal.where("docid = ?", String.valueOf(currentData.getId())).find(CacheNews.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (list != null && list.size() > 0)
                    for (CacheNews cacheNews : list) {
                        if (cacheNews.getType() == type)
                            return;
                    }

                CacheNews cacheNews = new CacheNews(currentData.getTitle(),
                        currentData.getImage(),
                        getString(R.string.zhihu),
                        currentData.getId() + "",
                        currentData.getBody(),
                        type,
                        TYPE_ZHIHU);
                cacheNews.save();
            }
        }).start();
    }

    private boolean checkStar(boolean isClear) {
        List<CacheNews> list = null;
        try {
            list = LitePal.where("docid = ?", String.valueOf(currentData.getId())).find(CacheNews.class);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        try {
            menu.getItem(0).setVisible(false);
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
                view.loadUrl("javascript:function setTop(){document.querySelector('.header-for-mobile').style.display=\"none\";}setTop();");
                view.loadUrl("javascript:function setTop(){document.querySelector('.bottom-wrap').style.display=\"none\";}setTop();");
                view.loadUrl("javascript:function setTop(){document.querySelector('.footer').style.display=\"none\";}setTop();");
                view.loadUrl("javascript:function setTop(){document.querySelector('.global-header').style.display=\"none\";}setTop();");
                if (isNightMode) {
                    view.loadUrl("javascript:function setTop(){document.querySelector('.headline').style.display=\"none\";}setTop();");
                    InputStream is = getResources().openRawResource(R.raw.night);
                    byte[] buffer = new byte[0];
                    try {
                        buffer = new byte[is.available()];
                        is.read(buffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    String nightCode = Base64.encodeToString(buffer, Base64.NO_WRAP);
                    webView.loadUrl("javascript:(function() {" + "var parent = document.getElementsByTagName('head').item(0);" + "var style = document.createElement('style');" + "style.type = 'text/css';" + "style.innerHTML = window.atob('" + nightCode + "');" + "parent.appendChild(style)" + "})();");
                }
                super.onProgressChanged(view, newProgress);
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
