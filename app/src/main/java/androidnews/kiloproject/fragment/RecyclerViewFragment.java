package androidnews.kiloproject.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ajguan.library.EasyRefreshLayout;
import com.blankj.utilcode.util.SnackbarUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;
import com.google.gson.Gson;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import java.util.ArrayList;
import java.util.List;

import androidnews.kiloproject.R;
import androidnews.kiloproject.activity.NewsDetailActivity;
import androidnews.kiloproject.adapter.MainRecyclerAdapter;
import androidnews.kiloproject.bean.MainDataBean;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by florentchampigny on 24/04/15.
 */
public class RecyclerViewFragment extends BaseFragment {

    private static final boolean GRID_LAYOUT = false;
    @BindView(R.id.refresh_layout)
    EasyRefreshLayout refreshLayout;
    Unbinder unbinder;

    private int currentPage = 0;
    private int questPage = 20;

    @BindView(R.id.rv_content)
    RecyclerView mRecyclerView;

    Gson gson = new Gson();
    MainRecyclerAdapter mainAdapter;
    MainDataBean contents;

    public static final int TYPE_LOADMORE = 1000;
    public static final int TYPE_REFRESH = 1001;

    public static RecyclerViewFragment newInstance() {
        return new RecyclerViewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        //setup materialviewpager

        if (GRID_LAYOUT) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(mActivity, 2));
        } else {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        }
        mRecyclerView.setHasFixedSize(true);

        //Use this now
        mRecyclerView.addItemDecoration(new MaterialViewPagerHeaderDecorator());

        requestData(TYPE_REFRESH);
    }


    private void requestData(int type) {
        String dataUrl = "";
        if (type == TYPE_REFRESH)
            dataUrl = "/nc/article/headline/T1348647909107/"
                    + questPage + "-" + 20 + ".html";
        else
            dataUrl = "/nc/article/headline/T1348647909107/"
                    + (currentPage + questPage) + "-" + 20 + ".html";
        EasyHttp.get(dataUrl)
                .readTimeOut(30 * 1000)//局部定义读超时
                .writeTimeOut(30 * 1000)
                .connectTimeout(30 * 1000)
                .timeStamp(true)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        switch (type) {
                            case TYPE_REFRESH:
                                refreshLayout.refreshComplete();
                                break;
                            case TYPE_LOADMORE:
                                refreshLayout.loadMoreComplete();
                                break;
                        }
                        SnackbarUtils.with(refreshLayout).
                                setMessage(getString(R.string.load_fail) + e.getMessage()).
                                showError();
                    }

                    @Override
                    public void onSuccess(String response) {
                        if (!TextUtils.isEmpty(response)) {
                            Observable.create(new ObservableOnSubscribe<String>() {

                                @Override
                                public void subscribe(ObservableEmitter<String> e) throws Exception {
                                    MainDataBean data = gson.fromJson(response, MainDataBean.class);
                                    for (int i = 0; i < data.getT1348647909107().size(); i++) {
                                        if (i == 0)
                                            data.getT1348647909107().get(i).setItemType(MainDataBean.T1348647909107Bean.HEADER);
                                        else
                                            data.getT1348647909107().get(i).setItemType(MainDataBean.T1348647909107Bean.CELL);
                                    }
                                    switch (type) {
                                        case TYPE_REFRESH:
                                            currentPage = questPage;
                                            contents = data;
                                            break;
                                        case TYPE_LOADMORE:
                                            currentPage += questPage;
                                            List<MainDataBean.T1348647909107Bean> newList = new ArrayList<>();
                                            newList.addAll(contents.getT1348647909107());
                                            for (MainDataBean.T1348647909107Bean newBean : data.getT1348647909107()) {
                                                for (MainDataBean.T1348647909107Bean myBean : contents.getT1348647909107())
                                                    if (TextUtils.equals(newBean.getDocid(), myBean.getDocid())) {
                                                        continue;
                                                    }
                                                newList.add(newBean);
                                            }
                                            contents.setT1348647909107(newList);
                                            break;
                                    }
                                    e.onNext("ok");
                                    e.onComplete();
                                }
                            }).subscribeOn(Schedulers.computation())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<String>() {
                                        @Override
                                        public void accept(String o) throws Exception {
                                            SnackbarUtils.with(refreshLayout).setMessage(getString(R.string.load_success)).showSuccess();
                                            if (mainAdapter == null || type == TYPE_REFRESH) {
                                                createAdapter();
                                                refreshLayout.refreshComplete();
                                            } else if (type == TYPE_LOADMORE) {
                                                mainAdapter.notifyDataSetChanged();
                                                refreshLayout.loadMoreComplete();
                                                contents.getT1348647909107();
                                            }
                                        }
                                    });

                        } else {
                            switch (type) {
                                case TYPE_REFRESH:
                                    refreshLayout.refreshComplete();
                                    SnackbarUtils.with(refreshLayout).showError();
                                    break;
                                case TYPE_LOADMORE:
                                    refreshLayout.loadMoreComplete();
                                    SnackbarUtils.with(refreshLayout).showError();
                                    break;
                            }
                        }
                    }
                });
    }

    private void createAdapter() {
        mainAdapter = new MainRecyclerAdapter(getActivity(), contents.getT1348647909107());
        mainAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                MainDataBean.T1348647909107Bean bean = contents.getT1348647909107().get(position);
                Intent intent = null;
                switch (bean.getItemType()) {
                    case MainDataBean.T1348647909107Bean.CELL:
                        intent = new Intent(getActivity(), NewsDetailActivity.class);
                        intent.putExtra("docid", bean.getDocid());
                        startActivity(intent);
                        break;
                }
            }
        });
        mRecyclerView.setAdapter(mainAdapter);

        refreshLayout.addEasyEvent(new EasyRefreshLayout.EasyEvent() {
            @Override
            public void onLoadMore() {
                requestData(TYPE_LOADMORE);
            }

            @Override
            public void onRefreshing() {
                requestData(TYPE_REFRESH);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
