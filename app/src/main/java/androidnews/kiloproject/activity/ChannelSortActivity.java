package androidnews.kiloproject.activity;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import androidnews.kiloproject.R;
import androidnews.kiloproject.fragment.ChannelFragment;
import androidnews.kiloproject.system.base.BaseActivity;

public class ChannelSortActivity extends BaseActivity {
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_sort);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        initStateBar(R.color.main_background, true);
    }

    @Override
    protected void initSlowly() {
    }
}
