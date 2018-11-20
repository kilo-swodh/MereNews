package androidnews.kiloproject.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import androidnews.kiloproject.R;
import androidnews.kiloproject.adapter.CacheNewsAdapter;
import androidnews.kiloproject.bean.data.CacheNews;
import androidnews.kiloproject.system.base.BaseActivity;


import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static androidnews.kiloproject.activity.MainActivity.TYPE_GUOKR;
import static androidnews.kiloproject.activity.MainActivity.TYPE_ZHIHU;
import static androidnews.kiloproject.bean.data.CacheNews.CACHE_COLLECTION;
import static androidnews.kiloproject.bean.data.CacheNews.CACHE_HISTORY;

public class CacheActivity extends BaseActivity {

    Toolbar toolbar;
    RecyclerView rvContent;
    ProgressBar progress;
    ConstraintLayout rootView;
    ConstraintLayout emptyView;

    CacheNewsAdapter cacheNewsAdapter;
    List<CacheNews> currentData;
    int type;
    boolean isChange;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        rvContent = (RecyclerView) findViewById(R.id.rv_content);
        progress = (ProgressBar) findViewById(R.id.progress);
        rootView = (ConstraintLayout) findViewById(R.id.root_view);
        emptyView = (ConstraintLayout) findViewById(R.id.empty_view);


        initToolbar(toolbar, true);
        type = getIntent().getIntExtra("type", 0);
//        type = (type == 0) ? CACHE_COLLECTION : type;
        switch (type) {
            case CACHE_HISTORY:
                getSupportActionBar().setTitle(getString(R.string.history));
                break;
            case CACHE_COLLECTION:
                getSupportActionBar().setTitle(getString(R.string.action_star));
                break;
            default:
                break;
        }
        initStateBar(R.color.main_background, true);
    }

    @Override
    protected void initSlowly() {
        if (type > 0) {
            Observable.create(new ObservableOnSubscribe<Integer>() {
                @Override
                public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                    String cacheJson = SPUtils.getInstance().getString(type + "", "");

                    if (TextUtils.isEmpty(cacheJson) || TextUtils.equals(cacheJson, "[]")) {
                        e.onNext(0);
                    } else {
                        currentData = gson.fromJson(cacheJson, new TypeToken<List<CacheNews>>() {
                        }.getType());
                        if (NetworkUtils.isConnected())
                            e.onNext(1);
                        else
                            e.onNext(2);
                    }
                    e.onComplete();
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Integer>() {
                        @Override
                        public void accept(Integer i) throws Exception {
                            progress.setVisibility(View.GONE);
                            cacheNewsAdapter = new CacheNewsAdapter(mActivity, Glide.with(mActivity), currentData);
                            if (i == 0)
                                setEmptyView();
                            cacheNewsAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                                    CacheNews cacheNews = currentData.get(position);
                                    Intent intent;
                                    switch (cacheNews.getType()) {
                                        case TYPE_ZHIHU:
                                            if (i == 0)
                                                return;
                                            intent = new Intent(mActivity, ZhiHuDetailActivity.class);
                                            try {
                                                intent.putExtra("id", Integer.parseInt(cacheNews.getDocid()));
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            break;
                                        case TYPE_GUOKR:
                                            if (i == 0)
                                                return;
                                            intent = new Intent(mActivity, GuoKrDetailActivity.class);
                                            try {
                                                intent.putExtra("id", Integer.parseInt(cacheNews.getDocid()));
                                                intent.putExtra("title", cacheNews.getTitle());
                                                intent.putExtra("img", cacheNews.getImgUrl());
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            break;
                                        default:
                                            intent = new Intent(mActivity, NewsDetailActivity.class);
                                            switch (i) {
                                                case 1:
                                                    intent.putExtra("docid", cacheNews.getDocid());
                                                    break;
                                                case 2:
                                                    intent.putExtra("htmlText", cacheNews.getHtmlText());
                                                    break;
                                            }
                                            break;
                                    }
                                    if (intent != null)
                                        startActivityForResult(intent, CACHE_RESULT);
                                }
                            });
                            cacheNewsAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
                                @Override
                                public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                                    builder.setTitle(R.string.delete)
                                            .setMessage(R.string.delete_message)
                                            .setCancelable(true)
                                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    isChange = true;
                                                    currentData.remove(position);
                                                    cacheNewsAdapter.notifyDataSetChanged();
                                                }
                                            }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).show();
                                    return true;
                                }
                            });
                            rvContent.setLayoutManager(new LinearLayoutManager(mActivity));
                            rvContent.setAdapter(cacheNewsAdapter);
                        }
                    });
        } else {
            setEmptyView();
        }
    }

    private void setEmptyView() {
        progress.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Observable.create(new ObservableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                    String cacheJson = SPUtils.getInstance().getString(type + "", "");
                    if (!TextUtils.isEmpty(cacheJson)) {
                        currentData.clear();
                        currentData.addAll(gson.fromJson(cacheJson, new TypeToken<List<CacheNews>>() {
                        }.getType()));
                        e.onNext(true);
                    }
                    e.onComplete();
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            if (aBoolean) {
                                cacheNewsAdapter.notifyDataSetChanged();
                                if (currentData.size() < 1)
                                    setEmptyView();
                            }
                        }
                    });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isChange) {
            String saveJson = gson.toJson(currentData, new TypeToken<List<CacheNews>>() {
            }.getType());
            SPUtils.getInstance().put(type + "", saveJson);
        }
    }
}
