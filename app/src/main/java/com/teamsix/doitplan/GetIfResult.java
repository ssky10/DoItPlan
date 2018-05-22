package com.teamsix.doitplan;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.ToggleButton;

import com.teamsix.doitplan.background.AlarmBraodCastReciever;
import com.teamsix.doitplan.background.BetteryReceiver;
import com.teamsix.doitplan.background.ForecastBraodCastReciever;
import com.teamsix.doitplan.background.SendNotification;
import com.teamsix.doitplan.background.ToggleSetting;
import com.teamsix.doitplan.kakao.KakaoSelfSend;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.teamsix.doitplan.background.SendNotification.sendNotification;

public class GetIfResult {
    public static List getBoolean(List plans, String... args){
        List<Plan> results = new ArrayList<>();
        Plan plan;
        for(int i=0; i<plans.size();i++){
            plan = ((Plan)plans.get(i)).clone();
            Log.e("ifValue",plan.ifValue);
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
                        plan.resultValue = plan.resultValue.replace("{하늘상태}", ForecastBraodCastReciever.forecast.getSky());
                        plan.resultValue = plan.resultValue.replace("{기온}",ForecastBraodCastReciever.forecast.getTemperature()+"℃");
                        plan.resultValue = plan.resultValue.replace("{강수량}",ForecastBraodCastReciever.forecast.getRain()+"mm");
                        break;
                    case Plan.IF_BATTERY:
                        plan.resultValue = plan.resultValue.replace("{퍼센트}",BetteryReceiver.level+"");
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
                    if(values.getBoolean("timedaysunny"))
                        if(ForecastBraodCastReciever.forecast.isSunny())
                            return true;
                    if(values.getBoolean("timedaycloud"))
                        if(ForecastBraodCastReciever.forecast.isCloud())
                            return true;
                    if(values.getBoolean("timedayrain"))
                        if(!ForecastBraodCastReciever.forecast.isRain())
                            return true;
                    return false;
                case Plan.IF_BATTERY:
                    return BetteryReceiver.level <= Integer.valueOf(values.getString("betterypercent"));
                case Plan.IF_CLIP:
                    long num = System.currentTimeMillis() - ApplicationController.getClipChangeTime();
                    return num < 5000;
                case Plan.IF_LOC:
                    Location loc1 = new Location("");
                    loc1.setLongitude(values.getDouble("lng"));
                    loc1.setLatitude(values.getDouble("lat"));

                    Location loc2 = new Location("");
                    loc2.setLongitude(Double.valueOf(args[1]));
                    loc2.setLatitude(Double.valueOf(args[0]));
                    Log.e("Loc",loc1.distanceTo(loc2)+"");
                    return loc1.distanceTo(loc2) < 500;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void doitResult(int code, String value, Context context, int... args){
        try {
            JSONObject values = new JSONObject(value);

            switch (code) {
                case Plan.RESULT_CALL:
                    if(args[0]==1) ApplicationController.setEndcall(-1);
                    else ApplicationController.setEndcall(System.currentTimeMillis());
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
                    StringBuffer sb = new StringBuffer();
                    sb.append("하늘 : ");
                    sb.append(ForecastBraodCastReciever.forecast.getSky());
                    sb.append("비/눈 : ");
                    sb.append(ForecastBraodCastReciever.forecast.getRain());
                    sb.append("기온 : ");
                    sb.append(ForecastBraodCastReciever.forecast.getHumidity()+"℃");
                    sendNotification(context,"현재 날씨",sb.toString());
                    break;
                case Plan.RESULT_ALARM:
                    Boolean isServer = values.getBoolean("serveralarm");
                    if(isServer)
                        new ConnectServer.PuchNotificationTask("Plan명",values.getString("alarmstring")).execute();
                    else
                        sendNotification(context,"Plan명",values.getString("alarmstring"));
                    break;
                case Plan.RESULT_NAVER:
                    SendNotification.sendNaverNotification(context,"네이버 검색",values.getString("naverstring"));
                    break;
                case Plan.RESULT_SETTING:
                    if(values.getBoolean("setblue")) ToggleSetting.onBluetooth(values.getBoolean("isBlueOn"));
                    if(values.getBoolean("setsilence")) ToggleSetting.onSilence(values.getBoolean("isSilenceOn"),context);
                    if(values.getBoolean("setwifi")) ToggleSetting.onWifi(values.getBoolean("isWifiOn"),context);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static long getNextDate(long now, String value){
        long ONE_DAY = 24*60*60*1000;
        JSONObject values = null;
        try {
            values = new JSONObject(value);
            SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd ");
            Date date = new Date(now);

            // nowDate 변수에 값을 저장한다.
            String nowDate = sdfNow.format(date);
            String nextDate = nowDate + values.getString("timeclock");
            sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            nowDate = sdfNow.format(date);


            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int select = cal.get(Calendar.DAY_OF_WEEK);

            String[] jsonstr = {"timedaysun","timedaymon","timedaytue","timedaywed","timedaythu","timedayfri","timedaysat"};

            if(values.getBoolean(jsonstr[select-1])){
                if(sdfNow.parse(nextDate).getTime()>sdfNow.parse(nowDate).getTime()){
                    return sdfNow.parse(nextDate).getTime();
                }
            }
            for(int i=0;i<7;i++){
                if(values.getBoolean(jsonstr[(select+i)%7])){
                    return sdfNow.parse(nextDate).getTime()+(ONE_DAY*i);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }


}
