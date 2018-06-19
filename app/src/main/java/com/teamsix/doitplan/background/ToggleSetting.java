package com.teamsix.doitplan.background;

import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Build;


public class ToggleSetting {
    public static void onWifi(boolean isOn, Context context){
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            wifiManager.setWifiEnabled(isOn);
        }
    }

    public static void onBluetooth(boolean isOn){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter != null) {
            if (isOn) {
                mBluetoothAdapter.enable();
            } else {
                mBluetoothAdapter.disable();
            }
        }
    }

    public static void onSilence(boolean isOn, Context context){
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if(!isOn)
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
            else
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
        }else{
            AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if(!isOn)
                mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            else
                mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT); //무음
        }
    }

}
