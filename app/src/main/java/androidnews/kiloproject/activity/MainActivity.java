package androidnews.kiloproject.activity;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.blankj.utilcode.util.CacheDiskUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.bumptech.glide.Glide;
import com.github.florent37.materialviewpager.MaterialViewPager;
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
import androidnews.kiloproject.bean.data.TypeArrayBean;
import androidnews.kiloproject.bean.net.PhotoCenterData;
import androidnews.kiloproject.event.RestartEvent;
import androidnews.kiloproject.fragment.MainRvFragment;
import androidnews.kiloproject.receiver.MessageEvent;
import androidnews.kiloproject.system.base.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

import static androidnews.kiloproject.activity.ChannelActivity.SELECT_RESULT;
import static androidnews.kiloproject.bean.data.CacheNews.CACHE_COLLECTION;
import static androidnews.kiloproject.bean.data.CacheNews.CACHE_HISTORY;
import static androidnews.kiloproject.system.AppConfig.CONFIG_AUTO_CLEAR;
import static androidnews.kiloproject.system.AppConfig.CONFIG_TYPE_ARRAY;

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
    TypeArrayBean typeArrayBean;

    public static final int DEFAULT_PAGE = 4;

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
        if (SPUtils.getInstance().getBoolean(CONFIG_AUTO_CLEAR)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Glide.get(mActivity).clearDiskCache();
                }
            }).start();
        }

        typeArrayBean = CacheDiskUtils.getInstance().getParcelable(CONFIG_TYPE_ARRAY, TypeArrayBean.CREATOR);
        if (typeArrayBean == null) {
            typeArrayBean = new TypeArrayBean();
            typeArrayBean.setTypeArray(new ArrayList<>());
            for (int i = 0; i < DEFAULT_PAGE; i++) {
                typeArrayBean.getTypeArray().add(i);
            }
            CacheDiskUtils.getInstance().put(CONFIG_TYPE_ARRAY, typeArrayBean);
        }

        navigation.setNavigationItemSelectedListener(this);

        ColorStateList csl = getBaseContext().getResources().getColorStateList(R.color.navigation_menu_item_color);
        navigation.setItemTextColor(csl);

        mViewPager.getViewPager().setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
//                switch (position % 4) {
//                    case 0:
//                        return MainRvFragment.newInstance();
//                    case 1:
//                        return TechRvFragment.newInstance();
//                    case 2:
//                        return EntertainmentRvFragment.newInstance();
//                    default:
                return MainRvFragment.newInstance(typeArrayBean.getTypeArray().get(position));
//                }
            }

            @Override
            public int getCount() {
                return typeArrayBean.getTypeArray().size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                String[] tags = getResources().getStringArray(R.array.address_tag);
                return tags[typeArrayBean.getTypeArray().get(position)];
            }
        });

        mViewPager.getViewPager().setOffscreenPageLimit(mViewPager.getViewPager().getAdapter().getCount());
        mViewPager.getPagerTitleStrip().setViewPager(mViewPager.getViewPager());

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

        String dataUrl = "http://pic.news.163.com/photocenter/api/list/0001/00AN0001,00AO0001,00AP0001/0/10/cacheMoreData.json";
        EasyHttp.get(dataUrl)
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
                    public void onSuccess(String s) {
                        String temp = s.replace(")", "}");
                        String response = temp.replace("cacheMoreData(", "{\"cacheMoreData\":");
                        if (!TextUtils.isEmpty(response) || TextUtils.equals(response, "{}")) {
                            try {
                                photoData = gson.fromJson(response, PhotoCenterData.class);
                            }catch (Exception e){
                                e.printStackTrace();
                                SnackbarUtils.with(mViewPager).setMessage(getString(R.string.server_fail)).showError();
                            }
                            startBgAnimate();
                        }
                    }
                });
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
            case R.id.nav_setting:
                intent = new Intent(mActivity, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_about:
                intent = new Intent(mActivity, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_exit:
                System.exit(0);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECT_RESULT:
                if (resultCode == RESULT_OK) {
                    initSlowly();
                }
                break;
        }
    }

    Timer timer;
    TimerTask timerTask;

    private void startBgAnimate() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mViewPager != null) {
                            if (bgPosition == photoData.getCacheMoreData().size()) {
                                bgPosition = 0;
                            }
                            mViewPager.setImageUrl(photoData.getCacheMoreData().get(bgPosition).getCover(), 300);
                            bgPosition++;
                        }
                    }
                });
            }
        };
        timer.schedule(timerTask, 0, 10 * 1000);
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
        Glide.with(mActivity).pauseRequests();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTimer();
        mViewPager = null;
        EventBus.getDefault().unregister(this);
    }
}
