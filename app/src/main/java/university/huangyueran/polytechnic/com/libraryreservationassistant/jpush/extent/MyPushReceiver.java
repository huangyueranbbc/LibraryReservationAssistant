package university.huangyueran.polytechnic.com.libraryreservationassistant.jpush.extent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义广播接受者
 * Created by huangyueran on 2016/12/17.
 */
public class MyPushReceiver extends BroadcastReceiver {
    private static final String TAG = "MyPushReceiver";

    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.d(TAG, "onReceive - " + intent.getAction());

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Log.i(TAG, "收到了自定义消息。消息内容是：" + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            // 自定义消息不会展示在通知栏，完全要开发者写代码去处理
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.i(TAG, "收到了通知");
            // 在这里可以做些统计，或者做些其他工作
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.i(TAG, "用户点击打开了通知");
            // 在这里可以自己写代码去定义用户点击后的行为
//            Intent i = new Intent(context, TestActivity.class);  //自定义打开的界面
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(i);
            Log.i(TAG, "消息标题: " + bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE));
            Log.i(TAG, "消息内容: " + bundle.getString(JPushInterface.EXTRA_ALERT));
            Log.i(TAG, "自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));

            String json = bundle.getString(JPushInterface.EXTRA_EXTRA);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(json);
                String url = jsonObject.getString("url");
                // 网页加载页面
                Log.i(TAG, "url: " + url);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            Log.d(TAG, "Unhandled intent - " + intent.getAction());
        }
    }
}
