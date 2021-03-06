package com.mzywx.liao.android;

import org.json.JSONException;
import org.json.JSONObject;

import com.mzywx.liao.android.ui.LiaoChatActivity;
import com.mzywx.liao.android.utils.JPushUtil;
import com.mzywx.liao.android.utils.NotificationHelper;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则： 1) 默认用户会打开主界面 2) 接收不到自定义消息
 */
public class JPushReceiver extends BroadcastReceiver {
    private static final String TAG = "JPush";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction()
                + ", extras: " + printBundle(bundle));

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle
                    .getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            // send the Registration Id to your server...

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent
                .getAction())) {
            Log.d(TAG,
                    "[MyReceiver] 接收到推送下来的自定义消息: "
                            + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            processCustomMessage(context, bundle);
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent
                .getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");// 富文本下发的action
            int notifactionId = bundle
                    .getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            // NotificationManager nm = (NotificationManager)
            // context.getSystemService(Context.NOTIFICATION_SERVICE);
            // nm.cancel(notifactionId);//取消通知
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent
                .getAction())) {
            Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent
                .getAction())) {
            Log.d(TAG,
                    "[MyReceiver] 用户收到到RICH PUSH CALLBACK: "
                            + bundle.getString(JPushInterface.EXTRA_EXTRA));
            // 在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity，
            // 打开一个网页等..
        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent
                .getAction())) {
            boolean connected = intent.getBooleanExtra(
                    JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            Log.w(TAG, "[MyReceiver]" + intent.getAction()
                    + " connected state change to " + connected);
        } else {
            Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    // send msg to LiaoChatActivity
    private void processCustomMessage(Context context, Bundle bundle) {
        if (LiaoChatActivity.isForeground) {
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);

            Intent msgIntent = new Intent(
                    LiaoChatActivity.MESSAGE_RECEIVED_ACTION);
            msgIntent.putExtra(LiaoChatActivity.KEY_MESSAGE, message);
            if (!JPushUtil.isEmpty(extras)) {
                try {
                    JSONObject extraJson = new JSONObject(extras);
                    if (null != extraJson && extraJson.length() > 0) {
                        if (!extraJson.isNull(LiaoChatActivity.KEY_IMG)) {
                            msgIntent
                                    .putExtra(
                                            LiaoChatActivity.KEY_IMG,
                                            extraJson
                                                    .getString(LiaoChatActivity.KEY_IMG));
                        }
                        if (!extraJson.isNull(LiaoChatActivity.KEY_VOICE)) {
                            msgIntent
                                    .putExtra(
                                            LiaoChatActivity.KEY_VOICE,
                                            extraJson
                                                    .getString(LiaoChatActivity.KEY_VOICE));
                        }
                    }
                } catch (JSONException e) {

                }
            }
            context.sendBroadcast(msgIntent);
        } else {
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            boolean hasImage, hasVoice;
            hasImage = hasVoice = false;

            Intent intent = new Intent("com.mzywx.android.MESSAGE_RECEIVED_ACTION");
            if (!JPushUtil.isEmpty(extras)) {
                try {
                    JSONObject extraJson = new JSONObject(extras);
                    if (null != extraJson && extraJson.length() > 0) {
                        if (!extraJson.isNull(LiaoChatActivity.KEY_IMG)) {
                            hasImage = true;
                            intent.putExtra(
                                    LiaoChatActivity.KEY_IMG,
                                    extraJson
                                            .getString(LiaoChatActivity.KEY_IMG));
                        }
                        if (!extraJson.isNull(LiaoChatActivity.KEY_VOICE)) {
                            hasVoice = true;
                            intent.putExtra(
                                    LiaoChatActivity.KEY_VOICE,
                                    extraJson
                                            .getString(LiaoChatActivity.KEY_VOICE));
                        }
                    }
                } catch (JSONException e) {

                }
            }
            
            intent.putExtra(LiaoChatActivity.KEY_MESSAGE, message);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 1,
                    intent,
                    PendingIntent.FLAG_CANCEL_CURRENT);
            
            NotificationHelper.postNotification(context, pendingIntent, message,
                    hasImage, hasVoice);
        }
    }
}
