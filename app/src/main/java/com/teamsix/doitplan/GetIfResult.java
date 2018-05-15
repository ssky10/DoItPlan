package com.teamsix.doitplan;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.ToggleButton;

import com.teamsix.doitplan.background.SendNotification;
import com.teamsix.doitplan.background.ToggleSetting;
import com.teamsix.doitplan.kakao.KakaoSelfSend;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetIfResult {
    public static List getBoolean(List plans, String... args){
        List<Plan> results = new ArrayList<>();
        Plan plan;
        for(int i=0; i<plans.size();i++){
            plan = ((Plan)plans.get(i)).clone();
            if(getIfBoolean(plan.ifCode,plan.ifValue,args)){
                switch (plan.ifCode) {
                    case Plan.IF_CALL:
                        plan.resultValue = plan.resultValue.replace("{발신자}",args[0]);
                break;
                    case Plan.IF_PHONE:
                        plan.resultValue = plan.resultValue.replace("{발신자}",args[0]);
                        plan.resultValue = plan.resultValue.replace("{내용}",args[1]);
                        break;
                    case Plan.IF_KAKAO:
                        plan.resultValue = plan.resultValue.replace("{발신자}",args[0]);
                        plan.resultValue = plan.resultValue.replace("{내용}",args[1]);
                        break;
                    case Plan.IF_TIME:
                        break;
                    case Plan.IF_WEATHER:
                        plan.resultValue = plan.resultValue.replace("{하늘상태}",ApplicationController.getForecast().getSky());
                        plan.resultValue = plan.resultValue.replace("{기온}",ApplicationController.getForecast().getTemperature()+"℃");
                        plan.resultValue = plan.resultValue.replace("{강수량}",ApplicationController.getForecast().getRain()+"mm");
                        break;
                    case Plan.IF_BATTERY:
                        plan.resultValue = plan.resultValue.replace("{퍼센트}",args[0]);
                        break;
                    case Plan.IF_CLIP:
                        plan.resultValue = plan.resultValue.replace("{내용}",args[0]);
                        break;
                    case Plan.IF_LOC:
                        plan.resultValue = plan.resultValue.replace("{위치}",args[0]);
                        break;
                }
                results.add(plan);
            }
        }
        return results;
    }
    private static boolean getIfBoolean(int code, String value, String... args){
        try {
            JSONObject values = new JSONObject(value);
            Pattern p; Matcher m;

            switch (code) {
                case Plan.IF_CALL:
                    p = Pattern.compile(values.getString("callnum"));
                    m = p.matcher(args[0]);
                    return m.matches();
                case Plan.IF_PHONE:
                    p = Pattern.compile(values.getString("phonenum"));
                    m = p.matcher(args[0]);
                    return m.matches() || args[1].contains(values.getString("phonestring"));
                case Plan.IF_KAKAO:
                    p = Pattern.compile(values.getString("kakaopeople"));
                    m = p.matcher(args[0]);
                    return m.matches() || args[1].contains(values.getString("kakaostring"));
                case Plan.IF_TIME:
                    long now = System.currentTimeMillis();
                    // 현재시간을 date 변수에 저장한다.
                    Date date = new Date(now);
                    SimpleDateFormat sdfNow = new SimpleDateFormat("HH:mm");
                    // nowDate 변수에 값을 저장한다.
                    String nowDate = sdfNow.format(date);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);

                    int select = cal.get(Calendar.DAY_OF_WEEK);
                    if (nowDate.equals(values.getString("timeclock"))) {
                        switch (select) {
                            case Calendar.MONDAY:
                                return values.getBoolean("timedaymon");
                            case Calendar.TUESDAY:
                                return values.getBoolean("timedaytue");
                            case Calendar.WEDNESDAY:
                                return values.getBoolean("timedaywed");
                            case Calendar.THURSDAY:
                                return values.getBoolean("timedaythu");
                            case Calendar.FRIDAY:
                                return values.getBoolean("timedayfri");
                            case Calendar.SATURDAY:
                                return values.getBoolean("timedaysat");
                            case Calendar.SUNDAY:
                                return values.getBoolean("timedaysun");
                        }
                    }
                    return false;
                case Plan.IF_WEATHER:
                    break;
                case Plan.IF_BATTERY:
                    break;
                case Plan.IF_CLIP:
                    long num = System.currentTimeMillis() - ApplicationController.getClipChangeTime();
                    return num < 5000;
                case Plan.IF_LOC:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Plan.RESULT_PHONE : 추가값 없음
     * Plan.RESULT_KAKAO : 추가값 없음
     * Plan.RESULT_APP : Service
     * Plan.RESULT_WEATHER : Context
     * Plan.RESULT_ALARM : Context
     * Plan.RESULT_NAVER : Service
     * Plan.RESULT_SETTING : Context
     * */
    public static void doitResult(int code, String value, Context context, String... args){
        try {
            JSONObject values = new JSONObject(value);

            switch (code) {
                case Plan.RESULT_CALL:

                    ApplicationController.setEndcall(System.currentTimeMillis());
                    break;
                case Plan.RESULT_PHONE:
                    Log.e("doitResult",values.getString("phonepeople"));
                    SmsManager sms = SmsManager.getDefault();
                    sms.sendTextMessage(values.getString("phonepeople"), null, values.getString("phonetext"), null, null);
                    break;
                case Plan.RESULT_KAKAO:
                    KakaoSelfSend kakaoSelfSend = new KakaoSelfSend();
                    kakaoSelfSend.requestSendMemo(values.getString("kakaostring"));
                    break;
                case Plan.RESULT_APP:
                    Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(values.getString("appPackage"));
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(launchIntent);
                    break;
                case Plan.RESULT_WEATHER:
                    SendNotification.sendNotification(context,"현재 날씨","날씨 정보");
                    break;
                case Plan.RESULT_ALARM:
                    new ConnectServer.PuchNotificationTask("Plan명",values.getString("alarmstring")).execute();
                    break;
                case Plan.RESULT_NAVER:
                    SendNotification.sendNaverNotification(context,"네이버 검색",values.getString("naverstring"));
                    break;
                case Plan.RESULT_SETTING:
                    if(values.getBoolean("setblue")) ToggleSetting.onBluetooth(true);
                    else ToggleSetting.onBluetooth(false);
                    if(values.getBoolean("setmobile")) ToggleSetting.onWifi(true,context);
                    else ToggleSetting.onWifi(true,context);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
