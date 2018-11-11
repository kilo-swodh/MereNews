package androidnews.kiloproject.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import java.util.ArrayList;
import java.util.List;

import androidnews.kiloproject.GlideImageLoader;
import androidnews.kiloproject.R;
import androidnews.kiloproject.activity.GuoKrDetailActivity;
import androidnews.kiloproject.adapter.GuoKrAdapter;
import androidnews.kiloproject.bean.data.CacheNews;
import androidnews.kiloproject.bean.data.GuoKrCacheData;
import androidnews.kiloproject.bean.net.GuoKrListData;
import androidnews.kiloproject.bean.net.GuoKrTopData;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static androidnews.kiloproject.activity.MainActivity.TYPE_GUOKR;
import static androidnews.kiloproject.bean.data.CacheNews.CACHE_HISTORY;
import static androidnews.kiloproject.system.AppConfig.CONFIG_AUTO_LOADMORE;
import static androidnews.kiloproject.system.AppConfig.CONFIG_AUTO_REFRESH;
import static androidnews.kiloproject.system.AppConfig.HOSTguoKr;
import static androidnews.kiloproject.system.AppConfig.getGuoKrList;
import static androidnews.kiloproject.system.AppConfig.getGuoKrTop;

public class GuoKrRvFragment extends BaseRvFragment {

    GuoKrAdapter mAdapter;
    //    MainListData contents;
    GuoKrCacheData contents;

    CardView header;

    private static final boolean GRID_LAYOUT = false;

    private String CACHE_LIST_DATA;

    String typeStr;

    private int currentPage = 0;

    private boolean isNoAdapter = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        typeStr = getResources().getStringArray(R.array.address)[TYPE_GUOKR];
        this.CACHE_LIST_DATA = typeStr + "_data";
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                String json = SPUtils.getInstance().getString(CACHE_LIST_DATA, "");
                if (!TextUtils.isEmpty(json)) {
                    contents = gson.fromJson(json, GuoKrCacheData.class);
                    if (contents != null) {
                        if (contents.getListData() != null) {
                            final String cacheJson = SPUtils.getInstance().getString(CACHE_HISTORY + "", "");
                            List<CacheNews> cacheNews = gson.fromJson(cacheJson, new TypeToken<List<CacheNews>>() {
                            }.getType());
                            if (cacheNews != null && cacheNews.size() > 0)
                                for (GuoKrListData.ResultBean data : contents.getListData().getResult()) {
                                    for (CacheNews cacheNew : cacheNews) {
                                        if (TextUtils.equals(data.getId() + "", cacheNew.getDocid())) {
                                            data.setReaded(true);
                                            break;
                                        }
                                    }
                                }
                            e.onNext(1);
                        }
                        if (contents.getTopData() != null) {
                            e.onNext(2);
                        }
                    } else {
                        e.onNext(0);
                    }
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
                        switch (i) {
                            case 0:
                                contents = new GuoKrCacheData();
                                break;
                            case 1:
                                createAdapter();
                                break;
                            case 2:
                                createBanner();
                                break;
                        }
                        GuoKrRvFragment.super.onViewCreated(view, savedInstanceState);
                    }
                });

        if (GRID_LAYOUT) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(mActivity, 2));
        } else {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        }
        mRecyclerView.setHasFixedSize(true);

        //Use this now
        mRecyclerView.addItemDecoration(new MaterialViewPagerHeaderDecorator());

//        refreshLayout.setRefreshHeader(new MaterialHeader(mActivity));
//        refreshLayout.setRefreshFooter(new ClassicsFooter(mActivity));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                requestData(TYPE_REFRESH);
                requestBanner();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                requestData(TYPE_LOADMORE);
            }
        });
    }

    protected void onFragmentVisibleChange(boolean isVisible) {
        if (isVisible) {
            if (contents.getListData() == null ||
                    (SPUtils.getInstance().getBoolean(CONFIG_AUTO_REFRESH)) &&
                            (System.currentTimeMillis() - lastAutoRefreshTime > dividerAutoRefresh)) {
                refreshLayout.autoRefresh();
            }
        }
    }

    @Override
    protected void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
    }

    private void requestData(int type) {
        String dataUrl = "";
        switch (type) {
            case TYPE_REFRESH:
                dataUrl = getGuoKrList + 0;
                break;
            case TYPE_LOADMORE:
                dataUrl = getGuoKrList + currentPage;
                break;
        }
        EasyHttp.get(HOSTguoKr + dataUrl)
                .readTimeOut(30 * 1000)//局部定义读超时
                .writeTimeOut(30 * 1000)
                .connectTimeout(30 * 1000)
                .timeStamp(true)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        if (refreshLayout != null) {
                            switch (type) {
                                case TYPE_REFRESH:
                                    refreshLayout.finishRefresh(false);
                                    break;
                                case TYPE_LOADMORE:
                                    if (SPUtils.getInstance().getBoolean(CONFIG_AUTO_LOADMORE))
                                        mAdapter.loadMoreFail();
                                    else
                                        refreshLayout.finishLoadMore(false);
                                    break;
                            }
                            SnackbarUtils.with(refreshLayout).
                                    setMessage(getString(R.string.load_fail) + e.getMessage()).
                                    showError();
                        }
                    }

                    @Override
                    public void onSuccess(String response) {
                        if (!TextUtils.isEmpty(response) || TextUtils.equals(response, "{}")) {
                            Observable.create(new ObservableOnSubscribe<Boolean>() {
                                @Override
                                public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                                    GuoKrListData newData = null;
                                    try {
                                        newData = gson.fromJson(response, GuoKrListData.class);
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                        loadFailed(type);
                                    }

                                    final String cacheJson = SPUtils.getInstance().getString(CACHE_HISTORY + "", "");
                                    List<CacheNews> cacheNews = gson.fromJson(cacheJson, new TypeToken<List<CacheNews>>() {
                                    }.getType());

                                    switch (type) {
                                        case TYPE_REFRESH:
                                            if (cacheNews != null && cacheNews.size() > 0 && newData != null) {
                                                for (GuoKrListData.ResultBean data : newData.getResult()) {
                                                    for (CacheNews cacheNew : cacheNews) {
                                                        if (TextUtils.equals(data.getId() + "", cacheNew.getDocid())) {
                                                            data.setReaded(true);
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                            contents.setListData(newData);
                                            lastAutoRefreshTime = System.currentTimeMillis();
                                            break;
                                        case TYPE_LOADMORE:
                                            try {
                                                if (cacheNews != null && cacheNews.size() > 0 && newData.getResult() != null) {
                                                    for (GuoKrListData.ResultBean data : newData.getResult()) {
                                                        for (CacheNews cacheNew : cacheNews) {
                                                            if (TextUtils.equals(data.getId() + "", cacheNew.getDocid())) {
                                                                data.setReaded(true);
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }
                                                for (GuoKrListData.ResultBean bean : newData.getResult()) {
                                                    contents.getListData().getResult().addAll(newData.getResult());
                                                }
                                            } catch (Exception e1) {
                                                e1.printStackTrace();
                                                loadFailed(type);
                                            }
                                            break;
                                    }
                                    e.onNext(true);
                                    e.onComplete();
                                }
                            }).subscribeOn(Schedulers.computation())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<Boolean>() {
                                        @Override
                                        public void accept(Boolean o) throws Exception {
                                            if (o) currentPage += 20;
                                            if (mAdapter == null || type == TYPE_REFRESH) {
                                                lastAutoRefreshTime = System.currentTimeMillis();
                                                createAdapter();
                                                try {
                                                    refreshLayout.finishRefresh(true);
                                                    SnackbarUtils.with(refreshLayout)
                                                            .setMessage(getString(R.string.load_success))
                                                            .showSuccess();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } else if (type == TYPE_LOADMORE) {
                                                mAdapter.notifyDataSetChanged();
                                                if (SPUtils.getInstance().getBoolean(CONFIG_AUTO_LOADMORE))
                                                    mAdapter.loadMoreComplete();
                                                else
                                                    try {
                                                        refreshLayout.finishLoadMore(true);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                            }
                                        }
                                    });
                        } else {
                            loadFailed(type);
                        }
                    }
                });
    }

    private void requestBanner() {
        EasyHttp.get(HOSTguoKr + getGuoKrTop)
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
                            Observable.create(new ObservableOnSubscribe<Boolean>() {
                                @Override
                                public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                                    GuoKrTopData newData = null;
                                    try {
                                        newData = gson.fromJson(response, GuoKrTopData.class);
                                        //设置头部轮播
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                    }
                                    if (newData.isOk()) {
                                        contents.setTopData(newData);
                                        e.onNext(true);
                                    }

                                    e.onComplete();
                                }
                            }).subscribeOn(Schedulers.computation())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<Boolean>() {
                                        @Override
                                        public void accept(Boolean aBoolean) throws Exception {
                                            if (aBoolean)
                                                createBanner();
                                        }
                                    });
                        }
                    }
                });
    }

    private void loadFailed(int type) {
        switch (type) {
            case TYPE_REFRESH:
                refreshLayout.finishRefresh(false);
                SnackbarUtils.with(refreshLayout).setMessage(getString(R.string.server_fail)).showError();
                break;
            case TYPE_LOADMORE:
                if (SPUtils.getInstance().getBoolean(CONFIG_AUTO_LOADMORE))
                    mAdapter.loadMoreFail();
                else
                    refreshLayout.finishLoadMore(false);
                SnackbarUtils.with(refreshLayout).setMessage(getString(R.string.server_fail)).showError();
                break;
        }
    }

    private void createAdapter() {
        if (contents == null || contents.getListData() == null || contents.getListData().getResult() == null)
            return;
        mAdapter = new GuoKrAdapter(mActivity,Glide.with(this), contents.getListData().getResult());
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                GuoKrListData.ResultBean bean = contents.getListData().getResult().get(position);
                Intent intent = new Intent(getActivity(), GuoKrDetailActivity.class);
                intent.putExtra("title", bean.getTitle());
                intent.putExtra("id", bean.getId());
                try {
                    intent.putExtra("img", bean.getImages().get(0));
                }catch (Exception e){
                    e.printStackTrace();
                }
                startActivity(intent);
                bean.setReaded(true);
                mAdapter.notifyDataSetChanged();
            }
        });

        if (mRecyclerView == null)
            return;
        if (isNoAdapter && header != null) {
            mAdapter.addHeaderView(header);
            isNoAdapter = false;
        }
        mRecyclerView.setAdapter(mAdapter);
        if (SPUtils.getInstance().getBoolean(CONFIG_AUTO_LOADMORE)) {
            mAdapter.setPreLoadNumber(3);
            mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
                @Override
                public void onLoadMoreRequested() {
                    requestData(TYPE_LOADMORE);
                }
            }, mRecyclerView);
            mAdapter.disableLoadMoreIfNotFullPage();
            refreshLayout.setEnableLoadMore(false);
        }
    }

    private void createBanner() {
        if (contents.getTopData() != null && contents.getTopData().getResult().size() > 0) {
            List<String> imgs = new ArrayList<>();
            List<String> titles = new ArrayList<>();
            for (GuoKrTopData.ResultBean bean : contents.getTopData().getResult()) {
                if (bean.getArticle_id() != 0) {
                    imgs.add(bean.getPicture());
                    titles.add(bean.getCustom_title());
                }
            }
            header = (CardView) getLayoutInflater().inflate(R.layout.list_item_card_big,
                    (ViewGroup) refreshLayout.getParent(), false);

            Banner banner = header.findViewById(R.id.banner);
            banner.setImageLoader(new GlideImageLoader(Glide.with(this)))
                    .setBannerAnimation(Transformer.FlipHorizontal)
                    .setBannerStyle(BannerConfig.NUM_INDICATOR_TITLE)
                    .setDelayTime(5 * 1000)
                    .setImages(imgs)
                    .setBannerTitles(titles)
                    .setOnBannerListener(new OnBannerListener() {
                        @Override
                        public void OnBannerClick(int position) {
                            Intent intent = new Intent(getActivity(), GuoKrDetailActivity.class);
                            intent.putExtra("title", contents.getTopData().getResult().get(position).getCustom_title());
                            intent.putExtra("id", contents.getTopData().getResult().get(position).getArticle_id());
                            intent.putExtra("img", contents.getTopData().getResult().get(position).getPicture());
                            startActivity(intent);
                        }
                    });
            banner.start();
            isNoAdapter = true;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ConvertUtils.dp2px(200));
            params.setMargins(ConvertUtils.dp2px(10), ConvertUtils.dp2px(8),
                    ConvertUtils.dp2px(10), ConvertUtils.dp2px(8));
            header.setLayoutParams(params);
        }
        SPUtils.getInstance().put(CACHE_LIST_DATA, gson.toJson(contents));
    }
}
