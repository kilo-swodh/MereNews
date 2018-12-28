package androidnews.kiloproject.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.SnackbarUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import androidnews.kiloproject.R;
import androidnews.kiloproject.entity.data.BlockItem;
import androidnews.kiloproject.system.base.BaseActivity;


import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static androidnews.kiloproject.entity.data.BlockItem.TYPE_KEYWORDS;
import static androidnews.kiloproject.entity.data.BlockItem.TYPE_SOURCE;

public class BlockActivity extends BaseActivity {

    Toolbar toolbar;
    RecyclerView rvContent;
    ProgressBar progress;
    View view;
    ConstraintLayout emptyView;
    ConstraintLayout rootView;
    BlockAdapter adapter;
    List<BlockItem> blockList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        rvContent = (RecyclerView) findViewById(R.id.rv_content);
        progress = (ProgressBar) findViewById(R.id.progress);
        view = (View) findViewById(R.id.view);
        emptyView = (ConstraintLayout) findViewById(R.id.empty_view);
        rootView = (ConstraintLayout) findViewById(R.id.root_view);

        initToolbar(toolbar, true);
        getSupportActionBar().setTitle(getString(R.string.block_rule));

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_delete_all:
                        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                        builder.setTitle(R.string.delete)
                                .setMessage(R.string.delete_message)
                                .setCancelable(true)
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            blockList.clear();
                                            LitePal.deleteAll(BlockItem.class);
                                            setResult(RESULT_OK);
                                            finish();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                        break;
                    case R.id.action_list_add:
                        final EditText editText = new EditText(mActivity);
                        editText.setHint(R.string.keywords);
                        editText.setTextColor(getResources().getColor(R.color.black));
                        new MaterialStyledDialog.Builder(mActivity)
                                .setHeaderDrawable(R.drawable.ic_edit)
                                .setHeaderScaleType(ImageView.ScaleType.CENTER)
                                .setCustomView(editText)
                                .setHeaderColor(R.color.colorAccent)
                                .setPositiveText(R.string.save)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        Observable.create(new ObservableOnSubscribe<Integer>() {
                                            @Override
                                            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                                                try {
                                                    if (blockList == null)
                                                        blockList = new ArrayList<>();

                                                    String keywords = editText.getText().toString();
                                                    boolean isAdd = true;
                                                    if (blockList.size() > 0) {
                                                        for (BlockItem blockItem : blockList) {
                                                            if (blockItem.getType() == TYPE_KEYWORDS && TextUtils.equals(blockItem.getText(), keywords))
                                                                isAdd = false;
                                                        }
                                                    }
                                                    if (isAdd) {
                                                        BlockItem newItem = new BlockItem(TYPE_KEYWORDS, keywords);
                                                        blockList.add(newItem);
                                                        e.onNext(1);
                                                        newItem.save();
                                                    } else {
                                                        e.onNext(2);
                                                    }
                                                } catch (Exception e1) {
                                                    e1.printStackTrace();
                                                    e.onNext(0);
                                                } finally {
                                                    e.onComplete();
                                                }
                                            }
                                        }).subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new Consumer<Integer>() {
                                                    @Override
                                                    public void accept(Integer i) throws Exception {
                                                        switch (i) {
                                                            case 0:
                                                                SnackbarUtils.with(toolbar).setMessage(getString(R.string.action_block_keywords)
                                                                        + " " + getString(R.string.fail)).showError();
                                                                break;
                                                            case 1:
                                                                SnackbarUtils.with(toolbar)
                                                                        .setMessage(getString(R.string.start_after_restart_list))
                                                                        .show();
                                                                setResult(RESULT_OK);
                                                                if (adapter != null)
                                                                    adapter.notifyDataSetChanged();
                                                                else {
                                                                    emptyView.setVisibility(View.GONE);
                                                                    initSlowly();
                                                                }
                                                                break;
                                                            case 2:
                                                                SnackbarUtils.with(toolbar)
                                                                        .setMessage(getString(R.string.repeated))
                                                                        .show();
                                                                break;
                                                        }
                                                    }
                                                });
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
                        break;
                }
                return false;
            }
        });
        initStatusBar(R.color.main_background, true);
    }

    @Override
    protected void initSlowly() {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                try {
                    blockList = LitePal.findAll(BlockItem.class);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                if (blockList != null && blockList.size() > 0) {
                    e.onNext(true);
                } else {
                    e.onNext(false);
                }
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        progress.setVisibility(View.GONE);
                        if (aBoolean) {
                            adapter = new BlockAdapter(blockList);
                            rvContent.setAdapter(adapter);
                            rvContent.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL));
                            rvContent.setLayoutManager(new LinearLayoutManager(mActivity));
                        } else {
                            setEmptyView();
                        }
                    }
                });
    }

    private void setEmptyView() {
        progress.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
    }

    class BlockAdapter extends BaseQuickAdapter<BlockItem, BaseViewHolder> {
        public BlockAdapter(List data) {
            super(R.layout.layout_block_item, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, final BlockItem item) {
            helper.setText(R.id.tv_text, item.getText());
            switch (item.getType()) {
                case TYPE_SOURCE:
                    helper.setText(R.id.tv_source, getResources().getString(R.string.source));
                    break;
                case TYPE_KEYWORDS:
                    helper.setText(R.id.tv_source, getResources().getString(R.string.keywords));
                    break;
            }
            helper.getView(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    builder.setTitle(R.string.delete)
                            .setMessage(R.string.delete_message)
                            .setCancelable(true)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteItem(item);
                                }
                            }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                }
            });
        }
    }

    private void deleteItem(BlockItem item) {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                try {
                    blockList.remove(item);
                    item.delete();
                    e.onNext(true);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    e.onNext(false);
                }
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            adapter.notifyDataSetChanged();
                            if (blockList.size() < 1)
                                setEmptyView();
                            setResult(RESULT_OK);
                        } else {
                            SnackbarUtils.with(toolbar).setMessage(getResources().getString(R.string.fail)).show();
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.block_items, menu);//加载menu布局
        return true;
    }
}
