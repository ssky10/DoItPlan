package com.teamsix.doitplan;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by khs on 2018-04-01.
 */

public class PermissionUtils {
    public static boolean requestPermission(
            Activity activity, int requestCode, String... permissions) {
        boolean granted = true;
        ArrayList<String> permissionsNeeded = new ArrayList<>();

        for (String s : permissions) {
            int permissionCheck = ContextCompat.checkSelfPermission(activity, s);
            boolean hasPermission = (permissionCheck == PackageManager.PERMISSION_GRANTED);
            granted &= hasPermission;
            if (!hasPermission) {
                permissionsNeeded.add(s);
            }
        }

        if (granted) {
            return true;
        } else {
            ActivityCompat.requestPermissions(activity,
                    permissionsNeeded.toArray(new String[permissionsNeeded.size()]),
                    requestCode);
            return false;
        }
    }


    public static boolean permissionGranted(
            int requestCode, int permissionCode, int[] grantResults) {
        if (requestCode == permissionCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public static boolean permissionWhitelist(final Activity activity){
        final Activity mainActivity = activity;
        AlertDialog.Builder dialog = new AlertDialog.Builder(mainActivity,android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
        dialog.setTitle(R.string.white_title)
                .setMessage(R.string.white_msg)
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent  = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                        intent.setData(Uri.parse("package:"+ mainActivity.getPackageName()));
                        mainActivity.startActivity(intent);
                    }
                })
                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(mainActivity, "권한 설정을 취소했습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setCancelable(false)
                .show();
        return true;
    }

    public static boolean isNotiPermissionAllowed(final Activity activity) {
        Set<String> notiListenerSet = NotificationManagerCompat.getEnabledListenerPackages(activity);
        String myPackageName = activity.getPackageName();

        for(String packageName : notiListenerSet) {
            if(packageName == null) {
                continue;
            }
            if(packageName.equals(myPackageName)) {
                return true;
            }
        }

        return false;
    }
}
