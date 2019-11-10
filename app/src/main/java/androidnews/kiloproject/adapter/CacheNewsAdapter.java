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
import androidnews.kiloproject.entity.data.CacheNews;
import androidnews.kiloproject.system.AppConfig;
import androidnews.kiloproject.util.GlideUtils;

public class CacheNewsAdapter extends BaseQuickAdapter<CacheNews, BaseViewHolder> {
    RequestOptions options;
    private Context mContext;
    private String offStr;

    public CacheNewsAdapter(Context Context, List data) {
        super(R.layout.list_item_card_cache, data);
        this.mContext = Context;
        offStr = "-" + mContext.getResources().getString(R.string.support_offline);
        options = new RequestOptions();
        options.centerCrop()
                .error(R.drawable.ic_error);
    }

    @Override
    protected void convert(BaseViewHolder helper, CacheNews item) {
        helper.setText(R.id.item_card_text, item.getTitle());
        helper.setText(R.id.item_card_info, item.getSource() + (TextUtils.isEmpty(item.getHtmlText()) ? "" : offStr));
        if (!AppConfig.isNoImage && GlideUtils.isValidContextForGlide(mContext))
            Glide.with(mContext)
                    .load(item.getImgUrl())
                    .apply(options)
                    .into((ImageView) helper.getView(R.id.item_card_img));
        else
            helper.setImageResource(R.id.item_card_img, R.drawable.ic_news_pic);
    }
}
