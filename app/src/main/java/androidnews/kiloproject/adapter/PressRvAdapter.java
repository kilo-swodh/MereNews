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
import androidnews.kiloproject.entity.net.PressListData;
import androidnews.kiloproject.system.AppConfig;
import androidnews.kiloproject.util.GlideUtils;

public class PressRvAdapter extends BaseQuickAdapter<PressListData, BaseViewHolder> {
    RequestOptions options;
    private Context mContext;

    public PressRvAdapter(Context Context, List data) {
        super(R.layout.list_item_card_linear,data);
        this.mContext = Context;
        options = new RequestOptions();
        options.centerCrop()
                .error(R.drawable.ic_error);
    }

    @Override
    protected void convert(BaseViewHolder helper, PressListData item) {
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
            if (!AppConfig.isNoImage && GlideUtils.isValidContextForGlide(mContext))
                Glide.with(mContext).load(item.getImgsrc())
                        .apply(options)
                        .into((ImageView) helper.getView(R.id.item_card_img));
            else
                helper.setImageResource(R.id.item_card_img, R.drawable.ic_news_pic);
            helper.setText(R.id.item_card_subtitle, "");
        }
    }
}
