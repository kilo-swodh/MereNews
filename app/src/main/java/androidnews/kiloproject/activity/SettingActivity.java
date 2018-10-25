package androidnews.kiloproject.activity;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.Group;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.bumptech.glide.Glide;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import androidnews.kiloproject.R;
import androidnews.kiloproject.system.base.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static androidnews.kiloproject.bean.data.CacheNews.CACHE_COLLECTION;
import static androidnews.kiloproject.bean.data.CacheNews.CACHE_HISTORY;
import static androidnews.kiloproject.system.AppConfig.CHECK_UPADTE_ADDRESS;
import static androidnews.kiloproject.system.AppConfig.CONFIG_AUTO_LOADMORE;
import static androidnews.kiloproject.system.AppConfig.CONFIG_AUTO_REFRESH;
import static androidnews.kiloproject.system.AppConfig.CONFIG_BACK_EXIT;
import static androidnews.kiloproject.system.AppConfig.CONFIG_LANGUAGE;
import static androidnews.kiloproject.system.AppConfig.CONFIG_RANDOM_HEADER;
import static androidnews.kiloproject.system.AppConfig.CONFIG_SWIPE_BACK;
import static androidnews.kiloproject.system.AppConfig.isSwipeBack;
import static com.blankj.utilcode.util.AppUtils.relaunchApp;

public class SettingActivity extends BaseActivity {

    public static final int SETTING_RESULT = 998;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.group_language)
    Group groupLanguage;
    @BindView(R.id.tv_language)
    TextView tvLanguage;
    @BindView(R.id.tv_language_detail)
    TextView tvLanguageDetail;
    @BindView(R.id.view_language)
    View viewLanguage;
    @BindView(R.id.group_clear_cache)
    Group groupClearCache;
    @BindView(R.id.tv_clear_cache)
    TextView tvClearCache;
    @BindView(R.id.tv_clear_cache_detail)
    TextView tvClearCacheDetail;
    @BindView(R.id.view_auto_refresh)
    View viewAutoRefresh;
    @BindView(R.id.group_auto_refresh)
    Group groupAutoRefresh;
    @BindView(R.id.tv_auto_refresh)
    TextView tvAutoRefresh;
    @BindView(R.id.tv_auto_refresh_detail)
    TextView tvAutoRefreshDetail;
    @BindView(R.id.sw_auto_refresh)
    Switch swAutoRefresh;
    @BindView(R.id.view_auto_loadmore)
    View viewAutoLoadmore;
    @BindView(R.id.group_auto_loadmore)
    Group groupAutoLoadmore;
    @BindView(R.id.tv_auto_loadmore)
    TextView tvAutoLoadmore;
    @BindView(R.id.tv_auto_loadmore_detail)
    TextView tvAutoLoadmoreDetail;
    @BindView(R.id.sw_auto_loadmore)
    Switch swAutoLoadmore;
    @BindView(R.id.view_back_exit)
    View viewBackExit;
    @BindView(R.id.group_back_exit)
    Group groupBackExit;
    @BindView(R.id.tv_back_exit)
    TextView tvBackExit;
    @BindView(R.id.tv_back_exit_detail)
    TextView tvBackExitDetail;
    @BindView(R.id.sw_back_exit)
    Switch swBackExit;
    @BindView(R.id.view_swipe_back)
    View viewSwipeBack;
    @BindView(R.id.group_swipe_back)
    Group groupSwipeBack;
    @BindView(R.id.tv_swipe_back)
    TextView tvSwipeBack;
    @BindView(R.id.tv_swipe_back_detail)
    TextView tvSwipeBackDetail;
    @BindView(R.id.sw_swipe_back)
    Switch swSwipeBack;
    @BindView(R.id.card_view_language)
    CardView cardViewLanguage;
    @BindView(R.id.textauto_refresh)
    TextView textautoRefresh;
    @BindView(R.id.group_check_update)
    Group checkUpdate;
    @BindView(R.id.tv_check_update)
    TextView tvCheckUpdate;
    @BindView(R.id.tv_check_update_detail)
    TextView tvCheckUpdateDetail;
    @BindView(R.id.view_random_header)
    View viewRandomHeader;
    @BindView(R.id.group_random_header)
    Group groupRandomHeader;
    @BindView(R.id.tv_random_header)
    TextView tvRandomHeader;
    @BindView(R.id.tv_random_header_detail)
    TextView tvRandomHeaderDetail;
    @BindView(R.id.sw_random_header)
    Switch swRandomHeader;
    @BindView(R.id.group_join_us)
    Group groupJoinUs;
    @BindView(R.id.tv_join_us)
    TextView tvJoinUs;
    @BindView(R.id.tv_join_us_detail)
    TextView tvJoinUsDetail;
    @BindView(R.id.view_check_update)
    View viewCheckUpdate;

    private int currentLanguage = 0;
    private boolean isAutoRefresh = false;
    private boolean isBackExit = false;
    private boolean isRandomHeader = false;
    private boolean isAutoLoadMore = false;
    SPUtils spUtils;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        initToolbar(toolbar, true);
        getSupportActionBar().setTitle(R.string.setting);
        spUtils = SPUtils.getInstance();
        initStateBar(R.color.main_background, true);
    }

    @Override
    protected void initSlowly() {
        currentLanguage = spUtils.getInt(CONFIG_LANGUAGE, 0);

        switch (currentLanguage) {
            case 0:
                tvLanguageDetail.setText(R.string.auto);
                break;
            case 1:
                tvLanguageDetail.setText(R.string.english);
                break;
            case 2:
                tvLanguageDetail.setText(R.string.chinese);
                break;
        }

        isAutoRefresh = spUtils.getBoolean(CONFIG_AUTO_REFRESH);
        swAutoRefresh.setChecked(isAutoRefresh);
        swAutoRefresh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isAutoRefresh = isChecked;
                spUtils.put(CONFIG_AUTO_REFRESH, isAutoRefresh);
            }
        });

        isAutoLoadMore = spUtils.getBoolean(CONFIG_AUTO_LOADMORE);
        swAutoLoadmore.setChecked(isAutoLoadMore);
        swAutoLoadmore.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isAutoLoadMore = isChecked;
                spUtils.put(CONFIG_AUTO_LOADMORE, isAutoLoadMore);
                setResult(RESULT_OK);
                SnackbarUtils.with(toolbar).setMessage(getString(R.string.start_after_restart_list)).showSuccess();
            }
        });

        isBackExit = spUtils.getBoolean(CONFIG_BACK_EXIT);
        swBackExit.setChecked(isBackExit);
        swBackExit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isBackExit = isChecked;
                spUtils.put(CONFIG_BACK_EXIT, isBackExit);
            }
        });

        isRandomHeader = spUtils.getBoolean(CONFIG_RANDOM_HEADER);
        swRandomHeader.setChecked(isRandomHeader);
        swRandomHeader.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isRandomHeader = isChecked;
                spUtils.put(CONFIG_RANDOM_HEADER, isRandomHeader);
                SnackbarUtils.with(toolbar).setMessage(getString(R.string.start_after_restart_app)).showSuccess();
            }
        });

        isSwipeBack = spUtils.getBoolean(CONFIG_SWIPE_BACK);
        swSwipeBack.setChecked(isSwipeBack);
        swSwipeBack.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isSwipeBack = isChecked;
                spUtils.put(CONFIG_SWIPE_BACK, isSwipeBack);
                SnackbarUtils.with(toolbar).setMessage(getString(R.string.start_after_exit)).showSuccess();
            }
        });

        tvCheckUpdateDetail.setText(getString(R.string.check_update_detail) + AppUtils.getAppVersionName());
    }


    @OnClick({R.id.tv_language, R.id.tv_language_detail,
            R.id.tv_back_exit, R.id.tv_back_exit_detail,
            R.id.tv_random_header, R.id.tv_random_header_detail,
            R.id.tv_auto_refresh, R.id.tv_auto_refresh_detail,
            R.id.tv_auto_loadmore, R.id.tv_auto_loadmore_detail,
            R.id.tv_clear_cache, R.id.tv_clear_cache_detail,
            R.id.tv_swipe_back, R.id.tv_swipe_back_detail,
            R.id.tv_check_update, R.id.tv_check_update_detail,
            R.id.tv_join_us, R.id.tv_join_us_detail,
    })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_language:
            case R.id.tv_language_detail:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.language)
                        .setCancelable(true)
                        .setSingleChoiceItems(getResources().getStringArray(R.array.language),
                                currentLanguage, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        currentLanguage = which;
                                    }
                                })
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                spUtils.put(CONFIG_LANGUAGE, currentLanguage);
                                switch (currentLanguage) {
                                    case 0:
                                        tvLanguageDetail.setText(R.string.auto);
                                        break;
                                    case 1:
                                        tvLanguageDetail.setText(R.string.english);
                                        break;
                                    case 2:
                                        tvLanguageDetail.setText(R.string.chinese);
                                        break;
                                }
                                restartWithAnime();
                            }
                        })
                        .create().show();
                break;

            case R.id.tv_clear_cache:
            case R.id.tv_clear_cache_detail:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SPUtils.getInstance().put(CACHE_HISTORY + "", "");
                        SPUtils.getInstance().put(CACHE_COLLECTION + "", "");
                        String[] address = getResources().getStringArray(R.array.address);
                        Glide.get(mActivity).clearDiskCache();
                        for (String typeStr : address) {
                            SPUtils.getInstance().put(typeStr + "_data", "");
                        }
                    }
                }).start();
                SnackbarUtils.with(toolbar).setMessage(getString(R.string.successfully)).showSuccess();
                break;

            case R.id.tv_auto_refresh:
            case R.id.tv_auto_refresh_detail:
                if (isAutoRefresh) {
                    swAutoRefresh.setChecked(false);
                    isAutoRefresh = false;
                } else {
                    swAutoRefresh.setChecked(true);
                    isAutoRefresh = true;
                }
                spUtils.put(CONFIG_AUTO_REFRESH, isAutoRefresh);
                break;

            case R.id.tv_auto_loadmore:
            case R.id.tv_auto_loadmore_detail:
                if (isAutoLoadMore) {
                    swAutoLoadmore.setChecked(false);
                    isAutoLoadMore = false;
                } else {
                    swAutoLoadmore.setChecked(true);
                    isAutoLoadMore = true;
                }
                spUtils.put(CONFIG_AUTO_LOADMORE, isAutoLoadMore);
                setResult(RESULT_OK);
                SnackbarUtils.with(toolbar).setMessage(getString(R.string.start_after_restart_list)).showSuccess();
                break;

            case R.id.tv_back_exit:
            case R.id.tv_back_exit_detail:
                if (isBackExit) {
                    swBackExit.setChecked(false);
                    isBackExit = false;
                } else {
                    swBackExit.setChecked(true);
                    isBackExit = true;
                }
                spUtils.put(CONFIG_BACK_EXIT, isBackExit);
                break;

            case R.id.tv_random_header:
            case R.id.tv_random_header_detail:
                if (isRandomHeader) {
                    swRandomHeader.setChecked(false);
                    isRandomHeader = false;
                } else {
                    swRandomHeader.setChecked(true);
                    isRandomHeader = true;
                }
                spUtils.put(CONFIG_RANDOM_HEADER, isRandomHeader);
                SnackbarUtils.with(toolbar).setMessage(getString(R.string.start_after_restart_app)).showSuccess();
                break;

            case R.id.tv_swipe_back:
            case R.id.tv_swipe_back_detail:
                if (isSwipeBack) {
                    swSwipeBack.setChecked(false);
                    isSwipeBack = false;
                } else {
                    swSwipeBack.setChecked(true);
                    isSwipeBack = true;
                }
                spUtils.put(CONFIG_SWIPE_BACK, isSwipeBack);
                SnackbarUtils.with(toolbar).setMessage(getString(R.string.start_after_exit)).showSuccess();
                break;

            case R.id.tv_check_update:
            case R.id.tv_check_update_detail:
                EasyHttp.get(CHECK_UPADTE_ADDRESS)
                        .readTimeOut(30 * 1000)//局部定义读超时
                        .writeTimeOut(30 * 1000)
                        .connectTimeout(30 * 1000)
                        .timeStamp(true)
                        .execute(new SimpleCallBack<String>() {
                            @Override
                            public void onError(ApiException e) {
                                SnackbarUtils.with(toolbar)
                                        .setMessage(getString(R.string.load_fail) + e.getMessage())
                                        .showError();
                            }

                            @Override
                            public void onSuccess(String response) {
                                if (response != null) {
                                    int newVersionCode;
                                    try {
                                        newVersionCode = Integer.parseInt(response.trim());
                                    } catch (Exception e) {
                                        SnackbarUtils.with(toolbar)
                                                .setMessage(getString(R.string.load_fail) + e.getMessage())
                                                .showError();
                                    }
                                    if (AppUtils.getAppVersionCode() < Integer.parseInt(response.trim())) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                                        builder.setTitle(R.string.update_title)
                                                .setMessage(R.string.update_message)
                                                .setCancelable(true)
                                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Uri uri = Uri.parse(getString(R.string.update_address));
                                                        startActivity(new Intent(Intent.ACTION_VIEW, uri));
                                                    }
                                                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).show();
                                    } else {
                                        SnackbarUtils.with(toolbar)
                                                .setMessage(getString(R.string.update_title_no))
                                                .show();
                                    }
                                } else {
                                    SnackbarUtils.with(toolbar)
                                            .setMessage(getString(R.string.load_fail))
                                            .showError();
                                }
                            }
                        });
                break;
            case R.id.tv_join_us:
            case R.id.tv_join_us_detail:
                Intent intent = new Intent();
                intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" +
                "dfXHsJjgt5dX_ma5KylHFi60LZmFsuLv"));
                // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    // 未安装手Q或安装的版本不支持
                    SnackbarUtils.with(toolbar)
                            .setMessage(getString(R.string.join_us_error))
                            .showError();
                }
                break;
        }
    }

    private void restartWithAnime() {
        animateRevealShow(toolbar, false, new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                swAutoRefresh.setEnabled(false);
                groupAutoRefresh.setClickable(false);
                swAutoLoadmore.setEnabled(false);
                groupAutoLoadmore.setClickable(false);
                swBackExit.setEnabled(false);
                groupRandomHeader.setClickable(false);
                swRandomHeader.setEnabled(false);
                groupBackExit.setClickable(false);
                swSwipeBack.setEnabled(false);
                groupSwipeBack.setClickable(false);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                relaunchApp();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                swAutoRefresh.setEnabled(true);
                groupAutoRefresh.setClickable(true);
                swAutoLoadmore.setEnabled(true);
                groupAutoLoadmore.setClickable(true);
                swBackExit.setEnabled(true);
                groupRandomHeader.setClickable(false);
                swRandomHeader.setEnabled(false);
                groupBackExit.setClickable(true);
                swSwipeBack.setEnabled(true);
                groupSwipeBack.setClickable(true);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void animateRevealShow(final View view, final boolean isReverse, @Nullable Animator.AnimatorListener listener) {
        if (!isLollipop())
            return;
        int cx = ScreenUtils.getScreenWidth() / 2;
        int cy = ScreenUtils.getScreenHeight() / 2;
        int finalRadius = Math.min(view.getWidth(), view.getHeight());
        Animator anim;
        if (isReverse) {
            //关闭
            anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, finalRadius, 0);
            anim.setInterpolator(new DecelerateInterpolator());
            anim.setDuration(450);
        } else {
            //开屏
            anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
            anim.setDuration(450);
        }


        anim.start();
        if (listener != null)
            anim.addListener(listener);
    }
}
