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
    public static BetteryReceiver instance;
    public static int level = 0;

    public static BetteryReceiver getInstance(){
        if(isLaunched) return instance;
        instance = new BetteryReceiver();
        isLaunched = true;
        return instance;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int newLevel = intent.getIntExtra("level", 0);
        if(level == newLevel) return;

        Log.e("Battery Level", newLevel + "!");

        level = newLevel;

        List<Plan> list = GetIfResult.getBoolean(ApplicationController.getIfPlanDB(Plan.IF_BATTERY));
        if(list.size()==0) return;
        for(int j=0;j<list.size();j++){
            GetIfResult.doitResult(list.get(j).resultCode,list.get(j).resultValue,context);
        }
    }
}
