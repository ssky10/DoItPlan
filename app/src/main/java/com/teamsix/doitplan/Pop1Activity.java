package com.teamsix.doitplan;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.teamsix.doitplan.background.AlarmUtils;
import com.teamsix.doitplan.background.ForecastBraodCastReciever;
import com.teamsix.doitplan.background.GPStracker;

import static com.teamsix.doitplan.background.ForecastBraodCastReciever.forecast;


public class Pop1Activity extends AppCompatActivity {

    private TextView txtText;
    private TextInputLayout text1;
    private TextInputLayout text2;
    private TimePicker time;
    private LinearLayout day;
    private LinearLayout weat;
    private CheckBox sun;
    private CheckBox mon;
    private CheckBox tue;
    private CheckBox wed;
    private CheckBox thu;
    private CheckBox fri;
    private CheckBox sat;
    private CheckBox sunny;
    private CheckBox rain;
    private CheckBox cloud;
    int data;
    int planNo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pop1);

        //UI 객체생성
        txtText = (TextView)findViewById(R.id.txtText);
        text1 = (TextInputLayout)findViewById(R.id.text1);
        text2 = (TextInputLayout)findViewById(R.id.text2);
        time = (TimePicker)findViewById(R.id.timePicker);
        day = (LinearLayout)findViewById(R.id.days);
        weat = (LinearLayout)findViewById(R.id.weather);
        sun = (CheckBox)findViewById(R.id.checkBox0);
        mon = (CheckBox)findViewById(R.id.checkBox1);
        tue = (CheckBox)findViewById(R.id.checkBox2);
        wed = (CheckBox)findViewById(R.id.checkBox3);
        thu = (CheckBox)findViewById(R.id.checkBox4);
        fri = (CheckBox)findViewById(R.id.checkBox5);
        sat = (CheckBox)findViewById(R.id.checkBox6);
        sunny = (CheckBox)findViewById(R.id.checkBox7);
        rain = (CheckBox)findViewById(R.id.checkBox9);
        cloud = (CheckBox)findViewById(R.id.checkBox8);

        time.setVisibility(View.GONE);

        //데이터 가져오기
        Intent intent = getIntent();
        data = intent.getIntExtra("if",1);
        int type = intent.getIntExtra("type", 0);
        planNo = intent.getIntExtra("planno",-1);

        if(data==Plan.IF_CALL) {
            txtText.setText("전화가 왔을 때");
            text1.setVisibility(View.GONE);
            text2.setHint("번호");

        }else if(data==Plan.IF_PHONE) {
            txtText.setText("문자가 왔을 때");
            text1.setHint("문자열");
            text2.setHint("번호");

        }else if(data==Plan.IF_KAKAO) {
            txtText.setText("카카오톡 메세지가 왔을 때");
            text1.setHint("문자열");
            text2.setHint("보낸 사람");
            EditText text = (EditText)findViewById(R.id.txtText2);
            text.setInputType(InputType.TYPE_CLASS_TEXT);

        }else if(data==Plan.IF_TIME) {
            txtText.setText("특정시간이 되었을 때");
            text1.setVisibility(View.GONE);
            text2.setVisibility(View.GONE);
            time.setVisibility(View.VISIBLE);
            day.setVisibility(View.VISIBLE);

        }else if(data==Plan.IF_WEATHER) {
            txtText.setText("날씨가 특정조건이 되었을 때");
            text1.setVisibility(View.GONE);
            text2.setVisibility(View.GONE);
            weat.setVisibility(View.VISIBLE);

        }else if(data==Plan.IF_BATTERY) {
            txtText.setText("배터리가 일정수준 이하가 되었을 때");
            text2.setHint("퍼센트");
            text1.setVisibility(View.GONE);

        }

        if(type == 1) setData();
    }

    private void setData(){
        Log.e("type1",String.valueOf(data));
        Intent intent = getIntent();

        if(data==Plan.IF_CALL) {
            text2.getEditText().setText(intent.getStringExtra("callnum"));
        }else if(data==Plan.IF_PHONE) {
            text1.getEditText().setText(intent.getStringExtra("phonestring"));
            text2.getEditText().setText(intent.getStringExtra("phonenum"));
        }else if(data==Plan.IF_KAKAO) {
            text1.getEditText().setText(intent.getStringExtra("kakaostring"));
            text2.getEditText().setText(intent.getStringExtra("kakaopeople"));

        }else if(data==Plan.IF_TIME) {
            String[] spl = intent.getStringExtra("timeclock").split(":");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                time.setHour(Integer.valueOf(spl[0]));
                time.setMinute(Integer.valueOf(spl[1]));
            }else{
                time.setCurrentHour(Integer.valueOf(spl[0]));
                time.setCurrentMinute(Integer.valueOf(spl[1]));
            }

            sun.setChecked(intent.getBooleanExtra("timedaysun",false));
            mon.setChecked(intent.getBooleanExtra("timedaymon",false));
            tue.setChecked(intent.getBooleanExtra("timedaytue",false));
            wed.setChecked(intent.getBooleanExtra("timedaywed",false));
            thu.setChecked(intent.getBooleanExtra("timedaythu",false));
            fri.setChecked(intent.getBooleanExtra("timedayfri",false));
            sat.setChecked(intent.getBooleanExtra("timedaysat",false));

        }else if(data==Plan.IF_WEATHER) {
            sunny.setChecked(intent.getBooleanExtra("timedaysunny",false));
            rain.setChecked(intent.getBooleanExtra("timedayrain",false));
            cloud.setChecked(intent.getBooleanExtra("timedaycloud",false));
        }else if(data==Plan.IF_BATTERY) {
            text2.getEditText().setText(intent.getStringExtra("betterypercent"));
        }
    }

    //확인 버튼 클릭
    public void mOnClose(View v){
        //데이터 전달하기

        Log.e("type1",String.valueOf(data));
        Intent intent = new Intent();
        intent.putExtra("if", data);
        if(planNo != -1)
            intent.putExtra("planno",planNo);
        if(data==Plan.IF_CALL) {
            intent.putExtra("callnum", text2.getEditText().getText().toString());
        }else if(data==Plan.IF_PHONE) {
            intent.putExtra("phonestring", text1.getEditText().getText().toString());
            intent.putExtra("phonenum", text2.getEditText().getText().toString());
        }else if(data==Plan.IF_KAKAO) {
            intent.putExtra("kakaostring", text1.getEditText().getText().toString());
            intent.putExtra("kakaopeople", text2.getEditText().getText().toString());
        }else if(data==Plan.IF_TIME) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                intent.putExtra("timeclock", time.getHour()+":"+time.getMinute());
            }else{
                intent.putExtra("timeclock", time.getCurrentHour()+":"+time.getCurrentMinute());
            }
            intent.putExtra("timedaysun", sun.isChecked());
            intent.putExtra("timedaymon", mon.isChecked());
            intent.putExtra("timedaytue", tue.isChecked());
            intent.putExtra("timedaywed", wed.isChecked());
            intent.putExtra("timedaythu", thu.isChecked());
            intent.putExtra("timedayfri", fri.isChecked());
            intent.putExtra("timedaysat", sat.isChecked());

        }else if(data==Plan.IF_WEATHER) {
            intent.putExtra("timedaysunny", sunny.isChecked());
            intent.putExtra("timedayrain", rain.isChecked());
            intent.putExtra("timedaycloud", cloud.isChecked());

        }else if(data==Plan.IF_BATTERY) {
            intent.putExtra("betterypercent", text1.getEditText().getText().toString());
        }
        setResult(RESULT_OK, intent);

        //액티비티(팝업) 닫기
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }

}
