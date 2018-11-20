package androidnews.kiloproject.util;

import com.blankj.utilcode.util.CacheDiskUtils;
import com.blankj.utilcode.util.SPUtils;

import org.litepal.LitePal;

import java.util.List;

import androidnews.kiloproject.bean.data.BlockArrayBean;
import androidnews.kiloproject.bean.data.BlockItem;
import androidnews.kiloproject.bean.data.IntArrayBean;

import static androidnews.kiloproject.system.AppConfig.CONFIG_BLOCK_LIST;
import static androidnews.kiloproject.system.AppConfig.CONFIG_HAVE_CHECK;
import static androidnews.kiloproject.system.AppConfig.CONFIG_TYPE_ARRAY;

public class TransformationUtils {
    public static boolean hasCheckEvent173(){
        return SPUtils.getInstance().getBoolean(CONFIG_HAVE_CHECK);
    }

    public static boolean transferBlockData(){
        BlockArrayBean blockArrayBean = CacheDiskUtils.getInstance().getParcelable(CONFIG_BLOCK_LIST, BlockArrayBean.CREATOR);
        if (blockArrayBean == null){
            return false;
        }else {
            LitePal.saveAllAsync(blockArrayBean.getTypeArray());
            return true;
        }
    }

    public static boolean transferChannel(){
        IntArrayBean intArrayBean = CacheDiskUtils.getInstance().getParcelable(CONFIG_TYPE_ARRAY, IntArrayBean.CREATOR);
        if (intArrayBean == null){
            return false;
        }else {
            StringBuilder sb = new StringBuilder();
            for (Integer integer : intArrayBean.getTypeArray()){
                sb.append(integer + "#");
            }
            SPUtils.getInstance().put(CONFIG_TYPE_ARRAY,sb.toString());
            return true;
        }
    }
}
