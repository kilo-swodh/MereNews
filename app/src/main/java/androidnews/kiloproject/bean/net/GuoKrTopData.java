package androidnews.kiloproject.bean.net;

import java.util.List;

public class GuoKrTopData {

    /**
     * now : 2018-10-14T16:09:26.548152+08:00
     * ok : true
     * result : [{"ordinal":0,"picture":"http://1-im.guokr.com/2fcna6c3sDa_5x5bXYuY3PY5ckwSWgjEAYpDogxA-IHcBQAAsAMAAEpQ.jpg","custom_title":"果壳年度最重磅活动来袭！一场\u201c科技与博物\u201d的异想狂欢","article_id":85799},{"ordinal":1,"picture":"https://2-im.guokr.com/wDkebsB5SwaCQT3rviOWJId9Ab4YD1xX-dxQKp06_VzoAwAABQIAAEpQ.jpg","custom_title":"为了享受二人世界，我们都发明了哪些\"第三者\"？","article_id":null},{"ordinal":2,"picture":"https://2-im.guokr.com/ALkqch4rYggYGhUmKfgVqBR5rIvy_0ZekProzXMY4Q8dAwAARQEAAEpQ.jpg","custom_title":"你在网的任何痕迹，都能忽悠你买买买","article_id":85705},{"ordinal":3,"picture":"https://2-im.guokr.com/e9sD_50UGcnjMymqd65H5ZuH54fZ33W7otHWYZJ_Pe9gAwAARAEAAEpQ.jpg?imageView2/1/w/555/h/208","custom_title":"2亿年前的\u201c色诱\u201d，可以说是相当惊艳了","article_id":85434},{"ordinal":4,"picture":"https://3-im.guokr.com/73qqTiqyKB590j1Li_EIWaj-4x88u9_iZIw4AnKU23X0AQAA-gAAAEdJ.gif","custom_title":"不幸者和幸运儿之间的差距竟然是这个？","article_id":85405}]
     */

    private String now;
    private boolean ok;
    private List<ResultBean> result;

    public String getNow() {
        return now;
    }

    public void setNow(String now) {
        this.now = now;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * ordinal : 0
         * picture : http://1-im.guokr.com/2fcna6c3sDa_5x5bXYuY3PY5ckwSWgjEAYpDogxA-IHcBQAAsAMAAEpQ.jpg
         * custom_title : 果壳年度最重磅活动来袭！一场“科技与博物”的异想狂欢
         * article_id : 85799
         */

        private int ordinal;
        private String picture;
        private String custom_title;
        private int article_id;

        public int getOrdinal() {
            return ordinal;
        }

        public void setOrdinal(int ordinal) {
            this.ordinal = ordinal;
        }

        public String getPicture() {
            return picture;
        }

        public void setPicture(String picture) {
            this.picture = picture;
        }

        public String getCustom_title() {
            return custom_title;
        }

        public void setCustom_title(String custom_title) {
            this.custom_title = custom_title;
        }

        public int getArticle_id() {
            return article_id;
        }

        public void setArticle_id(int article_id) {
            this.article_id = article_id;
        }
    }
}
