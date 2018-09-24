package androidnews.kiloproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
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
import static androidnews.kiloproject.system.AppConfig.CONFIG_STATUSBAR;

public class CacheActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_content)
    RecyclerView rvContent;

    CacheNewsAdapter cacheNewsAdapter;
    List<CacheNews> currentData;
    int type;
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
            progress.setVisibility(View.GONE);
            SnackbarUtils.with(rvContent).setMessage(getString(R.string.load_fail)).showError();
        }
        if (SPUtils.getInstance().getBoolean(CONFIG_STATUSBAR))
            ImmersionBar.with(mActivity).fitsSystemWindows(true).statusBarDarkFont(true).init();
        else
            initStateBar(android.R.color.white, true);
    }

    @Override
    protected void initSlowly() {
        if (type > 0) {
            final String cacheJson = CacheDiskUtils.getInstance().getString(type + "", "");
            Observable.create(new ObservableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                    if (!TextUtils.isEmpty(cacheJson)) {
                        currentData = gson.fromJson(cacheJson, new TypeToken<List<CacheNews>>() {
                        }.getType());
                        e.onNext(NetworkUtils.isConnected());
                    } else {
                        setEmptyView();
                    }
                    e.onComplete();
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            progress.setVisibility(View.GONE);
                            cacheNewsAdapter = new CacheNewsAdapter(mActivity, currentData);
                            cacheNewsAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                                    Intent intent = new Intent(mActivity, NewsDetailActivity.class);
                                    if (aBoolean) {
                                        intent.putExtra("docid", currentData.get(position).getDocid());
                                        startActivity(intent);
                                    } else {
                                        intent.putExtra("htmlText", currentData.get(position).getHtmlText());
                                        startActivity(intent);
                                    }
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
}
