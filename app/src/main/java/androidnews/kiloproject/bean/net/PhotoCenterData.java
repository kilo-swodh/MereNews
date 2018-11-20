package androidnews.kiloproject.bean.net;

import java.util.List;

public class PhotoCenterData {

  private List<CacheMoreDataBean> cacheMoreData;

  public List<CacheMoreDataBean> getCacheMoreData() {
    return cacheMoreData;
  }

  public void setCacheMoreData(List<CacheMoreDataBean> cacheMoreData) {
    this.cacheMoreData = cacheMoreData;
  }

  public static class CacheMoreDataBean {
    /**
     * desc : 当地时间9月20日上午，一架载有166名乘客的印度捷特航空班机从孟买起飞后不久，出现了多名乘客不适的状况，其中30人出现了耳鼻流血症状。图为旅客拍摄的客机机舱内，氧气面罩垂落下来供乘客使用。
     * pvnum :
     * createdate : 2018-09-21 19:30:56
     * scover : http://pic-bucket.nosdn.127.net/photo/0001/2018-09-21/DS8IO40J00AO0001NOS.jpg?imageView&thumbnail=100y75
     * setname : 印度一航班多名乘客不适 30余人耳鼻流血
     * cover : http://pic-bucket.nosdn.127.net/photo/0001/2018-09-21/DS8IO40J00AO0001NOS.jpg
     * pics : []
     * clientcover1 :
     * replynum : 53
     * topicname :
     * setid : 2296507
     * seturl : http://news.163.com/photoview/00AO0001/2296507.html
     * datetime : 2018-09-21 19:32:51
     * clientcover :
     * imgsum : 4
     * tcover : http://pic-bucket.nosdn.127.net/photo/0001/2018-09-21/DS8IO40J00AO0001NOS.jpg?imageView&thumbnail=160y120
     */

    private String desc;
    private String pvnum;
    private String createdate;
    private String scover;
    private String setname;
    private String cover;
    private String clientcover1;
    private String replynum;
    private String topicname;
    private String setid;
    private String seturl;
    private String datetime;
    private String clientcover;
    private String imgsum;
    private String tcover;
    private List<?> pics;

    public String getDesc() {
      return desc;
    }

    public void setDesc(String desc) {
      this.desc = desc;
    }

    public String getPvnum() {
      return pvnum;
    }

    public void setPvnum(String pvnum) {
      this.pvnum = pvnum;
    }

    public String getCreatedate() {
      return createdate;
    }

    public void setCreatedate(String createdate) {
      this.createdate = createdate;
    }

    public String getScover() {
      return scover;
    }

    public void setScover(String scover) {
      this.scover = scover;
    }

    public String getSetname() {
      return setname;
    }

    public void setSetname(String setname) {
      this.setname = setname;
    }

    public String getCover() {
      return cover;
    }

    public void setCover(String cover) {
      this.cover = cover;
    }

    public String getClientcover1() {
      return clientcover1;
    }

    public void setClientcover1(String clientcover1) {
      this.clientcover1 = clientcover1;
    }

    public String getReplynum() {
      return replynum;
    }

    public void setReplynum(String replynum) {
      this.replynum = replynum;
    }

    public String getTopicname() {
      return topicname;
    }

    public void setTopicname(String topicname) {
      this.topicname = topicname;
    }

    public String getSetid() {
      return setid;
    }

    public void setSetid(String setid) {
      this.setid = setid;
    }

    public String getSeturl() {
      return seturl;
    }

    public void setSeturl(String seturl) {
      this.seturl = seturl;
    }

    public String getDatetime() {
      return datetime;
    }

    public void setDatetime(String datetime) {
      this.datetime = datetime;
    }

    public String getClientcover() {
      return clientcover;
    }

    public void setClientcover(String clientcover) {
      this.clientcover = clientcover;
    }

    public String getImgsum() {
      return imgsum;
    }

    public void setImgsum(String imgsum) {
      this.imgsum = imgsum;
    }

    public String getTcover() {
      return tcover;
    }

    public void setTcover(String tcover) {
      this.tcover = tcover;
    }

    public List<?> getPics() {
      return pics;
    }

    public void setPics(List<?> pics) {
      this.pics = pics;
    }
  }
}
