package democode.kiloproject.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

import com.blankj.utilcode.util.ScreenUtils;

public class MyWebView extends WebView {
    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public MyWebView(Context context) {
        super(context);
    }

    @Override
    public void setOverScrollMode(int mode) {
        super.setOverScrollMode(mode);
        ScreenUtils.restoreAdaptScreen();
    }
}
