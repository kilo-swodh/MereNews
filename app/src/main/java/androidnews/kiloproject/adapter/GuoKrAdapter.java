package androidnews.kiloproject.adapter;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import androidnews.kiloproject.R;
import androidnews.kiloproject.bean.net.GuoKrListData;

public class GuoKrAdapter extends BaseQuickAdapter<GuoKrListData.ResultBean, BaseViewHolder> {

    RequestOptions options;
    Context mContext;

    public GuoKrAdapter(Context context,List<GuoKrListData.ResultBean> data) {
        super(R.layout.list_item_card_small,data);
        mContext = context;
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
                    mContext.getResources().getColor(R.color.main_text_color_drak));

        List<String> imgs = item.getImages();
        if (imgs != null && imgs.size() > 0)
            if (!((Activity) mContext).isFinishing())
                Glide.with(mContext)
                        .load(imgs.get(0))
                        .apply(options)
                        .into((ImageView) helper.getView(R.id.item_card_img));
        helper.setText(R.id.item_card_from, item.getSource_name());
    }
}
