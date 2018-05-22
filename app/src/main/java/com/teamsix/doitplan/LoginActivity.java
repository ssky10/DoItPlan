package com.teamsix.doitplan;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import okhttp3.FormBody;

import static android.Manifest.permission.READ_CONTACTS;
import static com.kakao.util.helper.Utility.getPackageInfo;

/**
 * 로그인화면
 */
public class LoginActivity extends AppCompatActivity{
    private static final String TAG = "LoginActivity";
    private SessionCallback callback;
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private Button btnRegist;
    private Button btnFb;
    private UserProfile profile;
    private String token;

    /**
     * 연락처 권한
     */
    private static final int REQUEST_READ_CONTACTS = 0;


    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private ConnectServer.ConnectServerTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isWhiteListing = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            isWhiteListing = pm.isIgnoringBatteryOptimizations(LoginActivity.this.getPackageName());
        }

        if (!isWhiteListing) {
            PermissionUtils.permissionWhitelist(this);
        }


        if (ApplicationController.getIsLogin()) {
            redirectSignupActivity();
        } else {

            // Set up the login form.
            mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

            mPasswordView = (EditText) findViewById(R.id.password);
            mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                        attemptLogin();
                        return true;
                    }
                    return false;
                }
            });

            Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
            mEmailSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin();
                }
            });

            mLoginFormView = findViewById(R.id.login_form);
            mProgressView = findViewById(R.id.login_progress);

            //카카오톡 로그인 설정
            callback = new SessionCallback();
            Session.getCurrentSession().addCallback(callback);
            Session.getCurrentSession().checkAndImplicitOpen();

            //페이스북 로그인 설정
            callbackManager = CallbackManager.Factory.create();
            loginButton = findViewById(R.id.login_button);
            loginButton.setReadPermissions("email");
            loginButton.registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(final LoginResult loginResult) {
                            GraphRequest request;
                            request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                                @Override
                                public void onCompleted(JSONObject user, GraphResponse response) {
                                    if (response.getError() != null) {

                                    } else {
                                        Log.i("TAG", "user: " + user.toString());
                                        Log.i("TAG", "AccessToken: " + loginResult.getAccessToken().getToken());
                                        setResult(RESULT_OK);

                                        try {
                                            @SuppressLint("StaticFieldLeak")
                                            ConnectServer.UserTokenCheckTask task
                                                    = new ConnectServer.UserTokenCheckTask(user.getString("email"), "facebook", AccessToken.getCurrentAccessToken().getUserId(), LoginActivity.this) {

                                                @Override
                                                protected void onPostExecute(final JSONObject success) {
                                                    asyncDialog.dismiss();
                                                    if (success == null) {
                                                        Toast.makeText(LoginActivity.this, "서버접속에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        try {
                                                            boolean result = success.getBoolean("result");
                                                            if (result) {
                                                                ApplicationController.login(mEmail, success.getString("nickname"));
                                                                redirectSignupActivity();
                                                            } else {
                                                                int code = success.getInt("code");
                                                                if (code == 0) {
                                                                    Intent intent = new Intent(getApplicationContext(), RegistActivity.class);
                                                                    intent.putExtra("type", 2);
                                                                    intent.putExtra("email", mEmail);
                                                                    intent.putExtra("Ftoken", mToken);
                                                                    intent.setFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                                    startActivityForResult(intent, 1000);
                                                                } else if (code == 1) {
                                                                    AlertDialog.Builder ad = new AlertDialog.Builder(new ContextThemeWrapper(LoginActivity.this, R.style.Theme_AppCompat_Dialog));

                                                                    ad.setTitle("계정확인");       // 제목 설정
                                                                    ad.setMessage("이미 동일한 메일의 계정이 존재합니다.\n연결하시려면 기존 비밀번호를 입력해주세요.");   // 내용 설정

                                                                    final EditText et = new EditText(LoginActivity.this);
                                                                    et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                                                    et.setTransformationMethod(PasswordTransformationMethod.getInstance());
                                                                    ad.setView(et);

                                                                    ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            Log.v(TAG, "Yes Btn Click");

                                                                            new ConnectServer.UserSingUpTokenTask(mEmail, et.getText().toString(), "facebook", mToken, LoginActivity.this) {
                                                                                @Override
                                                                                protected void onPostExecute(Boolean result) {
                                                                                    asyncDialog.dismiss();
                                                                                    if (result) {
                                                                                        Toast.makeText(LoginActivity.this, "정상적으로 연결되었습니다.\n다시 로그인 해주세요", Toast.LENGTH_SHORT).show();
                                                                                    } else {
                                                                                        Toast.makeText(LoginActivity.this, "연결이 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                    LoginManager.getInstance().logOut(); //페이스북 로그아웃
                                                                                }
                                                                            }.execute();
                                                                            dialog.dismiss();
                                                                        }
                                                                    });

                                                                    ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            Log.v(TAG, "No Btn Click");
                                                                            dialog.dismiss();     //닫기
                                                                            // Event
                                                                        }
                                                                    });

                                                                    ad.show();


                                                                } else if (code == 2) {
                                                                    ApplicationController.logout();
                                                                    Toast.makeText(LoginActivity.this, "동일한 이메일에 이미 등록된 계정이 존재합니다.", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }
                                            };
                                            task.execute();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id,name,email");
                            request.setParameters(parameters);
                            request.executeAsync();


                            Log.d(TAG, "onSucces LoginResult=" + loginResult);
                        }

                        @Override
                        public void onCancel() {
                            // App code
                            Log.d(TAG, "onCancel");
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            // App code
                            Log.d(TAG, "onError");
                        }
                    });

            //페이스북 커스텀 로그인버튼 설정
            btnFb = (Button) findViewById(R.id.btnFb);
            btnFb.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    loginButton.performClick();
                }
            });

            //회원가입버튼 설정
            btnRegist = (Button) findViewById(R.id.btnRegist);
            btnRegist.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), RegistActivity.class);
                    intent.putExtra("type", 0);
                    intent.setFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivityForResult(intent, 1000);
                }
            });

        }

        //토큰값 추출
        String str = getKeyHash(this);
        Log.e("TOKEN", str);

    }

    /**
     * 카카오톡 로그인 부분 시작
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }

    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            requestMe();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if (exception != null) {
                Logger.e(exception);
            }
            setContentView(R.layout.activity_login);
        }
    }

    private void requestMe() {
        List<String> propertyKeys = new ArrayList<String>();
        propertyKeys.add("kaccount_email");
        propertyKeys.add("nickname");
        propertyKeys.add("profile_image");
        propertyKeys.add("thumbnail_image");

        UserManagement.getInstance().requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
            }

            @SuppressLint("StaticFieldLeak")
            @Override
            public void onSuccess(UserProfile userProfile) {
                Logger.d("UserProfile : " + userProfile);
                Log.e("onSessionClosed", userProfile.toString());
                if (!LoginActivity.this.isFinishing()) {
                    profile = userProfile;
                    ConnectServer.UserTokenCheckTask task
                            = new ConnectServer.UserTokenCheckTask(profile.getEmail(), "kakao", String.valueOf(profile.getId()), LoginActivity.this) {

                        @Override
                        protected void onPostExecute(final JSONObject success) {
                            if (asyncDialog != null) asyncDialog.dismiss();
                            if (success == null) {
                                Toast.makeText(LoginActivity.this, "서버접속에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                try {
                                    boolean result = success.getBoolean("result");
                                    if (result) {
                                        ApplicationController.login(mEmail, success.getString("nickname"));
                                        redirectSignupActivity();
                                    } else {
                                        int code = success.getInt("code");
                                        if (code == 0) {
                                            Intent intent = new Intent(getApplicationContext(), RegistActivity.class);
                                            intent.putExtra("type", 1);
                                            intent.putExtra("email", mEmail);
                                            intent.putExtra("Ktoken", mToken);
                                            intent.setFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
                                            startActivityForResult(intent, 1000);
                                        } else if (code == 1) {
                                            AlertDialog.Builder ad = new AlertDialog.Builder(new ContextThemeWrapper(LoginActivity.this, R.style.Theme_AppCompat_Dialog));

                                            ad.setTitle("계정확인");       // 제목 설정
                                            ad.setMessage("이미 동일한 메일의 계정이 존재합니다.\n연결하시려면 기존 비밀번호를 입력해주세요.");   // 내용 설정

                                            final EditText et = new EditText(LoginActivity.this);
                                            et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                            et.setTransformationMethod(PasswordTransformationMethod.getInstance());
                                            ad.setView(et);

                                            ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Log.v(TAG, "Yes Btn Click");

                                                    new ConnectServer.UserSingUpTokenTask(mEmail, et.getText().toString(), "kakao", mToken, LoginActivity.this) {
                                                        @Override
                                                        protected void onPostExecute(Boolean result) {
                                                            asyncDialog.dismiss();
                                                            if (result) {
                                                                Toast.makeText(LoginActivity.this, "정상적으로 연결되었습니다.\n다시 로그인 해주세요", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(LoginActivity.this, "연결이 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                                            }
                                                            UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                                                                @Override
                                                                public void onCompleteLogout() {

                                                                }
                                                            });

                                                        }
                                                    }.execute();
                                                    dialog.dismiss();
                                                }
                                            });

                                            ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Log.v(TAG, "No Btn Click");
                                                    dialog.dismiss();     //닫기
                                                    // Event
                                                }
                                            });

                                            ad.show();


                                        } else if (code == 2) {
                                            ApplicationController.logout();
                                            Toast.makeText(LoginActivity.this, "동일한 이메일에 이미 등록된 계정이 존재합니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    };
                    task.execute();
                }
            }

            @Override
            public void onNotSignedUp() {
            }
        }, propertyKeys, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
    }

    @SuppressLint("StaticFieldLeak")
    protected void redirectSignupActivity() {
        new ConnectServer.ConnectServerDialogTask(
                LoginActivity.this,
                "로딩중입니다...",
                new FormBody.Builder()
                        .add("email", ApplicationController.getEmailId())
                        .build(),
                "getMyPlan.php") {

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                try {
                    Plan plan;
                    JSONArray jObjects = new JSONArray(result);
                    for (int i = 0; i < jObjects.length(); i++) {
                        JSONObject planJson = jObjects.getJSONObject(i);
                        plan = new Plan();
                        plan.planNo = planJson.getInt("plan_no");
                        plan.title = planJson.getString("msg");
                        plan.ifCode = planJson.getInt("if_code");
                        plan.ifValue = planJson.getString("if_value");
                        plan.resultCode = planJson.getInt("that_code");
                        plan.resultValue = planJson.getString("that_value");
                        plan.setIsShareFormInt(planJson.getInt("is_share"));
                        plan.likes = planJson.getInt("likes_num");
                        plan.setIsWorkFormInt(1);
                        ApplicationController.writePlanDB(plan);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
            }
        }.execute();
    }

    /**
     * 카카오톡 로그인 부분 끝
     */

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */

    @SuppressLint("StaticFieldLeak")
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new ConnectServer.ConnectServerTask(new FormBody.Builder()
                    .add("email", email)
                    .add("password",password)
                    .build(),"userCheck.php"){
                @Override
                protected void onPostExecute(final String success) {
                    mAuthTask = null;
                    showProgress(false);
                    try {
                        JSONObject jsonObject = new JSONObject(success);
                        if (jsonObject.getBoolean("result")) { //로그인에 성공할 경우
                            ApplicationController.login(email, jsonObject.getString("nickname"));
                            redirectSignupActivity();
                        } else { //로그인에 실패할 경우
                            mPasswordView.setError(jsonObject.getString("msg"));
                            //mPasswordView.requestFocus();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                protected void onCancelled() {
                    mAuthTask = null;
                    showProgress(false);
                }
            };
            mAuthTask.execute((Void) null);
        }
    }

    //이메일 조건 확인
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    //비밀번호 조건 확인
    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    //해쉬키 추출
    public static String getKeyHash(final Context context) {
        PackageInfo packageInfo = getPackageInfo(context, PackageManager.GET_SIGNATURES);
        if (packageInfo == null)
            return null;

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                return Base64.encodeToString(md.digest(), Base64.NO_WRAP);
            } catch (NoSuchAlgorithmException e) {
                Log.w("", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
        return null;
    }
}

