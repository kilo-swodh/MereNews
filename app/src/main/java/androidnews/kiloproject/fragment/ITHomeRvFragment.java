package androidnews.kiloproject.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.litepal.LitePal;

import java.util.List;

import androidnews.kiloproject.R;
import androidnews.kiloproject.activity.ITHomeDetailActivity;
import androidnews.kiloproject.adapter.ITHomeAdapter;
import androidnews.kiloproject.entity.data.CacheNews;
import androidnews.kiloproject.entity.net.ITHomeListData;
import androidnews.kiloproject.system.AppConfig;
import androidnews.kiloproject.util.ITHomeUtils;
import androidnews.kiloproject.util.XmlParseUtil;
import androidnews.kiloproject.widget.materialviewpager.header.MaterialViewPagerHeaderDecorator;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static androidnews.kiloproject.entity.data.CacheNews.CACHE_HISTORY;
import static androidnews.kiloproject.system.AppConfig.CONFIG_AUTO_LOADMORE;
import static androidnews.kiloproject.system.AppConfig.GET_IT_HOME_LOAD_MORE;
import static androidnews.kiloproject.system.AppConfig.GET_IT_HOME_REFRESH;
import static androidnews.kiloproject.system.AppConfig.HOST_IT_HOME;
import static androidnews.kiloproject.system.AppConfig.LIST_TYPE_MULTI;

public class ITHomeRvFragment extends BaseRvFragment {

    //    MainListData contents;
    ITHomeListData contents;

    private String CACHE_LIST_DATA;

    String typeStr;

    private String lastItemId = "";

    public static ITHomeRvFragment newInstance(int type) {
        ITHomeRvFragment f = new ITHomeRvFragment();
        Bundle b = new Bundle();
        b.putInt("type", type);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        int position = 999;
        if (args != null) {
            position = args.getInt("type");
        }
        typeStr = getResources().getStringArray(R.array.address)[position];
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
                    contents = gson.fromJson(json, ITHomeListData.class);
                    if (contents != null && contents.getChannel().size() > 0) {
                        List<CacheNews> cacheNews = null;
                        try {
                            cacheNews = LitePal.where("type = ?", String.valueOf(CACHE_HISTORY)).find(CacheNews.class);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        if (cacheNews != null && cacheNews.size() > 0)
                            for (ITHomeListData.ItemBean data : contents.getChannel()) {
                                for (CacheNews cacheNew : cacheNews) {
                                    if (TextUtils.equals(data.getNewsid() + "", cacheNew.getDocid())) {
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
                        ITHomeRvFragment.super.onViewCreated(view, savedInstanceState);
                    }
                });

        if (AppConfig.listType == LIST_TYPE_MULTI)
            mRecyclerView.setLayoutManager(new GridLayoutManager(mActivity, 2));
        else
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new MaterialViewPagerHeaderDecorator());

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
            if (contents == null || contents.getChannel() == null ||
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
                dataUrl = GET_IT_HOME_REFRESH.replace("{typeStr}", typeStr);
                break;
            case TYPE_LOADMORE:
                dataUrl = GET_IT_HOME_LOAD_MORE.replace("{typeStr}", typeStr)
                        .replace("{lastItemId}", ITHomeUtils.getMinNewsId(lastItemId));
                break;
        }

        EasyHttp.get(HOST_IT_HOME + dataUrl)
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
                        if (!TextUtils.isEmpty(response)) {
                            Observable.create(new ObservableOnSubscribe<Boolean>() {
                                @Override
                                public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                                    ITHomeListData newData = null;
                                    List<CacheNews> cacheNews = null;
                                    try {
                                        newData = XmlParseUtil.getITHomeListData(response);
                                        cacheNews = LitePal.where("type = ?", String.valueOf(CACHE_HISTORY)).find(CacheNews.class);
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                    }

                                    switch (type) {
                                        case TYPE_REFRESH:
                                            if (newData.getChannel() == null)
                                                return;
                                            if (cacheNews != null && cacheNews.size() > 0)
                                                for (ITHomeListData.ItemBean data : newData.getChannel()) {
                                                    for (CacheNews cacheNew : cacheNews) {
                                                        if (TextUtils.equals(data.getNewsid() + "", cacheNew.getDocid())) {
                                                            data.setReaded(true);
                                                            break;
                                                        }
                                                    }
                                                }
                                            contents = newData;
                                            SPUtils.getInstance().put(CACHE_LIST_DATA, gson.toJson(newData));
                                            e.onNext(true);
                                            break;
                                        case TYPE_LOADMORE:
                                            try {
                                                if (cacheNews != null && cacheNews.size() > 0 && newData.getChannel() != null)
                                                    for (ITHomeListData.ItemBean data : newData.getChannel()) {
                                                        for (CacheNews cacheNew : cacheNews) {
                                                            if (TextUtils.equals(data.getNewsid() + "", cacheNew.getDocid())) {
                                                                data.setReaded(true);
                                                                break;
                                                            }
                                                        }
                                                    }
                                                contents.getChannel().addAll(newData.getChannel());
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
                                            if (o && contents.getChannel().size() > 0)
                                                lastItemId = contents.getChannel().get(contents.getChannel().size() - 1).getNewsid();
                                            if (mAdapter == null || type == TYPE_REFRESH) {
                                                createAdapter();
                                                lastAutoRefreshTime = System.currentTimeMillis();
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
        if (contents == null || contents.getChannel() == null || contents.getChannel().size() < 1)
            return;
        mAdapter = new ITHomeAdapter(mActivity, contents.getChannel());
//        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                List<ITHomeListData.ItemBean> channels = contents.getChannel();
                if (channels == null || channels.size() < position) {
                    SnackbarUtils.with(refreshLayout).setMessage(getString(R.string.load_fail)).show();
                    return;
                }
                ITHomeListData.ItemBean bean = channels.get(position);
                Intent intent = new Intent(getActivity(), ITHomeDetailActivity.class);
                try {
                    intent.putExtra("title", bean.getTitle());
                    intent.putExtra("url", bean.getUrl());
                    intent.putExtra("id", bean.getNewsid());
                    intent.putExtra("time", bean.getPostdate());
                    intent.putExtra("img", bean.getImage());
                    if (!bean.isReaded()) {
                        bean.setReaded(true);
                        mAdapter.notifyItemChanged(position);
                    }
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
}
