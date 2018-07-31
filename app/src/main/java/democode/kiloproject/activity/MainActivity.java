package democode.kiloproject.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.constraint.Placeholder;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import democode.kiloproject.network.GoodsData;
import democode.kiloproject.network.ItemData;
import democode.kiloproject.R;

import static democode.kiloproject.activity.H5Activity.WEBVIEW_URL;

public class MainActivity extends BaseActivity {

    @BindView(R.id.lv_data)
    ListView lvData;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    private List<ItemData> mDatas;
    Gson gson = new Gson();
    CommonAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initStateBar(R.color.colorPrimary, false);
    }

    @Override
    void initView() {
        mDatas = new ArrayList<>();

        //刷新加载
        RefreshLayout refreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {

                if (adapter != null){
                    //数据请求
                    EasyHttp.get("index.php?app=search&act=index&cate_id=15")
                            .readTimeOut(30 * 1000)//局部定义读超时
                            .writeTimeOut(30 * 1000)
                            .connectTimeout(30 * 1000)
//                .params("name","22)
                            .timeStamp(true)
                            .execute(new SimpleCallBack<String>() {
                                @Override
                                public void onError(ApiException e) {
                                    ToastUtils.showShort(e.getMessage());
                                    refreshlayout.finishLoadMore(false);//传入false表示加载失败
                                }

                                @Override
                                public void onSuccess(String response) {
                                    if (response != null) {
                                        GoodsData user = gson.fromJson(response, GoodsData.class);
                                        for (GoodsData.ResultBean bean : user.getResult()) {
                                            mDatas.add(new ItemData("http://img.ssqfs.com/" + bean.getDefault_image(), bean.getGoods_name()));
                                        }
                                    }
                                    adapter.notifyDataSetChanged();
                                    refreshlayout.finishLoadMore(true/*,false*/);//传入false表示加载失败
                                }
                            });
            }else {
                    requestData();
                }
            }
        });

        requestPermission(new Action() {
            @Override
            public void onAction(Object data) {
                requestData();
            }
        },Permission.Group.STORAGE);
    }

    private void requestData() {
        //数据请求
        EasyHttp.get("index.php?app=search&act=index&cate_id=15")
                .readTimeOut(30 * 1000)//局部定义读超时
                .writeTimeOut(30 * 1000)
                .connectTimeout(30 * 1000)
//                .params("name","22)
                .timeStamp(true)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        ToastUtils.showShort(e.getMessage());
                    }

                    @Override
                    public void onSuccess(String response) {
                        if (response != null) {
                            GoodsData user = gson.fromJson(response, GoodsData.class);
                            for (GoodsData.ResultBean bean : user.getResult()) {
                                mDatas.add(new ItemData("http://img.ssqfs.com/" + bean.getDefault_image(), bean.getGoods_name()));
                            }
                        }

                        adapter = new CommonAdapter<ItemData>(MainActivity.this, R.layout.item_layout, mDatas) {
                            @Override
                            protected void convert(ViewHolder viewHolder, ItemData item, int position) {
                                viewHolder.setText(R.id.tv_title, item.getText());
                                Glide.with(mActivity)
                                        .load(item.getImgUrl())
                                        .apply(RequestOptions.placeholderOf(R.mipmap.ic_launcher))
                                        .apply(RequestOptions.errorOf(R.mipmap.ic_launcher_round))
                                        .into((ImageView) viewHolder.getView(R.id.iv_image));
                            }
                        };
                        lvData.setAdapter(adapter);
                        Intent i = new Intent(mActivity, H5Activity.class);
                        i.putExtra(WEBVIEW_URL, "http:www.4399.com");
                        startActivity(i);
                    }
                });
    }
}
