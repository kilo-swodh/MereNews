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
import com.chad.library.adapter.base.BaseQuickAdapter;

import androidnews.kiloproject.system.AppConfig;
import androidnews.kiloproject.widget.materialviewpager.header.MaterialViewPagerHeaderDecorator;

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

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import androidnews.kiloproject.util.GlideImageLoader;
import androidnews.kiloproject.R;
import androidnews.kiloproject.activity.GuoKrDetailActivity;
import androidnews.kiloproject.adapter.GuoKrAdapter;
import androidnews.kiloproject.entity.data.CacheNews;
import androidnews.kiloproject.entity.data.GuoKrCacheData;
import androidnews.kiloproject.entity.net.GuoKrListData;
import androidnews.kiloproject.entity.net.GuoKrTopData;
import androidnews.kiloproject.widget.materialviewpager.header.MaterialViewPagerHeaderDecoratorGrid;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static androidnews.kiloproject.entity.data.CacheNews.CACHE_HISTORY;
import static androidnews.kiloproject.system.AppConfig.CONFIG_AUTO_LOADMORE;
import static androidnews.kiloproject.system.AppConfig.HOST_GUO_KR;
import static androidnews.kiloproject.system.AppConfig.GET_GUO_KR_LIST;
import static androidnews.kiloproject.system.AppConfig.GET_GUO_KR_TOP;
import static androidnews.kiloproject.system.AppConfig.LIST_TYPE_MULTI;
import static androidnews.kiloproject.system.AppConfig.TYPE_GUOKR;

public class GuoKrRvFragment extends BaseRvFragment {

    Banner banner;
    //    MainListData contents;
    GuoKrCacheData contents;

    CardView header;

    private String CACHE_LIST_DATA;

    String typeStr;

    private int currentPage = 0;

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
                if (TextUtils.isEmpty(json) || TextUtils.equals(json, "[]")) {
                    e.onNext(0);
                } else {
                    contents = gson.fromJson(json, GuoKrCacheData.class);
                    if (contents != null) {
                        if (contents.getListData() != null) {
                            List<CacheNews> cacheNews = null;
                            try {
                                cacheNews = LitePal.where("type = ?", String.valueOf(CACHE_HISTORY)).find(CacheNews.class);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
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

        if (AppConfig.listType == LIST_TYPE_MULTI) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(mActivity, 2));
            mRecyclerView.addItemDecoration(new MaterialViewPagerHeaderDecoratorGrid());
        } else {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
            mRecyclerView.addItemDecoration(new MaterialViewPagerHeaderDecorator());
        }
        mRecyclerView.setHasFixedSize(true);

//        refreshLayout.setRefreshHeader(new MaterialHeader(mActivity));
//        refreshLayout.setRefreshFooter(new ClassicsFooter(mActivity));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                requestData(TYPE_REFRESH);
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
                    (AppConfig.isAutoRefresh) &&
                            (System.currentTimeMillis() - lastAutoRefreshTime > dividerAutoRefresh)) {
                refreshLayout.autoRefresh();
            }
        }
    }

    @Override
    protected void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
    }

    @Override
    public void requestData(int type) {
        String dataUrl = "";
        switch (type) {
            case TYPE_REFRESH:
                dataUrl = GET_GUO_KR_LIST + 0;
                break;
            case TYPE_LOADMORE:
                if (currentPage == 0
                        && contents.getListData() != null
                        && contents.getListData().getResult().size() > 0)
                    currentPage = 20;
                if (currentPage > 60) {
                    SnackbarUtils.with(mRecyclerView).setMessage(getResources().getString(R.string.empty_content)).show();
                    refreshLayout.finishLoadMore(false);
                    return;
                }
                dataUrl = GET_GUO_KR_LIST + currentPage;
                break;
        }
        EasyHttp.get(HOST_GUO_KR + dataUrl)
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
                                    if (SPUtils.getInstance().getBoolean(CONFIG_AUTO_LOADMORE) && mAdapter != null)
                                        mAdapter.loadMoreFail();
                                    else
                                        refreshLayout.finishLoadMore(false);
                                    break;
                            }
                            try {
                                SnackbarUtils.with(refreshLayout).
                                        setMessage(getString(R.string.load_fail) + e.getMessage()).
                                        showError();
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onSuccess(String response) {
                        if (!TextUtils.isEmpty(response) || TextUtils.equals(response, "{}")) {
                            Observable.create(new ObservableOnSubscribe<Boolean>() {
                                @Override
                                public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                                    GuoKrListData newData = null;
                                    List<CacheNews> cacheNews = null;
                                    try {
                                        newData = gson.fromJson(response, GuoKrListData.class);
                                        cacheNews = LitePal.where("type = ?", String.valueOf(CACHE_HISTORY)).find(CacheNews.class);
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                    }
                                    switch (type) {
                                        case TYPE_REFRESH:
                                            if (newData == null)
                                                return;
                                            if (cacheNews != null && cacheNews.size() > 0)
                                                for (GuoKrListData.ResultBean data : newData.getResult()) {
                                                    for (CacheNews cacheNew : cacheNews) {
                                                        if (TextUtils.equals(data.getId() + "", cacheNew.getDocid())) {
                                                            data.setReaded(true);
                                                            break;
                                                        }
                                                    }
                                                }
                                            contents.setListData(newData);
                                            lastAutoRefreshTime = System.currentTimeMillis();
                                            e.onNext(true);
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
                                                contents.getListData().getResult().addAll(newData.getResult());
                                                e.onNext(true);
                                            } catch (Exception e1) {
                                                e1.printStackTrace();
                                            }
                                            break;
                                    }
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
                                                requestBanner();
                                                try {
                                                    refreshLayout.finishRefresh(true);
                                                    if (!AppConfig.isDisNotice)
                                                        SnackbarUtils.with(refreshLayout)
                                                                .setMessage(getString(R.string.load_success))
                                                                .show();
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
        EasyHttp.get(HOST_GUO_KR + GET_GUO_KR_TOP)
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
                                    if (newData != null && newData.isOk()) {
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
        mAdapter = new GuoKrAdapter(mActivity, contents.getListData().getResult());
//        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                GuoKrListData.ResultBean bean = contents.getListData().getResult().get(position);
                Intent intent = new Intent(getActivity(), GuoKrDetailActivity.class);
                intent.putExtra("title", bean.getTitle());
                intent.putExtra("id", bean.getId());
                try {
                    intent.putExtra("img", bean.getImages().get(0));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!bean.isReaded()) {
                    bean.setReaded(true);
                    mAdapter.notifyItemChanged(position + 1);   //因为有个header,所以+1
                }
                startActivity(intent);
                bean.setReaded(true);
            }
        });

        if (mRecyclerView == null)
            return;
        mRecyclerView.setAdapter(mAdapter);
        if (SPUtils.getInstance().getBoolean(CONFIG_AUTO_LOADMORE)) {
            mAdapter.setPreLoadNumber(PRE_LOAD_ITEM);
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
            header = (CardView) LayoutInflater.from(mActivity).inflate(R.layout.list_item_card_banner,
                    (ViewGroup) refreshLayout.getParent(), false);

            banner = header.findViewById(R.id.banner);
            banner.setImageLoader(new GlideImageLoader())
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
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ConvertUtils.dp2px(200));
            params.setMargins(ConvertUtils.dp2px(10), ConvertUtils.dp2px(8),
                    ConvertUtils.dp2px(10), ConvertUtils.dp2px(8));
            header.setLayoutParams(params);
            try {
                mAdapter.setHeaderView(header);
                mRecyclerView.smoothScrollToPosition(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        SPUtils.getInstance().put(CACHE_LIST_DATA, gson.toJson(contents));
    }

    @Override
    public void onStart() {
        super.onStart();
        if (banner != null)
            banner.startAutoPlay();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (banner != null)
            banner.stopAutoPlay();
    }
}
