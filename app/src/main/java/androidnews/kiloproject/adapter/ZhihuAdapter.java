package androidnews.kiloproject.adapter;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import androidnews.kiloproject.R;
import androidnews.kiloproject.bean.net.ZhihuListData;

public class ZhihuAdapter extends BaseQuickAdapter<ZhihuListData.StoriesBean, BaseViewHolder> {
    RequestOptions options;
    RequestManager glide;
    private Context mContext;

    public ZhihuAdapter(Context Context,RequestManager glide,List<ZhihuListData.StoriesBean> data) {
        super(R.layout.list_item_card_small,data);
        this.glide = glide;
        this.mContext = Context;
        options = new RequestOptions();
        options.centerCrop()
                .error(R.drawable.ic_error);
    }

    @Override
    protected void convert(BaseViewHolder helper, ZhihuListData.StoriesBean item) {
        helper.setText(R.id.item_card_text, item.getTitle());
        if (item.isReaded())
            helper.setTextColor(R.id.item_card_text,
                    mContext.getResources().getColor(R.color.main_text_color_read));
        else
            helper.setTextColor(R.id.item_card_text,
                    mContext.getResources().getColor(R.color.main_text_color_drak));
        List<String> imgs = item.getImages();
        if (imgs != null && imgs.size() > 0)
            glide.load(imgs.get(0))
                    .apply(options)
                    .into((ImageView) helper.getView(R.id.item_card_img));
        helper.setText(R.id.item_card_from, R.string.zhihu);
    }
}
