package androidnews.kiloproject.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import java.util.LinkedList;
import java.util.List;

import androidnews.kiloproject.R;
import androidnews.kiloproject.bean.GalleyBean;
import androidnews.kiloproject.widget.PinchImageView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class GalleyActivity extends BaseActivity {

    @BindView(R.id.galley_viewpager)
    ViewPager galleyViewpager;
    @BindView(R.id.tv_galley_title)
    TextView tvGalleyTitle;
    @BindView(R.id.tv_galley_text)
    TextView tvGalleyText;
    @BindView(R.id.tv_galley_page)
    TextView tvGalleyPage;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private GalleyBean galleyContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galley);
        ButterKnife.bind(this);
    }

    @Override
    void initSlowly() {
        String skipID = getIntent().getStringExtra("skipID");
        if (!TextUtils.isEmpty(skipID)) {
            EasyHttp.get("/photo/api/set/" + skipID)
                    .readTimeOut(30 * 1000)//局部定义读超时
                    .writeTimeOut(30 * 1000)
                    .connectTimeout(30 * 1000)
                    .timeStamp(true)
                    .execute(new SimpleCallBack<String>() {
                        @Override
                        public void onError(ApiException e) {
                            ToastUtils.showShort(e.getMessage());
                        }

                        @Override
                        public void onSuccess(String response) {
                            if (!TextUtils.isEmpty(response)) {
                                galleyContent = gson.fromJson(response, GalleyBean.class);
                                initGalley();
                            }
                        }
                    });
        }
    }


    private void initGalley() {
        progressBar.setVisibility(View.GONE);
        List<GalleyBean.PhotosBean> beans = galleyContent.getPhotos();
        tvGalleyPage.setText("1/" + beans.size());
        tvGalleyTitle.setText(galleyContent.getSetname());
        tvGalleyText.setText(beans.get(0).getNote());

        final LinkedList<PinchImageView> viewCache = new LinkedList<PinchImageView>();
        RequestOptions options = new RequestOptions();
        options.error(R.drawable.ic_error);

        final int picWidth = Math.min(ScreenUtils.getScreenWidth(),ScreenUtils.getScreenHeight());
        galleyViewpager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return galleyContent.getPhotos().size();
            }

            @Override
            public boolean isViewFromObject(View view, Object o) {
                return view == o;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                PinchImageView piv;
                if (viewCache.size() > 0) {
                    piv = viewCache.remove();
                    piv.reset();
                } else {
                    piv = new PinchImageView(mActivity);
                }
                String imageUrl = galleyContent.getPhotos().get(position).getImgurl();

                Glide.with(mActivity).load(imageUrl).apply(options).into(piv);
                container.addView(piv);
                return piv;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                PinchImageView piv = (PinchImageView) object;
                container.removeView(piv);
                viewCache.add(piv);
            }

            @Override
            public void setPrimaryItem(ViewGroup container, int position, Object object) {
            }
        });
        galleyViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // 滚动的时候改变自定义控件的动画
            }

            @Override
            public void onPageSelected(int position) {
                List<GalleyBean.PhotosBean> beans = galleyContent.getPhotos();
                tvGalleyPage.setText((position + 1) + "/" + beans.size());
                tvGalleyTitle.setText(galleyContent.getSetname());
                tvGalleyText.setText(beans.get(position).getNote());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}

