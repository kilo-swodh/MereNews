package androidnews.kiloproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

import androidnews.kiloproject.GlideImageLoader;
import androidnews.kiloproject.R;
import androidnews.kiloproject.activity.GalleyActivity;
import androidnews.kiloproject.bean.MainDataBean;

import static com.blankj.utilcode.util.ActivityUtils.startActivity;

public class MainRecyclerAdapter extends BaseMultiItemQuickAdapter<MainDataBean.T1348647909107Bean, BaseViewHolder> {

    RequestOptions options;
    Context mContext;

    public MainRecyclerAdapter(Context context, List data) {
        super(data);
        mContext = context;
        addItemType(MainDataBean.T1348647909107Bean.HEADER, R.layout.list_item_card_big);
        addItemType(MainDataBean.T1348647909107Bean.CELL, R.layout.list_item_card_small);
        options = new RequestOptions();
        options.centerCrop()
                .error(R.drawable.ic_error);
    }

    @Override
    protected void convert(BaseViewHolder helper, MainDataBean.T1348647909107Bean item) {
        switch (helper.getItemViewType()) {
            case MainDataBean.T1348647909107Bean.HEADER:
                Banner banner = (Banner) helper.getView(R.id.banner);
                List<String> imgs = new ArrayList<>();
                List<String> titles = new ArrayList<>();
                titles.add(item.getTitle());
                imgs.add(item.getImgsrc());
                for (MainDataBean.T1348647909107Bean.AdsBean bean : item.getAds()) {
                    titles.add(bean.getTitle());
                    imgs.add(bean.getImgsrc());
                }
                banner.setImageLoader(new GlideImageLoader())
                        .setBannerAnimation(Transformer.FlipHorizontal)
                        .setBannerStyle(BannerConfig.NUM_INDICATOR_TITLE)
                        .setImages(imgs)
                        .setBannerTitles(titles)
                        .setOnBannerListener(new OnBannerListener() {
                            @Override
                            public void OnBannerClick(int position) {
                                String skipID;
                                String rawId;
                                if (position == 0)
                                    rawId = item.getSkipID();
                                else
                                    rawId = item.getAds().get(position - 1).getSkipID();
                                ToastUtils.showShort("rawId : "+ rawId);
                                skipID = rawId.split("000")[1];
                                if (!TextUtils.isEmpty(skipID)) {
                                    Intent intent = new Intent(mContext, GalleyActivity.class);
                                    intent.putExtra("skipID", "000" + skipID.replace("|","/") + ".json");
                                    startActivity(intent);
                                }
                            }
                        });
                banner.start();
                break;
            case MainDataBean.T1348647909107Bean.CELL:
                helper.setText(R.id.item_card_text, item.getTitle());
                helper.setText(R.id.item_card_time, item.getPtime().substring(5, item.getPtime().length()));
                helper.setText(R.id.item_card_from, item.getSource().replace("$", ""));
                if (TextUtils.isEmpty(item.getImgsrc())) {
                    helper.setText(R.id.item_card_subtitle, item.getDigest());
                    helper.setImageResource(R.id.item_card_img, R.color.white);
                } else {
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
