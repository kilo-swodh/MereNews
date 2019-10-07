package androidnews.kiloproject.entity.net;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

public class NewMainListData implements MultiItemEntity {

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    private int itemType;
    private boolean isReaded;
    private boolean isOpen;

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public boolean isReaded() {
        return isReaded;
    }

    public void setReaded(boolean readed) {
        isReaded = readed;
    }

    public NewMainListData(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    /**
     * template : normal1
     * skipID : 00AP0001|2296442
     * lmodify : 2018-09-19 09:26:21
     * postid : PHOT262JQ000100A
     * source : 视觉中国
     * title : 警方抓14个盗墓团伙:村支书带头盗墓
     * mtime : 2018-09-19 09:26:21
     * hasImg : 1
     * topic_background : http://img2.cache.netease.com/m/newsapp/reading/cover1/C1348646712614.jpg
     * digest :
     * photosetID : 00AP0001|2296442
     * boardid : photoview_bbs
     * alias : Top News
     * hasAD : 1
     * imgsrc : http://cms-bucket.nosdn.127.net/2018/09/19/9a4e8538609c42e6afc37fdf26f5da8e.jpeg
     * ptime : 2018-09-19 07:45:38
     * daynum : 17793
     * hasHead : 1
     * imgType : 1
     * order : 1
     * editor : []
     * votecount : 135
     * hasCover : false
     * docid : 9IG74V5H00963VRO_DS25IQADbjzhangxianchaoupdateDoc
     * tname : 头条
     * priority : 666
     * ads : [{"subtitle":"","skipType":"photoset","skipID":"00AN0001|2296444","tag":"photoset","title":"海南发放首批港澳台居民居住证","imgsrc":"bigimg","url":"00AN0001|2296444"},{"subtitle":"","skipType":"photoset","skipID":"00AP0001|2296443","tag":"photoset","title":"高校军训女生学防身:挥\"匕首\"喊\"杀\"","imgsrc":"bigimg","url":"00AP0001|2296443"},{"subtitle":"","skipType":"photoset","skipID":"00AP0001|2296436","tag":"photoset","title":"\"山竹\"影响广州街区 商户举家搬运货物","imgsrc":"bigimg","url":"00AP0001|2296436"},{"subtitle":"","skipType":"photoset","skipID":"00AO0001|2296434","tag":"photoset","title":"文在寅与金正恩在百花园国宾馆热聊","imgsrc":"bigimg","url":"00AO0001|2296434"},{"subtitle":"","skipType":"photoset","skipID":"00AN0001|2296432","tag":"photoset","title":"纪念\"九一八\" 市民参观大屠杀纪念馆","imgsrc":"bigimg","url":"00AN0001|2296432"}]
     * ename : androidnews
     * replyCount : 151
     * imgsum : 5
     * hasIcon : false
     * skipType : photoset
     * cid : C1348646712614
     * url_3w : http://news.163.com/18/0919/09/DS2CA63J000189FH.html
     * url : http://3g.163.com/news/18/0919/09/DS2CA63J000189FH.html
     * ltitle : 习近平这些贺信 与未来紧密关联
     * subtitle :
     * pixel : 660*380
     * specialextra : [{"votecount":0,"docid":"DS2G60JT0001875O","url_3w":"http://news.163.com/18/0919/10/DS2G60JT0001875O.html","source":"澎湃新闻","postid":"DS2G60JT0001875O","priority":78,"title":"金正恩：将尽快访问首尔 消除一切引发战争的因素","mtime":"2018-09-19 11:07:46","url":"http://3g.163.com/news/18/0919/10/DS2G60JT0001875O.html","replyCount":0,"ltitle":"金正恩：将尽快访问首尔 消除一切引发战争的因素","subtitle":"","digest":"朝韩首脑峰会官网的直播画面显示，19日上午，在结束与韩国总统文在寅的会谈并签署《平壤共同宣言》后，朝鲜最高领导人金正恩与文在寅共同举行联合记者会。这也是金正恩首","boardid":"news_guoji2_bbs","imgsrc":"http://cms-bucket.nosdn.127.net/2018/09/19/07e18543ce7e43eabf227665c7026ced.jpeg","ptime":"2018-09-19 10:50:53","daynum":"17793"},{"votecount":0,"docid":"DS2GPS590001875O","url_3w":"http://news.163.com/18/0919/11/DS2GPS590001875O.html","source":"海外网","postid":"DS2GPS590001875O","priority":77,"title":"朝韩商定废除宁边核设施和东仓里导弹发射基地","mtime":"2018-09-19 11:08:44","url":"http://3g.163.com/news/18/0919/11/DS2GPS590001875O.html","replyCount":0,"ltitle":"朝韩商定废除宁边核设施和东仓里导弹发射基地","subtitle":"","digest":"19日上午，朝韩领导人签署《平壤共同宣言》，并联合召开记者会发表协商成果。文在寅介绍称，朝韩商定永久废除朝鲜宁边核设施和东仓里导弹发射基地。","boardid":"news_guoji2_bbs","imgsrc":"http://cms-bucket.nosdn.127.net/2018/09/19/4bf3bdf56a834eddaa0d8088a53aabd7.png","ptime":"2018-09-19 11:01:43","daynum":"17793"},{"votecount":0,"docid":"DS2H17N40001875O","url_3w":"http://news.163.com/18/0919/11/DS2H17N40001875O.html","source":"环球时报-环球网","postid":"DS2H17N40001875O","priority":76,"title":"文在寅：将推进朝韩共同举办2032年夏季奥运会","mtime":"2018-09-19 11:08:52","url":"http://3g.163.com/news/18/0919/11/DS2H17N40001875O.html","replyCount":0,"ltitle":"文在寅：将推进朝韩共同举办2032年夏季奥运会","subtitle":"","digest":"19日上午，在结束与韩国总统文在寅的会谈并签署《平壤共同宣言》后，朝鲜最高领导人金正恩与文在寅共同举行联合记者会。金正恩表示，自己将于近期访问首尔。金正恩在记者","boardid":"news_guoji2_bbs","imgsrc":"http://cms-bucket.nosdn.127.net/2018/09/19/70100411fc8e483e99fd155296ba62a3.png","ptime":"2018-09-19 11:05:45","daynum":"17793"}]
     * specialtip : 进入专题
     * specialID : S1534149887897
     * speciallogo :
     * specialadlogo :
     */

    private String template;
    private String skipID;
    private String lmodify;
    private String postid;
    private String source;
    private String title;
    private String mtime;
    private int hasImg;
    private String topic_background;
    private String digest;
    private String photosetID;
    private String boardid;
    private String alias;
    private int hasAD;
    private String imgsrc;
    private String ptime;
    private String daynum;
    private int hasHead;
    private int imgType;
    private int order;
    private String articleType;
    private int votecount;
    private boolean hasCover;
    private String docid;
    private String tname;
    private int priority;
    private String ename;
    private int replyCount;
    private int imgsum;
    private boolean hasIcon;
    private String skipType;
    private String cid;
    private String url_3w;
    private String url;
    private String ltitle;
    private String subtitle;
    private String pixel;
    private String specialtip;
    private String specialID;
    private String speciallogo;
    private String specialadlogo;
    private String TAG;
    private String TAGS;
    private List<?> editor;
    private List<AdsBean> ads;
    private List<SpecialextraBean> specialextra;

    public String getTAG() {
        return TAG;
    }

    public void setTAG(String TAG) {
        this.TAG = TAG;
    }

    public String getTAGS() {
        return TAGS;
    }

    public void setTAGS(String TAGS) {
        this.TAGS = TAGS;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getSkipID() {
        return skipID;
    }

    public void setSkipID(String skipID) {
        this.skipID = skipID;
    }

    public String getLmodify() {
        return lmodify;
    }

    public void setLmodify(String lmodify) {
        this.lmodify = lmodify;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMtime() {
        return mtime;
    }

    public void setMtime(String mtime) {
        this.mtime = mtime;
    }

    public int getHasImg() {
        return hasImg;
    }

    public void setHasImg(int hasImg) {
        this.hasImg = hasImg;
    }

    public String getTopic_background() {
        return topic_background;
    }

    public void setTopic_background(String topic_background) {
        this.topic_background = topic_background;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public String getPhotosetID() {
        return photosetID;
    }

    public void setPhotosetID(String photosetID) {
        this.photosetID = photosetID;
    }

    public String getBoardid() {
        return boardid;
    }

    public void setBoardid(String boardid) {
        this.boardid = boardid;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getHasAD() {
        return hasAD;
    }

    public void setHasAD(int hasAD) {
        this.hasAD = hasAD;
    }

    public String getImgsrc() {
        return imgsrc;
    }

    public void setImgsrc(String imgsrc) {
        this.imgsrc = imgsrc;
    }

    public String getPtime() {
        return ptime;
    }

    public void setPtime(String ptime) {
        this.ptime = ptime;
    }

    public String getDaynum() {
        return daynum;
    }

    public void setDaynum(String daynum) {
        this.daynum = daynum;
    }

    public int getHasHead() {
        return hasHead;
    }

    public void setHasHead(int hasHead) {
        this.hasHead = hasHead;
    }

    public int getImgType() {
        return imgType;
    }

    public void setImgType(int imgType) {
        this.imgType = imgType;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getVotecount() {
        return votecount;
    }

    public void setVotecount(int votecount) {
        this.votecount = votecount;
    }

    public boolean isHasCover() {
        return hasCover;
    }

    public void setHasCover(boolean hasCover) {
        this.hasCover = hasCover;
    }

    public String getDocid() {
        return docid;
    }

    public void setDocid(String docid) {
        this.docid = docid;
    }

    public String getTname() {
        return tname;
    }

    public void setTname(String tname) {
        this.tname = tname;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getEname() {
        return ename;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public int getImgsum() {
        return imgsum;
    }

    public void setImgsum(int imgsum) {
        this.imgsum = imgsum;
    }

    public boolean isHasIcon() {
        return hasIcon;
    }

    public void setHasIcon(boolean hasIcon) {
        this.hasIcon = hasIcon;
    }

    public String getSkipType() {
        return skipType;
    }

    public void setSkipType(String skipType) {
        this.skipType = skipType;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getUrl_3w() {
        return url_3w;
    }

    public void setUrl_3w(String url_3w) {
        this.url_3w = url_3w;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLtitle() {
        return ltitle;
    }

    public void setLtitle(String ltitle) {
        this.ltitle = ltitle;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getPixel() {
        return pixel;
    }

    public void setPixel(String pixel) {
        this.pixel = pixel;
    }

    public String getSpecialtip() {
        return specialtip;
    }

    public void setSpecialtip(String specialtip) {
        this.specialtip = specialtip;
    }

    public String getSpecialID() {
        return specialID;
    }

    public void setSpecialID(String specialID) {
        this.specialID = specialID;
    }

    public String getSpeciallogo() {
        return speciallogo;
    }

    public void setSpeciallogo(String speciallogo) {
        this.speciallogo = speciallogo;
    }

    public String getSpecialadlogo() {
        return specialadlogo;
    }

    public void setSpecialadlogo(String specialadlogo) {
        this.specialadlogo = specialadlogo;
    }

    public List<?> getEditor() {
        return editor;
    }

    public void setEditor(List<?> editor) {
        this.editor = editor;
    }

    public List<AdsBean> getAds() {
        return ads;
    }

    public void setAds(List<AdsBean> ads) {
        this.ads = ads;
    }

    public List<SpecialextraBean> getSpecialextra() {
        return specialextra;
    }

    public void setSpecialextra(List<SpecialextraBean> specialextra) {
        this.specialextra = specialextra;
    }

    public String getArticleType() {
        return articleType;
    }

    public void setArticleType(String articleType) {
        this.articleType = articleType;
    }

    public static class AdsBean {
        /**
         * subtitle :
         * skipType : photoset
         * skipID : 00AN0001|2296444
         * tag : photoset
         * title : 海南发放首批港澳台居民居住证
         * imgsrc : bigimg
         * url : 00AN0001|2296444
         */

        private String subtitle;
        private String skipType;
        private String skipID;
        private String tag;
        private String title;
        private String imgsrc;
        private String url;

        public String getSubtitle() {
            return subtitle;
        }

        public void setSubtitle(String subtitle) {
            this.subtitle = subtitle;
        }

        public String getSkipType() {
            return skipType;
        }

        public void setSkipType(String skipType) {
            this.skipType = skipType;
        }

        public String getSkipID() {
            return skipID;
        }

        public void setSkipID(String skipID) {
            this.skipID = skipID;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getImgsrc() {
            return imgsrc;
        }

        public void setImgsrc(String imgsrc) {
            this.imgsrc = imgsrc;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class SpecialextraBean {
        /**
         * votecount : 0
         * docid : DS2G60JT0001875O
         * url_3w : http://news.163.com/18/0919/10/DS2G60JT0001875O.html
         * source : 澎湃新闻
         * postid : DS2G60JT0001875O
         * priority : 78
         * title : 金正恩：将尽快访问首尔 消除一切引发战争的因素
         * mtime : 2018-09-19 11:07:46
         * url : http://3g.163.com/news/18/0919/10/DS2G60JT0001875O.html
         * replyCount : 0
         * ltitle : 金正恩：将尽快访问首尔 消除一切引发战争的因素
         * subtitle :
         * digest : 朝韩首脑峰会官网的直播画面显示，19日上午，在结束与韩国总统文在寅的会谈并签署《平壤共同宣言》后，朝鲜最高领导人金正恩与文在寅共同举行联合记者会。这也是金正恩首
         * boardid : news_guoji2_bbs
         * imgsrc : http://cms-bucket.nosdn.127.net/2018/09/19/07e18543ce7e43eabf227665c7026ced.jpeg
         * ptime : 2018-09-19 10:50:53
         * daynum : 17793
         */

        private int votecount;
        private String docid;
        private String url_3w;
        private String source;
        private String postid;
        private int priority;
        private String title;
        private String mtime;
        private String url;
        private int replyCount;
        private String ltitle;
        private String subtitle;
        private String digest;
        private String boardid;
        private String imgsrc;
        private String ptime;
        private String daynum;
        private String TAG;
        private String TAGS;

        private boolean isReaded;

        public boolean isReaded() {
            return isReaded;
        }

        public void setReaded(boolean readed) {
            isReaded = readed;
        }

        public String getTAG() {
            return TAG;
        }

        public void setTAG(String TAG) {
            this.TAG = TAG;
        }

        public String getTAGS() {
            return TAGS;
        }

        public void setTAGS(String TAGS) {
            this.TAGS = TAGS;
        }

        public int getVotecount() {
            return votecount;
        }

        public void setVotecount(int votecount) {
            this.votecount = votecount;
        }

        public String getDocid() {
            return docid;
        }

        public void setDocid(String docid) {
            this.docid = docid;
        }

        public String getUrl_3w() {
            return url_3w;
        }

        public void setUrl_3w(String url_3w) {
            this.url_3w = url_3w;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getPostid() {
            return postid;
        }

        public void setPostid(String postid) {
            this.postid = postid;
        }

        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getMtime() {
            return mtime;
        }

        public void setMtime(String mtime) {
            this.mtime = mtime;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getReplyCount() {
            return replyCount;
        }

        public void setReplyCount(int replyCount) {
            this.replyCount = replyCount;
        }

        public String getLtitle() {
            return ltitle;
        }

        public void setLtitle(String ltitle) {
            this.ltitle = ltitle;
        }

        public String getSubtitle() {
            return subtitle;
        }

        public void setSubtitle(String subtitle) {
            this.subtitle = subtitle;
        }

        public String getDigest() {
            return digest;
        }

        public void setDigest(String digest) {
            this.digest = digest;
        }

        public String getBoardid() {
            return boardid;
        }

        public void setBoardid(String boardid) {
            this.boardid = boardid;
        }

        public String getImgsrc() {
            return imgsrc;
        }

        public void setImgsrc(String imgsrc) {
            this.imgsrc = imgsrc;
        }

        public String getPtime() {
            return ptime;
        }

        public void setPtime(String ptime) {
            this.ptime = ptime;
        }

        public String getDaynum() {
            return daynum;
        }

        public void setDaynum(String daynum) {
            this.daynum = daynum;
        }
    }
}

