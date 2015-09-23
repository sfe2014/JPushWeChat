package com.mzywx.liao.android.utils;

import com.mzywx.liao.android.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;

public class NotificationHelper {

    @SuppressWarnings("deprecation")
    public static void postNotification(Context context, PendingIntent intent,
            String message, boolean hasImage, boolean hasVoice) {
        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification();
        String contentText = "";
        if (hasImage) {
            contentText = "[图片]";
        } else if (hasVoice) {
            contentText = "[语音]";
        } else {
            contentText = message;
        }
        notification.when = System.currentTimeMillis();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notification.defaults = Notification.DEFAULT_ALL;
        notification.icon = R.drawable.ic_launcher;
        notification.setLatestEventInfo(context, "用户昵称:LiaoChat", contentText, intent);
        nm.notify(R.string.app_name, notification);
    }
}
