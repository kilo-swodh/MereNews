package androidnews.kiloproject.activity;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blankj.utilcode.util.SnackbarUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.gyf.immersionbar.ImmersionBar;
import com.jude.swipbackhelper.SwipeBackHelper;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.DownloadProgressCallBack;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import java.util.LinkedList;
import java.util.List;

import androidnews.kiloproject.R;
import androidnews.kiloproject.entity.net.GalleyData;
import androidnews.kiloproject.system.base.BaseActivity;
import androidnews.kiloproject.util.FileCompatUtil;
import androidnews.kiloproject.widget.PinchImageView;

import static androidnews.kiloproject.system.AppConfig.isSwipeBack;

public class GalleyActivity extends BaseActivity {

    ViewPager galleyViewpager;
    TextView tvGalleyTitle;
    TextView tvGalleyText;
    TextView tvGalleyPage;
    ProgressBar progressBar;
    View btnGalleyDownload;

    private GalleyData galleyContent;
    private String currentImg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galley);
        galleyViewpager = (ViewPager) findViewById(R.id.galley_viewpager);
        tvGalleyTitle = (TextView) findViewById(R.id.tv_galley_title);
        tvGalleyText = (TextView) findViewById(R.id.tv_galley_text);
        tvGalleyPage = (TextView) findViewById(R.id.tv_galley_page);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        btnGalleyDownload = (View) findViewById(R.id.btn_galley_download);

//        ViewCompat.setTransitionName(galleyViewpager, "banner_pic");
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        if (ImmersionBar.hasNavigationBar(mActivity)) {
            ImmersionBar.with(this).transparentNavigationBar().init();
            if (ImmersionBar.isNavigationAtBottom(mActivity)) {
                int navHeight = ImmersionBar.getNavigationBarHeight(mActivity);
                Resources res = getResources();
                int dimNor = res.getDimensionPixelSize(R.dimen.margin_normal);
                int dimLarge = res.getDimensionPixelSize(R.dimen.margin_large);
                ConstraintLayout.LayoutParams lpPage = (ConstraintLayout.LayoutParams) tvGalleyPage.getLayoutParams();
                lpPage.setMargins(dimNor, 0, 0, dimLarge + navHeight);

                ConstraintLayout.LayoutParams lpDownLoad = (ConstraintLayout.LayoutParams) btnGalleyDownload.getLayoutParams();
                lpDownLoad.setMargins(0, 0, dimNor, dimLarge + navHeight);
            }
        }
        SwipeBackHelper.getCurrentPage(mActivity).setDisallowInterceptTouchEvent(false);
    }

    @Override
    protected void initSlowly() {
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
                            SnackbarUtils.with(galleyViewpager).setMessage(getString(R.string.load_fail) + e.getMessage()).showError();
                        }

                        @Override
                        public void onSuccess(String response) {
                            try {
                                galleyContent = gson.fromJson(response, GalleyData.class);
                                initGalley();
                            } catch (Exception e) {
                                e.printStackTrace();
                                SnackbarUtils.with(galleyViewpager).setMessage(getString(R.string.load_fail)).showError();
                            }
                        }
                    });
        }
    }


    private void initGalley() {
        progressBar.setVisibility(View.GONE);
        List<GalleyData.PhotosBean> beans = galleyContent.getPhotos();
        if (beans == null || beans.size() == 0)
            return;
        tvGalleyPage.setText("1/" + beans.size());
        tvGalleyTitle.setText(galleyContent.getSetname());
        tvGalleyText.setText(beans.get(0).getNote());

        LinkedList<PinchImageView> viewCache = new LinkedList<PinchImageView>();
        RequestOptions options = new RequestOptions();
        options.error(R.drawable.ic_error);

        currentImg = galleyContent.getPhotos().get(0).getImgurl();
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
            public Object instantiateItem(ViewGroup container, final int position) {
                PinchImageView piv;
                if (viewCache.size() > 0) {
                    piv = viewCache.remove();
                    piv.reset();
                } else {
                    piv = new PinchImageView(mActivity);
                }
                String imageUrl = galleyContent.getPhotos().get(position).getImgurl();

                Glide.with(mActivity).load(imageUrl).apply(options).into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        piv.setImageDrawable(resource);
                        if (position == 0 && isLollipop())
                            ViewCompat.setTransitionName(piv, "big_card");
                    }
                });
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
                currentImg = galleyContent.getPhotos().get(position).getImgurl();
                if (isSwipeBack)
                    SwipeBackHelper.getCurrentPage(mActivity).setDisallowInterceptTouchEvent(position != 0);
            }

            @Override
            public void onPageSelected(int position) {
                List<GalleyData.PhotosBean> beans = galleyContent.getPhotos();
                tvGalleyPage.setText((position + 1) + "/" + beans.size());
                tvGalleyTitle.setText(galleyContent.getSetname());
                tvGalleyText.setText(beans.get(position).getNote());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_galley_download:
                try {
                    String fileName = currentImg.substring(currentImg.lastIndexOf('/'), currentImg.length());
                    String path = FileCompatUtil.getMediaDir(mActivity);
                    EasyHttp.downLoad(currentImg)
                            .savePath(path)
                            .saveName(fileName)//不设置默认名字是时间戳生成的
                            .execute(new DownloadProgressCallBack<String>() {
                                @Override
                                public void update(long bytesRead, long contentLength, boolean done) {
                                }

                                @Override
                                public void onStart() {
                                    //开始下载
                                }

                                @Override
                                public void onComplete(String path) {
                                    //下载完成，path：下载文件保存的完整路径
                                    SnackbarUtils.with(tvGalleyTitle)
                                            .setMessage(getString(R.string.download_success))
                                            .show();
                                }

                                @Override
                                public void onError(ApiException e) {
                                    //下载失败
                                    SnackbarUtils.with(tvGalleyTitle)
                                            .setMessage(getString(R.string.download_fail) + e.getMessage())
                                            .showError();
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                    SnackbarUtils.with(tvGalleyTitle)
                            .setMessage(getString(R.string.download_fail))
                            .showError();
                }
                break;
        }
    }
}

