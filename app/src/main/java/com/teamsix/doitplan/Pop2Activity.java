package com.teamsix.doitplan;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.maps.MapView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pop2Activity extends AppCompatActivity {

    private TextView txtText;
    private TextInputLayout text1;
    private TextInputLayout text2;
    private MapView map;
    private LinearLayout sett;
    private CheckBox blue;
    private CheckBox silence;
    private ListView listview;
    private ArrayList<String> appNameList = new ArrayList<>();
    private ArrayList<String> appPackageList = new ArrayList<>();
    int data;
    int planNo;
    private TextView selText;
    private int selNum = 0;
    private CheckBox wifi;
    private LinearLayout noti;
    private RadioGroup rg;
    private Switch blueIsOn;
    private Switch silenceIsOn;
    private Switch wifiIsOn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pop2);

        //UI 객체생성
        txtText = (TextView) findViewById(R.id.txtText);
        text1 = (TextInputLayout) findViewById(R.id.text1);
        text2 = (TextInputLayout) findViewById(R.id.text2);
        sett = (LinearLayout) findViewById(R.id.setting);
        noti = (LinearLayout) findViewById(R.id.layoutNotification);
        blue = (CheckBox) findViewById(R.id.checkBox7);
        silence = (CheckBox) findViewById(R.id.checkBox8);
        wifi = (CheckBox) findViewById(R.id.checkBox9);
        blueIsOn = (Switch) findViewById(R.id.switch7);
        silenceIsOn = (Switch) findViewById(R.id.switch8);
        wifiIsOn = (Switch) findViewById(R.id.switch9);
        listview = (ListView) findViewById(R.id.list);
        selText = (TextView) findViewById(R.id.tv_sel);
        rg = (RadioGroup)findViewById(R.id.radioGroup1);

        //데이터 가져오기
        Intent intent = getIntent();
        data = intent.getIntExtra("Result", 0);
        int type = intent.getIntExtra("type", 0);
        planNo = intent.getIntExtra("planno",-1);

        if (data == Plan.RESULT_CALL) {
            txtText.setText("전화를 건다");
            text1.setVisibility(View.GONE);
            text2.setVisibility(View.GONE);

        } else if (data == Plan.RESULT_PHONE) {
            txtText.setText("문자를 보낸다");
            text1.setHint("내용");
            text2.setHint("받는 사람");

        } else if (data == Plan.RESULT_KAKAO) {
            txtText.setText("나에게 보내기를 한다.");
            text1.setHint("내용");
            text2.setVisibility(View.GONE);

        } else if (data == Plan.RESULT_APP) {
            txtText.setText("특정어플리케이션을 실행한다.");
            getPackageList();
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, appNameList);
            listview.setAdapter(adapter);
            text1.setVisibility(View.GONE);
            text2.setVisibility(View.GONE);
            listview.setVisibility(View.VISIBLE);
            selText.setVisibility(View.VISIBLE);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    selText.setText("선택된 어플리케이션 : " + appNameList.get(i));
                    selNum = i;
                }
            });

        } else if (data == Plan.RESULT_WEATHER) {
            txtText.setText("날씨를 알려준다.");
            text1.setVisibility(View.GONE);
            text2.setVisibility(View.GONE);

        } else if (data == Plan.RESULT_ALARM) {
            txtText.setText("알림메세지를 보낸다.");
            text1.setHint("내용");
            text2.setVisibility(View.GONE);
            noti.setVisibility(View.VISIBLE);
        } else if (data == Plan.RESULT_NAVER) {
            txtText.setText("naver 검색을 한다.");
            text1.setHint("검색할 내용");
            text2.setVisibility(View.GONE);

        } else if (data == Plan.RESULT_SETTING) {
            txtText.setText("블루투스, Wifi 등 단말기의 설정을 on, off한다.");
            text1.setVisibility(View.GONE);
            text2.setVisibility(View.GONE);
            sett.setVisibility(View.VISIBLE);
            blue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    blueIsOn.setClickable(b);
                }
            });
            silence.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    silenceIsOn.setClickable(b);
                }
            });
            wifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    wifiIsOn.setClickable(b);
                }
            });
        }

        if(type == 2) setData();
    }

    private void setData(){
        Log.e("type1",String.valueOf(data));
        Intent intent = getIntent();

        if (data == Plan.RESULT_PHONE) {
            text1.getEditText().setText(intent.getStringExtra("phonetext"));
            text2.getEditText().setText(intent.getStringExtra("phonepeople"));
        } else if (data == Plan.RESULT_KAKAO) {
            text1.getEditText().setText(intent.getStringExtra("kakaostring"));
        } else if (data == Plan.RESULT_APP) {
            int idx = appPackageList.indexOf(intent.getStringExtra("appPackage"));
            if(idx == -1) idx = 0;
            selText.setText("선택된 어플리케이션 : " + appNameList.get(idx));
            selNum = idx;
        } else if (data == Plan.RESULT_ALARM) {
            text1.getEditText().setText(intent.getStringExtra("alarmstring"));
            if(intent.getBooleanExtra("serveralarm",false))
                rg.check(R.id.cb_serverNoti);
            else
                rg.check(R.id.cb_localNoti);

        } else if (data == Plan.RESULT_NAVER) {
            text1.getEditText().setText(intent.getStringExtra("naverstring"));
        } else if (data == Plan.RESULT_SETTING) {
            blue.setChecked(intent.getBooleanExtra("setblue",false));
            blueIsOn.setChecked(intent.getBooleanExtra("isBlueOn",false));
            silence.setChecked(intent.getBooleanExtra("setsilence",false));
            silenceIsOn.setChecked(intent.getBooleanExtra("isSilenceOn",false));
            wifi.setChecked(intent.getBooleanExtra("setwifi",false));
            wifiIsOn.setChecked(intent.getBooleanExtra("isWifiOn",false));
        }
    }

    //확인 버튼 클릭
    public void mOnClose(View v) {
        //데이터 전달하기

        Intent intent = new Intent();
        intent.putExtra("Result", data);
        if(planNo != -1)
            intent.putExtra("planno",planNo);

        if (data == Plan.RESULT_PHONE) {
            intent.putExtra("phonetext", text1.getEditText().getText().toString());
            intent.putExtra("phonepeople", text2.getEditText().getText().toString());

        } else if (data == Plan.RESULT_KAKAO) {
            intent.putExtra("kakaostring", text1.getEditText().getText().toString());
        } else if (data == Plan.RESULT_APP) {
            intent.putExtra("appPackage", appPackageList.get(selNum));
            intent.putExtra("appName", appNameList.get(selNum));
        } else if (data == Plan.RESULT_ALARM) {
            intent.putExtra("alarmstring", text1.getEditText().getText().toString());
            if(rg.getCheckedRadioButtonId()==R.id.cb_localNoti)
                intent.putExtra("serveralarm", false);
            else
                intent.putExtra("serveralarm", true);

        } else if (data == Plan.RESULT_NAVER) {
            intent.putExtra("naverstring", text1.getEditText().getText().toString());

        } else if (data == Plan.RESULT_SETTING) {
            intent.putExtra("setblue", blue.isChecked());
            intent.putExtra("isBlueOn",blueIsOn.isChecked());
            intent.putExtra("setsilence", silence.isChecked());
            intent.putExtra("isSilenceOn",silenceIsOn.isChecked());
            intent.putExtra("setwifi", wifi.isChecked());
            intent.putExtra("isWifiOn",wifiIsOn.isChecked());
        }
        setResult(RESULT_OK, intent);

        //액티비티(팝업) 닫기
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }

    public void getPackageList() {
        Log.e("getPackageList", "start");
        PackageManager pkgMgr = Pop2Activity.this.getPackageManager();
        List<ResolveInfo> mApps;

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mApps = pkgMgr.queryIntentActivities(mainIntent, 0); // 실행가능한 Package만 추출.

        //arrayPkgName = new String[mApps.size()];
        Collections.sort(mApps, new ResolveInfo.DisplayNameComparator(pkgMgr));

        for (int i = 0; i < mApps.size(); i++) {
            appNameList.add(mApps.get(i).activityInfo.loadLabel(pkgMgr).toString());
            appPackageList.add(mApps.get(i).activityInfo.packageName);
            Log.e("getPackageList", mApps.get(i).activityInfo.packageName);
        }

    }

}
