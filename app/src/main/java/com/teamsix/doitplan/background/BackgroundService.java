package com.teamsix.doitplan.background;

import android.app.Notification;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.teamsix.doitplan.ApplicationController;
import com.teamsix.doitplan.GetIfResult;
import com.teamsix.doitplan.Plan;

import java.util.List;

public class BackgroundService extends Service implements ClipboardManager.OnPrimaryClipChangedListener {
    public static boolean isLaunched = false;
    public static boolean isBetteryServiceLaunched = false;
    private BetteryReceiver bReceiver;
    ClipboardManager mManager;

    @Override
    public void onCreate() {
        super.onCreate();
        isLaunched = true;
        isBetteryServiceLaunched = true;
        mManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        mManager.addPrimaryClipChangedListener(this);
        bReceiver = BetteryReceiver.getInstance();
        getApplicationContext().registerReceiver(bReceiver,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(-4,new Notification());
        }
    }

    @Override
    public void onDestroy() {
        isLaunched = false;
        isBetteryServiceLaunched = false;
        super.onDestroy();
        // 리스너 해제
        mManager.removePrimaryClipChangedListener(this);
        getApplicationContext().unregisterReceiver(bReceiver);
        Log.e("Test", "BackgroundService onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onPrimaryClipChanged() {
        ApplicationController.setClipChangeTime(System.currentTimeMillis());
        Log.e("Test", "onPrimaryClipChanged()");
        if (mManager != null && mManager.getPrimaryClip() != null) {
            ClipData data = mManager.getPrimaryClip();

            // 한번의 복사로 복수 데이터를 넣었을 수 있으므로, 모든 데이터를 가져온다.
            int dataCount = data.getItemCount();
            for (int i = 0 ; i < dataCount ; i++) {
                List<Plan> list = GetIfResult.getBoolean(ApplicationController.getIfPlanDB(Plan.IF_CLIP),data.getItemAt(i).coerceToText(this).toString());
                for(int j=0;j<list.size();j++){
                    if(list.get(j).resultCode==Plan.RESULT_CALL) ;
                    else GetIfResult.doitResult(list.get(j).resultCode,list.get(j).resultValue,getApplicationContext());
                }
                Log.e("Test", "clip data - item : "+data.getItemAt(i).coerceToText(this));
            }
        } else {
            Log.e("Test", "No Manager or No Clip data");
        }
    }
}
