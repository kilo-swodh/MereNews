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

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.google.gson.reflect.TypeToken;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import androidnews.kiloproject.R;
import androidnews.kiloproject.bean.data.CacheNews;
import androidnews.kiloproject.bean.net.ZhihuDetailData;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static androidnews.kiloproject.activity.MainActivity.TYPE_ZHIHU;
import static androidnews.kiloproject.bean.data.CacheNews.CACHE_COLLECTION;
import static androidnews.kiloproject.bean.data.CacheNews.CACHE_HISTORY;
import static androidnews.kiloproject.system.AppConfig.HOSTzhihu;
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
                        intent.putExtra(Intent.EXTRA_TEXT, "【" + title + "】 "
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
                                                SnackbarUtils.with(toolbar).setMessage(getString(R.string.star_no)).showSuccess();
                                            } else
                                                SnackbarUtils.with(toolbar).setMessage(getString(R.string.fail)).showSuccess();
                                        }
                                    });
                            isStar = false;
                        } else {
                            item.setIcon(R.drawable.ic_star_ok);
                            saveCacheAsyn(CACHE_COLLECTION);
                            SnackbarUtils.with(toolbar).setMessage(getString(R.string.star_yes)).showSuccess();
                            isStar = true;
                        }
                        break;
                    case R.id.action_comment:
                        break;
                    case R.id.action_link:
                        ClipboardManager cm = (ClipboardManager) Utils.getApp().getSystemService(Context.CLIPBOARD_SERVICE);
                        //noinspection ConstantConditions
                        cm.setPrimaryClip(ClipData.newPlainText("link", currentData.getShare_url()));
                        SnackbarUtils.with(toolbar).setMessage(getString(R.string.action_link)
                                + " " + getString(R.string.successful)).showSuccess();
                        break;
                    case R.id.action_browser:
                        Uri uri = Uri.parse(currentData.getShare_url());
                        intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
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
            EasyHttp.get(HOSTzhihu + "/" + id)
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
                                                    toolbar.getMenu().findItem(R.id.action_star).setIcon(R.drawable.ic_star_ok);
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
                                progress.setVisibility(View.GONE);
                                SnackbarUtils.with(toolbar).setMessage(getString(R.string.load_fail)).showError();
                            }
                        }
                    });
        } else {
//            refreshLayout.finishRefresh();
            progress.setVisibility(View.GONE);
            SnackbarUtils.with(toolbar).setMessage(getString(R.string.load_fail)).showError();
        }
    }

    private void loadUrl() {
        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        webView.loadData(currentData.getBody(), "text/html; charset=UTF-8", null);
        webView.loadUrl(currentData.getShare_url());
        saveCacheAsyn(CACHE_HISTORY);
    }

    private void saveCacheAsyn(int type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String cacheJson = SPUtils.getInstance().getString(type + "", "");
                List<CacheNews> list;
                if (TextUtils.isEmpty(cacheJson)) {
                    list = new ArrayList<>();
                } else {
                    list = gson.fromJson(cacheJson, new TypeToken<List<CacheNews>>() {
                    }.getType());
                    for (CacheNews cacheNews : list) {
                        if (TextUtils.equals(cacheNews.getDocid(), currentData.getId() + ""))
                            return;
                    }
                }

                CacheNews cacheNews = new CacheNews(currentData.getTitle(),
                        currentData.getImage(),
                        getString(R.string.zhihu),
                        currentData.getId() + "",
                        currentData.getBody());
                cacheNews.setType(TYPE_ZHIHU);
                list.add(0, cacheNews);

                if (list.size() > MAX_HISTORY) {
                    list.remove(list.size() - 1);
                }

                String saveJson = gson.toJson(list, new TypeToken<List<CacheNews>>() {
                }.getType());
                SPUtils.getInstance().put(type + "", saveJson);
            }
        }).start();
    }

    private boolean checkStar(boolean isClear) {
        String hisJson = SPUtils.getInstance().getString(CACHE_COLLECTION + "", "");
        List<CacheNews> list;
        if (!TextUtils.isEmpty(hisJson)) {
            list = gson.fromJson(hisJson, new TypeToken<List<CacheNews>>() {
            }.getType());
            for (CacheNews cache : list) {
                if (TextUtils.equals(cache.getDocid(), currentData.getId() + "")) {
                    if (isClear) {
                        list.remove(cache);
                        String saveJson = gson.toJson(list, new TypeToken<List<CacheNews>>() {
                        }.getType());
                        SPUtils.getInstance().put(CACHE_COLLECTION + "", saveJson);
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
        menu.getItem(0).setVisible(false);
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
