package com.teamsix.doitplan;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
     * 카카오톡 토큰값 및 이메일 확인 백그라운드 작업
     */
    public static class UserTokenCheckTask extends AsyncTask<Void, Void, JSONObject> {
        final String mEmail;
        final String mToken;
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

                return new JSONObject(str.toString());

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
     * 기상청 API를 이용한 날씨정보 업데이트
     */
    public static class GetForecastTask extends AsyncTask<Void, Void, JSONObject> {

        int x, y;
        final String SERVICE_KEY = "tnC6l3h4zlaRPJ3gKXJRN8TxzoXz7H8V0DnVFOm6p2dumQizuEZ6Y45nI501a8PC%2BsY4BSIxTIGzlYxB9fpP9Q%3D%3D";
        String baseDate, baseTime;

        public GetForecastTask(double lat, double lon, String date, String time) {
            LatXLngY convert = convertGRID_GPS(0, lat, lon);
            x = (int) convert.x;
            y = (int) convert.y;
            baseDate = date;
            baseTime = time;
        }

        private LatXLngY convertGRID_GPS(int mode, double lat_X, double lon_Y) {
            double RE = 6371.00877; // 지구 반경(km)
            double GRID = 5.0; // 격자 간격(km)
            double SLAT1 = 30.0; // 투영 위도1(degree)
            double SLAT2 = 60.0; // 투영 위도2(degree)
            double OLON = 126.0; // 기준점 경도(degree)
            double OLAT = 38.0; // 기준점 위도(degree)
            double XO = 43; // 기준점 X좌표(GRID)
            double YO = 136; // 기1준점 Y좌표(GRID)

            //
            // LCC DFS 좌표변환 ( code : "TO_GRID"(위경도->좌표, lat_X:위도,  lng_Y:경도), "TO_GPS"(좌표->위경도,  lat_X:x, lng_Y:y) )
            //

            double DEGRAD = Math.PI / 180.0;
            double RADDEG = 180.0 / Math.PI;

            double re = RE / GRID;
            double slat1 = SLAT1 * DEGRAD;
            double slat2 = SLAT2 * DEGRAD;
            double olon = OLON * DEGRAD;
            double olat = OLAT * DEGRAD;

            double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
            sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
            double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
            sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
            double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
            ro = re * sf / Math.pow(ro, sn);
            LatXLngY rs = new LatXLngY();

            if (mode == 0) {
                rs.lat = lat_X;
                rs.lng = lon_Y;
                double ra = Math.tan(Math.PI * 0.25 + (lat_X) * DEGRAD * 0.5);
                ra = re * sf / Math.pow(ra, sn);
                double theta = lon_Y * DEGRAD - olon;
                if (theta > Math.PI) theta -= 2.0 * Math.PI;
                if (theta < -Math.PI) theta += 2.0 * Math.PI;
                theta *= sn;
                rs.x = Math.floor(ra * Math.sin(theta) + XO + 0.5);
                rs.y = Math.floor(ro - ra * Math.cos(theta) + YO + 0.5);
            } else {
                rs.x = lat_X;
                rs.y = lon_Y;
                double xn = lat_X - XO;
                double yn = ro - lon_Y + YO;
                double ra = Math.sqrt(xn * xn + yn * yn);
                if (sn < 0.0) {
                    ra = -ra;
                }
                double alat = Math.pow((re * sf / ra), (1.0 / sn));
                alat = 2.0 * Math.atan(alat) - Math.PI * 0.5;

                double theta = 0.0;
                if (Math.abs(xn) <= 0.0) {
                    theta = 0.0;
                } else {
                    if (Math.abs(yn) <= 0.0) {
                        theta = Math.PI * 0.5;
                        if (xn < 0.0) {
                            theta = -theta;
                        }
                    } else theta = Math.atan2(xn, yn);
                }
                double alon = theta / sn + olon;
                rs.lat = alat * RADDEG;
                rs.lng = alon * RADDEG;
            }
            return rs;
        }

        class LatXLngY {
            public double lat;
            public double lng;

            public double x;
            public double y;

        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject result = null;
            OkHttpClient client = new OkHttpClient();
            StringBuffer url = new StringBuffer();
            url.append("ServiceKey=" + SERVICE_KEY);
            url.append("&base_date=" + baseDate);
            url.append("&base_time=" + baseTime);
            url.append("&nx=" + x);
            url.append("&ny=" + y);
            url.append("&numOfRows=10&_type=json");


            Request request = new Request.Builder()
                    .url("http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastGrib?" + url.toString())
                    .build();

            try {
                Response response = client.newCall(request).execute();
                //Log.e("Forecast",response.body().string());
                result = new JSONObject(response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    /**
     * 서버에 Push메시지 요청작업
     */
    public static class PuchNotificationTask extends AsyncTask<Void, Void, JSONObject> {

        String title, msg;

        public PuchNotificationTask(String title, String msg) {
            this.title = title;
            this.msg = msg;
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject result = null;
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("email", ApplicationController.getEmailId())
                    .add("title", title)
                    .add("message", msg)
                    .build();

            Request request = new Request.Builder()
                    .url("https://doitplan.ml/dip/pushmsg.php")
                    .post(body)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                //Log.e("PuchNotificationTask",response.body().string());

                result = new JSONObject(response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return result;
        }
    }

    /**
     * 다이얼로그없이 서버 접속
     */
    public static class ConnectServerTask extends AsyncTask<Void, Void, String> {

        RequestBody post;
        String url;

        public ConnectServerTask(RequestBody data, String url) {
            this.post = data;
            this.url = url;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result = null;
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://doitplan.ml/dip/" + url)
                    .post(post)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                //Log.e("PuchNotificationTask",response.body().string());

                result = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }
    }

    /**
     * 다이얼로그를 포함한 서버 값 전달 TASK
     */
    public static class ConnectServerDialogTask extends AsyncTask<Void, Void, String> {

        private ProgressDialog asyncDialog;
        RequestBody post;
        String url;
        String msg;

        public ConnectServerDialogTask(Context context, String msg, RequestBody data, String url) {
            this.post = data;
            this.url = url;
            this.msg = msg;
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
            asyncDialog.setMessage(msg);

            // show dialog
            asyncDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result = null;
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://doitplan.ml/dip/" + url)
                    .post(post)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                //Log.e("PuchNotificationTask",response.body().string());

                result = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            asyncDialog.dismiss();
        }

        @Override
        protected void onCancelled() {
            asyncDialog.dismiss();
            super.onCancelled();
        }
    }

}
