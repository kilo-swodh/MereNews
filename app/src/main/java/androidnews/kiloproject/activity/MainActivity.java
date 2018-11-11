package androidnews.kiloproject.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.blankj.utilcode.util.CacheDiskUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.header.HeaderDesign;
import com.gyf.barlibrary.ImmersionBar;
import com.jude.swipbackhelper.SwipeBackHelper;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import androidnews.kiloproject.R;
import androidnews.kiloproject.bean.data.IntArrayBean;
import androidnews.kiloproject.bean.net.PhotoCenterData;
import androidnews.kiloproject.fragment.GuoKrRvFragment;
import androidnews.kiloproject.fragment.VideoRvFragment;
import androidnews.kiloproject.fragment.ZhihuRvFragment;
import androidnews.kiloproject.fragment.MainRvFragment;
import androidnews.kiloproject.receiver.MessageEvent;
import androidnews.kiloproject.system.base.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jzvd.Jzvd;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static androidnews.kiloproject.bean.data.CacheNews.CACHE_COLLECTION;
import static androidnews.kiloproject.bean.data.CacheNews.CACHE_HISTORY;
import static androidnews.kiloproject.system.AppConfig.CONFIG_BACK_EXIT;
import static androidnews.kiloproject.system.AppConfig.CONFIG_NIGHT_MODE;
import static androidnews.kiloproject.system.AppConfig.CONFIG_RANDOM_HEADER;
import static androidnews.kiloproject.system.AppConfig.CONFIG_TYPE_ARRAY;
import static androidnews.kiloproject.system.AppConfig.isNightMode;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.materialViewPager)
    MaterialViewPager mViewPager;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    ActionBarDrawerToggle mDrawerToggle;
    @BindView(R.id.navigation)
    NavigationView navigation;

    PhotoCenterData photoData;
    int bgPosition = 0;
    IntArrayBean intArrayBean;

    public static final int DEFAULT_PAGE = 4;

    public static final String SAVE_MD_VIEWPAGER = "view_pager";

    public static final String NEWS_PHOTO_URL = "http://pic.news.163.com/photocenter/api/list/0001/00AN0001,00AO0001,00AP0001/0/10/cacheMoreData.json";

    public static final int TYPE_ZHIHU = 38;
    public static final int TYPE_GUOKR = 39;
    public static final int TYPE_V_HOT = 40;
    public static final int TYPE_V_ENTERTAINMENT = 41;
    public static final int TYPE_V_FUNNY = 42;
    public static final int TYPE_V_EXCELLENT = 43;

    public static final int SELECT_RESULT = 999;
    public static final int SETTING_RESULT = 998;
    public static final int BLOCK_RESULT = 997;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, 0, 0);
        drawerLayout.addDrawerListener(mDrawerToggle);
        final Toolbar toolbar = mViewPager.getToolbar();
        initToolbar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_add_lib:
                        startActivityForResult(new Intent(mActivity, ChannelActivity.class), SELECT_RESULT);
                        break;
                }
                return false;
            }
        });
        SwipeBackHelper.getCurrentPage(this).setSwipeBackEnable(false);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initSlowly() {

        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                intArrayBean = CacheDiskUtils.getInstance().getParcelable(CONFIG_TYPE_ARRAY, IntArrayBean.CREATOR);
                if (intArrayBean == null) {
                    intArrayBean = new IntArrayBean();
                    intArrayBean.setTypeArray(new ArrayList<>());
                    e.onNext(true);
                    for (int i = 0; i < DEFAULT_PAGE; i++) {
                        intArrayBean.getTypeArray().add(i);
                    }
                    CacheDiskUtils.getInstance().put(CONFIG_TYPE_ARRAY, intArrayBean);
                }else e.onNext(true);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean){
                            mViewPager.getViewPager().setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
                                @Override
                                public Fragment getItem(int position) {
                                    int type = intArrayBean.getTypeArray().get(position);
                                    if (type >= TYPE_ZHIHU)
                                        switch (type) {
                                            case TYPE_ZHIHU:
                                                return new ZhihuRvFragment();
                                            case TYPE_GUOKR:
                                                return new GuoKrRvFragment();
                                            case TYPE_V_HOT:
                                            case TYPE_V_ENTERTAINMENT:
                                            case TYPE_V_FUNNY:
                                            case TYPE_V_EXCELLENT:
                                                return VideoRvFragment.newInstance(intArrayBean.getTypeArray().get(position));
                                        }
                                    return MainRvFragment.newInstance(intArrayBean.getTypeArray().get(position));
                                }

                                @Override
                                public int getCount() {
                                    return intArrayBean.getTypeArray().size();
                                }

                                @Override
                                public CharSequence getPageTitle(int position) {
                                    String[] tags = getResources().getStringArray(R.array.address_tag);
                                    return tags[intArrayBean.getTypeArray().get(position)];
                                }
                            });

                            mViewPager.getViewPager().setOffscreenPageLimit(mViewPager.getViewPager().getAdapter().getCount());
                            mViewPager.getPagerTitleStrip().setViewPager(mViewPager.getViewPager());

                            EasyHttp.get(NEWS_PHOTO_URL)
                                    .readTimeOut(30 * 1000)//局部定义读超时
                                    .writeTimeOut(30 * 1000)
                                    .connectTimeout(30 * 1000)
                                    .timeStamp(true)
                                    .execute(new SimpleCallBack<String>() {
                                        @Override
                                        public void onError(ApiException e) {
                                            e.printStackTrace();
                                        }

                                        @Override
                                        public void onSuccess(final String s) {
                                            Observable.create(new ObservableOnSubscribe<Boolean>() {
                                                @Override
                                                public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                                                    String temp = s.replace(")", "}");
                                                    String response = temp.replace("cacheMoreData(", "{\"cacheMoreData\":");
                                                    if (!TextUtils.isEmpty(response) || TextUtils.equals(response, "{}")) {
                                                        try {
                                                            photoData = gson.fromJson(response, PhotoCenterData.class);
                                                            e.onNext(true);
                                                        } catch (Exception e1) {
                                                            e1.printStackTrace();
                                                            e.onNext(false);
                                                        }
                                                    } else e.onNext(false);
                                                    e.onComplete();
                                                }
                                            }).subscribeOn(Schedulers.computation())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(new Consumer<Boolean>() {
                                                        @Override
                                                        public void accept(Boolean aBoolean) throws Exception {
                                                            if (aBoolean)
                                                                startBgAnimate();
                                                            else
                                                                SnackbarUtils.with(mViewPager).setMessage(getString(R.string.server_fail)).showError();
                                                        }
                                                    });
                                        }
                                    });
                        }
                    }
                });

        navigation.setNavigationItemSelectedListener(this);
        ColorStateList csl = getBaseContext().getResources().getColorStateList(R.color.navigation_menu_item_color);
        navigation.setItemTextColor(csl);

//        final View logo = findViewById(R.id.logo_white);
//        if (logo != null) {
//            logo.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mViewPager.notifyHeaderChanged();
//                    Toast.makeText(getApplicationContext(), "Yes, the title is clickable", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Glide.with(mActivity).resumeRequests();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_items, menu);//加载menu布局
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case R.id.nav_his:
                intent = new Intent(mActivity, CacheActivity.class);
                intent.putExtra("type", CACHE_HISTORY);
                startActivity(intent);
                break;
            case R.id.nav_coll:
                intent = new Intent(mActivity, CacheActivity.class);
                intent.putExtra("type", CACHE_COLLECTION);
                startActivity(intent);
                break;
            case R.id.nav_block:
                intent = new Intent(mActivity, BlockActivity.class);
                startActivityForResult(intent, BLOCK_RESULT);
                break;
            case R.id.nav_setting:
                intent = new Intent(mActivity, SettingActivity.class);
                startActivityForResult(intent, SETTING_RESULT);
                break;
            case R.id.nav_about:
                intent = new Intent(mActivity, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_theme:
                if (isNightMode) {
                    isNightMode = false;
                } else {
                    isNightMode = true;
                }
                SPUtils.getInstance().put(CONFIG_NIGHT_MODE, isNightMode);
                intent = getIntent();
                finish();
                startActivity(intent);
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) ||
                super.onOptionsItemSelected(item);
    }

    private long firstTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (Jzvd.backPress()) {
                return true;
            }
            if (SPUtils.getInstance().getBoolean(CONFIG_BACK_EXIT)
                    && System.currentTimeMillis() - firstTime > 2000) {
                SnackbarUtils.with(mViewPager).setMessage(getString(R.string.click_to_exit)).show();
                firstTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECT_RESULT:
            case SETTING_RESULT:
                if (resultCode == RESULT_OK)
                    initSlowly();
                break;
            case BLOCK_RESULT:
                if (resultCode == RESULT_OK)
                    SnackbarUtils.with(mViewPager)
                            .setMessage(getResources()
                                    .getString(R.string.start_after_restart_list))
                            .show();
                break;
        }
    }

    Timer timer;
    TimerTask timerTask;

    private void startBgAnimate() {
        if (SPUtils.getInstance().getBoolean(CONFIG_RANDOM_HEADER)) {
            mViewPager.setMaterialViewPagerListener(new MaterialViewPager.Listener() {
                @Override
                public HeaderDesign getHeaderDesign(int page) {
                    int postion = page % photoData.getCacheMoreData().size();

                    return HeaderDesign.fromColorResAndUrl(
                            R.color.deepskyblue,
                            photoData.getCacheMoreData().get(postion).getCover());
                }
                //execute others actions if needed (ex : modify your header logo)
            });
            mViewPager.setImageUrl(photoData.getCacheMoreData().get(0).getCover(), 200);
        } else {
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mViewPager != null && photoData != null) {
                                if (bgPosition == photoData.getCacheMoreData().size()) {
                                    bgPosition = 0;
                                }
                                try {
                                    mViewPager.setImageUrl(photoData.getCacheMoreData().get(bgPosition).getCover(), 300);
                                    bgPosition++;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }
            };
            timer.schedule(timerTask, 0, 10 * 1000);
        }
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {/* Do something */}

    @Override
    protected void onPause() {
        super.onPause();
        Jzvd.releaseAllVideos();
        Glide.with(mActivity).pauseRequests();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!SPUtils.getInstance().getBoolean(CONFIG_RANDOM_HEADER)
                && timer != null
                && timerTask != null)
            cancelTimer();
        EventBus.getDefault().unregister(this);
    }
}
