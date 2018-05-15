package com.teamsix.doitplan.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.teamsix.doitplan.ApplicationController;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class AlarmBraodCastReciever extends BroadcastReceiver {
    public static boolean isLaunched = false;


    @Override
    public void onReceive(Context context, Intent intent) {
        isLaunched = true;

        //SendNotification.sendNotification(context,"서비스","내용내용내용");
        if(ApplicationController.getLastKnownLocation()==null)
            ApplicationController.getForecast().getNowData(35.154483, 128.098444);
        else
            ApplicationController.getForecast().getNowData(ApplicationController.getLastKnownLocation().getLatitude(), ApplicationController.getLastKnownLocation().getLongitude());

        AlarmUtils.getInstance().startForecastUpdate(context);

        // 현재 시간을 화면에 보낸다.
        //saveTime(context);
    }

}
