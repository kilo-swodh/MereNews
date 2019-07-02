package androidnews.kiloproject.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.RomUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import androidnews.kiloproject.R;
import androidnews.kiloproject.activity.MainActivity;
import androidnews.kiloproject.system.AppConfig;
import androidnews.kiloproject.system.base.BaseLazyFragment;

import static androidnews.kiloproject.system.AppConfig.LIST_TYPE_SINGLE;
import static androidnews.kiloproject.system.AppConfig.isHighRam;


public abstract class BaseRvFragment extends BaseLazyFragment {

    RecyclerView mRecyclerView;
    SmartRefreshLayout refreshLayout;
    SkeletonScreen skeletonScreen;
    BaseQuickAdapter mAdapter;

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
            mRecyclerView.setItemViewCacheSize(15);
            mRecyclerView.setDrawingCacheEnabled(true);
            mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        }
        if (ImmersionBar.hasNotchScreen(mActivity))
            refreshLayout.setHeaderInsetStart(ConvertUtils.px2dp(ImmersionBar.getStatusBarHeight(mActivity)));
        refreshLayout.setHeaderTriggerRate(0.7f);
        if (!(this instanceof ZhihuRvFragment) && AppConfig.listType == LIST_TYPE_SINGLE && !RomUtils.isMeizu()){
            skeletonScreen = Skeleton.bind(mRecyclerView)
                    .adapter(mAdapter)
                    .shimmer(true)      // whether show shimmer animation.                      default is true
                    .count(10)          // the recycler view item count.                        default is 10
                    .color(R.color.awesome_background)       // the shimmer color.                                   default is #a2878787
                    .angle(20)          // the shimmer angle.                                   default is 20;
                    .duration(1200)     // the shimmer animation duration.                      default is 1000;
                    .frozen(true)      // whether frozen recyclerView during skeleton showing  default is true;
                    .load(R.layout.list_item_skeleton_news)
                    .show();
        }
        return view;
    }

    public abstract void requestData(int type);

    public void startLowMemory(){
        mRecyclerView.setItemViewCacheSize(5);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
    }

    protected MainActivity getMainActivity(){
        return (MainActivity)mActivity;
    }
}
