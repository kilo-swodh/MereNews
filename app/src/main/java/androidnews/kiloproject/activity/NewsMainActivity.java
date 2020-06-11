package androidnews.kiloproject.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.bumptech.glide.Glide;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.gyf.immersionbar.ImmersionBar;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidnews.kiloproject.R;
import androidnews.kiloproject.entity.data.BlockItem;
import androidnews.kiloproject.entity.net.PhotoCenterData;
import androidnews.kiloproject.fragment.BaseRvFragment;
import androidnews.kiloproject.fragment.CnBetaRvFragment;
import androidnews.kiloproject.fragment.GuoKrRvFragment;
import androidnews.kiloproject.fragment.ITHomeRvFragment;
import androidnews.kiloproject.fragment.MainRvFragment;
import androidnews.kiloproject.fragment.PressRvFragment;
import androidnews.kiloproject.fragment.SmartisanRvFragment;
import androidnews.kiloproject.fragment.VideoRvFragment;
import androidnews.kiloproject.fragment.ZhihuRvFragment;
import androidnews.kiloproject.service.PushIntentService;
import androidnews.kiloproject.system.AppConfig;
import androidnews.kiloproject.system.base.BaseActivity;
import androidnews.kiloproject.util.PollingUtils;
import androidnews.kiloproject.widget.materialviewpager.MaterialViewPager;
import androidnews.kiloproject.widget.materialviewpager.header.HeaderDesign;
import cn.jzvd.Jzvd;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static androidnews.kiloproject.system.AppConfig.CONFIG_RANDOM_HEADER;
import static androidnews.kiloproject.system.AppConfig.CONFIG_SHOW_EXPLORER;
import static androidnews.kiloproject.system.AppConfig.CONFIG_TYPE_ARRAY;
import static androidnews.kiloproject.system.AppConfig.DOWNLOAD_EXPLORER_ADDRESS;
import static androidnews.kiloproject.system.AppConfig.NEWS_PHOTO_URL;
import static androidnews.kiloproject.system.AppConfig.TYPE_CNBETA;
import static androidnews.kiloproject.system.AppConfig.TYPE_GUOKR;
import static androidnews.kiloproject.system.AppConfig.TYPE_ITHOME_END;
import static androidnews.kiloproject.system.AppConfig.TYPE_ITHOME_START;
import static androidnews.kiloproject.system.AppConfig.TYPE_PRESS_END;
import static androidnews.kiloproject.system.AppConfig.TYPE_PRESS_START;
import static androidnews.kiloproject.system.AppConfig.TYPE_SMARTISAN_END;
import static androidnews.kiloproject.system.AppConfig.TYPE_SMARTISAN_START;
import static androidnews.kiloproject.system.AppConfig.TYPE_VIDEO_END;
import static androidnews.kiloproject.system.AppConfig.TYPE_VIDEO_START;
import static androidnews.kiloproject.system.AppConfig.TYPE_ZHIHU;
import static androidnews.kiloproject.system.AppConfig.isPush;
import static androidnews.kiloproject.system.AppConfig.isPushMode;
import static androidnews.kiloproject.util.PollingUtils.PUSH_ACTIVE;
import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class NewsMainActivity extends BaseActivity implements View.OnClickListener {

    private BottomAppBar mAppBarBottom;
    private Toolbar toolbar;
    private TabLayout mCollectTab;
    private ViewPager2 mContentVp;
    private FloatingActionButton mFab;
    private CoordinatorLayout mLayoutCoordinator;

    FragmentStateAdapter mPagerAdapter;
    public static final int DEFAULT_PAGE = 4;
    PhotoCenterData photoData;
    int bgPosition = 0;
    int[] channelArray = new int[DEFAULT_PAGE];
    List<BaseRvFragment> fragments = new ArrayList<>();
    String[] tagNames;
    private SPUtils spUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_news);
        initView();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        if (AppConfig.isStatusBar)
            ImmersionBar.with(mActivity)
                    .statusBarColor(R.color.mask)
                    .navigationBarColor(R.color.mask, R.color.main_text_color_dark, 0.4f)
                    .fitsSystemWindows(true)
                    .init();
        else
            ImmersionBar.with(mActivity)
                    .statusBarColor(R.color.transparent)  //同时自定义状态栏和导航栏颜色，不写默认状态栏为透明色，导航栏为黑色
                    .navigationBarColor(ImmersionBar.isSupportNavigationIconDark() ? R.color.main_background : R.color.divider)
                    .navigationBarDarkIcon(!AppConfig.isNightMode && ImmersionBar.isSupportNavigationIconDark())
                    .init();
    }

    private void initView() {
        mAppBarBottom = (BottomAppBar) findViewById(R.id.bottom_app_bar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mCollectTab = (TabLayout) findViewById(R.id.tab_collect);
        mContentVp = (ViewPager2) findViewById(R.id.vp_content);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(this);
        mLayoutCoordinator = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        mFab.setShowMotionSpecResource(R.animator.fab_show);
        mFab.setHideMotionSpecResource(R.animator.fab_hide);

        //设置底部栏Menu的点击事件
        mAppBarBottom.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mAppBarBottom.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });
    }

    @Override
    protected void initSlowly() {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                if (spUtils == null)
                    spUtils = SPUtils.getInstance();

                tagNames = getResources().getStringArray(R.array.address_tag);

                String arrayStr = spUtils.getString(CONFIG_TYPE_ARRAY);

                AppConfig.blockList = LitePal.findAll(BlockItem.class);

                if (TextUtils.isEmpty(arrayStr)) {
                    channelArray = new int[DEFAULT_PAGE];
                    for (int i = 0; i < DEFAULT_PAGE; i++) {
                        channelArray[i] = i;
                    }
                    e.onNext(true);
                } else {
                    String[] channelStrArray = arrayStr.split("#");
                    List<Integer> channelList = new ArrayList<>();
                    for (int i = 0; i < channelStrArray.length; i++) {
                        int index = Integer.parseInt(channelStrArray[i]);
                        if (index > tagNames.length - 1)
                            continue;
                        if (!TextUtils.equals(tagNames[index], "fake")) {
                            channelList.add(index);
                        }
                    }
                    channelArray = new int[channelList.size()];
                    for (int i = 0; i < channelList.size(); i++) {
                        channelArray[i] = channelList.get(i);
                    }
                    e.onNext(true);
                }
                saveChannel(channelArray);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            if (mPagerAdapter == null) {
                                mPagerAdapter = new FragmentStateAdapter(mActivity) {
                                    @NonNull
                                    @Override
                                    public Fragment createFragment(int position) {
                                        BaseRvFragment fragment = NewsMainActivity.this.createFragment(position);
                                        fragments.add(fragment);
                                        return fragment;
                                    }

                                    @Override
                                    public int getItemCount() {
                                        return channelArray.length;
                                    }
                                };
                                mContentVp.setAdapter(mPagerAdapter);
                                mContentVp.setOffscreenPageLimit(2);
                                mCollectTab.setTabMode(TabLayout.MODE_AUTO);

                                //初始化+联动
                                new TabLayoutMediator(mCollectTab, mContentVp, new TabLayoutMediator.TabConfigurationStrategy() {
                                    @Override
                                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                                        tab.setText(tagNames[channelArray[position]]);
                                    }
                                }).attach();
                            } else {
                                mPagerAdapter.notifyDataSetChanged();
                            }
                        }
                        if (AppConfig.isEasterEggs && !SPUtils.getInstance().getBoolean(CONFIG_SHOW_EXPLORER)) {
                            new MaterialStyledDialog.Builder(mActivity)
                                    .setHeaderDrawable(R.drawable.ic_smile)
                                    .setHeaderScaleType(ImageView.ScaleType.CENTER)
                                    .setTitle(getResources().getString(R.string.explorer_title))
                                    .setDescription(getResources().getString(R.string.explorer_message))
                                    .setHeaderColor(R.color.colorPrimary)
                                    .setPositiveText(android.R.string.ok)
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            Uri uri = Uri.parse(DOWNLOAD_EXPLORER_ADDRESS);
                                            startActivity(new Intent(Intent.ACTION_VIEW, uri));
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
                            SPUtils.getInstance().put(CONFIG_SHOW_EXPLORER, true);
                        }
//                            checkUpdate();
                    }
                });
        if (isPush && isPushMode)
            checkPushCompat();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Glide.with(mActivity).resumeRequests();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                // TODO 20/06/09
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECT_RESULT:
                if (resultCode == RESULT_OK) {
                    data = getIntent();
                    finish();
                    startActivity(data);
                }
                break;
            case SETTING_RESULT:
                if (resultCode == RESULT_OK)
                    initSlowly();
                break;
            case BLOCK_RESULT:
                if (resultCode == RESULT_OK)
                    SnackbarUtils.with(mContentVp)
                            .setMessage(getResources()
                                    .getString(R.string.start_after_restart_list))
                            .show();
                break;
        }
    }

    private BaseRvFragment createFragment(int position) {
        int type = channelArray[position];
        if (type >= TYPE_ZHIHU) {
            switch (type) {
                case TYPE_ZHIHU:
                    return new ZhihuRvFragment();
                case TYPE_GUOKR:
                    return new GuoKrRvFragment();
                case TYPE_CNBETA:
                    return new CnBetaRvFragment();
            }
            if (type >= TYPE_VIDEO_START && type <= TYPE_VIDEO_END)
                return VideoRvFragment.newInstance(channelArray[position]);
            else if (type >= TYPE_ITHOME_START && type <= TYPE_ITHOME_END)
                return ITHomeRvFragment.newInstance(channelArray[position]);
            else if (type >= TYPE_PRESS_START && type <= TYPE_PRESS_END)
                return PressRvFragment.newInstance(channelArray[position]);
            else if (type >= TYPE_SMARTISAN_START && type <= TYPE_SMARTISAN_END)
                return SmartisanRvFragment.newInstance(channelArray[position]);
        }
        return MainRvFragment.newInstance(channelArray[position]);
    }


    Timer timer;
    TimerTask timerTask;

    private void requestBgData(final int type) {
//        EasyHttp.get(NEWS_PHOTO_URL)
//                .readTimeOut(30 * 1000)//局部定义读超时
//                .writeTimeOut(30 * 1000)
//                .connectTimeout(30 * 1000)
//                .timeStamp(true)
//                .execute(new SimpleCallBack<String>() {
//                    @Override
//                    public void onError(ApiException e) {
//                        e.printStackTrace();
//                    }
//
//                    @Override
//                    public void onSuccess(final String result) {
//                        Observable.create(new ObservableOnSubscribe<Boolean>() {
//                            @Override
//                            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
//                                String temp = result.replace(")", "}");
//                                String response = temp.replace("cacheMoreData(", "{\"cacheMoreData\":");
//                                if (!TextUtils.isEmpty(response) || TextUtils.equals(response, "{}")) {
//                                    try {
//                                        photoData = gson.fromJson(response, PhotoCenterData.class);
//                                        e.onNext(true);
//                                    } catch (Exception e1) {
//                                        e1.printStackTrace();
//                                        e.onNext(false);
//                                    }
//                                } else e.onNext(false);
//                                e.onComplete();
//                            }
//                        }).subscribeOn(Schedulers.computation())
//                                .observeOn(AndroidSchedulers.mainThread())
//                                .subscribe(new Consumer<Boolean>() {
//                                    @Override
//                                    public void accept(Boolean aBoolean) throws Exception {
//                                        if (aBoolean) {
//                                            switch (type) {
//                                                case 0:
//                                                    timer = new Timer();
//                                                    timerTask = new TimerTask() {
//                                                        @Override
//                                                        public void run() {
//                                                            runOnUiThread(new Runnable() {
//                                                                @Override
//                                                                public void run() {
//                                                                    try {
//                                                                        if (mViewPager != null && photoData != null) {
//                                                                            if (bgPosition == photoData.getCacheMoreData().size()) {
//                                                                                bgPosition = 0;
//                                                                            }
//                                                                            mViewPager.setImageUrl(photoData.getCacheMoreData().get(bgPosition).getCover(), 300);
//                                                                            bgPosition++;
//                                                                        }
//                                                                    } catch (Exception e) {
//                                                                        e.printStackTrace();
//                                                                    }
//                                                                }
//                                                            });
//                                                        }
//                                                    };
//                                                    timer.schedule(timerTask, 0, 10 * 1000);
//                                                    break;
//                                                case 1:
//                                                    mViewPager.setMaterialViewPagerListener(new MaterialViewPager.Listener() {
//                                                        @Override
//                                                        public HeaderDesign getHeaderDesign(int page) {
//                                                            int postion = page % photoData.getCacheMoreData().size();
//
//                                                            return HeaderDesign.fromColorResAndUrl(
//                                                                    R.color.deepskyblue,
//                                                                    photoData.getCacheMoreData().get(postion).getCover());
//                                                        }
//                                                        //execute others actions if needed (ex : modify your header logo)
//                                                    });
//                                                    mViewPager.setImageUrl(photoData.getCacheMoreData().get(0).getCover(), 200);
//                                                    break;
//                                            }
//                                        } else
//                                            SnackbarUtils.with(mViewPager).setMessage(getString(R.string.server_fail)).showError();
//                                    }
//                                });
//                    }
//                });
    }


    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    public static boolean saveChannel(int[] channelArray) {
        StringBuilder sb = new StringBuilder();

        try {
            for (Integer integer : channelArray) {
                sb.append(integer + "#");
            }
            SPUtils.getInstance().put(CONFIG_TYPE_ARRAY, sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onMessageEvent(MessageEvent event) {/* Do something */}

    @Override
    protected void onPause() {
        super.onPause();
        Jzvd.releaseAllVideos();
        Glide.with(mActivity).pauseRequests();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (spUtils.getInt(CONFIG_RANDOM_HEADER, 0) == 0
                && timer != null
                && timerTask != null)
            cancelTimer();
//        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            Glide.get(mActivity).clearMemory();
            for (BaseRvFragment fragment : fragments) {
                fragment.startLowMemory();
            }
        }
        Glide.get(mActivity).trimMemory(level);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        //内存低时,清理缓存
        Glide.get(mActivity).clearMemory();
    }

    @SuppressLint("NewApi")
    public void checkPushCompat() {
        if (!isPush) return;
        int sec = 0;
        switch (AppConfig.pushTime) {
            case 0:
                sec = 20 * 60;
                break;
            case 1:
                sec = 42 * 60;
                break;
            case 2:
                sec = 160 * 60;
                break;
            case 3:
                sec = 300 * 60;
                break;
        }
        if (sec != 0) {
            PollingUtils.startPollingService(this, sec, PushIntentService.class, PUSH_ACTIVE);
        }
    }
}
