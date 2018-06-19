package com.teamsix.doitplan.background;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.teamsix.doitplan.ApplicationController;
import com.teamsix.doitplan.Plan;

import java.util.ArrayList;

import static com.teamsix.doitplan.GetIfResult.getNextDate;

public class AlarmUtils {
    private final static int ONE_HOUR = 60 * 1000;

    private static AlarmUtils _instance;
    public static ArrayList<Integer> alarmPlanNo = new ArrayList<>();

    public static AlarmUtils getInstance() {
        if (_instance == null) _instance = new AlarmUtils();
        return _instance;
    }

    public void startAlarmUpdate(Context context,int planNo) {
        Log.e("startAlarmUpdate","start");

        // AlarmOneSecondBroadcastReceiver 초기화
        Intent alarmIntent = new Intent("com.teamsix.doitplan.ALARM_START");
        alarmIntent.putExtra("planNo",planNo);
        if(alarmPlanNo.indexOf(planNo) == -1) alarmPlanNo.add(planNo);
        alarmIntent.setClass(context, AlarmBraodCastReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, planNo, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long now = System.currentTimeMillis();
        // 현재시간을 date 변수에 저장한다.
        Plan plan = ApplicationController.getWorkPlan(planNo);

        if(plan==null){
            alarmPlanNo.remove(planNo);
            return;
        }

        long nextTime = getNextDate(now, plan.ifValue);

        if(nextTime==0){
            alarmPlanNo.remove(planNo);
            return;
        }

        Log.e("startAlarmUpdate",(nextTime - now)+"");
        // 1시간 뒤에 AlarmOneMinuteBroadcastReceiver 호출 한다.
        startAlram(context, pendingIntent, nextTime);
    }

    public void startForecastUpdate(Context context) {
        // AlarmOneSecondBroadcastReceiver 초기화
        Intent alarmIntent = new Intent("com.teamsix.doitplan.FORECAST_START");
        alarmIntent.setClass(context, ForecastBraodCastReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, -1, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.e("startForecastUpdate", "start");

        if(ForecastBraodCastReciever.forecast == null) ForecastBraodCastReciever.forecast = new Forecast();

        if(GPStracker.lastLocation==null)
            ForecastBraodCastReciever.forecast.getNowData(context,35.154483, 128.098444);
        else
            ForecastBraodCastReciever.forecast.getNowData(context, GPStracker.lastLocation.getLatitude(), GPStracker.lastLocation.getLongitude());

        // 1시간 뒤에 AlarmOneMinuteBroadcastReceiver 호출 한다.
        startAlram(context, pendingIntent, System.currentTimeMillis() + ONE_HOUR);
    }

    private void startAlram(Context context, PendingIntent pendingIntent, long millisTime) {

        Log.e("startAlram", "start");

        // AlarmManager 호출
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, millisTime, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            manager.setExact(AlarmManager.RTC_WAKEUP, millisTime, pendingIntent);
        } else {
            manager.set(AlarmManager.RTC_WAKEUP, millisTime, pendingIntent);
        }
    }

}
