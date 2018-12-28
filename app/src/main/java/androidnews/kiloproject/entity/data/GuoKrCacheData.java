package androidnews.kiloproject.entity.data;

import androidnews.kiloproject.entity.net.GuoKrListData;
import androidnews.kiloproject.entity.net.GuoKrTopData;

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
