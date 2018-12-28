package androidnews.kiloproject.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.blankj.utilcode.util.TimeUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import androidnews.kiloproject.R;
import androidnews.kiloproject.entity.net.SmartisanListData;
import androidnews.kiloproject.util.GlideUtil;

public class SmartisanAdapter extends BaseQuickAdapter<SmartisanListData.DataBean.ListBean, BaseViewHolder> {
    RequestOptions options;
    private Context mContext;

    public SmartisanAdapter(Context Context, List<SmartisanListData.DataBean.ListBean> data) {
        super(R.layout.list_item_card_linear_multi_pic, data);
        this.mContext = Context;
        options = new RequestOptions();
        options.centerCrop()
                .error(R.drawable.ic_error);
    }

    @Override
    protected void convert(BaseViewHolder helper, SmartisanListData.DataBean.ListBean item) {
        helper.setText(R.id.item_card_text, item.getTitle());
        if (item.isReaded()) {
            helper.setTextColor(R.id.item_card_text,
                    mContext.getResources().getColor(R.color.main_text_color_read));
            helper.setTextColor(R.id.item_card_subtitle,
                    mContext.getResources().getColor(R.color.main_text_color_read));
        } else {
            helper.setTextColor(R.id.item_card_text,
                    mContext.getResources().getColor(R.color.main_text_color_dark));
            helper.setTextColor(R.id.item_card_subtitle,
                    mContext.getResources().getColor(R.color.main_text_color_dark));
        }
        helper.setText(R.id.item_card_subtitle, item.getBrief());
        helper.setText(R.id.item_card_time,
                TimeUtils.millis2String(Long.parseLong(item.getUpdate_time()) * 1000));
        helper.setText(R.id.item_card_info, item.getSite_info().getName());

        String img1 = item.getPrepic1();
        if (!TextUtils.isEmpty(img1)) {
            if (GlideUtil.isValidContextForGlide(mContext))
                Glide.with(mContext)
                        .load(img1)
                        .apply(options)
                        .into((ImageView) helper.getView(R.id.item_card_img_1));
            String img2 = item.getPrepic2();
            String img3 = item.getPrepic3();
            if (!TextUtils.isEmpty(img2) && GlideUtil.isValidContextForGlide(mContext))
                Glide.with(mContext)
                        .load(img2)
                        .apply(options)
                        .into((ImageView) helper.getView(R.id.item_card_img_2));
            if (!TextUtils.isEmpty(img3) && GlideUtil.isValidContextForGlide(mContext))
                Glide.with(mContext)
                        .load(img3)
                        .apply(options)
                        .into((ImageView) helper.getView(R.id.item_card_img_3));
        }else {
            helper.setGone(R.id.item_card_img_1,false);
            helper.setGone(R.id.item_card_img_2,false);
            helper.setGone(R.id.item_card_img_3,false);
        }

        String imgLogo = item.getSite_info().getPic();
        if (!TextUtils.isEmpty(imgLogo) && GlideUtil.isValidContextForGlide(mContext))
            Glide.with(mContext)
                    .load(imgLogo)
                    .apply(options)
                    .into((ImageView) helper.getView(R.id.item_card_img_logo));
    }
}
