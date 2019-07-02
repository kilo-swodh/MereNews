package androidnews.kiloproject.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.NotificationTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import java.util.HashMap;
import java.util.List;

import androidnews.kiloproject.R;
import androidnews.kiloproject.activity.NewsDetailActivity;
import androidnews.kiloproject.entity.net.NewMainListData;
import androidnews.kiloproject.system.AppConfig;

import static androidnews.kiloproject.system.AppConfig.CACHE_LAST_PUSH_ID;
import static androidnews.kiloproject.system.AppConfig.CACHE_LAST_PUSH_TIME;
import static androidnews.kiloproject.system.AppConfig.GET_MAIN_DATA;

public class NotifyWork extends Worker {
    private Context mContext;
    private String typeStr = "T1429173683626";

    public NotifyWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        /**
         //         * WorkerResult.SUCCESS //成功
         //         * WorkerResult.FAILURE //失败
         //         * WorkerResult.RETRY //重试
         //         */
        //接收传递进来的参数
//        String parame_str = this.getInputData().getString("workparame");

        //执行网络请求
        try {
            getPushList();
        } catch (Exception e) {
            return Result.failure();
        }
        return Result.success();
    }

    /**
     * 网络请求
     */
    private void getPushList() {
        String dataUrl = GET_MAIN_DATA.replace("{typeStr}", typeStr).replace("{currentPage}", "0");
        EasyHttp.get(dataUrl)
                .readTimeOut(30 * 1000)//局部定义读超时
                .writeTimeOut(30 * 1000)
                .connectTimeout(30 * 1000)
                .timeStamp(true)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onSuccess(String response) {
                        if (!TextUtils.isEmpty(response) || TextUtils.equals(response, "{}")) {
                            HashMap<String, List<NewMainListData>> retMap = null;
                            try {
                                retMap = new Gson().fromJson(response,
                                        new TypeToken<HashMap<String, List<NewMainListData>>>() {
                                        }.getType());
                                NewMainListData mData = null;

                                long lastPushTime = SPUtils.getInstance().getLong(CACHE_LAST_PUSH_TIME);
                                String lastId = "";
                                if (TimeUtils.isToday(lastPushTime))
                                    lastId = SPUtils.getInstance().getString(CACHE_LAST_PUSH_ID);
                                for (int i = 0; i < retMap.get(typeStr).size(); i++) {
                                    if (i > 3)
                                        break;
                                    else {
                                        NewMainListData newMainListData = retMap.get(typeStr).get(i);
                                        if (!lastId.contains(newMainListData.getDocid())) {
                                            mData = newMainListData;
                                            break;
                                        }
                                    }
                                }
                                if (mData != null) {
                                    SPUtils.getInstance().put(CACHE_LAST_PUSH_ID, lastId + mData.getDocid());
                                    SPUtils.getInstance().put(CACHE_LAST_PUSH_TIME, System.currentTimeMillis());
                                    sendNotification(mData.getTitle(), mData.getDigest(), mData.getDocid().replace("_special", "").trim());
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                });
    }

    public void sendNotification(String title, String text, String doCid) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "Mere Push");
        mBuilder.setSmallIcon(R.drawable.ic_paper)//设置小图标
                .setContentTitle(title)//设置内容标题
                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
//                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setContentText("点击查看详情")//设置内容
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(text))
                .setTicker(mContext.getResources().getString(R.string.notification_ticker));//通知弹出时状态栏的提示文本
        if (AppConfig.isPushSound)
            mBuilder.setDefaults(Notification.DEFAULT_LIGHTS);
        else
            mBuilder.setDefaults(Notification.DEFAULT_ALL);//设置声音震动

        int msgId = (int) (1 + Math.random() * 30);
        //创建一个意图
        Intent intent = new Intent(mContext, NewsDetailActivity.class);
        intent.putExtra("docid", doCid);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), msgId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);

        //获取通知管理对象
        NotificationManager mNotificaionManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificaionManager.notify(msgId, mBuilder.build());
    }
}