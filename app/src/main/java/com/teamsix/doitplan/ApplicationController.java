package com.teamsix.doitplan;

import android.app.Activity;
import android.app.Application;

import com.kakao.auth.KakaoSDK;
import com.teamsix.doitplan.kakao.KakaoSDKAdapter;

public class ApplicationController extends Application {

    private static ApplicationController instance = null; //인스턴스 객체 선언
    private static volatile Activity ourrentActivity = null;

    //static 객체를 반환하는 이유 : 매번 객체를 생성하지 않고 다른 Activity에서도 사용 가능

    public static ApplicationController getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        ApplicationController.instance = this; // 인스턴스 객체 초기화
        KakaoSDK.init(new KakaoSDKAdapter());
    }

    public static Activity getCurrentActivity() {
        return ourrentActivity;
    }

    public static void setCurrentActivity(Activity currentActivity) {
        ApplicationController.ourrentActivity = currentActivity;
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


