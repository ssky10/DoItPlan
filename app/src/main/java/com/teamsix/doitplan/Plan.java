package com.teamsix.doitplan;

import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Plan {
    public static final int IF_CALL = 0;
    public static final int IF_PHONE = 1;
    public static final int IF_KAKAO = 2;
    public static final int IF_TIME = 3;
    public static final int IF_WEATHER = 4;
    public static final int IF_BATTERY = 5;
    public static final int IF_CLIP = 6;
    public static final int IF_LOC = 7;

    public static final int RESULT_CALL = 0;
    public static final int RESULT_PHONE = 1;
    public static final int RESULT_KAKAO = 2;
    public static final int RESULT_APP = 3;
    public static final int RESULT_WEATHER = 4;
    public static final int RESULT_ALARM = 5;
    public static final int RESULT_NAVER = 6;
    public static final int RESULT_SETTING = 7;

    public static final String[] IF_STR = {"전화", "문자", "카톡", "시간", "날씨", "배터리", "클립보드", "위치"};
    public static final String[] IF_STRLONG = {"전화가 왔을때 ", "문자가 왔을 때 ", "카톡이 왔을 때 ", "시간이 되었을 때 ", "특정날씨가 되었을 때 ", "배터리가 특정수준이 되었을 때 ", "문자를 복사했을 때 ", "특정위치에 도착했을 때 "};
    public static final String[] RESULT_STR = {"전화", "문자", "카톡", "특정앱", "날씨", "알림", "네이버", "기기설정"};
    public static final String[] RESULT_STRLONG = {"전화를 거절한다.", "문자를 보낸다.", "나에게 카톡을 보낸다.", "특정앱을 실행한다.", "날씨를 알려준다.", "푸시메시지를 보낸다.", "네이버검색을 한다.", "기기설정을 변경한다."};
    public static final String[] IF_REPLACE = {"{발신자}","{발신자}/{내용}","{발신자}/{내용}","{시간}","{하늘상태}/{기온}/{비/눈}","{퍼센트}","{내용}","{위치}"};

    public String title = "";
    public String planner = "";
    public int planNo = -1;
    public int likes = 0;
    public boolean isLike;
    public int ifCode = -1;
    public int resultCode = -1;
    public String ifValue = "";
    public String resultValue = "";
    public boolean isShare = false;
    public boolean isWork = true;

    public static String ifIntentToSting(Intent intent){
        int ifCode = intent.getIntExtra("if", 0);
        Map<String, Object> ifValue = new HashMap();
        switch (ifCode) {
            case Plan.IF_CALL:
                ifValue.put("callnum", intent.getStringExtra("callnum"));
                break;
            case Plan.IF_PHONE:
                ifValue.put("phonestring", intent.getStringExtra("phonestring"));
                ifValue.put("phonenum", intent.getStringExtra("phonenum"));
                break;
            case Plan.IF_KAKAO:
                ifValue.put("kakaostring", intent.getStringExtra("kakaostring"));
                ifValue.put("kakaopeople", intent.getStringExtra("kakaopeople"));
                break;
            case Plan.IF_TIME:
                ifValue.put("timeclock", intent.getStringExtra("timeclock"));
                ifValue.put("timedaysun", intent.getBooleanExtra("timedaysun", false));
                ifValue.put("timedaymon", intent.getBooleanExtra("timedaymon", false));
                ifValue.put("timedaytue", intent.getBooleanExtra("timedaytue", false));
                ifValue.put("timedaywed", intent.getBooleanExtra("timedaywed", false));
                ifValue.put("timedaythu", intent.getBooleanExtra("timedaythu", false));
                ifValue.put("timedayfri", intent.getBooleanExtra("timedayfri", false));
                ifValue.put("timedaysat", intent.getBooleanExtra("timedaysat", false));
                break;
            case Plan.IF_WEATHER:
                ifValue.put("timedaysunny", intent.getBooleanExtra("timedaysunny", false));
                ifValue.put("timedayrain", intent.getBooleanExtra("timedayrain", false));
                ifValue.put("timedaycloud", intent.getBooleanExtra("timedaycloud", false));
                break;
            case Plan.IF_BATTERY:
                ifValue.put("betterypercent", intent.getStringExtra("betterypercent"));
                break;
            case Plan.IF_CLIP:
                ifValue.put("callnum", intent.getStringExtra("callnum"));
                break;
            case Plan.IF_LOC:
                ifValue.put("lat", intent.getDoubleArrayExtra("latlng")[0]);
                ifValue.put("lng", intent.getDoubleArrayExtra("latlng")[1]);
                break;
        }

        Gson gson = new Gson();
        return gson.toJson(ifValue);
    }

    public static String resultIntentToSting(Intent intent) {
        int resultCode = intent.getIntExtra("Result", 0);
        Map<String, Object> resultValue = new HashMap();
        switch (resultCode) {
            case Plan.RESULT_CALL:
                break;
            case Plan.RESULT_PHONE:
                resultValue.put("phonetext", intent.getStringExtra("phonetext"));
                resultValue.put("phonepeople", intent.getStringExtra("phonepeople"));
                break;
            case Plan.RESULT_KAKAO:
                resultValue.put("kakaostring", intent.getStringExtra("kakaostring"));
                break;
            case Plan.RESULT_APP:
                resultValue.put("appPackage", intent.getStringExtra("appPackage"));
                resultValue.put("appName", intent.getStringExtra("appName"));
                break;
            case Plan.RESULT_WEATHER:
                break;
            case Plan.RESULT_ALARM:
                resultValue.put("alarmstring", intent.getStringExtra("alarmstring"));
                resultValue.put("serveralarm", intent.getBooleanExtra("serveralarm",false));
                break;
            case Plan.RESULT_NAVER:
                resultValue.put("naverstring", intent.getStringExtra("naverstring"));
                break;
            case Plan.RESULT_SETTING:
                resultValue.put("isBlueOn", intent.getBooleanExtra("isBlueOn",false));
                resultValue.put("isSilenceOn", intent.getBooleanExtra("isSilenceOn",false));
                resultValue.put("isWifiOn", intent.getBooleanExtra("isWifiOn",false));
                resultValue.put("setblue", intent.getBooleanExtra("setblue",false));
                resultValue.put("setsilence", intent.getBooleanExtra("setsilence",false));
                resultValue.put("setwifi", intent.getBooleanExtra("setwifi",false));
                break;
        }

        Gson gson = new Gson();
        return gson.toJson(resultValue);
    }

    public Plan(int no, String title, String planner, int likes){
        planNo = no;
        this.title = title;
        this.planner = planner;
        this.likes = likes;
    }

    public Plan(String title, String planner){
        planNo = -1;
        this.title = title;
        this.planner = planner;
        this.likes = 0;
    }

    public Plan(){

    }

    public Intent getResultIntent(){
        Intent intent= new Intent();
        try {
            JSONObject jsonObject = new JSONObject(resultValue);
            switch (resultCode) {
                case Plan.RESULT_CALL:
                    break;
                case Plan.RESULT_PHONE:
                    intent.putExtra("phonetext",jsonObject.getString("phonetext"));
                    intent.putExtra("phonepeople",jsonObject.getString("phonepeople"));
                    break;
                case Plan.RESULT_KAKAO:
                    intent.putExtra("kakaostring",jsonObject.getString("kakaostring"));
                    break;
                case Plan.RESULT_APP:
                    intent.putExtra("appPackage",jsonObject.getString("appPackage"));
                    intent.putExtra("appName",jsonObject.getString("appName"));
                    break;
                case Plan.RESULT_WEATHER:
                    break;
                case Plan.RESULT_ALARM:
                    intent.putExtra("alarmstring",jsonObject.getString("alarmstring"));
                    intent.putExtra("serveralarm",jsonObject.getBoolean("serveralarm"));
                    break;
                case Plan.RESULT_NAVER:
                    intent.putExtra("naverstring",jsonObject.getString("naverstring"));
                    break;
                case Plan.RESULT_SETTING:
                    intent.putExtra("isBlueOn",jsonObject.getBoolean("isBlueOn"));
                    intent.putExtra("isSilenceOn",jsonObject.getBoolean("isSilenceOn"));
                    intent.putExtra("isWifiOn",jsonObject.getBoolean("isWifiOn"));
                    intent.putExtra("setblue",jsonObject.getBoolean("setblue"));
                    intent.putExtra("setsilence",jsonObject.getBoolean("setsilence"));
                    intent.putExtra("setwifi",jsonObject.getBoolean("setwifi"));
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return intent;
    }

    public Intent getIfIntent(){
        Intent intent= new Intent();
        try {
            JSONObject jsonObject = new JSONObject(ifValue);
            switch (ifCode) {
                case Plan.IF_CALL:
                    intent.putExtra("callnum",jsonObject.getString("callnum"));
                    break;
                case Plan.IF_PHONE:
                    intent.putExtra("phonestring",jsonObject.getString("phonestring"));
                    intent.putExtra("phonenum",jsonObject.getString("phonenum"));
                    break;
                case Plan.IF_KAKAO:
                    intent.putExtra("kakaostring",jsonObject.getString("kakaostring"));
                    intent.putExtra("kakaopeople",jsonObject.getString("kakaopeople"));
                    break;
                case Plan.IF_TIME:
                    intent.putExtra("timeclock",jsonObject.getString("timeclock"));
                    intent.putExtra("timedaysun",jsonObject.getBoolean("timedaysun"));
                    intent.putExtra("timedaymon",jsonObject.getBoolean("timedaymon"));
                    intent.putExtra("timedaytue",jsonObject.getBoolean("timedaytue"));
                    intent.putExtra("timedaywed",jsonObject.getBoolean("timedaywed"));
                    intent.putExtra("timedaythu",jsonObject.getBoolean("timedaythu"));
                    intent.putExtra("timedayfri",jsonObject.getBoolean("timedayfri"));
                    intent.putExtra("timedaysat",jsonObject.getBoolean("timedaysat"));
                    break;
                case Plan.IF_WEATHER:
                    intent.putExtra("timedaysunny",jsonObject.getBoolean("timedaysunny"));
                    intent.putExtra("timedayrain",jsonObject.getBoolean("timedayrain"));
                    intent.putExtra("timedaycloud",jsonObject.getBoolean("timedaycloud"));
                    break;
                case Plan.IF_BATTERY:
                    intent.putExtra("betterypercent",jsonObject.getString("betterypercent"));
                    break;
                case Plan.IF_CLIP:

                    break;
                case Plan.IF_LOC:
                    double[] latlng = new double[]{jsonObject.getDouble("lat"),jsonObject.getDouble("lng")};
                    Log.e("latlng",latlng[0]+"/"+latlng[1]);
                    intent.putExtra("latlng",latlng);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return intent;
    }

    public int getIsShareToInt(){
        if(isShare) return 1;
        else return 0;
    }

    public void setIsShareFormInt(int num){
        if(num == 0) isShare = false;
        else isShare = true;
    }

    public int getIsWorkToInt(){
        if(isWork) return 1;
        else return 0;
    }

    public void setIsWorkFormInt(int num){
        if(num == 0) isWork = false;
        else isWork = true;
    }

    public Plan clone(){
        Plan p = new Plan();
        p.title = title;
        p.planner = planner;
        p.planNo = planNo;
        p.likes = likes;
        p.isLike = isLike;
        p.ifCode = ifCode;
        p.resultCode = resultCode;
        p.ifValue = ifValue;
        p.resultValue = resultValue;
        p.isShare = isShare;
        p.isWork = isWork;
        return p;
    }


}
