package com.teamsix.doitplan.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.teamsix.doitplan.ApplicationController;
import com.teamsix.doitplan.GetIfResult;
import com.teamsix.doitplan.Plan;

import java.util.List;

public class MyReceiver extends BroadcastReceiver {
    /** * 안드로이드에 문자메시지가 도착할 경우 실행된다. * @param context * @param intent */

    @Override
    public void onReceive(Context context, Intent intent) {

        SmsManager sms = SmsManager.getDefault();


        //Toast.makeText(context,"문자메시지가 도착했습니다!!",Toast.LENGTH_SHORT).show();

        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        String str = "";
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            for (int i = 0; i < msgs.length; i++) {
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                str += "SMS from " + msgs[i].getOriginatingAddress();
                str += " :";
                str += msgs[i].getMessageBody().toString();
                str += "\n";
                List<Plan> list = GetIfResult.getBoolean(ApplicationController.getIfPlanDB(Plan.IF_PHONE),msgs[i].getOriginatingAddress(),msgs[i].getMessageBody().toString());
                if(list.size()==0) break;
                for(int j=0;j<list.size();j++){
                    if(list.get(j).resultCode==Plan.RESULT_CALL) ;
                    else GetIfResult.doitResult(list.get(j).resultCode,list.get(j).resultValue,context);
                }
                //sms.sendTextMessage(msgs[i].getOriginatingAddress(), null, msgs[i].getMessageBody().toString(), null, null);
            }
            //Toast.makeText(context, str, Toast.LENGTH_SHORT).show();

        }
    }
}
