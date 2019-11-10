package androidnews.kiloproject.entity.data;

import java.util.List;

public class ExportBean {
    public String arrayStr;
    public boolean isNightMode,
            isHighRam,
            isSwipeBack,
            isAutoRefresh,
            isAutoLoadMore,
            isStatusBar,
            isDisNotice,
            isBackExit,
            isPush,
            isPushSound,
            isPushMode,
            isHaptic,
            isNoImage,
            isEasterEggs,
            isShowSkeleton;
    public int listType,
            currentRandomHeader,
            currentLanguage,
            mTextSize,
            pushTime;
    public List<BlockItem> blockList;
}
