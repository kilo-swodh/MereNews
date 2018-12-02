package androidnews.kiloproject.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemDragListener;

import java.util.ArrayList;
import java.util.List;

import androidnews.kiloproject.R;
import androidnews.kiloproject.adapter.ChannelPagerAdapter;
import androidnews.kiloproject.adapter.ItemDragAdapter;
import androidnews.kiloproject.fragment.ChannelFragment;
import androidnews.kiloproject.system.base.BaseActivity;


import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static androidnews.kiloproject.system.AppConfig.CONFIG_TYPE_ARRAY;
import static androidnews.kiloproject.system.AppConfig.CONFIG_TYPE_SORT;
import static androidnews.kiloproject.system.AppConfig.TYPE_ITHOME_END_USED;
import static androidnews.kiloproject.system.AppConfig.TYPE_ITHOME_START;
import static androidnews.kiloproject.system.AppConfig.TYPE_NETEASE_END_USED;
import static androidnews.kiloproject.system.AppConfig.TYPE_NETEASE_START;
import static androidnews.kiloproject.system.AppConfig.TYPE_OTHER_END_USED;
import static androidnews.kiloproject.system.AppConfig.TYPE_VIDEO_END_USED;
import static androidnews.kiloproject.system.AppConfig.TYPE_VIDEO_START;
import static androidnews.kiloproject.system.AppConfig.TYPE_ZHIHU;

public class ChannelActivity extends BaseActivity {

    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager mViewpager;
    ChannelPagerAdapter adapter;
    private List<Fragment> fragmentList = new ArrayList<Fragment>();
    String[] channnelTabs;
    int[] sortArray;
    SPUtils spUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tab_channel);
        mViewpager = (ViewPager) findViewById(R.id.vp_channel);

        initStateBar(R.color.main_background, true);
        initToolbar(toolbar, true);
        getSupportActionBar().setTitle(getString(R.string.channel_select));
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_save:
                        item.setEnabled(false);
                        saveChannel();
                        break;
                    case R.id.action_sort:
                        RecyclerView rvSort = new RecyclerView(mActivity);
                        List<String> list = new ArrayList<String>();
                        for (int i : sortArray) {
                            list.add(channnelTabs[i]);
                        }

                        ItemDragAdapter mAdapter = new ItemDragAdapter(list);
                        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(mAdapter);
                        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
                        itemTouchHelper.attachToRecyclerView(rvSort);

                        mAdapter.enableDragItem(itemTouchHelper, R.id.root_view, true);
                        mAdapter.setOnItemDragListener(new OnItemDragListener() {
                            @Override
                            public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos) {
                            }

                            @Override
                            public void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to) {
                                exchange(sortArray, from, to);
                            }

                            @Override
                            public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos) {
                            }
                        });
                        rvSort.setAdapter(mAdapter);
                        rvSort.setLayoutManager(new LinearLayoutManager(mActivity));
                        new AlertDialog.Builder(mActivity)
                                .setMessage(R.string.sort_tip)
                                .setCancelable(true)
                                .setView(rvSort).show();
                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected void initSlowly() {
        spUtils = SPUtils.getInstance();

        channnelTabs = getResources().getStringArray(R.array.channel_tab);
        sortArray = new int[channnelTabs.length];
        String sortStr = spUtils.getString(CONFIG_TYPE_SORT);

        if (TextUtils.isEmpty(sortStr)) {
            StringBuilder sb = new StringBuilder();
            sortArray = new int[channnelTabs.length];
            for (int i = 0; i < channnelTabs.length; i++) {
                sortArray[i] = i;
                sb.append(i + "-");
            }
            spUtils.put(CONFIG_TYPE_SORT, sb.toString());
        } else {
            String[] channelStrArray = sortStr.split("-");
            if (channelStrArray.length == channnelTabs.length) {
                for (int i = 0; i < channelStrArray.length; i++) {
                    sortArray[i] = Integer.parseInt(channelStrArray[i]);
                }
            } else {
                for (int i = 0; i < channelStrArray.length; i++) {
                    sortArray[i] = Integer.parseInt(channelStrArray[i]);
                }
                for (int i = channelStrArray.length; i < channnelTabs.length; i++) {
                    sortArray[i] = i;
                }
            }
        }

        tabLayout.setupWithViewPager(mViewpager);
        fragmentList.add(ChannelFragment.newInstance(TYPE_NETEASE_START, TYPE_NETEASE_END_USED));
        fragmentList.add(ChannelFragment.newInstance(TYPE_ZHIHU, TYPE_OTHER_END_USED));
        fragmentList.add(ChannelFragment.newInstance(TYPE_VIDEO_START, TYPE_VIDEO_END_USED));
        fragmentList.add(ChannelFragment.newInstance(TYPE_ITHOME_START, TYPE_ITHOME_END_USED));
        adapter = new ChannelPagerAdapter(getSupportFragmentManager(), fragmentList);
        mViewpager.setOffscreenPageLimit(fragmentList.size());      //静态页面,就都保存在内存里了
        mViewpager.setAdapter(adapter);
        for (int i : sortArray) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setText(channnelTabs[i]);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (NetworkUtils.isConnected())
            getMenuInflater().inflate(R.menu.channel_items, menu);//加载menu布局
        return true;
    }

    private void saveChannel() {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                StringBuilder channelSb = new StringBuilder();
                StringBuilder sortSb = new StringBuilder();
                for (int i : sortArray) {
                    sortSb.append(i + "-");
                    int[] channelArray = ((ChannelFragment) fragmentList.get(i)).getChannel();
                    if (channelArray != null && channelArray.length > 0) {
                        for (int channel : channelArray) {
                            channelSb.append(channel + "#");
                        }
                    }
                }
                spUtils.put(CONFIG_TYPE_SORT, sortSb.toString());
                if (channelSb.length() < 2)
                    spUtils.put(CONFIG_TYPE_ARRAY, "0#");
                else
                    spUtils.put(CONFIG_TYPE_ARRAY, channelSb.toString());

                e.onNext(true);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        setResult(RESULT_OK);
                        finish();
                    }
                });
    }

    // 交换两个元素
    private void exchange(int[] nums, int x, int y) {
        int temp = nums[x];
        nums[x] = nums[y];
        nums[y] = temp;
    }
}
