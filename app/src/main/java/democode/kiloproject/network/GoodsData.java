package democode.kiloproject.network;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GoodsData {

    /**
     * sgrade : [{"key":"1","value":"指定专营店"},{"key":"2","value":"合作专供店"},{"key":"3","value":"运营中心"},{"key":"4","value":"网店"},{"key":"5","value":"品牌店"}]
     * year : [{"key":"20163","value":"2016年秋季"},{"key":"20164","value":"2016年冬季"},{"key":"20161","value":"2016年春季"},{"key":"20171","value":"2017年春"},{"key":"20173","value":"2017年秋季"}]
     * orders : {"add_time desc":"add_time_desc","price asc":"price_asc","price desc":"price_desc"}
     * result : [{"goods_id":"637752","fac_goods_id":"16269","store_id":"4075","type":"material","goods_name":"衬衫男韩版秋季新品男士长袖纯棉纯色青年时尚休闲修身立领衬衣","cate_id":"39","cate_name":"男装\t上装\t衬衫","brand":"","year":"2015","spec_qty":"2","spec_name_1":"颜色","spec_name_2":"尺码","if_show":"1","closed":"0","add_time":"1515963277","recommended":"1","default_image":"data/files/store_4030/goods_30/201801151250302079.jpg?x-oss-process=image/resize,h_300,w_300","spec_id":"811331","spec_1":"黑色","spec_2":"M","color_rgb":"","price":"120.00","stock":"999","store_name":"时尚圈商城（男爵）男装合作专供店","region_id":"610304003","region_name":"陕西省\t宝鸡市\t陈仓区\t千渭街道","credit_value":"0","sgrade":"2","pvs":null,"views":"16","sales":"0","comments":"0"},{"goods_id":"636751","fac_goods_id":"16269","store_id":"4067","type":"material","goods_name":"衬衫男韩版秋季新品男士长袖纯棉纯色青年时尚休闲修身立领衬衣","cate_id":"39","cate_name":"男装\t上装\t衬衫","brand":"","year":"2015","spec_qty":"2","spec_name_1":"颜色","spec_name_2":"尺码","if_show":"1","closed":"0","add_time":"1515963277","recommended":"1","default_image":"data/files/store_4030/goods_30/201801151250302079.jpg?x-oss-process=image/resize,h_300,w_300","spec_id":"799322","spec_1":"黑色","spec_2":"M","color_rgb":"","price":"120.00","stock":"999","store_name":"时尚圈商城（格调）男装合作专供店","region_id":"610327100","region_name":"陕西省\t宝鸡市\t陇县\t城关镇","credit_value":"0","sgrade":"2","pvs":null,"views":"29","sales":"0","comments":"0"},{"goods_id":"637075","fac_goods_id":"16269","store_id":"4069","type":"material","goods_name":"衬衫男韩版秋季新品男士长袖纯棉纯色青年时尚休闲修身立领衬衣","cate_id":"41","cate_name":"男装\t上装\t毛衣","brand":"","year":"2015","spec_qty":"2","spec_name_1":"颜色","spec_name_2":"尺码","if_show":"1","closed":"0","add_time":"1515963277","recommended":"1","default_image":"data/files/store_4030/goods_30/201801151250302079.jpg?x-oss-process=image/resize,h_300,w_300","spec_id":"802857","spec_1":"黑色","spec_2":"M","color_rgb":"","price":"120.00","stock":"999","store_name":"时尚圈商城   男装指定专营店","region_id":"610322","region_name":"陕西省\t宝鸡市\t凤翔县","credit_value":"0","sgrade":"1","pvs":null,"views":"30","sales":"0","comments":"0"},{"goods_id":"637780","fac_goods_id":"16269","store_id":"4073","type":"material","goods_name":"衬衫男韩版秋季新品男士长袖纯棉纯色青年时尚休闲修身立领衬衣","cate_id":"39","cate_name":"男装\t上装\t衬衫","brand":"","year":"2015","spec_qty":"2","spec_name_1":"颜色","spec_name_2":"尺码","if_show":"1","closed":"0","add_time":"1515963277","recommended":"1","default_image":"data/files/store_4030/goods_30/201801151250302079.jpg?x-oss-process=image/resize,h_300,w_300","spec_id":"811810","spec_1":"黑色","spec_2":"M","color_rgb":"","price":"120.00","stock":"999","store_name":"时尚圈商城（迪克斯）男装合作专供店","region_id":"610328","region_name":"陕西省\t宝鸡市\t千阳县","credit_value":"0","sgrade":"2","pvs":null,"views":"18","sales":"0","comments":"0"},{"goods_id":"638043","fac_goods_id":"16269","store_id":"4079","type":"material","goods_name":"衬衫男韩版秋季新品男士长袖纯棉纯色青年时尚休闲修身立领衬衣","cate_id":"39","cate_name":"男装\t上装\t衬衫","brand":"","year":"2015","spec_qty":"2","spec_name_1":"颜色","spec_name_2":"尺码","if_show":"1","closed":"0","add_time":"1515963277","recommended":"1","default_image":"data/files/store_4030/goods_30/201801151250302079.jpg?x-oss-process=image/resize,h_300,w_300","spec_id":"815678","spec_1":"黑色","spec_2":"M","color_rgb":"","price":"120.00","stock":"999","store_name":"时尚圈商城（华坤）男装合作专供店","region_id":"610331","region_name":"陕西省\t宝鸡市\t太白县","credit_value":"0","sgrade":"2","pvs":null,"views":"22","sales":"0","comments":"0"},{"goods_id":"639709","fac_goods_id":"16269","store_id":"4104","type":"material","goods_name":"衬衫男韩版秋季新品男士长袖纯棉纯色青年时尚休闲修身立领衬衣","cate_id":"39","cate_name":"男装\t上装\t衬衫","brand":"","year":"2015","spec_qty":"2","spec_name_1":"颜色","spec_name_2":"尺码","if_show":"1","closed":"0","add_time":"1515963277","recommended":"1","default_image":"data/files/store_4030/goods_30/201801151250302079.jpg?x-oss-process=image/resize,h_300,w_300","spec_id":"829105","spec_1":"黑色","spec_2":"M","color_rgb":"","price":"120.00","stock":"999","store_name":"时尚圈商城（聚焦）男装合作专供店","region_id":"610323","region_name":"陕西省\t宝鸡市\t岐山县","credit_value":"0","sgrade":"2","pvs":null,"views":"24","sales":"0","comments":"0"},{"goods_id":"637753","fac_goods_id":"16229","store_id":"4075","type":"material","goods_name":"春秋新款卫衣男士韩版休闲外套青年修身圆领拼色长袖男式套头卫衣","cate_id":"41","cate_name":"男装\t上装\t毛衣","brand":"","year":"2015","spec_qty":"2","spec_name_1":"颜色","spec_name_2":"尺码","if_show":"1","closed":"0","add_time":"1515954689","recommended":"1","default_image":"data/files/store_4030/goods_188/201801151026289309.jpg?x-oss-process=image/resize,h_300,w_300","spec_id":"811351","spec_1":"蓝色","spec_2":"M","color_rgb":"","price":"64.00","stock":"999","store_name":"时尚圈商城（男爵）男装合作专供店","region_id":"610304003","region_name":"陕西省\t宝鸡市\t陈仓区\t千渭街道","credit_value":"0","sgrade":"2","pvs":null,"views":"23","sales":"0","comments":"0"},{"goods_id":"637077","fac_goods_id":"16229","store_id":"4069","type":"material","goods_name":"春秋新款卫衣男士韩版休闲外套青年修身圆领拼色长袖男式套头卫衣","cate_id":"41","cate_name":"男装\t上装\t毛衣","brand":"","year":"2015","spec_qty":"2","spec_name_1":"颜色","spec_name_2":"尺码","if_show":"1","closed":"0","add_time":"1515954689","recommended":"1","default_image":"data/files/store_4030/goods_188/201801151026289309.jpg?x-oss-process=image/resize,h_300,w_300","spec_id":"802881","spec_1":"蓝色","spec_2":"M","color_rgb":"","price":"64.00","stock":"999","store_name":"时尚圈商城   男装指定专营店","region_id":"610322","region_name":"陕西省\t宝鸡市\t凤翔县","credit_value":"0","sgrade":"1","pvs":null,"views":"17","sales":"0","comments":"0"},{"goods_id":"638057","fac_goods_id":"16229","store_id":"4079","type":"material","goods_name":"春秋新款卫衣男士韩版休闲外套青年修身圆领拼色长袖男式套头卫衣","cate_id":"4043","cate_name":"男装\t上装\t针织衫","brand":"","year":"2015","spec_qty":"2","spec_name_1":"颜色","spec_name_2":"尺码","if_show":"1","closed":"0","add_time":"1515954689","recommended":"1","default_image":"data/files/store_4030/goods_188/201801151026289309.jpg?x-oss-process=image/resize,h_300,w_300","spec_id":"815735","spec_1":"蓝色","spec_2":"M","color_rgb":"","price":"64.00","stock":"999","store_name":"时尚圈商城（华坤）男装合作专供店","region_id":"610331","region_name":"陕西省\t宝鸡市\t太白县","credit_value":"0","sgrade":"2","pvs":null,"views":"20","sales":"0","comments":"0"},{"goods_id":"639696","fac_goods_id":"16229","store_id":"4104","type":"material","goods_name":"春秋新款卫衣男士韩版休闲外套青年修身圆领拼色长袖男式套头卫衣","cate_id":"40","cate_name":"男装\t上装\tPOLO衫","brand":"","year":"2015","spec_qty":"2","spec_name_1":"颜色","spec_name_2":"尺码","if_show":"1","closed":"0","add_time":"1515954689","recommended":"1","default_image":"data/files/store_4030/goods_188/201801151026289309.jpg?x-oss-process=image/resize,h_300,w_300","spec_id":"829012","spec_1":"蓝色","spec_2":"M","color_rgb":"","price":"64.00","stock":"999","store_name":"时尚圈商城（聚焦）男装合作专供店","region_id":"610323","region_name":"陕西省\t宝鸡市\t岐山县","credit_value":"0","sgrade":"2","pvs":null,"views":"21","sales":"0","comments":"0"},{"goods_id":"647009","fac_goods_id":"16229","store_id":"4181","type":"material","goods_name":"春秋新款卫衣男士韩版休闲外套青年修身圆领拼色长袖男式套头卫衣","cate_id":"42","cate_name":"男装\t上装\t卫衣","brand":"","year":"2015","spec_qty":"2","spec_name_1":"颜色","spec_name_2":"尺码","if_show":"1","closed":"0","add_time":"1515954689","recommended":"1","default_image":"data/files/store_4030/goods_188/201801151026289309.jpg?x-oss-process=image/resize,h_300,w_300","spec_id":"890741","spec_1":"蓝色","spec_2":"M","color_rgb":"","price":"64.00","stock":"999","store_name":"时尚圈商城  男装指定专营店","region_id":"610331","region_name":"陕西省\t宝鸡市\t太白县","credit_value":"0","sgrade":"1","pvs":null,"views":"32","sales":"0","comments":"0"},{"goods_id":"637754","fac_goods_id":"16105","store_id":"4075","type":"material","goods_name":"新款长袖T恤男 圆领修身上衣韩版秋衣男士卫衣青少年打底衫","cate_id":"41","cate_name":"男装\t上装\t毛衣","brand":"","year":"2015","spec_qty":"2","spec_name_1":"颜色","spec_name_2":"尺码","if_show":"1","closed":"0","add_time":"1515817559","recommended":"1","default_image":"data/files/store_4030/goods_72/201801130421129392.jpg?x-oss-process=image/resize,h_300,w_300","spec_id":"811381","spec_1":"白色","spec_2":"M","color_rgb":"","price":"50.00","stock":"999","store_name":"时尚圈商城（男爵）男装合作专供店","region_id":"610304003","region_name":"陕西省\t宝鸡市\t陈仓区\t千渭街道","credit_value":"0","sgrade":"2","pvs":null,"views":"25","sales":"0","comments":"0"},{"goods_id":"636491","fac_goods_id":"16105","store_id":"4064","type":"material","goods_name":"新款长袖T恤男 圆领修身上衣韩版秋衣男士卫衣青少年打底衫","cate_id":"41","cate_name":"男装\t上装\t毛衣","brand":"","year":"2015","spec_qty":"2","spec_name_1":"颜色","spec_name_2":"尺码","if_show":"1","closed":"0","add_time":"1515817559","recommended":"1","default_image":"data/files/store_4030/goods_72/201801130421129392.jpg?x-oss-process=image/resize,h_300,w_300","spec_id":"797413","spec_1":"白色","spec_2":"M","color_rgb":"","price":"50.00","stock":"999","store_name":"时尚圈商城（马可）男装合作专供店","region_id":"610322","region_name":"陕西省\t宝鸡市\t凤翔县","credit_value":"0","sgrade":"2","pvs":null,"views":"44","sales":"0","comments":"0"},{"goods_id":"637079","fac_goods_id":"16105","store_id":"4069","type":"material","goods_name":"新款长袖T恤男 圆领修身上衣韩版秋衣男士卫衣青少年打底衫","cate_id":"41","cate_name":"男装\t上装\t毛衣","brand":"","year":"2015","spec_qty":"2","spec_name_1":"颜色","spec_name_2":"尺码","if_show":"1","closed":"0","add_time":"1515817559","recommended":"1","default_image":"data/files/store_4030/goods_72/201801130421129392.jpg?x-oss-process=image/resize,h_300,w_300","spec_id":"802917","spec_1":"白色","spec_2":"M","color_rgb":"","price":"50.00","stock":"999","store_name":"时尚圈商城   男装指定专营店","region_id":"610322","region_name":"陕西省\t宝鸡市\t凤翔县","credit_value":"0","sgrade":"1","pvs":null,"views":"36","sales":"0","comments":"0"},{"goods_id":"638058","fac_goods_id":"16105","store_id":"4079","type":"material","goods_name":"新款长袖T恤男 圆领修身上衣韩版秋衣男士卫衣青少年打底衫","cate_id":"4043","cate_name":"男装\t上装\t针织衫","brand":"","year":"2015","spec_qty":"2","spec_name_1":"颜色","spec_name_2":"尺码","if_show":"1","closed":"0","add_time":"1515817559","recommended":"1","default_image":"data/files/store_4030/goods_72/201801130421129392.jpg?x-oss-process=image/resize,h_300,w_300","spec_id":"815765","spec_1":"白色","spec_2":"M","color_rgb":"","price":"50.00","stock":"999","store_name":"时尚圈商城（华坤）男装合作专供店","region_id":"610331","region_name":"陕西省\t宝鸡市\t太白县","credit_value":"0","sgrade":"2","pvs":null,"views":"29","sales":"0","comments":"0"},{"goods_id":"637751","fac_goods_id":"16101","store_id":"4075","type":"material","goods_name":"男装长袖t恤青少年爆款T恤韩版修身圆领纯色上衣百搭打底衫","cate_id":"41","cate_name":"男装\t上装\t毛衣","brand":"","year":"2015","spec_qty":"2","spec_name_1":"颜色","spec_name_2":"尺码","if_show":"1","closed":"0","add_time":"1515816785","recommended":"1","default_image":"data/files/store_4030/goods_150/201801130359105514.jpg?x-oss-process=image/resize,h_300,w_300","spec_id":"811307","spec_1":"粉红色","spec_2":"M","color_rgb":"","price":"56.00","stock":"999","store_name":"时尚圈商城（男爵）男装合作专供店","region_id":"610304003","region_name":"陕西省\t宝鸡市\t陈仓区\t千渭街道","credit_value":"0","sgrade":"2","pvs":null,"views":"25","sales":"0","comments":"0"},{"goods_id":"636490","fac_goods_id":"16101","store_id":"4064","type":"material","goods_name":"男装长袖t恤青少年爆款T恤韩版修身圆领纯色上衣百搭打底衫","cate_id":"41","cate_name":"男装\t上装\t毛衣","brand":"","year":"2015","spec_qty":"2","spec_name_1":"颜色","spec_name_2":"尺码","if_show":"1","closed":"0","add_time":"1515816785","recommended":"1","default_image":"data/files/store_4030/goods_150/201801130359105514.jpg?x-oss-process=image/resize,h_300,w_300","spec_id":"797389","spec_1":"粉红色","spec_2":"M","color_rgb":"","price":"56.00","stock":"999","store_name":"时尚圈商城（马可）男装合作专供店","region_id":"610322","region_name":"陕西省\t宝鸡市\t凤翔县","credit_value":"0","sgrade":"2","pvs":null,"views":"20","sales":"0","comments":"0"},{"goods_id":"637080","fac_goods_id":"16101","store_id":"4069","type":"material","goods_name":"男装长袖t恤青少年爆款T恤韩版修身圆领纯色上衣百搭打底衫","cate_id":"41","cate_name":"男装\t上装\t毛衣","brand":"","year":"2015","spec_qty":"2","spec_name_1":"颜色","spec_name_2":"尺码","if_show":"1","closed":"0","add_time":"1515816785","recommended":"1","default_image":"data/files/store_4030/goods_150/201801130359105514.jpg?x-oss-process=image/resize,h_300,w_300","spec_id":"802929","spec_1":"粉红色","spec_2":"M","color_rgb":"","price":"56.00","stock":"999","store_name":"时尚圈商城   男装指定专营店","region_id":"610322","region_name":"陕西省\t宝鸡市\t凤翔县","credit_value":"0","sgrade":"1","pvs":null,"views":"32","sales":"0","comments":"0"},{"goods_id":"637749","fac_goods_id":"16101","store_id":"4073","type":"material","goods_name":"男装长袖t恤青少年爆款T恤韩版修身圆领纯色上衣百搭打底衫","cate_id":"37","cate_name":"男装\t上装\tT恤","brand":"","year":"2015","spec_qty":"2","spec_name_1":"颜色","spec_name_2":"尺码","if_show":"1","closed":"0","add_time":"1515816785","recommended":"1","default_image":"data/files/store_4030/goods_150/201801130359105514.jpg?x-oss-process=image/resize,h_300,w_300","spec_id":"811259","spec_1":"粉红色","spec_2":"M","color_rgb":"","price":"56.00","stock":"999","store_name":"时尚圈商城（迪克斯）男装合作专供店","region_id":"610328","region_name":"陕西省\t宝鸡市\t千阳县","credit_value":"0","sgrade":"2","pvs":null,"views":"27","sales":"0","comments":"0"},{"goods_id":"638059","fac_goods_id":"16101","store_id":"4079","type":"material","goods_name":"男装长袖t恤青少年爆款T恤韩版修身圆领纯色上衣百搭打底衫","cate_id":"4043","cate_name":"男装\t上装\t针织衫","brand":"","year":"2015","spec_qty":"2","spec_name_1":"颜色","spec_name_2":"尺码","if_show":"1","closed":"0","add_time":"1515816785","recommended":"1","default_image":"data/files/store_4030/goods_150/201801130359105514.jpg?x-oss-process=image/resize,h_300,w_300","spec_id":"815777","spec_1":"粉红色","spec_2":"M","color_rgb":"","price":"56.00","stock":"999","store_name":"时尚圈商城（华坤）男装合作专供店","region_id":"610331","region_name":"陕西省\t宝鸡市\t太白县","credit_value":"0","sgrade":"2","pvs":null,"views":"31","sales":"0","comments":"0"}]
     * page_count : 74
     * search_name :
     */

    private OrdersBean orders;
    private int page_count;
    private String search_name;
    private List<SgradeBean> sgrade;
    private List<YearBean> year;
    private List<ResultBean> result;

    public OrdersBean getOrders() {
        return orders;
    }

    public void setOrders(OrdersBean orders) {
        this.orders = orders;
    }

    public int getPage_count() {
        return page_count;
    }

    public void setPage_count(int page_count) {
        this.page_count = page_count;
    }

    public String getSearch_name() {
        return search_name;
    }

    public void setSearch_name(String search_name) {
        this.search_name = search_name;
    }

    public List<SgradeBean> getSgrade() {
        return sgrade;
    }

    public void setSgrade(List<SgradeBean> sgrade) {
        this.sgrade = sgrade;
    }

    public List<YearBean> getYear() {
        return year;
    }

    public void setYear(List<YearBean> year) {
        this.year = year;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class OrdersBean {
        @SerializedName("add_time desc")
        private String _$Add_timeDesc318; // FIXME check this code
        @SerializedName("price asc")
        private String _$PriceAsc103; // FIXME check this code
        @SerializedName("price desc")
        private String _$PriceDesc149; // FIXME check this code

        public String get_$Add_timeDesc318() {
            return _$Add_timeDesc318;
        }

        public void set_$Add_timeDesc318(String _$Add_timeDesc318) {
            this._$Add_timeDesc318 = _$Add_timeDesc318;
        }

        public String get_$PriceAsc103() {
            return _$PriceAsc103;
        }

        public void set_$PriceAsc103(String _$PriceAsc103) {
            this._$PriceAsc103 = _$PriceAsc103;
        }

        public String get_$PriceDesc149() {
            return _$PriceDesc149;
        }

        public void set_$PriceDesc149(String _$PriceDesc149) {
            this._$PriceDesc149 = _$PriceDesc149;
        }
    }

    public static class SgradeBean {
        /**
         * key : 1
         * value : 指定专营店
         */

        private String key;
        private String value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class YearBean {
        /**
         * key : 20163
         * value : 2016年秋季
         */

        private String key;
        private String value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class ResultBean {
        /**
         * goods_id : 637752
         * fac_goods_id : 16269
         * store_id : 4075
         * type : material
         * goods_name : 衬衫男韩版秋季新品男士长袖纯棉纯色青年时尚休闲修身立领衬衣
         * cate_id : 39
         * cate_name : 男装	上装	衬衫
         * brand :
         * year : 2015
         * spec_qty : 2
         * spec_name_1 : 颜色
         * spec_name_2 : 尺码
         * if_show : 1
         * closed : 0
         * add_time : 1515963277
         * recommended : 1
         * default_image : data/files/store_4030/goods_30/201801151250302079.jpg?x-oss-process=image/resize,h_300,w_300
         * spec_id : 811331
         * spec_1 : 黑色
         * spec_2 : M
         * color_rgb :
         * price : 120.00
         * stock : 999
         * store_name : 时尚圈商城（男爵）男装合作专供店
         * region_id : 610304003
         * region_name : 陕西省	宝鸡市	陈仓区	千渭街道
         * credit_value : 0
         * sgrade : 2
         * pvs : null
         * views : 16
         * sales : 0
         * comments : 0
         */

        private String goods_id;
        private String fac_goods_id;
        private String store_id;
        private String type;
        private String goods_name;
        private String cate_id;
        private String cate_name;
        private String brand;
        private String year;
        private String spec_qty;
        private String spec_name_1;
        private String spec_name_2;
        private String if_show;
        private String closed;
        private String add_time;
        private String recommended;
        private String default_image;
        private String spec_id;
        private String spec_1;
        private String spec_2;
        private String color_rgb;
        private String price;
        private String stock;
        private String store_name;
        private String region_id;
        private String region_name;
        private String credit_value;
        private String sgrade;
        private Object pvs;
        private String views;
        private String sales;
        private String comments;

        public String getGoods_id() {
            return goods_id;
        }

        public void setGoods_id(String goods_id) {
            this.goods_id = goods_id;
        }

        public String getFac_goods_id() {
            return fac_goods_id;
        }

        public void setFac_goods_id(String fac_goods_id) {
            this.fac_goods_id = fac_goods_id;
        }

        public String getStore_id() {
            return store_id;
        }

        public void setStore_id(String store_id) {
            this.store_id = store_id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getGoods_name() {
            return goods_name;
        }

        public void setGoods_name(String goods_name) {
            this.goods_name = goods_name;
        }

        public String getCate_id() {
            return cate_id;
        }

        public void setCate_id(String cate_id) {
            this.cate_id = cate_id;
        }

        public String getCate_name() {
            return cate_name;
        }

        public void setCate_name(String cate_name) {
            this.cate_name = cate_name;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public String getYear() {
            return year;
        }

        public void setYear(String year) {
            this.year = year;
        }

        public String getSpec_qty() {
            return spec_qty;
        }

        public void setSpec_qty(String spec_qty) {
            this.spec_qty = spec_qty;
        }

        public String getSpec_name_1() {
            return spec_name_1;
        }

        public void setSpec_name_1(String spec_name_1) {
            this.spec_name_1 = spec_name_1;
        }

        public String getSpec_name_2() {
            return spec_name_2;
        }

        public void setSpec_name_2(String spec_name_2) {
            this.spec_name_2 = spec_name_2;
        }

        public String getIf_show() {
            return if_show;
        }

        public void setIf_show(String if_show) {
            this.if_show = if_show;
        }

        public String getClosed() {
            return closed;
        }

        public void setClosed(String closed) {
            this.closed = closed;
        }

        public String getAdd_time() {
            return add_time;
        }

        public void setAdd_time(String add_time) {
            this.add_time = add_time;
        }

        public String getRecommended() {
            return recommended;
        }

        public void setRecommended(String recommended) {
            this.recommended = recommended;
        }

        public String getDefault_image() {
            return default_image;
        }

        public void setDefault_image(String default_image) {
            this.default_image = default_image;
        }

        public String getSpec_id() {
            return spec_id;
        }

        public void setSpec_id(String spec_id) {
            this.spec_id = spec_id;
        }

        public String getSpec_1() {
            return spec_1;
        }

        public void setSpec_1(String spec_1) {
            this.spec_1 = spec_1;
        }

        public String getSpec_2() {
            return spec_2;
        }

        public void setSpec_2(String spec_2) {
            this.spec_2 = spec_2;
        }

        public String getColor_rgb() {
            return color_rgb;
        }

        public void setColor_rgb(String color_rgb) {
            this.color_rgb = color_rgb;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getStock() {
            return stock;
        }

        public void setStock(String stock) {
            this.stock = stock;
        }

        public String getStore_name() {
            return store_name;
        }

        public void setStore_name(String store_name) {
            this.store_name = store_name;
        }

        public String getRegion_id() {
            return region_id;
        }

        public void setRegion_id(String region_id) {
            this.region_id = region_id;
        }

        public String getRegion_name() {
            return region_name;
        }

        public void setRegion_name(String region_name) {
            this.region_name = region_name;
        }

        public String getCredit_value() {
            return credit_value;
        }

        public void setCredit_value(String credit_value) {
            this.credit_value = credit_value;
        }

        public String getSgrade() {
            return sgrade;
        }

        public void setSgrade(String sgrade) {
            this.sgrade = sgrade;
        }

        public Object getPvs() {
            return pvs;
        }

        public void setPvs(Object pvs) {
            this.pvs = pvs;
        }

        public String getViews() {
            return views;
        }

        public void setViews(String views) {
            this.views = views;
        }

        public String getSales() {
            return sales;
        }

        public void setSales(String sales) {
            this.sales = sales;
        }

        public String getComments() {
            return comments;
        }

        public void setComments(String comments) {
            this.comments = comments;
        }
    }
}
