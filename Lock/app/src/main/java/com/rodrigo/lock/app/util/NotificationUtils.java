package com.rodrigo.lock.app.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationUtils {


    public static String getNotificationChannelId(Context context) {
        String NOTIFICATION_CHANNEL_ID = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NOTIFICATION_CHANNEL_ID = "lockapp_channel_id_01";
            String channelName = "Lock";

            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);

            // Configure the notification channel.
            notificationChannel.setDescription("Lock tasks");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            //notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);

            NotificationManager nManager =(NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            nManager.createNotificationChannel(notificationChannel);

        }
        return NOTIFICATION_CHANNEL_ID;
    }
}
