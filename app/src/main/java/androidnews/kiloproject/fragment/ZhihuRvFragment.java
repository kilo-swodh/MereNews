package androidnews.kiloproject.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;

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
import java.util.Iterator;
import java.util.List;

import androidnews.kiloproject.system.AppConfig;
import androidnews.kiloproject.util.GlideImageLoader;
import androidnews.kiloproject.R;
import androidnews.kiloproject.activity.ZhiHuDetailActivity;
import androidnews.kiloproject.adapter.ZhihuAdapter;
import androidnews.kiloproject.entity.data.CacheNews;
import androidnews.kiloproject.entity.net.ZhihuListData;
import androidnews.kiloproject.widget.materialviewpager.header.MaterialViewPagerHeaderDecoratorGrid;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static androidnews.kiloproject.entity.data.CacheNews.CACHE_HISTORY;
import static androidnews.kiloproject.system.AppConfig.CONFIG_AUTO_LOADMORE;
import static androidnews.kiloproject.system.AppConfig.CONFIG_AUTO_REFRESH;
import static androidnews.kiloproject.system.AppConfig.HOST_ZHIHU;
import static androidnews.kiloproject.system.AppConfig.GET_ZHIHU_LOAD_MORE;
import static androidnews.kiloproject.system.AppConfig.GET_ZHIHU_REFRESH;
import static androidnews.kiloproject.system.AppConfig.LIST_TYPE_MULTI;
import static androidnews.kiloproject.system.AppConfig.TYPE_ZHIHU;

public class ZhihuRvFragment extends BaseRvFragment {

    ZhihuAdapter mAdapter;
    Banner banner;
    //    MainListData contents;
    ZhihuListData contents;

    private String CACHE_LIST_DATA;

    String loadMoreDate;

    String typeStr;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        typeStr = getResources().getStringArray(R.array.address)[TYPE_ZHIHU];
        this.CACHE_LIST_DATA = typeStr + "_data";
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                String json = SPUtils.getInstance().getString(CACHE_LIST_DATA, "");
                if (!TextUtils.isEmpty(json)) {
                    contents = gson.fromJson(json, ZhihuListData.class);
                    if (contents != null && contents.getStories().size() > 0) {
                        List<CacheNews> cacheNews = null;
                        try {
                            cacheNews = LitePal.where("type = ?", String.valueOf(CACHE_HISTORY)).find(CacheNews.class);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        if (cacheNews != null && cacheNews.size() > 0)
                            for (ZhihuListData.StoriesBean data : contents.getStories()) {
                                for (CacheNews cacheNew : cacheNews) {
                                    if (TextUtils.equals(data.getId() + "", cacheNew.getDocid())) {
                                        data.setReaded(true);
                                        break;
                                    }
                                }
                            }
                        e.onNext(true);
                    } else
                        e.onNext(false);
                } else e.onNext(false);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean s) throws Exception {
                        if (s)
                            createAdapter();
                        ZhihuRvFragment.super.onViewCreated(view, savedInstanceState);
                    }
                });

        if (AppConfig.type_list == LIST_TYPE_MULTI)
            mRecyclerView.setLayoutManager(new GridLayoutManager(mActivity, 3));
        else
            mRecyclerView.setLayoutManager(new GridLayoutManager(mActivity, 2));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new MaterialViewPagerHeaderDecoratorGrid());

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
            if (contents == null ||
                    (SPUtils.getInstance().getBoolean(CONFIG_AUTO_REFRESH)) &&
                            (System.currentTimeMillis() - lastAutoRefreshTime > dividerAutoRefresh)) {
                refreshLayout.autoRefresh();
            }
        }
    }

    @Override
    public void requestData(int type) {
        String dataUrl = "";
        switch (type) {
            case TYPE_REFRESH:
                dataUrl = GET_ZHIHU_REFRESH;
                break;
            case TYPE_LOADMORE:
                if (TextUtils.isEmpty(loadMoreDate))
                    return;
                dataUrl = GET_ZHIHU_LOAD_MORE + loadMoreDate;
                break;
        }
        EasyHttp.get(HOST_ZHIHU + dataUrl)
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
                            try {
                                SnackbarUtils.with(refreshLayout).
                                        setMessage(getString(R.string.load_fail) + e.getMessage()).
                                        showError();
                            }catch (Exception e1){
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
                                    ZhihuListData newData = null;
                                    List<CacheNews> cacheNews = null;
                                    try {
                                        newData = gson.fromJson(response, ZhihuListData.class);
                                        //设置头部轮播
                                        loadMoreDate = newData.getDate();
                                        cacheNews = LitePal.where("type = ?", String.valueOf(CACHE_HISTORY)).find(CacheNews.class);
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                    }

                                    switch (type) {
                                        case TYPE_REFRESH:
                                            if (cacheNews != null && cacheNews.size() > 0)
                                                for (Iterator<ZhihuListData.StoriesBean> it = newData.getStories().iterator(); it.hasNext(); ) {
                                                    ZhihuListData.StoriesBean data = it.next();
                                                    boolean isSame = false;
                                                    if (contents != null && contents.getTop_stories() != null)
                                                        for (ZhihuListData.TopStoriesBean topStoriesBean : contents.getTop_stories()) {
                                                            if (topStoriesBean.getId() == data.getId()) {
                                                                isSame = true;
                                                                break;
                                                            }
                                                        }
                                                    if (isSame) {
                                                        it.remove();
                                                        continue;
                                                    }
                                                    for (CacheNews cacheNew : cacheNews) {
                                                        if (TextUtils.equals(data.getId() + "", cacheNew.getDocid())) {
                                                            data.setReaded(true);
                                                            break;
                                                        }
                                                    }
                                                }
                                            contents = newData;
                                            SPUtils.getInstance().put(CACHE_LIST_DATA, gson.toJson(newData));
                                            break;
                                        case TYPE_LOADMORE:
                                            if (contents == null) return;
                                            try {
                                                if (cacheNews != null && cacheNews.size() > 0)
                                                    for (Iterator<ZhihuListData.StoriesBean> it = newData.getStories().iterator(); it.hasNext(); ) {
                                                        ZhihuListData.StoriesBean data = it.next();
                                                        boolean isSame = false;
                                                        for (ZhihuListData.TopStoriesBean topStoriesBean : contents.getTop_stories()) {
                                                            if (topStoriesBean.getId() == data.getId()) {
                                                                isSame = true;
                                                                break;
                                                            }
                                                        }
                                                        if (isSame) {
                                                            it.remove();
                                                            continue;
                                                        }
                                                        for (CacheNews cacheNew : cacheNews) {
                                                            if (TextUtils.equals(data.getId() + "", cacheNew.getDocid())) {
                                                                data.setReaded(true);
                                                                break;
                                                            }
                                                        }
                                                    }
                                                contents.getStories().addAll(newData.getStories());
                                            } catch (Exception e1) {
                                                e1.printStackTrace();
                                            }
                                            contents.setDate(newData.getDate());
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
                                            if (mAdapter == null || type == TYPE_REFRESH) {
                                                createAdapter();
                                                lastAutoRefreshTime = System.currentTimeMillis();
                                                try {
                                                    refreshLayout.finishRefresh(true);
                                                    SnackbarUtils.with(refreshLayout)
                                                            .setMessage(getString(R.string.load_success))
                                                            .show();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } else if (type == TYPE_LOADMORE) {
                                                mAdapter.notifyDataSetChanged();
                                                loadMoreDate = contents.getDate();
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
        if (contents == null)
            return;
        loadMoreDate = contents.getDate();
        mAdapter = new ZhihuAdapter(mActivity, contents.getStories());
        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(contents == null)
                    return;
                ZhihuListData.StoriesBean bean = contents.getStories().get(position);
                Intent intent = new Intent(getActivity(), ZhiHuDetailActivity.class);
                intent.putExtra("id", bean.getId());
                if (!bean.isReaded()) {
                    bean.setReaded(true);
                    mAdapter.notifyItemChanged(position + 1); //因为有个header,所以+1
                }
                startActivity(intent);
            }
        });

        if (mRecyclerView == null)
            return;
        mRecyclerView.setAdapter(mAdapter);
        if (contents.getTop_stories() != null && contents.getTop_stories().size() > 0) {
            List<String> imgs = new ArrayList<>();
            List<String> titles = new ArrayList<>();
            for (ZhihuListData.TopStoriesBean bean : contents.getTop_stories()) {
                imgs.add(bean.getImage());
                titles.add(bean.getTitle());
            }
            CardView header = (CardView) LayoutInflater.from(mActivity).inflate(R.layout.list_item_card_banner,
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
                            try {
                                Intent intent = new Intent(getActivity(), ZhiHuDetailActivity.class);
                                intent.putExtra("id", contents.getTop_stories().get(position).getId());
                                startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
            banner.start();
            mAdapter.addHeaderView(header);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ConvertUtils.dp2px(200));
            params.setMargins(ConvertUtils.dp2px(10), ConvertUtils.dp2px(8),
                    ConvertUtils.dp2px(10), ConvertUtils.dp2px(8));
            header.setLayoutParams(params);
        }
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
