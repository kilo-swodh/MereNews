package androidnews.kiloproject.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.blankj.utilcode.util.SnackbarUtils;
import com.blankj.utilcode.util.Utils;

import org.litepal.LitePal;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import androidnews.kiloproject.R;
import androidnews.kiloproject.bean.data.CacheNews;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static androidnews.kiloproject.bean.data.CacheNews.CACHE_COLLECTION;
import static androidnews.kiloproject.bean.data.CacheNews.CACHE_HISTORY;
import static androidnews.kiloproject.system.AppConfig.HOST_GUO_KR_DETAIL;
import static androidnews.kiloproject.system.AppConfig.GET_GUO_KR_DETAIL;
import static androidnews.kiloproject.system.AppConfig.TYPE_GUOKR;
import static androidnews.kiloproject.system.AppConfig.isNightMode;

public class GuoKrDetailActivity extends BaseDetailActivity {
    private String currentUrl;
    private String currentTitle;
    private String currentImg;
    private int detailId;
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
                        if (currentUrl == null)
                            break;
                        String title = "";
                        try {
                            title = currentTitle;
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
                                                try {
                                                    item.setIcon(R.drawable.ic_star_no);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                SnackbarUtils.with(toolbar).setMessage(getString(R.string.star_no)).show();
                                            } else
                                                SnackbarUtils.with(toolbar).setMessage(getString(R.string.fail)).showError();
                                        }
                                    });
                            isStar = false;
                        } else {
                            try {
                                item.setIcon(R.drawable.ic_star_ok);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            saveCacheAsyn(CACHE_COLLECTION);
                            SnackbarUtils.with(toolbar).setMessage(getString(R.string.star_yes)).show();
                            isStar = true;
                        }
                        break;
                    case R.id.action_comment:
                        break;
                    case R.id.action_link:
                        ClipboardManager cm = (ClipboardManager) Utils.getApp().getSystemService(Context.CLIPBOARD_SERVICE);
                        //noinspection ConstantConditions
                        cm.setPrimaryClip(ClipData.newPlainText("link", currentUrl));
                        SnackbarUtils.with(toolbar).setMessage(getString(R.string.action_link)
                                + " " + getString(R.string.successful)).show();
                        break;
                    case R.id.action_browser:
                        try {
                            Uri uri = Uri.parse(currentUrl);
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
        detailId = getIntent().getIntExtra("id", 0);
        currentUrl = HOST_GUO_KR_DETAIL + GET_GUO_KR_DETAIL.replace("{newsId}", String.valueOf(detailId));
        currentTitle = getIntent().getStringExtra("title");
        currentImg = getIntent().getStringExtra("img");

        if (detailId == 0 || TextUtils.isEmpty(currentTitle)) {
            SnackbarUtils.with(toolbar).setMessage(getString(R.string.load_fail)).showError();
//            refreshLayout.finishRefresh();
        } else {
            if (webView != null) {
                initWeb();
                loadUrl();
            }
        }
    }

    private void loadUrl() {
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        progress.setVisibility(View.GONE);
        webView.loadUrl(currentUrl);
        saveCacheAsyn(CACHE_HISTORY);
    }

    private void saveCacheAsyn(int type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<CacheNews> list = new ArrayList<>();
                try {
                    list = LitePal.where("docid = ?", String.valueOf(detailId)).find(CacheNews.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (list != null && list.size() > 0)
                    for (CacheNews cacheNews : list) {
                        if (cacheNews.getType() == type)
                            return;
                    }

                CacheNews cacheNews = new CacheNews(currentTitle,
                        currentImg,
                        getString(R.string.guokr),
                        detailId + "",
                        null,
                        type,
                        TYPE_GUOKR);
                cacheNews.save();
            }
        }).start();
    }

    private boolean checkStar(boolean isClear) {
        List<CacheNews> list = null;
        try {
            list = LitePal.where("docid = ?", String.valueOf(detailId)).find(CacheNews.class);
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
                            menu.findItem(R.id.action_star).setIcon(R.drawable.ic_star_ok);
                        }
//                           refreshLayout.finishRefresh();
                    }
                });
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
                if (isNightMode) {
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
