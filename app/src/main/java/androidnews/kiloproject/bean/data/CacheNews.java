package androidnews.kiloproject.bean.data;

import android.os.Parcel;
import android.os.Parcelable;

public class CacheNews{

    public static final int CACHE_HISTORY = 1001;
    public static final int CACHE_COLLECTION = 1002;

    String title;
    String imgUrl;
    String source;
    String docid;
    String htmlText;

    public CacheNews(String title, String imgUrl, String source, String docid, String htmlText) {
        this.title = title;
        this.imgUrl = imgUrl;
        this.source = source;
        this.docid = docid;
        this.htmlText = htmlText;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDocid() {
        return docid;
    }

    public void setDocid(String docid) {
        this.docid = docid;
    }

    public String getHtmlText() {
        return htmlText;
    }

    public void setHtmlText(String htmlText) {
        this.htmlText = htmlText;
    }
}
