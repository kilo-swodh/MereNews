package androidnews.kiloproject.util;

import android.content.Context;
import android.os.Build;

import java.io.File;

public class FileCompatUtil {

    public static String getMediaDir(Context mContext) {
        String path = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            File[] files = mContext.getExternalMediaDirs();
            if (files != null && files.length > 0)
                path = files[0].getPath();
        } else
            path = "/sdcard/Download";
        return path;
    }
}
