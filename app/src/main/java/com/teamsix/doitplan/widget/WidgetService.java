package com.teamsix.doitplan.widget;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.teamsix.doitplan.GetIfResult;


public class WidgetService extends BroadcastReceiver {
    public WidgetService() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("onReceive","start!");
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        if (intent.getAction().equals("com.teamsix.doitplan.BUTTON_CLICK")) {
            int viewIndex = intent.getIntExtra("result", 0);
            Log.e("onReceive","viewIndex-"+viewIndex);
            String resultValue = intent.getStringExtra("value");
            Log.e("resultValue:onReceive",resultValue);
            GetIfResult.doitResult(viewIndex,resultValue,context);
        }
    }

}
