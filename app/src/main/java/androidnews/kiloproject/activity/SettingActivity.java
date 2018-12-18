package androidnews.kiloproject.activity;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Group;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.bumptech.glide.Glide;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.litepal.LitePal;

import androidnews.kiloproject.R;
import androidnews.kiloproject.system.AppConfig;
import androidnews.kiloproject.system.base.BaseActivity;
import androidnews.kiloproject.util.AlipayUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static androidnews.kiloproject.system.AppConfig.BUGLY_KEY;
import static androidnews.kiloproject.system.AppConfig.CHECK_UPADTE_ADDRESS;
import static androidnews.kiloproject.system.AppConfig.CONFIG_AUTO_LOADMORE;
import static androidnews.kiloproject.system.AppConfig.CONFIG_AUTO_REFRESH;
import static androidnews.kiloproject.system.AppConfig.CONFIG_BACK_EXIT;
import static androidnews.kiloproject.system.AppConfig.CONFIG_HEADER_COLOR;
import static androidnews.kiloproject.system.AppConfig.CONFIG_LIST_TYPE;
import static androidnews.kiloproject.system.AppConfig.CONFIG_LANGUAGE;
import static androidnews.kiloproject.system.AppConfig.CONFIG_RANDOM_HEADER;
import static androidnews.kiloproject.system.AppConfig.CONFIG_SWIPE_BACK;
import static androidnews.kiloproject.system.AppConfig.CONFIG_TEXT_SIZE;
import static androidnews.kiloproject.system.AppConfig.DOWNLOAD_ADDRESS;
import static androidnews.kiloproject.system.AppConfig.LIST_TYPE_SINGLE;
import static androidnews.kiloproject.system.AppConfig.isSwipeBack;
import static com.blankj.utilcode.util.AppUtils.relaunchApp;

public class SettingActivity extends BaseActivity {

    Toolbar toolbar;
    TextView textView;
    Group groupLanguage;
    TextView tvLanguage;
    TextView tvLanguageDetail;
    Group groupClearCache;
    TextView tvClearCache;
    TextView tvClearCacheDetail;
    Group groupTextSize;
    TextView tvTextSize;
    TextView tvTextSizeDetail;
    Group groupAutoRefresh;
    TextView tvAutoRefresh;
    TextView tvAutoRefreshDetail;
    Switch swAutoRefresh;
    Group groupAutoLoadmore;
    TextView tvAutoLoadmore;
    TextView tvAutoLoadmoreDetail;
    Switch swAutoLoadmore;
    Group groupBackExit;
    TextView tvBackExit;
    TextView tvBackExitDetail;
    Switch swBackExit;
    Group groupSwipeBack;
    TextView tvSwipeBack;
    TextView tvSwipeBackDetail;
    Switch swSwipeBack;
    CardView cardViewLanguage;
    TextView textautoRefresh;
    Group checkUpdate;
    TextView tvCheckUpdate;
    TextView tvCheckUpdateDetail;
    Group groupRandomHeader;
    TextView tvRandomHeader;
    TextView tvRandomHeaderDetail;
    Group groupListType;
    TextView tvListType;
    TextView tvListTypeDetail;

    private int currentLanguage;
    private int currentRandomHeader;
    private boolean isAutoRefresh = false;
    private boolean isBackExit = false;
    private boolean isAutoLoadMore = false;
    SPUtils spUtils;

    String[] languageItems;
    String[] headerItems;
    String[] listItems;
    String[] sizeItems;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        textView = (TextView) findViewById(R.id.textView);
        groupLanguage = (Group) findViewById(R.id.group_language);
        tvLanguage = (TextView) findViewById(R.id.tv_language);
        tvLanguageDetail = (TextView) findViewById(R.id.tv_language_detail);
        groupClearCache = (Group) findViewById(R.id.group_clear_cache);
        tvClearCache = (TextView) findViewById(R.id.tv_clear_cache);
        tvClearCacheDetail = (TextView) findViewById(R.id.tv_clear_cache_detail);
        groupTextSize = (Group) findViewById(R.id.group_text_size);
        tvTextSize = (TextView) findViewById(R.id.tv_text_size);
        tvTextSizeDetail = (TextView) findViewById(R.id.tv_text_size_detail);
        groupAutoRefresh = (Group) findViewById(R.id.group_auto_refresh);
        tvAutoRefresh = (TextView) findViewById(R.id.tv_auto_refresh);
        tvAutoRefreshDetail = (TextView) findViewById(R.id.tv_auto_refresh_detail);
        swAutoRefresh = (Switch) findViewById(R.id.sw_auto_refresh);
        groupAutoLoadmore = (Group) findViewById(R.id.group_auto_loadmore);
        tvAutoLoadmore = (TextView) findViewById(R.id.tv_auto_loadmore);
        tvAutoLoadmoreDetail = (TextView) findViewById(R.id.tv_auto_loadmore_detail);
        swAutoLoadmore = (Switch) findViewById(R.id.sw_auto_loadmore);
        groupBackExit = (Group) findViewById(R.id.group_back_exit);
        tvBackExit = (TextView) findViewById(R.id.tv_back_exit);
        tvBackExitDetail = (TextView) findViewById(R.id.tv_back_exit_detail);
        swBackExit = (Switch) findViewById(R.id.sw_back_exit);
        groupSwipeBack = (Group) findViewById(R.id.group_swipe_back);
        tvSwipeBack = (TextView) findViewById(R.id.tv_swipe_back);
        tvSwipeBackDetail = (TextView) findViewById(R.id.tv_swipe_back_detail);
        swSwipeBack = (Switch) findViewById(R.id.sw_swipe_back);
        cardViewLanguage = (CardView) findViewById(R.id.card_view_language);
        textautoRefresh = (TextView) findViewById(R.id.textauto_refresh);
        checkUpdate = (Group) findViewById(R.id.group_check_update);
        tvCheckUpdate = (TextView) findViewById(R.id.tv_check_update);
        tvCheckUpdateDetail = (TextView) findViewById(R.id.tv_check_update_detail);
        groupRandomHeader = (Group) findViewById(R.id.group_random_header);
        tvRandomHeader = (TextView) findViewById(R.id.tv_random_header);
        tvRandomHeaderDetail = (TextView) findViewById(R.id.tv_random_header_detail);
        groupListType = (Group) findViewById(R.id.group_list_type);
        tvListType = (TextView) findViewById(R.id.tv_list_type);
        tvListTypeDetail = (TextView) findViewById(R.id.tv_list_type_detail);

        initToolbar(toolbar, true);
        getSupportActionBar().setTitle(R.string.setting);
        initStatusBar(R.color.main_background, true);
    }

    @Override
    protected void initSlowly() {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                spUtils = SPUtils.getInstance();

                headerItems = getResources().getStringArray(R.array.header_setting);
                listItems = getResources().getStringArray(R.array.list_type);
                languageItems = getResources().getStringArray(R.array.language);
                sizeItems = getResources().getStringArray(R.array.size);

                AppConfig.TextSize = spUtils.getInt(CONFIG_TEXT_SIZE, 1);

                currentRandomHeader = spUtils.getInt(CONFIG_RANDOM_HEADER, 0);

                currentLanguage = spUtils.getInt(CONFIG_LANGUAGE, 0);

                isAutoRefresh = spUtils.getBoolean(CONFIG_AUTO_REFRESH);

                isAutoLoadMore = spUtils.getBoolean(CONFIG_AUTO_LOADMORE);

                isBackExit = spUtils.getBoolean(CONFIG_BACK_EXIT);

                isSwipeBack = spUtils.getBoolean(CONFIG_SWIPE_BACK);

                e.onNext(true);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        tvRandomHeaderDetail.setText(headerItems[currentRandomHeader]);
                        tvListTypeDetail.setText(listItems[spUtils.getInt(CONFIG_LIST_TYPE, LIST_TYPE_SINGLE)]);
                        tvTextSizeDetail.setText(sizeItems[AppConfig.TextSize]);
                        tvLanguageDetail.setText(languageItems[currentLanguage]);

                        swAutoRefresh.setChecked(isAutoRefresh);
                        swAutoRefresh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                isAutoRefresh = isChecked;
                                spUtils.put(CONFIG_AUTO_REFRESH, isAutoRefresh);
                            }
                        });

                        swAutoLoadmore.setChecked(isAutoLoadMore);
                        swAutoLoadmore.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                isAutoLoadMore = isChecked;
                                spUtils.put(CONFIG_AUTO_LOADMORE, isAutoLoadMore);
                                setResult(RESULT_OK);
                                SnackbarUtils.with(toolbar).setMessage(getString(R.string.start_after_restart_list)).show();
                            }
                        });

                        swBackExit.setChecked(isBackExit);
                        swBackExit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                isBackExit = isChecked;
                                spUtils.put(CONFIG_BACK_EXIT, isBackExit);
                            }
                        });

                        swSwipeBack.setChecked(isSwipeBack);
                        swSwipeBack.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                isSwipeBack = isChecked;
                                spUtils.put(CONFIG_SWIPE_BACK, isSwipeBack);
                                SnackbarUtils.with(toolbar).setMessage(getString(R.string.start_after_exit)).show();
                            }
                        });

                        tvCheckUpdateDetail.setText(getString(R.string.check_update_detail) + AppUtils.getAppVersionName());
                    }
                });
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
                groupListType.setClickable(false);
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

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_language:
            case R.id.tv_language_detail:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.language)
                        .setCancelable(true)
                        .setSingleChoiceItems(languageItems,
                                currentLanguage, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        currentLanguage = which;
                                        spUtils.put(CONFIG_LANGUAGE, currentLanguage);
                                        tvLanguageDetail.setText(languageItems[currentLanguage]);
                                        restartWithAnime();
                                    }
                                })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .create().show();
                break;

            case R.id.tv_clear_cache:
            case R.id.tv_clear_cache_detail:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LitePal.deleteDatabase("setting");
                        String[] address = getResources().getStringArray(R.array.address);
                        Glide.get(mActivity).clearDiskCache();
                        for (String typeStr : address) {
                            SPUtils.getInstance().put(typeStr + "_data", "");
                        }
                    }
                }).start();
                SnackbarUtils.with(toolbar).setMessage(getString(R.string.successful)).show();
                break;

            case R.id.tv_text_size:
            case R.id.tv_text_size_detail:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.text_size)
                        .setCancelable(true)
                        .setSingleChoiceItems(getResources().getStringArray(R.array.size),
                                AppConfig.TextSize, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        AppConfig.TextSize = which;
                                        spUtils.put(CONFIG_TEXT_SIZE, which);
                                        tvTextSizeDetail.setText(sizeItems[which]);
                                        SnackbarUtils.with(toolbar)
                                                .setMessage(getResources().getString(R.string.successful))
                                                .show();
                                    }
                                })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .create().show();
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
                SnackbarUtils.with(toolbar).setMessage(getString(R.string.start_after_restart_list)).show();
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
                new AlertDialog.Builder(mActivity)
                        .setTitle(R.string.random_header_server)
                        .setCancelable(true)
                        .setSingleChoiceItems(
                                headerItems, currentRandomHeader, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        spUtils.put(CONFIG_RANDOM_HEADER, which);
                                        tvRandomHeaderDetail.setText(headerItems[which]);
                                        if (which > 3)
                                            showColorPicker();
                                        else
                                            SnackbarUtils.with(toolbar)
                                                    .setMessage(getString(R.string.start_after_restart_app))
                                                    .show();
                                        dialog.dismiss();
                                    }
                                }
                        ).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
                break;

            case R.id.tv_list_type:
            case R.id.tv_list_type_detail:
                new AlertDialog.Builder(mActivity)
                        .setTitle(R.string.list_type)
                        .setCancelable(true)
                        .setSingleChoiceItems(listItems, AppConfig.type_list, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        spUtils.put(CONFIG_LIST_TYPE, which);
                                        tvListTypeDetail.setText(listItems[which]);
                                        AppConfig.type_list = which;
                                        SnackbarUtils.with(toolbar)
                                                .setMessage(getString(R.string.start_after_restart_app))
                                                .show();
                                        dialog.dismiss();
                                    }
                                }
                        ).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
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
                SnackbarUtils.with(toolbar).setMessage(getString(R.string.start_after_exit)).show();
                break;

            case R.id.tv_check_update:
            case R.id.tv_check_update_detail:
                SnackbarUtils.with(toolbar).setMessage(getResources()
                        .getString(R.string.loading)).show();
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
                                        if (AppUtils.getAppVersionCode() < newVersionCode) {
                                            new MaterialStyledDialog.Builder(mActivity)
                                                    .setHeaderDrawable(R.drawable.ic_warning)
                                                    .setHeaderScaleType(ImageView.ScaleType.CENTER)
                                                    .setTitle(getResources().getString(R.string.update_title))
                                                    .setDescription(getResources().getString(R.string.update_message))
                                                    .setHeaderColor(R.color.colorPrimary)
                                                    .setPositiveText(android.R.string.ok)
                                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                        @Override
                                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                            Uri uri = Uri.parse(DOWNLOAD_ADDRESS);
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
                                        } else {
                                            SnackbarUtils.with(toolbar)
                                                    .setMessage(getString(R.string.update_title_no))
                                                    .show();
                                        }
                                    } catch (Exception e) {
                                        SnackbarUtils.with(toolbar)
                                                .setMessage(getString(R.string.load_fail) + e.getMessage())
                                                .showError();
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
                intent.setData(Uri.parse(BUGLY_KEY));
                // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    // 未安装手Q或安装的版本不支持
                    SnackbarUtils.with(toolbar)
                            .setMessage(getString(R.string.join_us_error))
                            .showError();
                }
            case R.id.tv_donate:
            case R.id.tv_donate_detail:
                AlipayUtil.startAlipayClient(mActivity, AlipayUtil.PAY_ID);
                break;
        }
    }

    private void showColorPicker() {
        int color = spUtils.getInt(CONFIG_HEADER_COLOR, 9999);
        if (color == 9999)
            color = Color.parseColor("#FFA000");
        ColorPickerDialogBuilder
                .with(mActivity)
                .setTitle(R.string.color_choose)
                .initialColor(color)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
//                        ToastUtils.showShort("onColorSelected: 0x" + Integer.toHexString(selectedColor));
                    }
                })
                .setPositiveButton(android.R.string.ok, new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        spUtils.put(CONFIG_HEADER_COLOR, selectedColor);
                        SnackbarUtils.with(toolbar)
                                .setMessage(getString(R.string.start_after_restart_app))
                                .show();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }
}
