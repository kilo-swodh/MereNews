package androidnews.kiloproject.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.ConvertUtils;
import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import androidnews.kiloproject.R;
import androidnews.kiloproject.system.base.BaseLazyFragment;

import static androidnews.kiloproject.system.AppConfig.isHighRam;


public abstract class BaseRvFragment extends BaseLazyFragment {

    RecyclerView mRecyclerView;
    SmartRefreshLayout refreshLayout;

    Gson gson = new Gson();

    protected long lastAutoRefreshTime = 0;
    public static final long dividerAutoRefresh = 3 * 60 * 1000;
    public static final int PRE_LOAD_ITEM = 5;

    public static final int TYPE_LOADMORE = 1000;
    public static final int TYPE_REFRESH = 1001;

    public static final int HEADER = 1;
    public static final int CELL = 0;
    public static final int CELL_EXTRA = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        refreshLayout = (SmartRefreshLayout) view.findViewById(R.id.refreshLayout);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_content);
        if (isHighRam) {
            mRecyclerView.setItemViewCacheSize(20);
            mRecyclerView.setDrawingCacheEnabled(true);
            mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        }
        if (ImmersionBar.hasNotchScreen(mActivity))
            refreshLayout.setHeaderInsetStart(ConvertUtils.px2dp(ImmersionBar.getStatusBarHeight(mActivity)));
        refreshLayout.setHeaderTriggerRate(0.7f);
        return view;
    }

    public abstract void requestData(int type);
}
