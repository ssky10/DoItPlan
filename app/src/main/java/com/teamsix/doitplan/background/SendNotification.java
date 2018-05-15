package com.teamsix.doitplan.background;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.teamsix.doitplan.MainActivity;
import com.teamsix.doitplan.R;

public class SendNotification {
    private static final String TAG = "MyFirebaseMsgService";
    private static int notificationID = 0;

    public static void sendNotification(Context context, String messageTitle, String messageBody) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setTicker(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setShowWhen(true)
                .setColor(Color.GREEN)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_MAX);


        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel("DIP", "DIP_Notification", NotificationManager.IMPORTANCE_HIGH);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.GREEN);
            mChannel.enableVibration(true);
            notificationManager.createNotificationChannel(mChannel);
            notification.setChannelId("DIP");
        }

        notificationManager.notify(notificationID++, notification.build());

        Log.d(TAG, "Message Notification title: " + messageTitle);
    }

    public static void sendNaverNotification(Context context, String messageTitle, String keyword) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://search.naver.com/search.naver?&ie=utf8&query="+keyword));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(messageTitle)
                .setContentText(keyword+" 검색")
                .setTicker(keyword+" 검색")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setShowWhen(true)
                .setColor(Color.GREEN)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_MAX);


        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel("DIP", "DIP_Notification", NotificationManager.IMPORTANCE_HIGH);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.GREEN);
            mChannel.enableVibration(true);
            notificationManager.createNotificationChannel(mChannel);
            notification.setChannelId("DIP");
        }

        notificationManager.notify(notificationID++, notification.build());

        Log.d(TAG, "Message Notification title: " + messageTitle);
    }

}
