package com.teamsix.doitplan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
                intent = new Intent(this, Pop1Activity.class);
                intent.putExtra("if", Plan.IF_KAKAO);
                startActivityForResult(intent, 1);
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
                break;
            case R.id.button9:
                intent = new Intent(this, MapsActivity.class);
                intent.putExtra("if", Plan.IF_LOC);
                startActivityForResult(intent, 1);
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
