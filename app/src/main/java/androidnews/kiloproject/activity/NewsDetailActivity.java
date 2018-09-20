package androidnews.kiloproject.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.blankj.utilcode.util.ToastUtils;
import com.gyf.barlibrary.ImmersionBar;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import androidnews.kiloproject.R;
import androidnews.kiloproject.bean.NewsDetailData;
import butterknife.BindView;
import butterknife.ButterKnife;

import static androidnews.kiloproject.system.Appconfig.getNewsDetailA;
import static androidnews.kiloproject.system.Appconfig.getNewsDetailB;

public class NewsDetailActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.webview)
    WebView webview;
    @BindView(R.id.progress)
    ProgressBar progress;
    private NewsDetailData currentData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        initTitle();
        initStateBar(android.R.color.white,true);
    }

    @Override
    void initSlowly() {
        String docid = getIntent().getStringExtra("docid");
        if (!TextUtils.isEmpty(docid)) {
            EasyHttp.get(getNewsDetailA + docid + getNewsDetailB)
                    .readTimeOut(30 * 1000)//局部定义读超时
                    .writeTimeOut(30 * 1000)
                    .connectTimeout(30 * 1000)
                    .timeStamp(true)
                    .execute(new SimpleCallBack<String>() {
                        @Override
                        public void onError(ApiException e) {
                            ToastUtils.showShort("网络异常"+e.getMessage());
                            progress.setVisibility(View.GONE);
                        }

                        @Override
                        public void onSuccess(String response) {
                            if (!TextUtils.isEmpty(response)) {
                                String jsonNoHeader = response.substring(20, response.length());
                                String jsonFine = jsonNoHeader.substring(0, jsonNoHeader.length() - 1);
                                currentData = gson.fromJson(jsonFine, NewsDetailData.class);
                                initWeb();
                            }
                            progress.setVisibility(View.GONE);
                        }
                    });
        }
    }

    private void initTitle(){
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setTitle("正在加载...");
        //menu item点击事件监听
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_share:

                        break;
                    case R.id.action_star:
                        item.setIcon(R.drawable.ic_star_ok);
                        break;
                    case R.id.action_comment:

                        break;
                    case R.id.action_link:

                        break;
                    case R.id.action_browser:

                        break;
                }
                return false;
            }
        });
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_items,menu);//加载menu布局
        return true;
    }

    private void initWeb() {
        WebSettings webSetting = webview.getSettings();

        webSetting.setJavaScriptEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSetting.setAppCacheEnabled(true);
        webSetting.setDatabaseEnabled(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        String html = "<!DOCTYPE html>" +
                "<html lang=\"zh\">" +
                "<head>" +
                "<meta charset=\"UTF-8\" />" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />" +
                "<meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\" />" +
                "<title>Document</title>" +
                "<style>" +
                "body img{" +
                "width: 100%;" +
                "height: 100%;" +
                "}" +
                "body video{" +
                "width: 100%;" +
                "height: 100%;" +
                "}" +
                "div{width:100%;height:30px;} #from{width:auto;float:left;color:gray;} #time{width:auto;float:right;color:gray;}" +
                "</style>" +
                "</head>" +
                "<body>"
                + "<p><h2>" + currentData.getTitle() +"</h2></p>"
                + "<p><div><div id=\"from\">"+currentData.getSource()+
                "</div><div id=\"time\">"+currentData.getPtime()+"</div></div></p>"
                + currentData.getBody() + "</body>" +
                "</html>";
        if (currentData.getVideo() != null) {
            for (NewsDetailData.VideoBean videoBean : currentData.getVideo()) {
                html = html.replace(videoBean.getRef(),
                        "<video src=\"" + videoBean.getMp4_url() +
                                "\" controls=\"controls\" poster=\"" + videoBean.getCover() + "\"></video>");
            }
        }
        if (currentData.getImg() != null) {
            for (NewsDetailData.ImgBean imgBean : currentData.getImg()) {
                html = html.replace(imgBean.getRef(), "<img src=\"" + imgBean.getSrc() + "\"/>");
            }
        }
        webview.loadData(html, "text/html; charset=UTF-8", null);
    }
}
