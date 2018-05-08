package com.teamsix.doitplan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ResultListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_list_);
    }


    public void btnClick(View view){
        Intent intent;
        switch (view.getId()){
            case R.id.button11:
                intent = new Intent(this, Pop2Activity.class);
                intent.putExtra("Result", Plan.RESULT_CALL);
                startActivityForResult(intent, 1);
                break;
            case R.id.button12:
                intent = new Intent(this, Pop2Activity.class);
                intent.putExtra("Result", Plan.RESULT_PHONE);
                startActivityForResult(intent, 1);
                break;
            case R.id.button13:
                intent = new Intent(this, Pop2Activity.class);
                intent.putExtra("Result", Plan.RESULT_KAKAO);
                startActivityForResult(intent, 1);
                break;
            case R.id.button14:
                intent = new Intent(this, Pop2Activity.class);
                intent.putExtra("Result", Plan.RESULT_APP);
                startActivityForResult(intent, 1);
                break;
            case R.id.button15:
                intent = new Intent(this, Pop2Activity.class);
                intent.putExtra("Result", Plan.RESULT_WEATHER);
                startActivityForResult(intent, 1);
                break;
            case R.id.button16:
                intent = new Intent(this, Pop2Activity.class);
                intent.putExtra("Result", Plan.RESULT_ALARM);
                startActivityForResult(intent, 1);
                break;
            case R.id.button18:
                intent = new Intent(this, Pop2Activity.class);
                intent.putExtra("Result", Plan.RESULT_NAVER);
                startActivityForResult(intent, 1);
                break;
            case R.id.button19:
                intent = new Intent(this, Pop2Activity.class);
                intent.putExtra("Result", Plan.RESULT_SETTING);
                startActivityForResult(intent, 1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1){
            if(resultCode==RESULT_OK){
                Intent intent = new Intent(data);
                intent.putExtra("type","Result");
                setResult(RESULT_OK, intent);
                //액티비티(팝업) 닫기
                finish();
            }
        }
    }
}
