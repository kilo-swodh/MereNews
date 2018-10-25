package androidnews.kiloproject.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import androidnews.kiloproject.R;
import androidnews.kiloproject.system.base.BaseLazyFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BaseRvFragment extends BaseLazyFragment {

    @BindView(R.id.rv_content)
    RecyclerView mRecyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    Unbinder unbinder;

    Gson gson = new Gson();

    protected long lastAutoRefreshTime = 0;
    public static final long dividerAutoRefresh = 3 * 60 * 1000;

    public static final int TYPE_LOADMORE = 1000;
    public static final int TYPE_REFRESH = 1001;

    public static final int HEADER = 1;
    public static final int CELL = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
