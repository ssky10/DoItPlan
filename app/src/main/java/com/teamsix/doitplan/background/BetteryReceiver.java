package com.teamsix.doitplan.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.teamsix.doitplan.ApplicationController;
import com.teamsix.doitplan.GetIfResult;
import com.teamsix.doitplan.Plan;

import java.util.List;

public class BetteryReceiver extends BroadcastReceiver {
    public static boolean isLaunched = false;
    public static int level = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        isLaunched = true;
        int newLevel = intent.getIntExtra("level", 0);
        if(level == newLevel) return;

        Log.e("Battery Level", level + "");

        level = newLevel;

        List<Plan> list = GetIfResult.getBoolean(ApplicationController.getIfPlanDB(Plan.IF_BATTERY),level+"");
        if(list.size()==0) return;
        for(int j=0;j<list.size();j++){
            GetIfResult.doitResult(list.get(j).resultCode,list.get(j).resultValue,context);
        }
    }
}
