package androidnews.kiloproject.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.litepal.LitePal;

import java.util.List;

import androidnews.kiloproject.R;
import androidnews.kiloproject.activity.CnBetaDetailActivity;
import androidnews.kiloproject.adapter.CnBetaAdapter;
import androidnews.kiloproject.entity.data.CacheNews;
import androidnews.kiloproject.entity.net.CnBetaListData;
import androidnews.kiloproject.system.AppConfig;
import androidnews.kiloproject.util.CNBetaUtils;
import androidnews.kiloproject.widget.materialviewpager.header.MaterialViewPagerHeaderDecorator;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static androidnews.kiloproject.entity.data.CacheNews.CACHE_HISTORY;
import static androidnews.kiloproject.system.AppConfig.CONFIG_AUTO_LOADMORE;
import static androidnews.kiloproject.system.AppConfig.GET_CNBETA_EXTRA;
import static androidnews.kiloproject.system.AppConfig.GET_CNBETA_MD5_EXTRA;
import static androidnews.kiloproject.system.AppConfig.HOST_CNBETA;
import static androidnews.kiloproject.system.AppConfig.GET_CNBETA_REFRESH;
import static androidnews.kiloproject.system.AppConfig.GET_CNBETA_LOADMORE;
import static androidnews.kiloproject.system.AppConfig.LIST_TYPE_MULTI;
import static androidnews.kiloproject.system.AppConfig.TYPE_CNBETA;

public class CnBetaRvFragment extends BaseRvFragment {

    //    MainListData contents;
    List<CnBetaListData.ResultBean> contents;

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
        typeStr = getResources().getStringArray(R.array.address)[TYPE_CNBETA];
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
                    contents = gson.fromJson(json, new TypeToken<List<CnBetaListData.ResultBean>>() {
                    }.getType());
                    if (contents != null && contents.size() > 0) {
                        List<CacheNews> cacheNews = null;
                        try {
                            cacheNews = LitePal.where("type = ?", String.valueOf(CACHE_HISTORY)).find(CacheNews.class);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        if (cacheNews != null && cacheNews.size() > 0)
                            for (CnBetaListData.ResultBean data : contents) {
                                for (CacheNews cacheNew : cacheNews) {
                                    if (TextUtils.equals(data.getSid() + "", cacheNew.getDocid())) {
                                        data.setReaded(true);
                                        break;
                                    }
                                }
                            }
                        lastItemId = contents.get(contents.size() - 1).getSid();
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
                        CnBetaRvFragment.super.onViewCreated(view, savedInstanceState);
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
            if (contents == null || contents.size() == 0 ||
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
                dataUrl = GET_CNBETA_REFRESH.replace("{timestamp}", System.currentTimeMillis() + "");
                dataUrl = dataUrl + GET_CNBETA_EXTRA + CNBetaUtils.md5(dataUrl + GET_CNBETA_MD5_EXTRA);
                break;
            case TYPE_LOADMORE:
                dataUrl = GET_CNBETA_LOADMORE.replace("{timestamp}", System.currentTimeMillis() + "").replace("{end_sid}", lastItemId);
                dataUrl = dataUrl + GET_CNBETA_EXTRA + CNBetaUtils.md5(dataUrl + GET_CNBETA_MD5_EXTRA);
                break;
        }

        EasyHttp.get(HOST_CNBETA + dataUrl)
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
                                    if (AppConfig.isHaptic)
                                        refreshLayout.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                                    refreshLayout.finishRefresh(false);
                                    break;
                                case TYPE_LOADMORE:
                                    if (SPUtils.getInstance().getBoolean(CONFIG_AUTO_LOADMORE) && mAdapter != null)
                                        mAdapter.loadMoreFail();
                                    else
                                        refreshLayout.finishLoadMore(false);
                                    break;
                            }
                            ToastUtils.showShort(getString(R.string.load_fail) + e.getMessage());
                        }
                    }

                    @Override
                    public void onSuccess(String response) {
                        if (!TextUtils.isEmpty(response)) {
                            Observable.create(new ObservableOnSubscribe<Boolean>() {
                                @Override
                                public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                                    CnBetaListData newData = null;
                                    List<CacheNews> cacheNews = null;
                                    try {
                                        newData = gson.fromJson(response, CnBetaListData.class);
                                        //设置头部轮播
                                        cacheNews = LitePal.where("type = ?", String.valueOf(CACHE_HISTORY)).find(CacheNews.class);
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                    }

                                    switch (type) {
                                        case TYPE_REFRESH:
                                            if (newData == null || newData.getResult() == null)
                                                return;
                                            if (cacheNews != null && cacheNews.size() > 0)
                                                for (CnBetaListData.ResultBean data : newData.getResult()) {
                                                    for (CacheNews cacheNew : cacheNews) {
                                                        if (TextUtils.equals(data.getSid() + "", cacheNew.getDocid())) {
                                                            data.setReaded(true);
                                                            break;
                                                        }
                                                    }
                                                }
                                            contents = newData.getResult();
                                            SPUtils.getInstance().put(CACHE_LIST_DATA, gson.toJson(contents));
                                            e.onNext(true);
                                            break;
                                        case TYPE_LOADMORE:
                                            try {
                                                if (cacheNews != null && cacheNews.size() > 0 && newData.getResult() != null) {
                                                    for (CnBetaListData.ResultBean data : newData.getResult()) {
                                                        for (CacheNews cacheNew : cacheNews) {
                                                            if (TextUtils.equals(data.getSid() + "", cacheNew.getDocid())) {
                                                                data.setReaded(true);
                                                                break;
                                                            }
                                                        }
                                                    }
                                                    contents.addAll(newData.getResult());
                                                    e.onNext(true);
                                                }
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
                                            if (o && contents.size() > 0)
                                                lastItemId = contents.get(contents.size() - 1).getSid();
                                            if (mAdapter == null || type == TYPE_REFRESH) {
                                                createAdapter();
                                                lastAutoRefreshTime = System.currentTimeMillis();
                                                try {
                                                    if (AppConfig.isHaptic)
                                                        refreshLayout.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
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
                if (AppConfig.isHaptic)
                    refreshLayout.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
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
        if (contents == null || contents.size() < 1)
            return;
        mAdapter = new CnBetaAdapter(mActivity, contents);
//        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (contents == null || contents.size() < position) {
                    SnackbarUtils.with(refreshLayout).setMessage(getString(R.string.load_fail)).show();
                    return;
                }
                CnBetaListData.ResultBean bean = contents.get(position);
                Intent intent = new Intent(getActivity(), CnBetaDetailActivity.class);
                try {
                    intent.putExtra("sid", bean.getSid());
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
