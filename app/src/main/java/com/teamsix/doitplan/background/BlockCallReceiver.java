package com.teamsix.doitplan.background;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.teamsix.doitplan.ApplicationController;
import com.teamsix.doitplan.GetIfResult;
import com.teamsix.doitplan.Plan;

import java.lang.reflect.Method;
import java.util.List;

public class BlockCallReceiver extends BroadcastReceiver {
    int mode = -1;
    boolean isDoing = false;

    @SuppressLint("WrongConstant")
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Bundle myBundle = intent.getExtras();
        if (myBundle != null)
        {
            System.out.println("--------Not null-----");
            try
            {
                if (intent.getAction().equals("android.intent.action.PHONE_STATE"))
                {
                    String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                    System.out.println("--------in state-----");
                    if (state.equals(TelephonyManager.EXTRA_STATE_RINGING))
                    {
                        // Incoming call
                        String incomingNumber =intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                        System.out.println("--------------my number---------"+incomingNumber);

                        List<Plan> list = GetIfResult.getBoolean(ApplicationController.getIfPlanDB(Plan.IF_CALL),incomingNumber);
                        if(list.size()==0) return;
                        for(int i=0;i<list.size();i++){
                            GetIfResult.doitResult(list.get(i).resultCode,list.get(i).resultValue,context);
                        }
                        Log.e("CallStateReceiver",String.valueOf(System.currentTimeMillis()-ApplicationController.getEndcall()));
                        if((System.currentTimeMillis()-ApplicationController.getEndcall()>5000)
                                ||(ApplicationController.getEndcall() == -1)) return;

                        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
                            NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

                            //am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                            if(mode == -1){
                                mode = notificationManager.getCurrentInterruptionFilter();
                            }
                            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
                            //am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                        }else{
                            // this is main section of the code,. could also be use for particular number.
                            // Get the boring old TelephonyManager.
                            TelephonyManager telephonyManager =(TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

                            // Get the getITelephony() method
                            Class<?> classTelephony = Class.forName(telephonyManager.getClass().getName());
                            Method methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony");

                            // Ignore that the method is supposed to be private
                            methodGetITelephony.setAccessible(true);

                            // Invoke getITelephony() to get the ITelephony interface
                            Object telephonyInterface = methodGetITelephony.invoke(telephonyManager);

                            // Get the endCall method from ITelephony
                            Class<?> telephonyInterfaceClass = Class.forName(telephonyInterface.getClass().getName());
                            Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");

                            // Invoke endCall()
                            methodEndCall.invoke(telephonyInterface);

                            SendNotification.sendNotification(context,"전화가 자동 거절되었습니다.",incomingNumber);
                        }
                        isDoing = true;
                    }
                    else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE))
                    {
                        Log.d("EXTRA_STATE_IDLE",mode+"");
                        if(isDoing){
                            if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
                                NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

                                if(mode != -1){
                                    notificationManager.setInterruptionFilter(mode);
                                    mode = -1;
                                }

                            }
                            isDoing = false;
                        }
                    }
                }
            }
            catch (Exception ex)
            { // Many things can go wrong with reflection calls
                ex.printStackTrace();
            }
        }
    }
}