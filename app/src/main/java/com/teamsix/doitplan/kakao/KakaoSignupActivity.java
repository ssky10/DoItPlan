package com.teamsix.doitplan.kakao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.helper.log.Logger;
import com.teamsix.doitplan.LoginActivity;
import com.teamsix.doitplan.MainActivity;

import java.util.ArrayList;
import java.util.List;


public class KakaoSignupActivity extends Activity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestMe();
    }


    protected void requestMe() {
        List<String> propertyKeys = new ArrayList<String>();
        propertyKeys.add("kaccount_email");
        propertyKeys.add("nickname");
        propertyKeys.add("profile_image");

        UserManagement.getInstance().requestMe(new MeResponseCallback() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.e("onSessionClosed", errorResult.getErrorMessage());
                redirectLoginActivity();
            }

            @Override
            public void onNotSignedUp() {
                Log.e("onNotSignedUp", "start");
                showSignup();
            }

            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);

                redirectLoginActivity();
            }

            @Override
            public void onSuccess(UserProfile userProfile) {
                Logger.d("UserProfile:" + userProfile);
                Log.v("user", userProfile.toString());
                redirectMainActivity();
            }

        }, propertyKeys, false);
    }

    protected void showSignup() {
        redirectLoginActivity();
    }

    protected void redirectMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    protected void redirectLoginActivity() {
        final Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

}
