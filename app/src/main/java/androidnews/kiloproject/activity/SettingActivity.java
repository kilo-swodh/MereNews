package androidnews.kiloproject.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.Group;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.blankj.utilcode.util.CacheDiskUtils;
import com.blankj.utilcode.util.CacheDoubleUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.gyf.barlibrary.ImmersionBar;

import androidnews.kiloproject.R;
import androidnews.kiloproject.system.base.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static androidnews.kiloproject.system.AppConfig.CONFIG_AUTO_REFRESH;
import static androidnews.kiloproject.system.AppConfig.CONFIG_LANGUAGE;
import static androidnews.kiloproject.system.AppConfig.CONFIG_STATUSBAR;
import static androidnews.kiloproject.system.AppConfig.CONFIG_SWIPE_BACK;
import static androidnews.kiloproject.system.AppConfig.isSwipeBack;
import static com.blankj.utilcode.util.AppUtils.relaunchApp;

public class SettingActivity extends BaseActivity {

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
    @BindView(R.id.view1)
    View view1;
    @BindView(R.id.group_clear_cache)
    Group groupClearCache;
    @BindView(R.id.tv_clear_cache)
    TextView tvClearCache;
    @BindView(R.id.tv_clear_cache_detail)
    TextView tvClearCacheDetail;
    @BindView(R.id.view2)
    View view2;
    @BindView(R.id.group_auto_refresh)
    Group groupAutoRefresh;
    @BindView(R.id.tv_auto_refresh)
    TextView tvAutoRefresh;
    @BindView(R.id.tv_auto_refresh_detail)
    TextView tvAutoRefreshDetail;
    @BindView(R.id.cb_auto_refresh)
    CheckBox cbAutoRefresh;
    @BindView(R.id.view3)
    View view3;
    @BindView(R.id.group_md_statusbar)
    Group groupMdStatusbar;
    @BindView(R.id.tv_md_statusbar)
    TextView tvMdStatusbar;
    @BindView(R.id.tv_md_statusbar_detail)
    TextView tvMdStatusbarDetail;
    @BindView(R.id.cb_md_statusbar)
    CheckBox cbMdStatusbar;
    @BindView(R.id.card_view1)
    CardView cardView1;
    @BindView(R.id.textView2)
    TextView textView2;
    @BindView(R.id.open_source)
    Group openSource;
    @BindView(R.id.tv_open_source)
    TextView tvOpenSource;
    @BindView(R.id.tv_open_source_detail)
    TextView tvOpenSourceDetail;
    @BindView(R.id.view4)
    View view4;
    @BindView(R.id.group_swipe_back)
    Group groupSwipeBack;
    @BindView(R.id.tv_swipe_back)
    TextView tvSwipeBack;
    @BindView(R.id.tv_swipe_back_detail)
    TextView tvSwipeBackDetail;
    @BindView(R.id.cb_swipe_back)
    CheckBox cbSwipeBack;

    SPUtils spUtils;
    private int currentLanguage = 0;
    private boolean isAutoRefresh = false;
    private boolean isStatusBar = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        initToolbar(toolbar, true);
        getSupportActionBar().setTitle(R.string.setting);
        spUtils = SPUtils.getInstance();
        if (SPUtils.getInstance().getBoolean(CONFIG_STATUSBAR))
            ImmersionBar.with(mActivity).fitsSystemWindows(true).statusBarDarkFont(true).init();
        else
            initStateBar(android.R.color.white, true);
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

        tvClearCacheDetail.setText(CacheDoubleUtils.getInstance().getCacheDiskSize() / 1024 + "MB");

        isAutoRefresh = spUtils.getBoolean(CONFIG_AUTO_REFRESH);
        cbAutoRefresh.setChecked(isAutoRefresh);
        cbAutoRefresh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isAutoRefresh = isChecked;
                spUtils.put(CONFIG_AUTO_REFRESH, isAutoRefresh);
            }
        });

        isStatusBar = spUtils.getBoolean(CONFIG_STATUSBAR);
        cbMdStatusbar.setChecked(isStatusBar);
        cbMdStatusbar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isStatusBar = isChecked;
                spUtils.put(CONFIG_STATUSBAR, isStatusBar);
            }
        });

        isSwipeBack = spUtils.getBoolean(CONFIG_SWIPE_BACK);
        cbSwipeBack.setChecked(isSwipeBack);
        cbSwipeBack.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isSwipeBack = isChecked;
                spUtils.put(CONFIG_SWIPE_BACK, isSwipeBack);
            }
        });
    }


    @OnClick({R.id.tv_language, R.id.tv_language_detail, R.id.tv_clear_cache, R.id.tv_clear_cache_detail,
            R.id.tv_auto_refresh, R.id.tv_auto_refresh_detail, R.id.tv_md_statusbar,
            R.id.tv_md_statusbar_detail, R.id.tv_open_source, R.id.tv_open_source_detail})
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
                                relaunchApp();
                            }
                        })
                        .create().show();
                break;

            case R.id.tv_clear_cache:
            case R.id.tv_clear_cache_detail:
                CacheDiskUtils.getInstance().clear();
                tvClearCacheDetail.setText("0B");
                SnackbarUtils.with(textView).setMessage(getString(R.string.successfully)).showSuccess();
                break;

            case R.id.tv_auto_refresh:
            case R.id.tv_auto_refresh_detail:
                if (isAutoRefresh) {
                    cbAutoRefresh.setChecked(false);
                    isAutoRefresh = false;
                } else {
                    cbAutoRefresh.setChecked(true);
                    isAutoRefresh = true;
                }
                spUtils.put(CONFIG_AUTO_REFRESH, isAutoRefresh);
                break;

            case R.id.tv_md_statusbar:
            case R.id.tv_md_statusbar_detail:
                if (isStatusBar) {
                    cbMdStatusbar.setChecked(false);
                    isStatusBar = false;
                } else {
                    cbMdStatusbar.setChecked(true);
                    isStatusBar = true;
                }
                spUtils.put(CONFIG_STATUSBAR, isStatusBar);
                break;

            case R.id.tv_swipe_back:
            case R.id.tv_swipe_back_detail:
                if (isSwipeBack) {
                    cbSwipeBack.setChecked(false);
                    isSwipeBack = false;
                } else {
                    cbSwipeBack.setChecked(true);
                    isSwipeBack = true;
                }
                spUtils.put(CONFIG_SWIPE_BACK, isSwipeBack);
                break;

            case R.id.tv_open_source:
            case R.id.tv_open_source_detail:
                Uri uri = Uri.parse(getString(R.string.github_address));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
        }
    }
}
