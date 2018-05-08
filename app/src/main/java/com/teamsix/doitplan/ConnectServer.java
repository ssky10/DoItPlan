package com.teamsix.doitplan;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

public class ConnectServer {
    /**
     * 회원가입 백그라운드 작업
     */
    public static class UserSingUpTask extends AsyncTask<Void, Void, Boolean> {
        private final int mtype;
        private final String mEmail;
        private final String mPassword;
        private final String mNickname;
        private final String mToken;

        ProgressDialog asyncDialog;


        UserSingUpTask(int type, String email, String password, String nickname, Context context, String token) {
            mtype = type;
            mEmail = email;
            mPassword = password;
            mNickname = nickname;
            asyncDialog = new ProgressDialog(context);
            mToken = token;
        }

        @Override
        protected void onPreExecute() {
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setCanceledOnTouchOutside(false);
            asyncDialog.setCancelable(false);
            asyncDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                }
            });
            asyncDialog.setMessage("회원가입중입니다...");

            // show dialog
            asyncDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            Boolean result = false;
            InputStreamReader in = null;
            BufferedReader br = null;
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost("https://doitplan.ml/dip/sing_process.php");
            ArrayList<NameValuePair> nameValues =
                    new ArrayList<NameValuePair>();
            try {
                //Post방식으로 넘길 값들을 각각 지정을 해주어야 한다.
                nameValues.add(new BasicNameValuePair(
                        "email", URLDecoder.decode(mEmail, "UTF-8")));
                nameValues.add(new BasicNameValuePair(
                        "password", URLDecoder.decode(mPassword, "UTF-8")));
                nameValues.add(new BasicNameValuePair(
                        "nickname", URLDecoder.decode(mNickname, "UTF-8")));
                nameValues.add(new BasicNameValuePair(
                        "type", URLDecoder.decode(String.valueOf(mtype), "UTF-8")));
                nameValues.add(new BasicNameValuePair(
                        "token", URLDecoder.decode(mToken, "UTF-8")));

                //HttpPost에 넘길 값을들 Set해주기
                post.setEntity(
                        new UrlEncodedFormEntity(
                                nameValues, "UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                Log.e("Insert Log", ex.toString());
            }

            try {
                //설정한 URL을 실행시키기
                HttpResponse response = client.execute(post);
                //통신 값을 받은 Log 생성. (200이 나오는지 확인할 것~) 200이 나오면 통신이 잘 되었다는 뜻!
                Log.i("Insert Log", "response.getStatusCode:" + response.getStatusLine().getStatusCode());
                HttpEntity resEntity = response.getEntity();
                StringBuilder str = new StringBuilder();
                in = new InputStreamReader(resEntity.getContent());
                br = new BufferedReader(in);
                String buf;
                while ((buf = br.readLine()) != null) {
                    str.append(buf);
                }

                Log.e("response", str.toString());

                JSONObject jObject = new JSONObject(str.toString());
                result = jObject.getBoolean("result");

            } catch (Exception e) {
                e.printStackTrace();
            }

            // TODO: register the new account here.
            return result;
        }
    }

    /**
     * 중복확인 백그라운드 작업
     */
    public static class UserIdCheckTask extends AsyncTask<Void, Void, Boolean> {
        private final String mEmail;

        UserIdCheckTask(String email) {
            mEmail = email;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            Boolean result = false;
            InputStreamReader in = null;
            BufferedReader br = null;
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost("https://doitplan.ml/dip/checkId.php");
            ArrayList<NameValuePair> nameValues =
                    new ArrayList<NameValuePair>();
            try {
                //Post방식으로 넘길 값들을 각각 지정을 해주어야 한다.
                nameValues.add(new BasicNameValuePair(
                        "email", URLDecoder.decode(mEmail, "UTF-8")));

                //HttpPost에 넘길 값을들 Set해주기
                post.setEntity(
                        new UrlEncodedFormEntity(
                                nameValues, "UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                Log.e("Insert Log", ex.toString());
            }

            try {
                //설정한 URL을 실행시키기
                HttpResponse response = client.execute(post);
                //통신 값을 받은 Log 생성. (200이 나오는지 확인할 것~) 200이 나오면 통신이 잘 되었다는 뜻!
                Log.i("Insert Log", "response.getStatusCode:" + response.getStatusLine().getStatusCode());
                HttpEntity resEntity = response.getEntity();
                StringBuilder str = new StringBuilder();
                in = new InputStreamReader(resEntity.getContent());
                br = new BufferedReader(in);
                String buf;
                while ((buf = br.readLine()) != null) {
                    str.append(buf);
                }

                Log.e("response", str.toString());

                JSONObject jObject = new JSONObject(str.toString());
                result = jObject.getBoolean("result");

            } catch (Exception e) {
                e.printStackTrace();
            }

            // TODO: register the new account here.
            return result;
        }
    }

    /**
     * 로그인 연결 확인 부분
     */
    public static class UserLoginTask extends AsyncTask<Void, Void, JSONObject> {

        protected final String mEmail;
        private final String mPassword;
        protected JSONObject jObject;


        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            Boolean result = false;
            JSONObject jObject = null;
            InputStreamReader in = null;
            BufferedReader br = null;
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost("https://doitplan.ml/dip/userCheck.php");
            ArrayList<NameValuePair> nameValues =
                    new ArrayList<NameValuePair>();
            try {
                //Post방식으로 넘길 값들을 각각 지정을 해주어야 한다.
                nameValues.add(new BasicNameValuePair(
                        "email", URLDecoder.decode(mEmail, "UTF-8")));
                nameValues.add(new BasicNameValuePair(
                        "password", URLDecoder.decode(mPassword, "UTF-8")));

                //HttpPost에 넘길 값을들 Set해주기
                post.setEntity(
                        new UrlEncodedFormEntity(
                                nameValues, "UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                Log.e("Insert Log", ex.toString());
            }

            try {
                //설정한 URL을 실행시키기
                HttpResponse response = client.execute(post);
                //통신 값을 받은 Log 생성. (200이 나오는지 확인할 것~) 200이 나오면 통신이 잘 되었다는 뜻!
                Log.i("Insert Log", "response.getStatusCode:" + response.getStatusLine().getStatusCode());
                HttpEntity resEntity = response.getEntity();
                StringBuilder str = new StringBuilder();
                in = new InputStreamReader(resEntity.getContent());
                br = new BufferedReader(in);
                String buf;
                while ((buf = br.readLine()) != null) {
                    str.append(buf); //반환값 문자열로 변경
                }

                Log.e("response", str.toString());

                jObject = new JSONObject(str.toString()); //JSON형태의 반환값 JSON으로 변경
                result = jObject.getBoolean("result"); //result값 추출 및 저장

            } catch (Exception e) {
                e.printStackTrace();
            }

            // TODO: register the new account here.
            return jObject; //결과 값 반환
        }
    }

    /**
     * 카카오톡 토큰값 및 이메일 확인 백그라운드 작업
     */
    public static class UserTokenCheckTask extends AsyncTask<Void, Void, JSONObject> {
        protected final String mEmail;
        protected final String mToken;
        private final String mType;

        ProgressDialog asyncDialog;

        UserTokenCheckTask(String email, String type, String token, Context context) {
            mEmail = email;
            mType = type;
            mToken = token;
            asyncDialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setCanceledOnTouchOutside(false);
            asyncDialog.setCancelable(false);
            asyncDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                }
            });
            asyncDialog.setMessage("로그인중입니다...");

            // show dialog
            asyncDialog.show();
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            Boolean result = false;
            InputStreamReader in = null;
            BufferedReader br = null;
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost("https://doitplan.ml/dip/userTokenCheck.php");
            ArrayList<NameValuePair> nameValues =
                    new ArrayList<NameValuePair>();
            try {
                //Post방식으로 넘길 값들을 각각 지정을 해주어야 한다.
                nameValues.add(new BasicNameValuePair(
                        "email", URLDecoder.decode(mEmail, "UTF-8")));
                nameValues.add(new BasicNameValuePair(
                        "token", URLDecoder.decode(mToken, "UTF-8")));
                nameValues.add(new BasicNameValuePair(
                        "type", URLDecoder.decode(mType, "UTF-8")));

                //HttpPost에 넘길 값을들 Set해주기
                post.setEntity(
                        new UrlEncodedFormEntity(
                                nameValues, "UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                Log.e("Insert Log", ex.toString());
            }

            try {
                //설정한 URL을 실행시키기
                HttpResponse response = client.execute(post);
                //통신 값을 받은 Log 생성. (200이 나오는지 확인할 것~) 200이 나오면 통신이 잘 되었다는 뜻!
                Log.i("Insert Log", "response.getStatusCode:" + response.getStatusLine().getStatusCode());
                HttpEntity resEntity = response.getEntity();
                StringBuilder str = new StringBuilder();
                in = new InputStreamReader(resEntity.getContent());
                br = new BufferedReader(in);
                String buf;
                while ((buf = br.readLine()) != null) {
                    str.append(buf);
                }

                Log.e("response", str.toString());

                JSONObject jObject = new JSONObject(str.toString());
                result = jObject.getBoolean("result");
                return jObject;

            } catch (Exception e) {
                e.printStackTrace();
            }

            // TODO: register the new account here.
            return null;
        }
    }

    /**
     * 토큰값 저장 백그라운드 작업
     */
    public static class UserSingUpTokenTask extends AsyncTask<Void, Void, Boolean> {
        private final String mEmail;
        private final String mPassword;
        private final String mType;
        private final String mToken;

        ProgressDialog asyncDialog;


        UserSingUpTokenTask(String email, String password, String type, String token, Context context) {
            mEmail = email;
            mPassword = password;
            mType = type;
            mToken = token;
            asyncDialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setCanceledOnTouchOutside(false);
            asyncDialog.setCancelable(false);
            asyncDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                }
            });
            asyncDialog.setMessage("연결중입니다...");

            // show dialog
            asyncDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            Boolean result = false;
            InputStreamReader in = null;
            BufferedReader br = null;
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost("https://doitplan.ml/dip/userSingUpToken.php");
            ArrayList<NameValuePair> nameValues =
                    new ArrayList<NameValuePair>();
            try {
                //Post방식으로 넘길 값들을 각각 지정을 해주어야 한다.
                nameValues.add(new BasicNameValuePair(
                        "email", URLDecoder.decode(mEmail, "UTF-8")));
                nameValues.add(new BasicNameValuePair(
                        "password", URLDecoder.decode(mPassword, "UTF-8")));
                nameValues.add(new BasicNameValuePair(
                        "token", URLDecoder.decode(mToken, "UTF-8")));
                nameValues.add(new BasicNameValuePair(
                        "type", URLDecoder.decode(mType, "UTF-8")));

                //HttpPost에 넘길 값을들 Set해주기
                post.setEntity(
                        new UrlEncodedFormEntity(
                                nameValues, "UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                Log.e("Insert Log", ex.toString());
            }

            try {
                //설정한 URL을 실행시키기
                HttpResponse response = client.execute(post);
                //통신 값을 받은 Log 생성. (200이 나오는지 확인할 것~) 200이 나오면 통신이 잘 되었다는 뜻!
                Log.i("Insert Log", "response.getStatusCode:" + response.getStatusLine().getStatusCode());
                HttpEntity resEntity = response.getEntity();
                StringBuilder str = new StringBuilder();
                in = new InputStreamReader(resEntity.getContent());
                br = new BufferedReader(in);
                String buf;
                while ((buf = br.readLine()) != null) {
                    str.append(buf);
                }

                Log.e("response", str.toString());

                JSONObject jObject = new JSONObject(str.toString());
                result = jObject.getBoolean("result");

            } catch (Exception e) {
                e.printStackTrace();
            }

            // TODO: register the new account here.
            return result;
        }
    }

    /**
     * 새로만든 plan 저장
     */
    public static class newPlanTask extends AsyncTask<Void, Void, Boolean> {
        private final int ifCode;
        private final int resultCode;
        private final String msg;
        private Map<String, String> ifValue = new HashMap();
        private Map<String, String> resultValue = new HashMap();


        public ProgressDialog asyncDialog;


        public newPlanTask(Intent ifValue, Intent resultValue, String msg, Context context) {
            asyncDialog = new ProgressDialog(context);
            this.msg = msg;
            ifCode = ifValue.getIntExtra("if", 0);
            switch (ifCode) {
                case Plan.IF_CALL:
                    this.ifValue.put("callnum", ifValue.getStringExtra("callnum"));
                    break;
                case Plan.IF_PHONE:
                    this.ifValue.put("phonestring", ifValue.getStringExtra("phonestring"));
                    this.ifValue.put("phonenum", ifValue.getStringExtra("phonenum"));
                    break;
                case Plan.IF_KAKAO:
                    this.ifValue.put("kakaostring", ifValue.getStringExtra("kakaostring"));
                    this.ifValue.put("kakaopeople", ifValue.getStringExtra("kakaopeople"));
                    break;
                case Plan.IF_TIME:
                    this.ifValue.put("timeclock", ifValue.getStringExtra("timeclock"));
                    this.ifValue.put("timedaysun", String.valueOf(ifValue.getBooleanExtra("timedaysun", false)));
                    this.ifValue.put("timedaymon", String.valueOf(ifValue.getBooleanExtra("timedaymon", false)));
                    this.ifValue.put("timedaytue", String.valueOf(ifValue.getBooleanExtra("timedaytue", false)));
                    this.ifValue.put("timedaywed", String.valueOf(ifValue.getBooleanExtra("timedaywed", false)));
                    this.ifValue.put("timedaythu", String.valueOf(ifValue.getBooleanExtra("timedaythu", false)));
                    this.ifValue.put("timedayfri", String.valueOf(ifValue.getBooleanExtra("timedayfri", false)));
                    this.ifValue.put("timedaysat", String.valueOf(ifValue.getBooleanExtra("timedaysat", false)));
                    break;
                case Plan.IF_WEATHER:
                    this.ifValue.put("timedaysunny", String.valueOf(ifValue.getBooleanExtra("timedaysunny", false)));
                    this.ifValue.put("timedayrain", String.valueOf(ifValue.getBooleanExtra("timedayrain", false)));
                    this.ifValue.put("timedaysnow", String.valueOf(ifValue.getBooleanExtra("timedaysnow", false)));
                    break;
                case Plan.IF_BATTERY:
                    this.ifValue.put("betterypercent", ifValue.getStringExtra("betterypercent"));
                    break;
                case Plan.IF_CLIP:
                    this.ifValue.put("callnum", ifValue.getStringExtra("callnum"));
                    break;
                case Plan.IF_LOC:
                    this.ifValue.put("lat", String.valueOf(ifValue.getDoubleArrayExtra("latlng")[0]));
                    this.ifValue.put("lng", String.valueOf(ifValue.getDoubleArrayExtra("latlng")[1]));
                    break;
            }
            resultCode = resultValue.getIntExtra("Result", 0);
            switch (resultCode) {
                case Plan.RESULT_CALL:
                    break;
                case Plan.RESULT_PHONE:
                    this.resultValue.put("phonetext", resultValue.getStringExtra("phonetext"));
                    this.resultValue.put("phonepeople", resultValue.getStringExtra("phonepeople"));
                    break;
                case Plan.RESULT_KAKAO:
                    this.resultValue.put("kakaostring", resultValue.getStringExtra("kakaostring"));
                    break;
                case Plan.RESULT_APP:
                    this.resultValue.put("appPackage", resultValue.getStringExtra("appPackage"));
                    this.resultValue.put("appName", resultValue.getStringExtra("appName"));
                    break;
                case Plan.RESULT_WEATHER:
                    break;
                case Plan.RESULT_ALARM:
                    this.resultValue.put("alarmstring", resultValue.getStringExtra("alarmstring"));
                    break;
                case Plan.RESULT_NAVER:
                    this.resultValue.put("naverstring", resultValue.getStringExtra("naverstring"));
                    break;
                case Plan.RESULT_SETTING:
                    this.resultValue.put("setblue", resultValue.getStringExtra("setblue"));
                    this.resultValue.put("setmobile", resultValue.getStringExtra("setmobile"));
                    break;
            }
        }

        @Override
        protected void onPreExecute() {
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setCanceledOnTouchOutside(false);
            asyncDialog.setCancelable(false);
            asyncDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                }
            });
            asyncDialog.setMessage("Plan등록중입니다...");

            // show dialog
            asyncDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            Boolean result = false;
            InputStreamReader in = null;
            BufferedReader br = null;
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost("https://doitplan.ml/dip/newPlan.php");
            ArrayList<NameValuePair> nameValues =
                    new ArrayList<NameValuePair>();
            try {
                //Post방식으로 넘길 값들을 각각 지정을 해주어야 한다.
                nameValues.add(new BasicNameValuePair(
                        "email", URLDecoder.decode(ApplicationController.getEmailId(), "UTF-8")));
                nameValues.add(new BasicNameValuePair(
                        "ifCode", URLDecoder.decode(String.valueOf(ifCode), "UTF-8")));
                nameValues.add(new BasicNameValuePair(
                        "resultCode", URLDecoder.decode(String.valueOf(resultCode), "UTF-8")));
                JSONObject json = new JSONObject();
                json.put("ifValue", ifValue);
                json.put("resultValue", resultValue);
                nameValues.add(new BasicNameValuePair(
                        "value", URLDecoder.decode(json.toString(), "UTF-8")));
                nameValues.add(new BasicNameValuePair(
                        "msg", URLDecoder.decode(msg, "UTF-8")));

                //HttpPost에 넘길 값을들 Set해주기
                post.setEntity(
                        new UrlEncodedFormEntity(
                                nameValues, "UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                Log.e("Insert Log", ex.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                //설정한 URL을 실행시키기
                HttpResponse response = client.execute(post);
                //통신 값을 받은 Log 생성. (200이 나오는지 확인할 것~) 200이 나오면 통신이 잘 되었다는 뜻!
                Log.i("Insert Log", "response.getStatusCode:" + response.getStatusLine().getStatusCode());
                HttpEntity resEntity = response.getEntity();
                StringBuilder str = new StringBuilder();
                in = new InputStreamReader(resEntity.getContent());
                br = new BufferedReader(in);
                String buf;
                while ((buf = br.readLine()) != null) {
                    str.append(buf);
                }

                Log.e("response", str.toString());

                JSONObject jObject = new JSONObject(str.toString());
                result = jObject.getBoolean("result");

            } catch (Exception e) {
                e.printStackTrace();
            }

            // TODO: register the new account here.
            return result;
        }
    }

    public static class GetRecommendPlanTask extends AsyncTask<Void, Void, ArrayList<Plan>> {

        public ProgressDialog asyncDialog;

        public GetRecommendPlanTask(Context context) {
            asyncDialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setCanceledOnTouchOutside(false);
            asyncDialog.setCancelable(false);
            asyncDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                }
            });
            asyncDialog.setMessage("로딩중입니다...");

            // show dialog
            asyncDialog.show();
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Plan> doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            ArrayList<Plan> plans = new ArrayList<>();
            Boolean result = false;
            JSONArray jObjects = null;
            InputStreamReader in = null;
            BufferedReader br = null;
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost("https://doitplan.ml/dip/getRecommendPlan.php");
            ArrayList<NameValuePair> nameValues =
                    new ArrayList<NameValuePair>();
            try {
                //HttpPost에 넘길 값을들 Set해주기
                post.setEntity(
                        new UrlEncodedFormEntity(
                                nameValues, "UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                Log.e("Insert Log", ex.toString());
            }

            try {
                //설정한 URL을 실행시키기
                HttpResponse response = client.execute(post);
                //통신 값을 받은 Log 생성. (200이 나오는지 확인할 것~) 200이 나오면 통신이 잘 되었다는 뜻!
                Log.i("Insert Log", "response.getStatusCode:" + response.getStatusLine().getStatusCode());
                HttpEntity resEntity = response.getEntity();
                StringBuilder str = new StringBuilder();
                in = new InputStreamReader(resEntity.getContent());
                br = new BufferedReader(in);
                String buf;
                while ((buf = br.readLine()) != null) {
                    str.append(buf); //반환값 문자열로 변경
                }

                Log.e("response", str.toString());

                jObjects = new JSONArray(str.toString());
                for (int i = 0; i < jObjects.length(); i++) {
                    JSONObject plan = jObjects.getJSONObject(i);
                    plans.add(new Plan(plan.getInt("plan_no"), plan.getString("msg"), plan.getString("nickname"), plan.getInt("likes_num")));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            // TODO: register the new account here.
            return plans; //결과 값 반환
        }
    }

    public static class GetMyPlanTask extends AsyncTask<Void, Void, ArrayList<Plan>> {

        public ProgressDialog asyncDialog;

        public GetMyPlanTask(Context context) {
            asyncDialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setCanceledOnTouchOutside(false);
            asyncDialog.setCancelable(false);
            asyncDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                }
            });
            asyncDialog.setMessage("로딩중입니다...");

            // show dialog
            asyncDialog.show();
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Plan> doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            ArrayList<Plan> plans = new ArrayList<>();
            Boolean result = false;
            JSONArray jObjects = null;
            InputStreamReader in = null;
            BufferedReader br = null;
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost("https://doitplan.ml/dip/getMyPlan.php");
            ArrayList<NameValuePair> nameValues =
                    new ArrayList<NameValuePair>();
            try {
                nameValues.add(new BasicNameValuePair(
                        "email", URLDecoder.decode(ApplicationController.getEmailId(), "UTF-8")));
                //HttpPost에 넘길 값을들 Set해주기
                post.setEntity(
                        new UrlEncodedFormEntity(
                                nameValues, "UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                Log.e("Insert Log", ex.toString());
            }

            try {
                //설정한 URL을 실행시키기
                HttpResponse response = client.execute(post);
                //통신 값을 받은 Log 생성. (200이 나오는지 확인할 것~) 200이 나오면 통신이 잘 되었다는 뜻!
                Log.i("Insert Log", "response.getStatusCode:" + response.getStatusLine().getStatusCode());
                HttpEntity resEntity = response.getEntity();
                StringBuilder str = new StringBuilder();
                in = new InputStreamReader(resEntity.getContent());
                br = new BufferedReader(in);
                String buf;
                while ((buf = br.readLine()) != null) {
                    str.append(buf); //반환값 문자열로 변경
                }

                Log.e("response", str.toString());
                Plan plan;
                jObjects = new JSONArray(str.toString());
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
                    plans.add(plan);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            // TODO: register the new account here.
            return plans; //결과 값 반환
        }
    }

    public static class ShareToggleTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            ArrayList<Plan> plans = new ArrayList<>();
            Boolean result = false;
            JSONObject jObjects = null;
            InputStreamReader in = null;
            BufferedReader br = null;
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost("https://doitplan.ml/dip/ShareToggle.php");
            ArrayList<NameValuePair> nameValues =
                    new ArrayList<NameValuePair>();
            try {
                nameValues.add(new BasicNameValuePair(
                        "plan_ID", URLDecoder.decode(ApplicationController.getEmailId(), "UTF-8")));
                //HttpPost에 넘길 값을들 Set해주기
                post.setEntity(
                        new UrlEncodedFormEntity(
                                nameValues, "UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                Log.e("Insert Log", ex.toString());
            }

            try {
                //설정한 URL을 실행시키기
                HttpResponse response = client.execute(post);
                //통신 값을 받은 Log 생성. (200이 나오는지 확인할 것~) 200이 나오면 통신이 잘 되었다는 뜻!
                Log.i("Insert Log", "response.getStatusCode:" + response.getStatusLine().getStatusCode());
                HttpEntity resEntity = response.getEntity();
                StringBuilder str = new StringBuilder();
                in = new InputStreamReader(resEntity.getContent());
                br = new BufferedReader(in);
                String buf;
                while ((buf = br.readLine()) != null) {
                    str.append(buf); //반환값 문자열로 변경
                }

                Log.e("response", str.toString());
                Plan plan;
                jObjects = new JSONObject(str.toString());
                result = jObjects.getBoolean("result");

            } catch (Exception e) {
                e.printStackTrace();
            }

            // TODO: register the new account here.
            return result; //결과 값 반환
        }
    }

}
