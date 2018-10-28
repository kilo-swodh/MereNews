package androidnews.kiloproject.bean.data;

import androidnews.kiloproject.bean.net.GuoKrListData;
import androidnews.kiloproject.bean.net.GuoKrTopData;

public class GuoKrCacheData {
    GuoKrTopData topData;
    GuoKrListData ListData;

    public GuoKrTopData getTopData() {
        return topData;
    }

    public void setTopData(GuoKrTopData topData) {
        this.topData = topData;
    }

    public GuoKrListData getListData() {
        return ListData;
    }

    public void setListData(GuoKrListData listData) {
        ListData = listData;
    }
}
