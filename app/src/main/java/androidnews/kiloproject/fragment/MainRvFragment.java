package androidnews.kiloproject.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.blankj.utilcode.util.Utils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;

import androidnews.kiloproject.widget.materialviewpager.header.MaterialViewPagerHeaderDecorator;

import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidnews.kiloproject.R;
import androidnews.kiloproject.activity.GalleyActivity;
import androidnews.kiloproject.activity.NewsDetailActivity;
import androidnews.kiloproject.adapter.MainRvAdapter;
import androidnews.kiloproject.bean.data.BlockItem;
import androidnews.kiloproject.bean.data.CacheNews;
import androidnews.kiloproject.bean.net.GalleyData;
import androidnews.kiloproject.bean.net.NewMainListData;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static androidnews.kiloproject.bean.data.BlockItem.TYPE_KEYWORDS;
import static androidnews.kiloproject.bean.data.BlockItem.TYPE_SOURCE;
import static androidnews.kiloproject.bean.data.CacheNews.CACHE_HISTORY;
import static androidnews.kiloproject.system.AppConfig.CONFIG_AUTO_LOADMORE;
import static androidnews.kiloproject.system.AppConfig.CONFIG_AUTO_REFRESH;
import static androidnews.kiloproject.system.AppConfig.getMainDataA;
import static androidnews.kiloproject.system.AppConfig.getMainDataB;

public class MainRvFragment extends BaseRvFragment {

    MainRvAdapter mainAdapter;
    //    MainListData contents;
    List<NewMainListData> contents;

    String[] goodTags;

    private static final boolean GRID_LAYOUT = false;

    private String CACHE_LIST_DATA;

    private int currentPage = 0;
    private int questPage = 20;

    private int adSize = 0;
    private int realPicCount = 0;

    List<BlockItem> blockList;
    String typeStr;

    public static MainRvFragment newInstance(int type) {
        MainRvFragment f = new MainRvFragment();
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
                try {
                    blockList = LitePal.findAll(BlockItem.class);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                goodTags = mActivity.getResources().getStringArray(R.array.good_tag);

                String json = SPUtils.getInstance().getString(CACHE_LIST_DATA, "");
                if (!TextUtils.isEmpty(json)) {
                    contents = gson.fromJson(json, new TypeToken<List<NewMainListData>>() {
                    }.getType());
                    if (contents != null && contents.size() > 0) {
                        try {
                            NewMainListData first = contents.get(0);
                            first.setItemType(HEADER);
                            requestRealPic(first);
                            final String cacheJson = SPUtils.getInstance().getString(CACHE_HISTORY + "", "");
                            List<CacheNews> cacheNews = gson.fromJson(cacheJson, new TypeToken<List<CacheNews>>() {
                            }.getType());
                            if (cacheNews != null && cacheNews.size() > 0)
                                for (NewMainListData dataItem : contents) {
                                    boolean isHisBingo = false;
                                    for (CacheNews cacheNew : cacheNews) {
                                        if (isHisBingo)
                                            break;
                                        if (TextUtils.equals(dataItem.getDocid(), cacheNew.getDocid())) {
                                            dataItem.setReaded(true);
                                            isHisBingo = true;
                                            break;
                                        }
                                    }
                                    if (blockList != null && blockList.size() > 0
                                            && dataItem.getAds() == null) {
                                        boolean isBlockBingo = false;
                                        for (BlockItem blockItem : blockList) {
                                            if (isBlockBingo)
                                                break;
                                            switch (blockItem.getType()) {
                                                case TYPE_SOURCE:
                                                    if (TextUtils.equals(dataItem.getSource(), blockItem.getText())) {
                                                        dataItem.setBlocked(true);
                                                        isBlockBingo = true;
                                                    } else
                                                        dataItem.setBlocked(false);
                                                    break;
                                                case TYPE_KEYWORDS:
                                                    if (dataItem.getTitle().contains(blockItem.getText())) {
                                                        dataItem.setBlocked(true);
                                                        isBlockBingo = true;
                                                    } else
                                                        dataItem.setBlocked(false);
                                                    break;
                                            }
                                        }
                                    }
                                }
                            e.onNext(true);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                            e.onNext(false);
                        }
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
                        MainRvFragment.super.onViewCreated(view, savedInstanceState);
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

    private void requestData(int type) {
        String dataUrl = "";
        switch (type) {
            case TYPE_REFRESH:
                currentPage = 0;
            case TYPE_LOADMORE:
                dataUrl = getMainDataA + typeStr + "/" + currentPage + getMainDataB;
                break;
        }
        EasyHttp.get(dataUrl)
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
                                        mainAdapter.loadMoreFail();
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
                                    HashMap<String, List<NewMainListData>> retMap = null;
                                    try {
                                        retMap = gson.fromJson(response,
                                                new TypeToken<HashMap<String, List<NewMainListData>>>() {
                                                }.getType());
                                        //设置头部轮播
                                        if (type == TYPE_REFRESH) {
                                            NewMainListData first = retMap.get(typeStr).get(0);
                                            first.setItemType(HEADER);
                                            requestRealPic(first);
                                        }
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                        loadFailed(type);
                                    }
                                    List<NewMainListData> newList = new ArrayList<>();

                                    final String cacheJson = SPUtils.getInstance().getString(CACHE_HISTORY + "", "");
                                    List<CacheNews> cacheNews = gson.fromJson(cacheJson, new TypeToken<List<CacheNews>>() {
                                    }.getType());

                                    switch (type) {
                                        case TYPE_REFRESH:
                                            currentPage = 0;
                                            contents = new ArrayList<>();
                                            try {
                                                newList = retMap.get(typeStr);
                                            } catch (Exception e1) {
                                                e1.printStackTrace();
                                                loadFailed(type);
                                            }
                                            for (NewMainListData dataItem : newList) {
                                                if (isGoodItem(dataItem))
                                                    contents.add(dataItem);
                                                if (cacheNews != null && cacheNews.size() > 0) {
                                                    boolean isHisBingo = false;
                                                    for (CacheNews cacheNew : cacheNews) {
                                                        if (isHisBingo)
                                                            break;
                                                        if (TextUtils.equals(dataItem.getDocid(), cacheNew.getDocid())) {
                                                            dataItem.setReaded(true);
                                                            isHisBingo = true;
                                                            break;
                                                        }
                                                    }
                                                }
                                                if (blockList != null && blockList.size() > 0) {
                                                    boolean isBlockBingo = false;
                                                    for (BlockItem blockItem : blockList) {
                                                        if (isBlockBingo)
                                                            break;
                                                        switch (blockItem.getType()) {
                                                            case TYPE_SOURCE:
                                                                if (TextUtils.equals(dataItem.getSource(), blockItem.getText())) {
                                                                    dataItem.setBlocked(true);
                                                                    isBlockBingo = true;
                                                                } else
                                                                    dataItem.setBlocked(false);
                                                                break;
                                                            case TYPE_KEYWORDS:
                                                                if (dataItem.getTitle().contains(blockItem.getText())) {
                                                                    dataItem.setBlocked(true);
                                                                    isBlockBingo = true;
                                                                } else
                                                                    dataItem.setBlocked(false);
                                                                break;
                                                        }
                                                    }
                                                }
                                            }
                                            try {
                                                SPUtils.getInstance().put(CACHE_LIST_DATA, gson.toJson(contents));
                                            } catch (Exception e1) {
                                                e1.printStackTrace();
                                            }
                                            break;
                                        case TYPE_LOADMORE:
                                            currentPage += questPage;
                                            try {
                                                newList.addAll(contents);
                                            } catch (Exception e1) {
                                                e1.printStackTrace();
                                            }
                                            boolean isAllSame = true;
                                            try {
                                                for (NewMainListData dataItem : retMap.get(typeStr)) {
                                                    boolean isSame = false;
//                                                if (TextUtils.isEmpty(newBean.getSource()) && !TextUtils.isEmpty(newBean.getTAG())){
                                                    if (!isGoodItem(dataItem)) {
                                                        continue;
                                                    }
                                                    for (NewMainListData myBean : contents) {
                                                        if (TextUtils.equals(myBean.getDocid(), dataItem.getDocid())) {
                                                            isSame = true;
                                                            break;
                                                        }
                                                    }
                                                    if (!isSame) {
                                                        if (cacheNews != null && cacheNews.size() > 0) {
                                                            for (CacheNews cacheNew : cacheNews) {
                                                                if (TextUtils.equals(dataItem.getDocid(), cacheNew.getDocid())) {
                                                                    dataItem.setReaded(true);
                                                                    break;
                                                                }
                                                            }
                                                        }
                                                        if (blockList != null && blockList.size() > 0) {
                                                            boolean isBlockBingo = false;
                                                            for (BlockItem blockItem : blockList) {
                                                                if (isBlockBingo)
                                                                    break;
                                                                switch (blockItem.getType()) {
                                                                    case TYPE_SOURCE:
                                                                        if (TextUtils.equals(dataItem.getSource(), blockItem.getText())) {
                                                                            dataItem.setBlocked(true);
                                                                            isBlockBingo = true;
                                                                        } else
                                                                            dataItem.setBlocked(false);
                                                                        break;
                                                                    case TYPE_KEYWORDS:
                                                                        if (dataItem.getTitle().contains(blockItem.getText())) {
                                                                            dataItem.setBlocked(true);
                                                                            isBlockBingo = true;
                                                                        } else
                                                                            dataItem.setBlocked(false);
                                                                        break;
                                                                }
                                                            }
                                                        }
                                                        newList.add(dataItem);
                                                        isAllSame = false;
                                                    }
                                                }
                                            } catch (Exception e1) {
                                                e1.printStackTrace();
                                                loadFailed(type);
                                            }
                                            if (!isAllSame) {
                                                contents.clear();
                                                contents.addAll(newList);
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
                                            if (mainAdapter == null || type == TYPE_REFRESH) {
                                                createAdapter();
                                                lastAutoRefreshTime = System.currentTimeMillis();
                                                try {
                                                    refreshLayout.finishRefresh(true);
                                                    SnackbarUtils.with(refreshLayout)
                                                            .setMessage(getString(R.string.load_success))
                                                            .showSuccess();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                            } else if (type == TYPE_LOADMORE) {
                                                mainAdapter.notifyDataSetChanged();
                                                if (SPUtils.getInstance().getBoolean(CONFIG_AUTO_LOADMORE))
                                                    mainAdapter.loadMoreComplete();
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
                    mainAdapter.loadMoreFail();
                else
                    refreshLayout.finishLoadMore(false);
                SnackbarUtils.with(refreshLayout).setMessage(getString(R.string.server_fail)).showError();
                break;
        }
    }

    private void createAdapter() {
        realPicCount = 0;
        if (contents.get(0).getAds() != null) {
            adSize = contents.get(0).getAds().size();
        } else {
            adSize = 0;
        }
        mainAdapter = new MainRvAdapter(mActivity, Glide.with(this), contents);
        mainAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                NewMainListData item = contents.get(position);
                Intent intent = null;
                switch (item.getItemType()) {
                    case CELL:
                        if (!TextUtils.isEmpty(item.getSkipID()) && TextUtils.equals(item.getSkipType(), "photoset")) {
                            String skipID = "";
                            String rawId;
                            rawId = item.getSkipID();
                            if (!TextUtils.isEmpty(rawId)) {
                                int index = rawId.lastIndexOf("|");
                                if (index != -1) {

                                    skipID = rawId.substring(index - 4, rawId.length());
                                    intent = new Intent(mActivity, GalleyActivity.class);
                                    intent.putExtra("skipID", skipID.replace("|", "/") + ".json");
                                    startActivity(intent);
                                } else {
                                    SnackbarUtils.with(refreshLayout).setMessage(getString(R.string.server_fail)).showError();
                                    return;
                                }
                            }
                            break;
                        } else {
                            intent = new Intent(getActivity(), NewsDetailActivity.class);
                            intent.putExtra("docid", item.getDocid().replace("_special", "").trim());
                            startActivity(intent);
                            if (!item.isReaded()) {
                                item.setReaded(true);
                                mainAdapter.notifyItemChanged(position);
                            }
                        }
                }
            }
        });
        mainAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                final String[] items = {
                        getResources().getString(R.string.action_link)
                        , getResources().getString(R.string.action_block_source)
                        , getResources().getString(R.string.action_block_keywords)
                };
                new AlertDialog.Builder(mActivity).setItems(items,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        ClipboardManager cm = (ClipboardManager) Utils.getApp().getSystemService(Context.CLIPBOARD_SERVICE);
                                        //noinspection ConstantConditions
                                        cm.setPrimaryClip(ClipData.newPlainText("link", contents.get(position).getUrl()));
                                        SnackbarUtils.with(refreshLayout).setMessage(getString(R.string.action_link)
                                                + " " + getString(R.string.successful)).showSuccess();
                                        break;
                                    case 1:
                                        Observable.create(new ObservableOnSubscribe<Integer>() {
                                            @Override
                                            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                                                try {
                                                    String newSource = contents.get(position).getSource();
                                                    if (blockList == null)
                                                        blockList = new ArrayList<>();
                                                    boolean isAdd = true;
                                                    if (blockList.size() > 0) {
                                                        for (BlockItem blockItem : blockList) {
                                                            if (blockItem.getType() == TYPE_SOURCE && TextUtils.equals(blockItem.getText(), newSource))
                                                                isAdd = false;
                                                        }
                                                    }
                                                    if (isAdd) {
                                                        BlockItem newItem = new BlockItem(TYPE_SOURCE, newSource);
                                                        blockList.add(newItem);
                                                        e.onNext(1);
                                                        newItem.save();
                                                    } else {
                                                        e.onNext(2);
                                                    }
                                                } catch (Exception e1) {
                                                    e1.printStackTrace();
                                                    e.onNext(0);
                                                } finally {
                                                    e.onComplete();
                                                }
                                            }
                                        }).subscribeOn(Schedulers.computation())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new Consumer<Integer>() {
                                                    @Override
                                                    public void accept(Integer i) throws Exception {
                                                        switch (i) {
                                                            case 0:
                                                                SnackbarUtils.with(refreshLayout)
                                                                        .setMessage(getString(R.string.action_block_source) + " " + getString(R.string.fail))
                                                                        .showSuccess();
                                                                break;
                                                            case 1:
                                                                SnackbarUtils.with(refreshLayout)
                                                                        .setMessage(getString(R.string.start_after_restart_list))
                                                                        .showSuccess();
                                                                break;
                                                            case 2:
                                                                SnackbarUtils.with(refreshLayout)
                                                                        .setMessage(getString(R.string.repeated))
                                                                        .showSuccess();
                                                                break;
                                                        }
                                                    }
                                                });
                                        dialog.dismiss();
                                        break;
                                    case 2:
                                        final EditText editText = new EditText(mActivity);
                                        editText.setText(contents.get(position).getTitle());
                                        editText.setTextColor(getResources().getColor(R.color.black));
                                        new MaterialStyledDialog.Builder(mActivity)
                                                .setHeaderDrawable(R.drawable.ic_edit)
                                                .setHeaderScaleType(ImageView.ScaleType.CENTER)
                                                .setCustomView(editText)
                                                .setHeaderColor(R.color.colorAccent)
                                                .setPositiveText(R.string.save)
                                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                    @Override
                                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                        Observable.create(new ObservableOnSubscribe<Integer>() {
                                                            @Override
                                                            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                                                                try {
                                                                    if (blockList == null)
                                                                        blockList = new ArrayList<>();

                                                                    String keywords = editText.getText().toString();
                                                                    boolean isAdd = true;
                                                                    if (blockList.size() > 0) {
                                                                        for (BlockItem blockItem : blockList) {
                                                                            if (blockItem.getType() == TYPE_KEYWORDS && TextUtils.equals(blockItem.getText(), keywords))
                                                                                isAdd = false;
                                                                        }
                                                                    }
                                                                    if (isAdd) {
                                                                        BlockItem newItem = new BlockItem(TYPE_KEYWORDS, keywords);
                                                                        blockList.add(newItem);
                                                                        e.onNext(1);
                                                                        newItem.save();
                                                                    } else {
                                                                        e.onNext(2);
                                                                    }
                                                                } catch (Exception e1) {
                                                                    e1.printStackTrace();
                                                                    e.onNext(0);
                                                                } finally {
                                                                    e.onComplete();
                                                                }
                                                            }
                                                        }).subscribeOn(Schedulers.computation())
                                                                .observeOn(AndroidSchedulers.mainThread())
                                                                .subscribe(new Consumer<Integer>() {
                                                                    @Override
                                                                    public void accept(Integer i) throws Exception {
                                                                        switch (i) {
                                                                            case 0:
                                                                                SnackbarUtils.with(refreshLayout).setMessage(getString(R.string.action_block_keywords)
                                                                                        + " " + getString(R.string.fail)).showSuccess();
                                                                                break;
                                                                            case 1:
                                                                                SnackbarUtils.with(refreshLayout)
                                                                                        .setMessage(getString(R.string.start_after_restart_list))
                                                                                        .showSuccess();
                                                                                break;
                                                                            case 2:
                                                                                SnackbarUtils.with(refreshLayout)
                                                                                        .setMessage(getString(R.string.repeated))
                                                                                        .showSuccess();
                                                                                break;
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                })
                                                .setNegativeText(getResources().getString(android.R.string.cancel))
                                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                                    @Override
                                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                        dialog.dismiss();
                                                    }
                                                })
                                                .show();
                                        dialog.dismiss();
                                        break;
                                }
                            }
                        }).show();
                return true;
            }
        });
        if (mRecyclerView == null)
            return;
        mRecyclerView.setAdapter(mainAdapter);
        if (SPUtils.getInstance().getBoolean(CONFIG_AUTO_LOADMORE)) {
            mainAdapter.setPreLoadNumber(5);
            mainAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
                @Override
                public void onLoadMoreRequested() {
                    requestData(TYPE_LOADMORE);
                }
            }, mRecyclerView);
            mainAdapter.disableLoadMoreIfNotFullPage();
            refreshLayout.setEnableLoadMore(false);
        }
    }

    private void requestRealPic(NewMainListData first) {
        if (first.getAds() != null)
            for (int i = 0; i < first.getAds().size(); i++) {
                final int position = i;
                NewMainListData.AdsBean bean =
                        first.getAds().get(i);
                if (!bean.getSkipID().contains("|") ||
                        TextUtils.equals(bean.getTitle(), (first.getTitle()))) {
                    first.getAds().remove(i);
                    continue;
                }
                if (TextUtils.equals(bean.getImgsrc(), "bigimg")) {
                    String skipID = bean.getSkipID().split("000")[1];
                    EasyHttp.get("/photo/api/set/" + "000" + skipID.replace("|", "/") + ".json")
                            .readTimeOut(30 * 1000)//局部定义读超时
                            .writeTimeOut(30 * 1000)
                            .connectTimeout(30 * 1000)
                            .timeStamp(true)
                            .execute(new SimpleCallBack<String>() {
                                @Override
                                public void onError(ApiException e) {
                                    if (refreshLayout != null)
                                        SnackbarUtils.with(refreshLayout).setMessage(getString(R.string.load_fail) + e.getMessage()).showError();
                                }

                                @Override
                                public void onSuccess(String response) {
                                    if (!TextUtils.isEmpty(response) || TextUtils.equals(response, "{}")) {
                                        GalleyData galleyContent;
                                        try {
                                            galleyContent = gson.fromJson(response, GalleyData.class);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            return;
                                        }
                                        if (contents != null && contents.size() > 0) {
                                            NewMainListData bean = contents.get(0);
                                            try {
                                                bean.getAds().get(position).setImgsrc(galleyContent.getPhotos().get(0).getImgurl());
                                                if (mainAdapter != null)
                                                    mainAdapter.notifyItemChanged(0);
                                                realPicCount++;
                                                if (adSize > 0 && realPicCount == adSize) {
                                                    String json = gson.toJson(contents);
                                                    SPUtils.getInstance().put(CACHE_LIST_DATA, json);
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }
                            });
                }
            }
    }

    private boolean isGoodItem(NewMainListData data) {
        if (data.getAds() != null && data.getAds().size() > 0)
            return true;
        String mTag = data.getTAGS();
        if (TextUtils.isEmpty(data.getUrl_3w())) {       //老新闻
            return false;
        } else if (TextUtils.isEmpty(mTag))
            return true;
        else {
            for (String gTag : goodTags) {
                if (TextUtils.equals(gTag, mTag)) {
                    return true;
                }
            }
            return false;
        }
    }
}
