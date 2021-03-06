package com.teamsix.doitplan.widget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;
import com.teamsix.doitplan.BuildConfig;
import com.teamsix.doitplan.Plan;
import com.teamsix.doitplan.R;
import com.teamsix.doitplan.ResultListActivity;

import static com.teamsix.doitplan.Plan.resultIntentToSting;

public class WidgetSettingActivity extends AppCompatActivity implements ColorPickerDialogListener {

    private int mAppWidgetId;
    private EditText widgetTitle;
    private Button btn;

    private AppWidgetManager appWidgetManager;
    private RemoteViews remoteView;

    private String color;
    private Button btn_result;

    private Intent data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_setting);
        Bundle mExtras = getIntent().getExtras();
        if (mExtras != null) {
            mAppWidgetId = mExtras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        appWidgetManager = AppWidgetManager.getInstance(this);
        remoteView = new RemoteViews(this.getPackageName(),
                R.layout.widget_style);

        widgetTitle = (EditText) findViewById(R.id.editText);

        btn = (Button)findViewById(R.id.btn_color_picker);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPickerDialog.newBuilder()
                        .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                        .setAllowPresets(false)
                        .setDialogId(1)
                        .setColor(Color.WHITE)
                        .setShowAlphaSlider(false)
                        .show(WidgetSettingActivity.this);
            }
        });

        btn_result = (Button)findViewById(R.id.btn_get_result);
        btn_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ResultListActivity.class);
                startActivityForResult(intent,1);
            }
        });
    }

    public void getWidget(View view) {
        Log.e("getWidget","start");
        if(data==null){
            Toast.makeText(getApplicationContext(),"결과를 선택해주세요",Toast.LENGTH_SHORT).show();
            return;
        }
        String mText = widgetTitle.getText().toString();

        SharedPreferences widgetData = getSharedPreferences(mAppWidgetId+"WidgetData", MODE_PRIVATE);
        SharedPreferences.Editor e = widgetData.edit();
        e.putInt("color",Color.parseColor(color));
        e.putString("text",mText);
        e.putInt("code",data.getIntExtra("Result",0));
        e.putString("value",resultIntentToSting(data));
        e.apply();

        Log.e("getWidget",String.valueOf(data.getIntExtra("result",0)));

        WidgetProvider.updateWidget(this,appWidgetManager,mAppWidgetId);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_CANCELED, resultValue);
        finish();
    }

    @Override
    public void onColorSelected(int dialogId, final int color) {
        final int invertColor = ~color;
        this.color = String.format("#%X", color);
        String hexInvertColor = String.format("%X", invertColor);
        if (BuildConfig.DEBUG) {
            Toast.makeText(this, "id " + dialogId + " c: " + this.color + " i:" + hexInvertColor, Toast.LENGTH_SHORT).show();
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btn.setBackgroundColor(color);
            }
        });
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            if(resultCode == RESULT_OK){
                int code = data.getIntExtra("Result",0);
                this.data = data;
                btn_result.setText(Plan.RESULT_STRLONG[code]);
            }
        }
    }
}
