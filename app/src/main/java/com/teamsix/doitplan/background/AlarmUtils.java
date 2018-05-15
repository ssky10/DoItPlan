package com.teamsix.doitplan.background;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class AlarmUtils {
    private final static int ONE_HOUR = 60 * 60 * 1000;
    private final static int ONE_MINUTE = 60 * 1000;

    private static AlarmUtils _instance;

    public static AlarmUtils getInstance() {
        if (_instance == null) _instance = new AlarmUtils();
        return _instance;
    }

    public void startGPStracker(Context context) {
        // AlarmOneSecondBroadcastReceiver 초기화
        Intent gpsIntent = new Intent("com.teamsix.doitplan.GPS_START");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, gpsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.e("startGPStracker", "start");

        // 1시간 뒤에 AlarmOneMinuteBroadcastReceiver 호출 한다.
        startAlram(context, pendingIntent, ONE_MINUTE);
    }

    public void startForecastUpdate(Context context) {
        // AlarmOneSecondBroadcastReceiver 초기화
        Intent alarmIntent = new Intent("com.teamsix.doitplan.ALARM_START");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.e("startForecastUpdate", "start");

        // 1시간 뒤에 AlarmOneMinuteBroadcastReceiver 호출 한다.
        startAlram(context, pendingIntent, ONE_HOUR);
    }

    private void startAlram(Context context, PendingIntent pendingIntent, int delay) {

        Log.e("startAlram", "start");

        // AlarmManager 호출
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // 1분뒤에 AlarmOneMinuteBroadcastReceiver 호출 한다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delay, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            manager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delay, pendingIntent);
        } else {
            manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delay, pendingIntent);
        }
    }

}
