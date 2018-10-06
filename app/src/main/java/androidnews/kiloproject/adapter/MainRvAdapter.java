package androidnews.kiloproject.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.widget.ImageView;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

import androidnews.kiloproject.GlideImageLoader;
import androidnews.kiloproject.R;
import androidnews.kiloproject.activity.GalleyActivity;
import androidnews.kiloproject.activity.NewsDetailActivity;
import androidnews.kiloproject.bean.net.NewMainListData;

import static androidnews.kiloproject.fragment.BaseRvFragment.CELL;
import static androidnews.kiloproject.fragment.BaseRvFragment.HEADER;
import static androidnews.kiloproject.system.base.BaseActivity.isLollipop;
import static com.blankj.utilcode.util.ActivityUtils.startActivity;

public class MainRvAdapter extends BaseMultiItemQuickAdapter<NewMainListData, BaseViewHolder> {

    RequestOptions options;
    Context mContext;

    public MainRvAdapter(Context context, List data) {
        super(data);
        mContext = context;
        addItemType(HEADER, R.layout.list_item_card_big);
        addItemType(CELL, R.layout.list_item_card_small);
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
                                                    .makeSceneTransitionAnimation((Activity) mContext, helper.getView(R.id.card_view), "big_card");
                                            startActivity(intent, activityOptions.toBundle());
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
                helper.setText(R.id.item_card_text, item.getTitle());
                helper.setText(R.id.item_card_time, item.getPtime().substring(5, item.getPtime().length()));
                helper.setText(R.id.item_card_from, item.getSource().replace("$", ""));
                if (TextUtils.isEmpty(item.getImgsrc())) {
                    helper.setText(R.id.item_card_subtitle, item.getDigest().replace("&nbsp", ""));
                    helper.setImageResource(R.id.item_card_img, R.color.white);
                } else {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN)
                        if (!((Activity) mContext).isDestroyed()) {
                            Glide.with(mContext)
                                    .load(item.getImgsrc())
                                    .apply(options)
                                    .into((ImageView) helper.getView(R.id.item_card_img));
                        } else
                            Glide.with(mContext)
                                    .load(item.getImgsrc())
                                    .apply(options)
                                    .into((ImageView) helper.getView(R.id.item_card_img));
                    helper.setText(R.id.item_card_subtitle, "");
                }
                break;
        }
    }
}
