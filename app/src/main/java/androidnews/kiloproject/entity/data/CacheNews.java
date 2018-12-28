package androidnews.kiloproject.entity.data;

import org.litepal.crud.LitePalSupport;

public class CacheNews extends LitePalSupport {

    public static final int CACHE_HISTORY = 1001;
    public static final int CACHE_COLLECTION = 1002;

    private long id;
    private String title;
    private String imgUrl;
    private String source;      //锤子需要
    private String docid;
    private String htmlText;
    private int type;
    private int channel;
    private String url;         //IT之家需要 连接
    private String timeStr;     //IT之家需要 字符串时间

    public CacheNews(String title, String imgUrl, String source, String docid, String htmlText, int type, int channel) {
        this.title = title;
        this.imgUrl = imgUrl;
        this.source = source;
        this.docid = docid;
        this.htmlText = htmlText;
        this.type = type;
        this.channel = channel;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTimeStr() {
        return timeStr;
    }

    public void setTimeStr(String timeStr) {
        this.timeStr = timeStr;
    }
}
