package democode.kiloproject;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.yanzhenjie.alertdialog.AlertDialog;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.RationaleListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import democode.kiloproject.sqlite.Album;

public class MainActivity extends BaseActivity {

    @BindView(R.id.btn_show_picker)
    Button btnShowPicker;
    @BindView(R.id.tv_demo)
    TextView tvDemo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

//        requestPermission();
    }

    /**
     * 申请权限
     */
    private void requestPermission() {
        AndPermission
                .with(mActivity)
                .requestCode(200)
                .permission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE
                )
                .rationale(rationaleListener)
                .callback(listener)
                .start();
    }

    /**
     * 权限监听
     * 国产系统适配版
     */
    private PermissionListener listener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, List<String> grantedPermissions) {
            // Successfully.
            if (requestCode == 200) {
                if (AndPermission.hasPermission(mActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE)) {
                    // TODO 执行拥有权限时的下一步。
                    saveSQL();
                } else {
                    // 使用AndPermission提供的默认设置dialog，用户点击确定后会打开App的设置页面让用户授权。
                    AndPermission.defaultSettingDialog(mActivity, requestCode).show();

                    // 建议：自定义这个Dialog，提示具体需要开启什么权限，自定义Dialog具体实现上面有示例代码。
                }
            }
        }

        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            // Failure.
            if (requestCode == 200) {
                if (AndPermission.hasPermission(mActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE)) {
                    // TODO 执行拥有权限时的下一步。

                } else {
                    // 使用AndPermission提供的默认设置dialog，用户点击确定后会打开App的设置页面让用户授权。
                    AndPermission.defaultSettingDialog(mActivity, requestCode).show();

                    // 建议：自定义这个Dialog，提示具体需要开启什么权限，自定义Dialog具体实现上面有示例代码。
                }
            }
        }
    };

    /**
     * 自定义对话框提醒用户授予权限
     */
    private RationaleListener rationaleListener = (requestCode, rationale) -> {
        AlertDialog.newBuilder(this)
                .setTitle("Tips")
                .setMessage("Request permission to recommend content for you.")
                .setPositiveButton("OK", (dialog, which) -> {
                    rationale.resume();
                })
                .setNegativeButton("NO", (dialog, which) -> {
                    rationale.cancel();
                }).show();
    };


    /**
     * 进行保存数据库操作(往外部存储卡)
     */
    private void saveSQL() {
        Album album = new Album("name", 135.4f);
        album.save();
    }

    @OnClick(R.id.btn_show_picker)
    public void onViewClicked() {
        //时间选择器
        TimePickerView pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                tvDemo.setText(new SimpleDateFormat("yyyyMMddHHmmss").format(date));
            }
        }).build();
        pvTime.setDate(Calendar.getInstance());//注：根据需求来决定是否使用该方法（一般是精确到秒的情况），此项可以在弹出选择器的时候重新设置当前时间，避免在初始化之后由于时间已经设定，导致选中时间与当前时间不匹配的问题。
        pvTime.show();

        //条件选择器
//        OptionsPickerView pvOptions = new  OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
//            @Override
//            public void onOptionsSelect(int options1, int option2, int options3 ,View v) {
//                //返回的分别是三个级别的选中位置
//                String tx = options1Items.get(options1).getPickerViewText()
//                        + options2Items.get(options1).get(option2)
//                        + options3Items.get(options1).get(option2).get(options3).getPickerViewText();
//                tvOptions.setText(tx);
//            }
//        }).build();
//        pvOptions.setPicker(options1Items, options2Items, options3Items);
//        pvOptions.show();
    }
}
