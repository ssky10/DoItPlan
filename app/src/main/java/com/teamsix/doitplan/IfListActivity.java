package com.teamsix.doitplan;

import android.Manifest;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.teamsix.doitplan.background.ClipboardService;

public class IfListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_if_list);
    }


    public void btnClick(View view){
        Intent intent;
        switch (view.getId()){
            case R.id.button2:
                intent = new Intent(this, Pop1Activity.class);
                intent.putExtra("if", Plan.IF_CALL);
                startActivityForResult(intent, 1);
                break;
            case R.id.button3:
                intent = new Intent(this, Pop1Activity.class);
                intent.putExtra("if", Plan.IF_PHONE);
                startActivityForResult(intent, 1);
                break;
            case R.id.button4:
                boolean isPermissionAllowed = PermissionUtils.isNotiPermissionAllowed(this);
                if(!isPermissionAllowed) {
                    Intent intentPermission = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                    startActivity(intentPermission);
                }else{
                    intent = new Intent(this, Pop1Activity.class);
                    intent.putExtra("if", Plan.IF_KAKAO);
                    startActivityForResult(intent, 1);
                }
                break;
            case R.id.button5:
                intent = new Intent(this, Pop1Activity.class);
                intent.putExtra("if", Plan.IF_TIME);
                startActivityForResult(intent, 1);
                break;
            case R.id.button6:
                intent = new Intent(this, Pop1Activity.class);
                intent.putExtra("if", Plan.IF_WEATHER);
                startActivityForResult(intent, 1);
                break;
            case R.id.button7:
                intent = new Intent(this, Pop1Activity.class);
                intent.putExtra("if", Plan.IF_BATTERY);
                startActivityForResult(intent, 1);
                break;
            case R.id.button8:
                intent = new Intent();
                intent.putExtra("if", Plan.IF_CLIP);
                intent.putExtra("type","if");
                setResult(RESULT_OK, intent);
                //액티비티(팝업) 닫기
                finish();
                break;
            case R.id.button9:
                if(PermissionUtils.requestPermission(this,123,Manifest.permission.ACCESS_FINE_LOCATION)){
                    intent = new Intent(this, MapsActivity.class);
                    intent.putExtra("if", Plan.IF_LOC);
                    startActivityForResult(intent, 1);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("onActivityResult","IfListActivity-start");
        if(requestCode==1){
            if(resultCode==RESULT_OK){
                Intent intent = new Intent(data);
                intent.putExtra("type","if");
                setResult(RESULT_OK, intent);
                //액티비티(팝업) 닫기
                finish();
            }
        }
        Log.e("onActivityResult","IfListActivity-end");
    }
}
