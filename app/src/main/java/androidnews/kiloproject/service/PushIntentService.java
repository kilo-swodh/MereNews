package androidnews.kiloproject.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import java.util.HashMap;
import java.util.List;

import androidnews.kiloproject.entity.net.NewMainListData;
import androidnews.kiloproject.push.NotifyWork;
import androidnews.kiloproject.system.AppConfig;

import static androidnews.kiloproject.system.AppConfig.CACHE_LAST_PUSH_ID;


public class PushIntentService extends IntentService {

    private String typeStr = "T1429173683626";

    public PushIntentService() {
        super("Push service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        getPushList();
    }

    /**
     * 网络请求
     */
    private void getPushList() {
        String dataUrl = AppConfig.GET_MAIN_DATA.replace("{typeStr}", typeStr).replace("{currentPage}", "0");
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
                                String lastId = SPUtils.getInstance().getString(CACHE_LAST_PUSH_ID);
                                for (int i = 0; i < 5; i++) {
                                    NewMainListData newMainListData = retMap.get(typeStr).get(i);
                                    if (!lastId.contains(newMainListData.getDocid())) {
                                        mData = newMainListData;
                                        break;
                                    }
                                }

                                if (mData != null) {
                                    if (lastId.length() > 188) {
                                        lastId = mData.getDocid() + "," + lastId.substring(0, 160);
                                    }
                                    String newLastId = mData.getDocid() + "," + lastId;

                                    SPUtils.getInstance().put(CACHE_LAST_PUSH_ID, newLastId);
                                    NotifyWork.sendNotification(PushIntentService.this,mData.getTitle(), mData.getDigest(), mData.getDocid().replace("_special", "").trim());
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                });
    }
}
