package com.teamsix.doitplan.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.teamsix.doitplan.ApplicationController;
import com.teamsix.doitplan.GetIfResult;
import com.teamsix.doitplan.Plan;

import java.lang.reflect.Method;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by khs on 2018-04-07.
 */

public class CallStateReceiver extends BroadcastReceiver {
    public static boolean isLaunched = false;

    @Override
    public void onReceive(final Context context, Intent intent) {
        isLaunched = true;
        final TelephonyManager telephony;
        Log.e(TAG, "start1!");
        telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(new PhoneStateListener(){

            public void onCallStateChanged(int state, String incommingNumber){

                boolean endCall = false;
                switch(state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        Log.d(TAG, " CALL_STATE_RINGING.:" + incommingNumber);
                        List<Plan> list = GetIfResult.getBoolean(ApplicationController.getIfPlanDB(Plan.IF_CALL),incommingNumber);
                        if(list.size()==0) break;
                        for(int i=0;i<list.size();i++){
                            GetIfResult.doitResult(list.get(i).resultCode,list.get(i).resultValue,context);
                        }
                        Log.e("CallStateReceiver",String.valueOf(System.currentTimeMillis()-ApplicationController.getEndcall()));
                        if((System.currentTimeMillis()-ApplicationController.getEndcall()>5000)
                                ||(ApplicationController.getEndcall() == -1)) break;
                        try {
                            Class c = Class.forName(telephony.getClass().getName());
                            Method m = c.getDeclaredMethod("getITelephony");
                            m.setAccessible(true);
                            ITelephony telephonyService = (ITelephony) m.invoke(telephony);
                            telephonyService.endCall();

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "FATAL ERROR: could not connect to telephony subsystem");
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        Log.d(TAG,"CALL_STATE_IDLE ");
                        break;

                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        Log.d(TAG,"CALL_STATE_OFFHOOK ");
                        break;
                }
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);
    }
}
