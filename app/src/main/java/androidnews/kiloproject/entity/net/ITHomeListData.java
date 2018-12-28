package androidnews.kiloproject.entity.net;

import java.util.ArrayList;
import java.util.List;

public class ITHomeListData {

    private List<ItemBean> channel = new ArrayList<>();

    public List<ItemBean> getChannel() {
        return channel;
    }

    public void setChannel(List<ItemBean> channel) {
        this.channel = channel;
    }

    public static class ItemBean {
        private String newsid;
        private String title;
        private String url;
        private String postdate;
        private String image;
        private String description;

        private boolean isReaded;

        public boolean isReaded() {
            return isReaded;
        }

        public void setReaded(boolean readed) {
            isReaded = readed;
        }

        public String getNewsid() {
            return newsid;
        }

        public void setNewsid(String newsid) {
            this.newsid = newsid;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getPostdate() {
            return postdate;
        }

        public void setPostdate(String postdate) {
            this.postdate = postdate;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

    }
}
