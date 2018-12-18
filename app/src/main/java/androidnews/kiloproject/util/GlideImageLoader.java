package androidnews.kiloproject.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.youth.banner.loader.ImageLoader;

import androidnews.kiloproject.R;
import androidnews.kiloproject.util.GlideUtil;

public class GlideImageLoader extends ImageLoader {

    RequestOptions options;

    public GlideImageLoader() {
        options = new RequestOptions();
        options.centerCrop()
                .error(R.drawable.ic_error);
    }

    @Override
    public void displayImage(Context mContext, Object path, ImageView imageView) {
        /**
         注意：
         1.图片加载器由自己选择，这里不限制，只是提供几种使用方法
         2.返回的图片路径为Object类型，由于不能确定你到底使用的那种图片加载器，
         传输的到的是什么格式，那么这种就使用Object接收和返回，你只需要强转成你传输的类型就行，
         切记不要胡乱强转！
         */
        if (GlideUtil.isValidContextForGlide(mContext))
            Glide.with(mContext)
                    .load(path)
                    .apply(options)
                    .into(imageView);
    }
}