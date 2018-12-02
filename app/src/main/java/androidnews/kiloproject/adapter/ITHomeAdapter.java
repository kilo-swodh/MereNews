package androidnews.kiloproject.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import androidnews.kiloproject.R;
import androidnews.kiloproject.bean.net.ITHomeListData;
import androidnews.kiloproject.bean.net.ITHomeListData.ItemBean;
import androidnews.kiloproject.util.GlideUtil;

public class ITHomeAdapter extends BaseQuickAdapter<ITHomeListData.ItemBean, BaseViewHolder> {
    RequestOptions options;
    private Context mContext;

    public ITHomeAdapter(Context Context, List<ITHomeListData.ItemBean> data) {
        super(R.layout.list_item_card_linear_big, data);
        this.mContext = Context;
        options = new RequestOptions();
        options.centerCrop()
                .error(R.drawable.ic_error);
    }

    @Override
    protected void convert(BaseViewHolder helper, ITHomeListData.ItemBean item) {
        try {
            helper.setText(R.id.item_card_text, item.getTitle());
            helper.setText(R.id.item_card_time, item.getPostdate());
            helper.setText(R.id.item_card_subtitle, item.getDescription());
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
        if (GlideUtil.isValidContextForGlide(mContext))
            Glide.with(mContext).load(item.getImage())
                    .apply(options)
                    .into((ImageView) helper.getView(R.id.item_card_img));
    }
}
