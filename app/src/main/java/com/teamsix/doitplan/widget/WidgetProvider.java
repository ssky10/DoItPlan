package com.teamsix.doitplan.widget;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.widget.RemoteViews;

import com.teamsix.doitplan.R;


public class WidgetProvider extends AppWidgetProvider {

    /**
     * 브로드캐스트를 수신할때, Override된 콜백 메소드가 호출되기 직전에 호출됨
     */
    @Override
    public void onReceive(Context context, Intent intent) {
            super.onReceive(context, intent);
    }

    /**
     * 위젯을 갱신할때 호출됨
     *
     * 주의 : Configure Activity를 정의했을때는 위젯 등록시 처음 한번은 호출이 되지 않습니다
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId);
        }
    }

    /**
     * 위젯이 처음 생성될때 호출됨
     *
     * 동일한 위젯이 생성되도 최초 생성때만 호출됨
     */
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    /**
     * 위젯의 마지막 인스턴스가 제거될때 호출됨
     *
     * onEnabled()에서 정의한 리소스 정리할때
     */
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    /**
     * 위젯이 사용자에 의해 제거될때 호출됨
     */
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        for (int appWidgetId : appWidgetIds) {
            SharedPreferences pref = context.getSharedPreferences(appWidgetId+"WidgetData", Activity.MODE_PRIVATE);
            pref.edit().clear().apply();
        }
    }

    static void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId){
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_style);

        Log.e("WidgetData",appWidgetId+"");

        SharedPreferences pref = context.getSharedPreferences(appWidgetId+"WidgetData", Activity.MODE_PRIVATE);

        Intent intent = new Intent(context, WidgetService.class);
        intent.setAction("com.teamsix.doitplan.BUTTON_CLICK");
        intent.putExtra("result",pref.getInt("code",0));
        intent.putExtra("value",pref.getString("value",""));
        Log.e("value",pref.getString("value",""));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent re_btn = PendingIntent.getBroadcast(context,
                appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.wImage, re_btn);

        views.setTextViewText(R.id.wTitle,pref.getString("text",""));
        views.setInt(R.id.wImage,"setColorFilter",pref.getInt("color", Color.BLACK));

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}