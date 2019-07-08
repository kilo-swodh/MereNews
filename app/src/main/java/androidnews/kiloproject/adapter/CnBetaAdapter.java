package androidnews.kiloproject.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import androidnews.kiloproject.R;
import androidnews.kiloproject.entity.net.CnBetaListData;
import androidnews.kiloproject.util.GlideUtil;

public class CnBetaAdapter extends BaseQuickAdapter<CnBetaListData.ResultBean, BaseViewHolder> {
    RequestOptions options;
    private Context mContext;

    public CnBetaAdapter(Context Context, List<CnBetaListData.ResultBean> data) {
        super(R.layout.list_item_card_linear_smaill_pic, data);
        this.mContext = Context;
        options = new RequestOptions();
        options.centerCrop()
                .error(R.drawable.ic_error);
    }

    @Override
    protected void convert(BaseViewHolder helper, CnBetaListData.ResultBean item) {
        try {
            helper.setText(R.id.item_card_text, item.getTitle());
            helper.setText(R.id.item_card_time, item.getPubtime());
            helper.setText(R.id.item_card_subtitle, item.getSummary());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (item.isReaded()) {
            helper.setTextColor(R.id.item_card_text,
                    mContext.getResources().getColor(R.color.main_text_color_read));
            helper.setTextColor(R.id.item_card_subtitle,
                    mContext.getResources().getColor(R.color.main_text_color_read));
        }else {
            helper.setTextColor(R.id.item_card_text,
                    mContext.getResources().getColor(R.color.main_text_color_dark));
            helper.setTextColor(R.id.item_card_subtitle,
                    mContext.getResources().getColor(R.color.main_text_color_dark));
        }
        if (GlideUtil.isValidContextForGlide(mContext)) {
            Glide.with(mContext).load(item.getThumb())
                    .apply(options)
                    .into((ImageView) helper.getView(R.id.item_card_img));
            Glide.with(mContext).load(item.getTopic_logo())
                    .apply(options)
                    .into((ImageView) helper.getView(R.id.item_card_img_logo));
        }
    }
}
