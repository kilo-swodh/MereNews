package androidnews.kiloproject.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import androidnews.kiloproject.R;
import androidnews.kiloproject.entity.net.GuoKrListData;
import androidnews.kiloproject.util.GlideUtil;

public class GuoKrAdapter extends BaseQuickAdapter<GuoKrListData.ResultBean, BaseViewHolder> {
    RequestOptions options;
    private Context mContext;

    public GuoKrAdapter(Context Context, List<GuoKrListData.ResultBean> data) {
        super(R.layout.list_item_card_linear_lite, data);
        this.mContext = Context;
        options = new RequestOptions();
        options.centerCrop()
                .error(R.drawable.ic_error);
    }

    @Override
    protected void convert(BaseViewHolder helper, GuoKrListData.ResultBean item) {
        helper.setText(R.id.item_card_text, item.getTitle());
        if (item.isReaded())
            helper.setTextColor(R.id.item_card_text,
                    mContext.getResources().getColor(R.color.main_text_color_read));
        else
            helper.setTextColor(R.id.item_card_text,
                    mContext.getResources().getColor(R.color.main_text_color_dark));

        if (GlideUtil.isValidContextForGlide(mContext))
                Glide.with(mContext)
                        .load(item.getHeadline_img())
                        .apply(options)
                        .into((ImageView) helper.getView(R.id.item_card_img));
        helper.setText(R.id.item_card_info, item.getSource_name());
    }
}
