package com.teamsix.doitplan;

import android.app.Activity;
import android.app.Application;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.kakao.auth.KakaoSDK;
import com.teamsix.doitplan.firebase.MyFirebaseInstanceIDService;
import com.teamsix.doitplan.kakao.KakaoSDKAdapter;

import java.util.ArrayList;
import java.util.List;

public class ApplicationController extends Application {

    private final static String DB_NAME = "MyPlans.db";
    private final static String DB_TABLE = "PLANS";
    private final static int DB_VERSION = 1;

    private static ApplicationController instance = null; //인스턴스 객체 선언
    private static volatile Activity ourrentActivity = null;
    private static Boolean isLogin = false;
    private static String emailId = "";
    private static String nickname = "";
    private static SharedPreferences loginData;
    private static MyPlanDBManager dbManager;
    private static SQLiteDatabase db;

    //static 객체를 반환하는 이유 : 매번 객체를 생성하지 않고 다른 Activity에서도 사용 가능

    public static ApplicationController getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        loginData = getSharedPreferences("loginData", MODE_PRIVATE);

        ApplicationController.instance = this; // 인스턴스 객체 초기화
        ApplicationController.isLogin = loginData.getBoolean("SAVE_LOGIN_DATA", false);
        if (isLogin) {
            ApplicationController.emailId = loginData.getString("ID", "");
            ApplicationController.nickname = loginData.getString("NICKNAME", "");
        }
        dbManager = new MyPlanDBManager(getApplicationContext(), DB_NAME, null, DB_VERSION);
        db = dbManager.getWritableDatabase();
        KakaoSDK.init(new KakaoSDKAdapter());
    }

    public static Activity getCurrentActivity() {
        return ourrentActivity;
    }

    public static void setCurrentActivity(Activity currentActivity) {
        ApplicationController.ourrentActivity = currentActivity;
    }

    public static Boolean getIsLogin() {
        return isLogin;
    }

    public static void setIsLogin(boolean isLogin) {
        ApplicationController.isLogin = isLogin;
    }

    public static String getEmailId() {
        return emailId;
    }

    public static void setEmailId(String id) {
        ApplicationController.emailId = id;
    }

    public static String getNickname() {
        return nickname;
    }

    public static void setNickname(String nickname) {
        ApplicationController.nickname = nickname;
    }

    public static void logout() {
        SharedPreferences.Editor editor = loginData.edit();
        isLogin = false;
        emailId = "";
        editor.remove("SAVE_LOGIN_DATA");
        editor.remove("id");
        editor.apply();
        db.execSQL("delete from " + DB_TABLE);
    }


    public static void login(String id, String nickname) {
        // SharedPreferences 객체만으론 저장 불가능 Editor 사용
        SharedPreferences.Editor editor = loginData.edit();

        // 에디터객체.put타입( 저장시킬 이름, 저장시킬 값 )
        // 저장시킬 이름이 이미 존재하면 덮어씌움
        editor.putBoolean("SAVE_LOGIN_DATA", true);
        editor.putString("ID", id);
        editor.putString("NICKNAME", nickname);

        // apply, commit 을 안하면 변경된 내용이 저장되지 않음
        editor.apply();

        setIsLogin(true);
        setEmailId(id);
        setNickname(nickname);

        new AsyncTask<Void, Void, Boolean>() {
            protected Boolean doInBackground(Void... params) {
                MyFirebaseInstanceIDService a = new MyFirebaseInstanceIDService();
                a.onTokenRefresh(emailId);
                return true;
            }
        }.execute();
    }

    public static void writePlanDB(Plan plan){
        ContentValues values = new ContentValues();
        values.put("_ID", plan.planNo);
        values.put("MSG", plan.title);
        values.put("IF_CODE", plan.ifCode);
        values.put("IF_VALUE", plan.ifValue);
        values.put("RESULT_CODE", plan.resultCode);
        values.put("RESULT_VALUE", plan.resultValue);
        values.put("IS_SHARE", plan.getIsShareToInt());
        values.put("IS_WORK", 1);
        db.replace(DB_TABLE, null, values);
    }

    public static List getAllPlanDB() {
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT * FROM ");
        sb.append(DB_TABLE);
        Cursor cursor = db.rawQuery(sb.toString(), null);
        List plans = new ArrayList();
        Plan plan = null;
        // moveToNext 다음에 데이터가 있으면 true 없으면 false
        while (cursor.moveToNext()) {
            plan = new Plan();
            plan.planNo = cursor.getInt(0);
            plan.title = cursor.getString(1);
            plan.ifCode = cursor.getInt(2);
            plan.ifValue = cursor.getString(3);
            plan.resultCode = cursor.getInt(4);
            plan.resultValue = cursor.getString(5);
            plan.setIsShareFormInt(cursor.getInt(6));
            plan.setIsWorkFormInt(cursor.getInt(7));
            plans.add(plan);
        }
        return plans;
    }

    public static void setWork(int no, int bool){
        ContentValues values = new ContentValues();
        values.put("IS_WORK",bool);
        db.update(DB_TABLE,values,"_ID = ?",new String[]{String.valueOf(no)});
    }


    /**
     * 애플리케이션 종료시 객체 초기화한다.
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
        instance = null;
    }
}


