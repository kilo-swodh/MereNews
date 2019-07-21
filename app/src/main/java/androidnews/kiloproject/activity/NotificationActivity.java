package androidnews.kiloproject.activity;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.work.WorkManager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SPUtils;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.xw.repo.BubbleSeekBar;

import androidnews.kiloproject.R;
import androidnews.kiloproject.system.AppConfig;
import androidnews.kiloproject.system.base.BaseActivity;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static androidnews.kiloproject.system.AppConfig.CONFIG_PUSH;
import static androidnews.kiloproject.system.AppConfig.CONFIG_PUSH_SOUND;
import static androidnews.kiloproject.system.AppConfig.CONFIG_PUSH_TIME;
import static androidnews.kiloproject.system.AppConfig.PUSH_WORK_NAME;
import static androidnews.kiloproject.system.AppConfig.pushTime;

public class NotificationActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private TextView mPushTv;
    private TextView mPushDetailTv;
    private Switch mPushSw;
    private TextView mPushSoundTv;
    private TextView mPushSoundDetailTv;
    private Switch mPushSoundSw;
    private TextView mPushTimeTv;
    private BubbleSeekBar mPushTimeSb;

    SPUtils spUtils;

    String[] timeItems;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mPushTv = (TextView) findViewById(R.id.tv_push);
        mPushDetailTv = (TextView) findViewById(R.id.tv_push_detail);
        mPushSw = (Switch) findViewById(R.id.sw_push);
        mPushSoundTv = (TextView) findViewById(R.id.tv_push_sound);
        mPushSoundDetailTv = (TextView) findViewById(R.id.tv_push_sound_detail);
        mPushSoundSw = (Switch) findViewById(R.id.sw_push_sound);
        mPushTimeTv = (TextView) findViewById(R.id.tv_push_time);
        mPushTimeSb = (BubbleSeekBar) findViewById(R.id.sb_push_time);

        initToolbar(toolbar, true);
        getSupportActionBar().setTitle(R.string.notification);
        initBar(R.color.main_background, true);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_save:
                        item.setEnabled(false);
                        saveSetting();
                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected void initSlowly() {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                spUtils = SPUtils.getInstance();
                timeItems = getResources().getStringArray(R.array.push_time_setting);
                mPushTimeSb.setProgress(AppConfig.pushTime * 40);

                e.onNext(true);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        mPushSw.setChecked(AppConfig.isPush);
                        mPushSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                AppConfig.isPush = isChecked;
                                spUtils.put(CONFIG_PUSH, AppConfig.isPush);
                            }
                        });

                        mPushSoundSw.setChecked(AppConfig.isPushSound);
                        mPushSoundSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                AppConfig.isPushSound = isChecked;
                                spUtils.put(CONFIG_PUSH_SOUND, AppConfig.isPushSound);
                            }
                        });

                        mPushTimeSb.setCustomSectionTextArray(new BubbleSeekBar.CustomSectionTextArray() {
                            @NonNull
                            @Override
                            public SparseArray<String> onCustomize(int sectionCount, @NonNull SparseArray<String> array) {
                                array.clear();
                                for (int i = 0; i < timeItems.length; i++) {
                                    array.put(i, timeItems[i]);
                                }
                                return array;
                            }
                        });
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_push:
            case R.id.tv_push_detail:
                if (AppConfig.isPush) {
                    mPushSw.setChecked(false);
                    AppConfig.isPush = false;
                } else {
                    mPushSw.setChecked(true);
                    AppConfig.isPush = true;
                }
                spUtils.put(CONFIG_PUSH, AppConfig.isPush);
                break;
            case R.id.tv_push_sound:
            case R.id.tv_push_sound_detail:
                if (AppConfig.isPushSound) {
                    mPushSoundSw.setChecked(false);
                    AppConfig.isPushSound = false;
                } else {
                    mPushSoundSw.setChecked(true);
                    AppConfig.isPushSound = true;
                }
                spUtils.put(CONFIG_PUSH_SOUND, AppConfig.isPushSound);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (NetworkUtils.isConnected())
            getMenuInflater().inflate(R.menu.notification_items, menu);//加载menu布局
        return true;
    }

    private void saveSetting() {
        AppConfig.pushTime = mPushTimeSb.getProgress() / 40;
        SPUtils.getInstance().put(CONFIG_PUSH_TIME, pushTime);
        if (AppConfig.isPush) {
            restartWithAnime(R.id.root_view, R.id.content);
        } else
            WorkManager.getInstance().cancelUniqueWork(PUSH_WORK_NAME);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN && AppConfig.isPush) {
                new MaterialStyledDialog.Builder(mActivity)
                        .setHeaderDrawable(R.drawable.ic_save_white)
                        .setHeaderScaleType(ImageView.ScaleType.CENTER)
                        .setTitle(R.string.message_save_title)
                        .setDescription(getString(R.string.message_save_meesage))
                        .setHeaderColor(R.color.colorAccent)
                        .setPositiveText(android.R.string.ok)
                        .setNegativeText(android.R.string.no)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                saveSetting();
                                dialog.dismiss();
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                AppConfig.pushTime = mPushTimeSb.getProgress() / 25;
                                SPUtils.getInstance().put(CONFIG_PUSH_TIME, pushTime);
                                dialog.dismiss();
                                finish();
                            }
                        })
                        .show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
