package com.teamsix.doitplan.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.teamsix.doitplan.ApplicationController;
import com.teamsix.doitplan.GetIfResult;
import com.teamsix.doitplan.Plan;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AlarmBraodCastReciever extends BroadcastReceiver {
    public static boolean isLaunched = false;


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("AlarmBraodCastReciever","start");
        isLaunched = true;

        int planNo = intent.getIntExtra("planNo",-1);
        if(planNo == -1) return;

        Plan plan = ApplicationController.getWorkPlan(planNo);
        if(plan == null) return;

        plan.resultValue = plan.resultValue.replace("{시간}",new SimpleDateFormat("yyyy/MM/dd(E) HH:mm", Locale.KOREAN).format(System.currentTimeMillis()));

        if(plan.resultCode==Plan.RESULT_CALL) ApplicationController.setEndcall(System.currentTimeMillis()+(55*1000));
        else GetIfResult.doitResult(plan.resultCode,plan.resultValue,context);

        AlarmUtils.getInstance().startAlarmUpdate(context,planNo);
    }


}
