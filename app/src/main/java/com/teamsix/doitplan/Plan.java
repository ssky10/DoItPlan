package com.teamsix.doitplan;

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
    public static final String[] IF_STRLONG = {"전화가 왔을때 ", "문자가 왔을 때 ", "카톡이 왔을 때 ", "시간이 되었을 때 ", "특정날씨가 되었을 때 ", "배터리가 특정수준이 되었을 때 ", "특정값을 복사했을 때 ", "특정위치에 도착했을 때 "};
    public static final String[] RESULT_STR = {"전화", "문자", "카톡", "특정앱", "날씨", "알림", "네이버", "기기설정"};
    public static final String[] RESULT_STRLONG = {"전화를 거절한다.", "문자를 보낸다.", "나에게 카톡을 보낸다.", "특정앱을 실행한다.", "날씨를 알려준다.", "푸시메시지를 보낸다.", "네이버검색을 한다.", "기기설정을 변경한다."};

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
