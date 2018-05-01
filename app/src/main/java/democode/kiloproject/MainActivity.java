package democode.kiloproject;

import android.Manifest;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {


    boolean isStart = false;
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
        isStart = true;
//        requestPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isStart) {

            //初始化逻辑代码
            doSomething();

            isStart = false;
        }
    }

    private void doSomething() {
        mDatas = new ArrayList<>();

        //刷新加载
        RefreshLayout refreshLayout = (RefreshLayout)findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败

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
                                        mDatas.add(new ItemData("http://img.ssqfs.com/" + bean.getDefault_image(),bean.getGoods_name()));
                                    }
                                };
                                adapter.notifyDataSetChanged();
                                refreshlayout.finishLoadMore(true/*,false*/);//传入false表示加载失败
                            }
                        });
            }
        });

        //权限
        Acp.getInstance(this).request(new AcpOptions.Builder()
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE
                                , Manifest.permission.READ_PHONE_STATE)
                        /*以下为自定义提示语、按钮文字
                        .setDeniedMessage()
                        .setDeniedCloseBtn()
                        .setDeniedSettingBtn()
                        .setRationalMessage()
                        .setRationalBtn()*/
                        .build(),
                new AcpListener() {
                    @Override
                    public void onGranted() {
                        requestData();
                    }

                    @Override
                    public void onDenied(List<String> permissions) {
                        ToastUtils.showShort("权限被拒绝");
                    }
                });

    }

    private void requestData(){
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
                                mDatas.add(new ItemData("http://img.ssqfs.com/" + bean.getDefault_image(),bean.getGoods_name()));
                            }
                        };

                        adapter = new CommonAdapter<ItemData>(MainActivity.this, R.layout.item_layout, mDatas) {
                            @Override
                            protected void convert(ViewHolder viewHolder, ItemData item, int position) {
                                viewHolder.setText(R.id.tv_title, item.getText());
                                Glide.with(MainActivity.this).load(item.getImgUrl()).into((ImageView) viewHolder.getView(R.id.iv_image));
                            }
                        };
                        lvData.setAdapter(adapter);
                    }
                });
    }
}
