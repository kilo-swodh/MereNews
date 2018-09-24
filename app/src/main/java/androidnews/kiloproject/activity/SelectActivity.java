package androidnews.kiloproject.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.blankj.utilcode.util.CacheDiskUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.gyf.barlibrary.ImmersionBar;

import java.util.ArrayList;
import java.util.List;

import androidnews.kiloproject.R;
import androidnews.kiloproject.bean.data.TypeArrayBean;
import androidnews.kiloproject.system.base.BaseActivity;
import androidnews.kiloproject.widget.easytagdragview.EasyTipDragView;
import androidnews.kiloproject.widget.easytagdragview.bean.SimpleTitleTip;
import androidnews.kiloproject.widget.easytagdragview.bean.Tip;
import androidnews.kiloproject.widget.easytagdragview.widget.TipItemView;
import butterknife.BindView;
import butterknife.ButterKnife;

import static androidnews.kiloproject.system.AppConfig.CONFIG_STATUSBAR;
import static androidnews.kiloproject.system.AppConfig.CONFIG_TYPE_ARRAY;

public class SelectActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.easy_tip_drag_view)
    EasyTipDragView easyTipDragView;

    public static final int SELECT_RESULT = 998;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        ButterKnife.bind(this);
        initToolbar(toolbar, true);
        if (SPUtils.getInstance().getBoolean(CONFIG_STATUSBAR))
            ImmersionBar.with(mActivity).fitsSystemWindows(true).statusBarDarkFont(true).init();
        else
            initStateBar(android.R.color.white, true);
    }

    @Override
    protected void initSlowly() {
        List<Tip> dragList = new ArrayList<>();
        List<Tip> addList = new ArrayList<>();
        TypeArrayBean typeArrayBean = CacheDiskUtils.getInstance().getParcelable(CONFIG_TYPE_ARRAY, TypeArrayBean.CREATOR);
        String[] tags = getResources().getStringArray(R.array.address_tag);
        for (int i = 0; i < tags.length; i++) {
            boolean isSame = false;
            for (Integer integer : typeArrayBean.getTypeArray()) {
                if (i == integer)
                    isSame = true;
            }
            if (isSame)
                dragList.add(new SimpleTitleTip(i, tags[i]));
            else
                addList.add(new SimpleTitleTip(i, tags[i]));
        }

        easyTipDragView.setAddData(addList);
//        //设置可以添加的标签数据
        easyTipDragView.setDragData(dragList);
        //在easyTipDragView处于非编辑模式下点击item的回调（编辑模式下点击item作用为删除item）
        easyTipDragView.setSelectedListener(new TipItemView.OnSelectedListener() {
            @Override
            public void onTileSelected(Tip entity, int position, View view) {
                ToastUtils.showShort(((SimpleTitleTip) entity).getTip());
            }
        });
        //设置每次数据改变后的回调（例如每次拖拽排序了标签或者增删了标签都会回调）
        easyTipDragView.setDataResultCallback(new EasyTipDragView.OnDataChangeResultCallback() {
            @Override
            public void onDataChangeResult(ArrayList<Tip> tips) {
                Log.i("heheda", tips.toString());
            }
        });
        //设置点击“确定”按钮后最终数据的回调
        easyTipDragView.setOnCompleteCallback(new EasyTipDragView.OnCompleteCallback() {
            @Override
            public void onComplete(ArrayList<Tip> tips) {
                ToastUtils.showShort("最终数据：" + tips.toString());
                //   btn.setVisibility(View.VISIBLE);
                List<Integer> result = new ArrayList<Integer>();
                if (tips.size() < 1)
                    result.add(0);
                else
                    for (Tip titleTip : tips) {
                        result.add(titleTip.getId());
                    }
                CacheDiskUtils.getInstance().put(CONFIG_TYPE_ARRAY, new TypeArrayBean(result));
                setResult(RESULT_OK);
                finish();
            }
        });
        easyTipDragView.open();
    }
}
