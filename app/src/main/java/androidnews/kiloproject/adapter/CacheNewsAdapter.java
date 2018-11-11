package androidnews.kiloproject.adapter;

import android.content.Context;
import android.widget.ImageView;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import androidnews.kiloproject.R;
import androidnews.kiloproject.bean.data.CacheNews;

public class CacheNewsAdapter extends BaseQuickAdapter<CacheNews, BaseViewHolder> {
    RequestOptions options;
    private final RequestManager glide;
    private Context mContext;

    public CacheNewsAdapter(Context Context,RequestManager glide, List data) {
        super(R.layout.list_item_card_small, data);
        this.glide = glide;
        this.mContext = Context;
        options = new RequestOptions();
        options.centerCrop()
                .error(R.drawable.ic_error);
    }

    @Override
    protected void convert(BaseViewHolder helper, CacheNews item) {
        helper.setText(R.id.item_card_text, item.getTitle());
        helper.setText(R.id.item_card_from, item.getSource());
        glide.load(item.getImgUrl())
                .apply(options)
                .into((ImageView) helper.getView(R.id.item_card_img));
    }
}
