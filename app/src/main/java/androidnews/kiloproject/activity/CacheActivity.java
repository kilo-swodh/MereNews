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

import com.blankj.utilcode.util.CacheDiskUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.reflect.TypeToken;
import com.gyf.barlibrary.ImmersionBar;

import java.util.ArrayList;
import java.util.List;

import androidnews.kiloproject.R;
import androidnews.kiloproject.adapter.CacheNewsAdapter;
import androidnews.kiloproject.bean.data.CacheNews;
import androidnews.kiloproject.system.base.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static androidnews.kiloproject.bean.data.CacheNews.CACHE_COLLECTION;
import static androidnews.kiloproject.bean.data.CacheNews.CACHE_HISTORY;

public class CacheActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_content)
    RecyclerView rvContent;

    CacheNewsAdapter cacheNewsAdapter;
    List<CacheNews> currentData;
    int type;
    boolean isChange;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.root_view)
    ConstraintLayout rootView;
    @BindView(R.id.empty_view)
    ConstraintLayout emptyView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache);
        ButterKnife.bind(this);

        initToolbar(toolbar, true);
        type = getIntent().getIntExtra("type", 0);
        if (type != 0) {
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
        } else {
            setEmptyView();
            SnackbarUtils.with(rvContent).setMessage(getString(R.string.load_fail)).showError();
        }

        initStateBar(R.color.main_background, true);
    }

    @Override
    protected void initSlowly() {
        if (type > 0) {
            final String cacheJson = CacheDiskUtils.getInstance().getString(type + "", "");
            Observable.create(new ObservableOnSubscribe<Integer>() {
                @Override
                public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                    if (!TextUtils.isEmpty(cacheJson)) {
                        currentData = gson.fromJson(cacheJson, new TypeToken<List<CacheNews>>() {
                        }.getType());
                        if (NetworkUtils.isConnected())
                            e.onNext(1);
                        else
                            e.onNext(2);
                    } else {
                        e.onNext(0);
                    }
                    e.onComplete();
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Integer>() {
                        @Override
                        public void accept(Integer i) throws Exception {
                            progress.setVisibility(View.GONE);
                            cacheNewsAdapter = new CacheNewsAdapter(mActivity, currentData);
                            cacheNewsAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                                    Intent intent = new Intent(mActivity, NewsDetailActivity.class);
                                    switch (i) {
                                        case 1:
                                            intent.putExtra("docid", currentData.get(position).getDocid());
                                            break;
                                        case 2:
                                            intent.putExtra("htmlText", currentData.get(position).getHtmlText());
                                            break;
                                    }
                                    startActivity(intent);
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
                            if (i == 0)
                                setEmptyView();
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
    protected void onDestroy() {
        super.onDestroy();
        if (isChange){
            String saveJson = gson.toJson(currentData, new TypeToken<List<CacheNews>>() {
            }.getType());
            CacheDiskUtils.getInstance().put(type + "",saveJson);
        }
    }
}
