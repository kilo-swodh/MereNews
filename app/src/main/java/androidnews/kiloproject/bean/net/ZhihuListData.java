package androidnews.kiloproject.bean.net;

import java.util.List;

public class ZhihuListData {

    /**
     * date : 20181013
     * stories : [{"images":["https://pic1.zhimg.com/v2-31a1cdc66b7200379f4391224f77f31c.jpg"],"type":0,"id":9698284,"ga_prefix":"101319","title":"买了衣服愁没裤子搭，买了裤子愁没鞋子搭，真烦"},{"images":["https://pic3.zhimg.com/v2-0b2f34c919beca5865cc5d19bf432746.jpg"],"type":0,"id":9697180,"ga_prefix":"101318","title":"从设计到装修，这些事悔得我肠子都紫了"},{"images":["https://pic2.zhimg.com/v2-6542e05008e296918e0dd1e774229821.jpg"],"type":0,"id":9698306,"ga_prefix":"101317","title":"人为啥会突然炸毛？"},{"images":["https://pic2.zhimg.com/v2-492d81c412f6cb19b224cd2ae35543bd.jpg"],"type":0,"id":9698312,"ga_prefix":"101315","title":"父母对你好不好，先来后到很重要"},{"title":"每周一吸 · 麻烦给我来只这样的","ga_prefix":"101313","images":["https://pic3.zhimg.com/v2-afe96413827d4ed308faf92ac928d3de.jpg"],"multipic":true,"type":0,"id":9698429},{"images":["https://pic1.zhimg.com/v2-efb765691a5ac64da3eb3b3f62a645dc.jpg"],"type":0,"id":9698334,"ga_prefix":"101312","title":"大误 · 范进中了 SCI"},{"images":["https://pic1.zhimg.com/v2-5bd5afbc9ef8909a3338a744d668d018.jpg"],"type":0,"id":9698247,"ga_prefix":"101310","title":"如何提升自己的文笔？"},{"images":["https://pic2.zhimg.com/v2-9fd2fd2b88e9ea233ba2583401132861.jpg"],"type":0,"id":9698264,"ga_prefix":"101308","title":"给薛定谔加猫？量子力学思想实验越来越诡异了\u2026\u2026"},{"images":["https://pic2.zhimg.com/v2-94ab388c18ded3a71f1fd1adb39182e1.jpg"],"type":0,"id":9698252,"ga_prefix":"101307","title":"甘肃人、青海人，你们是最接近欧洲人的「混血儿」"},{"images":["https://pic4.zhimg.com/v2-3f1f7303db24ef0cd73c4d4277fbff03.jpg"],"type":0,"id":9698436,"ga_prefix":"101307","title":"郭德纲的庙堂与江湖"},{"images":["https://pic2.zhimg.com/v2-740baec599d5bbb5d318a780a6308f4d.jpg"],"type":0,"id":9698344,"ga_prefix":"101306","title":"瞎扯 · 如何正确地吐槽"}]
     * top_stories : [{"image":"https://pic3.zhimg.com/v2-00f0fb7c9d1d0fd0ab45c3f7482a06d6.jpg","type":0,"id":9698252,"ga_prefix":"101307","title":"甘肃人、青海人，你们是最接近欧洲人的「混血儿」"},{"image":"https://pic4.zhimg.com/v2-9a58467952a372af82f69b0f17ed59b3.jpg","type":0,"id":9698436,"ga_prefix":"101307","title":"郭德纲的庙堂与江湖"},{"image":"https://pic3.zhimg.com/v2-4545dbf13bcd1b81d7310752d63629d6.jpg","type":0,"id":9698420,"ga_prefix":"101216","title":"真没想到，S8 刚开始打小组赛，团战就如此精彩"},{"image":"https://pic3.zhimg.com/v2-ceeeec32cab6a80c2301c0da6e756e86.jpg","type":0,"id":9698271,"ga_prefix":"101208","title":"有的猫说不出哪里好，但他妹就是替代不了"},{"image":"https://pic2.zhimg.com/v2-001afa57d487b9e074d3e4275471f245.jpg","type":0,"id":9698238,"ga_prefix":"101123","title":"年轻人，还没睡吧？"}]
     */

    private String date;
    private List<StoriesBean> stories;
    private List<TopStoriesBean> top_stories;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<StoriesBean> getStories() {
        return stories;
    }

    public void setStories(List<StoriesBean> stories) {
        this.stories = stories;
    }

    public List<TopStoriesBean> getTop_stories() {
        return top_stories;
    }

    public void setTop_stories(List<TopStoriesBean> top_stories) {
        this.top_stories = top_stories;
    }

    public static class StoriesBean {
        /**
         * images : ["https://pic1.zhimg.com/v2-31a1cdc66b7200379f4391224f77f31c.jpg"]
         * type : 0
         * id : 9698284
         * ga_prefix : 101319
         * title : 买了衣服愁没裤子搭，买了裤子愁没鞋子搭，真烦
         * multipic : true
         */

        private boolean isReaded;

        public boolean isReaded() {
            return isReaded;
        }

        public void setReaded(boolean readed) {
            isReaded = readed;
        }
        private int type;
        private int id;
        private String ga_prefix;
        private String title;
        private boolean multipic;
        private List<String> images;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getGa_prefix() {
            return ga_prefix;
        }

        public void setGa_prefix(String ga_prefix) {
            this.ga_prefix = ga_prefix;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public boolean isMultipic() {
            return multipic;
        }

        public void setMultipic(boolean multipic) {
            this.multipic = multipic;
        }

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }
    }

    public static class TopStoriesBean {
        /**
         * image : https://pic3.zhimg.com/v2-00f0fb7c9d1d0fd0ab45c3f7482a06d6.jpg
         * type : 0
         * id : 9698252
         * ga_prefix : 101307
         * title : 甘肃人、青海人，你们是最接近欧洲人的「混血儿」
         */

        private String image;
        private int type;
        private int id;
        private String ga_prefix;
        private String title;

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getGa_prefix() {
            return ga_prefix;
        }

        public void setGa_prefix(String ga_prefix) {
            this.ga_prefix = ga_prefix;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
