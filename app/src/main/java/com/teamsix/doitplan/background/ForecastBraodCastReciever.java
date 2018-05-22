package com.teamsix.doitplan.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ForecastBraodCastReciever extends BroadcastReceiver {
    public static boolean isLaunched = false;
    public static Forecast forecast = new Forecast();


    @Override
    public void onReceive(Context context, Intent intent) {
        isLaunched = true;
        String oldSky = forecast.getSky();
        String oldState = forecast.getState();

        if(GPStracker.lastLocation==null)
            forecast.getNowData(context,35.154483, 128.098444);
        else
            forecast.getNowData(context, GPStracker.lastLocation.getLatitude(), GPStracker.lastLocation.getLongitude());

        AlarmUtils.getInstance().startForecastUpdate(context);

    }

}
