package androidnews.kiloproject.entity.data;

import java.util.List;

public class ExportBean {
    public String arrayStr;
    public boolean isNightMode,
            isHighRam,
            isSwipeBack,
            isAutoRefresh,
            isAutoLoadMore,
            isBackExit;
    public int listType,
            currentRandomHeader,
            currentLanguage,
            mTextSize;
    public List<BlockItem> blockList;
}
