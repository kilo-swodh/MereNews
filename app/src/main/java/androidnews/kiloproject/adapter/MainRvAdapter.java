package androidnews.kiloproject.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

import androidnews.kiloproject.util.GlideImageLoader;
import androidnews.kiloproject.R;
import androidnews.kiloproject.activity.GalleyActivity;
import androidnews.kiloproject.activity.NewsDetailActivity;
import androidnews.kiloproject.entity.net.NewMainListData;
import androidnews.kiloproject.util.GlideUtil;

import static androidnews.kiloproject.fragment.BaseRvFragment.CELL;
import static androidnews.kiloproject.fragment.BaseRvFragment.CELL_EXTRA;
import static androidnews.kiloproject.fragment.BaseRvFragment.HEADER;
import static androidnews.kiloproject.system.base.BaseActivity.isLollipop;
import static com.blankj.utilcode.util.ActivityUtils.startActivity;

public class MainRvAdapter extends BaseMultiItemQuickAdapter<NewMainListData, BaseViewHolder> {
    RequestOptions options;
    private Context mContext;
    private RecyclerView.RecycledViewPool childRvPool;

    public MainRvAdapter(Context Context, List data) {
        super(data);
        this.mContext = Context;
        addItemType(HEADER, R.layout.list_item_card_banner);
        addItemType(CELL, R.layout.list_item_card_linear);
        addItemType(CELL_EXTRA, R.layout.list_item_card_linear_extra);
        options = new RequestOptions();
        options.centerCrop()
                .error(R.drawable.ic_error);
    }

    @Override
    protected void convert(BaseViewHolder helper, NewMainListData item) {
        switch (helper.getItemViewType()) {
            case HEADER:
                Banner banner = (Banner) helper.getView(R.id.banner);
                List<String> imgs = new ArrayList<>();
                List<String> titles = new ArrayList<>();
                titles.add(item.getTitle());
                imgs.add(item.getImgsrc());
                if (item.getAds() != null)
                    for (NewMainListData.AdsBean bean : item.getAds()) {
                        titles.add(bean.getTitle());
                        imgs.add(bean.getImgsrc());
                    }
                banner.setImageLoader(new GlideImageLoader())
                        .setBannerAnimation(Transformer.FlipHorizontal)
                        .setBannerStyle(BannerConfig.NUM_INDICATOR_TITLE)
                        .setDelayTime(5 * 1000)
                        .setImages(imgs)
                        .setBannerTitles(titles)
                        .setOnBannerListener(new OnBannerListener() {
                            @Override
                            public void OnBannerClick(int position) {
                                String skipID = "";
                                String rawId;
                                if (position == 0)
                                    rawId = item.getSkipID();
                                else
                                    rawId = item.getAds().get(position - 1).getSkipID();
                                Intent intent;
                                if (!TextUtils.isEmpty(rawId)) {
                                    int index = rawId.lastIndexOf("|");
                                    if (index != -1) {
                                        skipID = rawId.substring(index - 4, rawId.length());
                                        intent = new Intent(mContext, GalleyActivity.class);
                                        intent.putExtra("skipID", skipID.replace("|", "/") + ".json");
                                        if (isLollipop()) {
                                            ActivityOptionsCompat activityOptions = ActivityOptionsCompat
                                                    .makeSceneTransitionAnimation((Activity) mContext, banner, banner.getTransitionName());
                                            try {
                                                startActivity(intent, activityOptions.toBundle());
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                startActivity(intent);
                                            }
                                        } else
                                            startActivity(intent);
                                    } else {
                                        ToastUtils.showShort(R.string.server_fail);
                                        return;
                                    }
                                } else {
                                    intent = new Intent(mContext, NewsDetailActivity.class);
                                    intent.putExtra("docid", item.getDocid().replace("_special", "").trim());
                                    startActivity(intent);
                                }
                            }
                        });
                banner.start();
                break;
            case CELL:
                try {
                    helper.setText(R.id.item_card_text, item.getTitle());
                    helper.setText(R.id.item_card_time, item.getPtime().substring(5, item.getPtime().length()));
                    helper.setText(R.id.item_card_info, item.getSource().replace("$", ""));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (item.isReaded())
                    helper.setTextColor(R.id.item_card_text,
                            mContext.getResources().getColor(R.color.main_text_color_read));
                else
                    helper.setTextColor(R.id.item_card_text,
                            mContext.getResources().getColor(R.color.main_text_color_dark));
                if (TextUtils.isEmpty(item.getImgsrc())) {
                    helper.setText(R.id.item_card_subtitle, item.getDigest().replace("&nbsp", ""));
                    helper.setImageResource(R.id.item_card_img, R.color.white);
                } else {
                    if (GlideUtil.isValidContextForGlide(mContext))
                        Glide.with(mContext).load(item.getImgsrc())
                                .apply(options)
                                .into((ImageView) helper.getView(R.id.item_card_img));
                    helper.setText(R.id.item_card_subtitle, "");
                }
                break;
            case CELL_EXTRA:
                ImageView ivPic = helper.getView(R.id.item_card_img);
                TextView tvInfo = helper.getView(R.id.item_card_info);
                try {
                    helper.setText(R.id.item_card_text, item.getTitle());
                    helper.setText(R.id.item_card_time, item.getPtime().substring(5, item.getPtime().length()));
                    tvInfo.setText(item.getSource().replace("$", ""));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (GlideUtil.isValidContextForGlide(mContext))
                    Glide.with(mContext).load(item.getImgsrc())
                            .apply(options)
                            .into(ivPic);

                View.OnClickListener listener = new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, NewsDetailActivity.class);
                        intent.putExtra("docid", item.getDocid().replace("_special", "").trim());
                        if (!item.isReaded()) {
                            item.setReaded(true);
                            notifyItemChanged(helper.getAdapterPosition());
                        }
                        startActivity(intent);
                    }
                };
                ivPic.setOnClickListener(listener);
                tvInfo.setOnClickListener(listener);

                RecyclerView recyclerView = helper.getView(R.id.rv_extra);
                ExtraAdapter adapter = new ExtraAdapter(mContext,item.getSpecialextra());
                adapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                        Intent intent = new Intent(mContext, NewsDetailActivity.class);
                        NewMainListData.SpecialextraBean bean = item.getSpecialextra().get(position);
                        intent.putExtra("docid", bean.getDocid().replace("_special", "").trim());
                        if (!bean.isReaded()) {
                            bean.setReaded(true);
                            adapter.notifyItemChanged(position);
                        }
                        startActivity(intent);
                    }
                });
                recyclerView.setAdapter(adapter);
                recyclerView.setHasFixedSize(true);
                recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
                if (childRvPool != null)
                    recyclerView.setRecycledViewPool(childRvPool);
                else
                    childRvPool = recyclerView.getRecycledViewPool();

                recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

                TextView tvOpen = helper.getView(R.id.tv_open);
                tvOpen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        item.setOpen(true);
                        notifyItemChanged(helper.getAdapterPosition());
                    }
                });

                if (item.isOpen()){
                    tvOpen.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }else {
                    tvOpen.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }

                break;
        }
    }

    class ExtraAdapter extends BaseQuickAdapter<NewMainListData.SpecialextraBean, BaseViewHolder>{
        RequestOptions options;
        private Context mContext;

        public ExtraAdapter(Context Context, List data) {
            super(R.layout.layout_linear_extra_item, data);
            this.mContext = Context;
            options = new RequestOptions();
            options.centerCrop()
                    .error(R.drawable.ic_error);
        }

        @Override
        protected void convert(BaseViewHolder helper, NewMainListData.SpecialextraBean item) {
            try {
                helper.setText(R.id.item_card_text, item.getTitle());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (item.isReaded())
                helper.setTextColor(R.id.item_card_text,
                        mContext.getResources().getColor(R.color.main_text_color_read));
            else
                helper.setTextColor(R.id.item_card_text,
                        mContext.getResources().getColor(R.color.main_text_color_dark));

            if (GlideUtil.isValidContextForGlide(mContext))
                Glide.with(mContext).load(item.getImgsrc())
                        .apply(options)
                        .into((ImageView) helper.getView(R.id.item_card_img));
        }
    }
}
