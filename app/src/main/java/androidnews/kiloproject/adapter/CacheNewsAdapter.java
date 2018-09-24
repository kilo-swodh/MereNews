package androidnews.kiloproject.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import androidnews.kiloproject.R;
import androidnews.kiloproject.bean.data.CacheNews;

public class CacheNewsAdapter extends BaseQuickAdapter<CacheNews, BaseViewHolder> {
    RequestOptions options;
    private Context mContext;

    public CacheNewsAdapter(Context context, List data) {
        super(R.layout.list_item_card_small, data);
        this.mContext = context;

        options = new RequestOptions();
        options.centerCrop()
                .error(R.drawable.ic_error);
    }

    @Override
    protected void convert(BaseViewHolder helper, CacheNews item) {
        helper.setText(R.id.item_card_text, item.getTitle());
        helper.setText(R.id.item_card_from, item.getSource());
        Glide.with(mContext)
                .load(item.getImgUrl())
                .apply(options)
                .into((ImageView) helper.getView(R.id.item_card_img));
    }

}
