package com.teamsix.doitplan;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
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

public class RegistActivity extends AppCompatActivity {


    private EditText etEmail; //이메일 입력 부분
    private EditText etPassword; //비밀번호 입력 부분
    private EditText etPasswordConfirm; //비밀번호 입력 확인
    private Button btnDone; //확인버튼
    private Button btnCancel; //취소 버튼
    private EditText etNickname; //닉네입 입력 팡
    private Button btnCheck; //중복 확인 버튼
    private Boolean isCheck = false; //중복 확인 여부

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        //각 요소와 자바 객체 연결
        etEmail = (EditText) findViewById(R.id.etEmail);
        etNickname = (EditText) findViewById(R.id.etNick);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etPasswordConfirm = (EditText) findViewById(R.id.etPasswordConfirm);
        btnDone = (Button) findViewById(R.id.btnDone);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCheck = (Button) findViewById(R.id.btnCheck);

        //이메일 창의 값이 변경된 경우 실행
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //값이 변경되기전
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //값이 변경될때
                if (isCheck) { //중복확인 후 이메일 값 변경시 중복확인 취소
                    isCheck = false;
                    btnCheck.setTextColor(Color.BLACK);
                    btnCheck.setText("중복확인");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //값이 변경된 후
            }
        });

        etPasswordConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = etPassword.getText().toString();
                String confirm = etPasswordConfirm.getText().toString();

                if (password.equals(confirm)) {
                } else {
                    etPasswordConfirm.setError("비밀번호가 일치하지 않습니다.");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((etEmail.getText().toString().length() == 0) || (!isEmailValid(etEmail.getText().toString()))) {
                    Toast.makeText(RegistActivity.this, "올바른 Email을 입력하세요!", Toast.LENGTH_SHORT).show();
                    etEmail.requestFocus();
                    return;
                }

                if (etPassword.getText().toString().length() == 0) {
                    Toast.makeText(RegistActivity.this, "비밀번호를 입력하세요!", Toast.LENGTH_SHORT).show();
                    etPassword.requestFocus();
                    return;
                }
                if (!etPassword.getText().toString().equals(etPasswordConfirm.getText().toString())) {
                    Toast.makeText(RegistActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    etPasswordConfirm.requestFocus();
                    return;
                }
                if (!isCheck) {
                    Toast.makeText(RegistActivity.this, "이메일 중복확인을 해주세요.", Toast.LENGTH_SHORT).show();
                    etEmail.requestFocus();
                    return;
                }
                new UserSingUpTask(etEmail.getText().toString(), etPassword.getText().toString(), etNickname.getText().toString()).execute();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCheck) return;
                if ((etEmail.getText().toString().length() <= 4) || (!isEmailValid(etEmail.getText().toString()))) {
                    Toast.makeText(RegistActivity.this, "올바른 Email을 입력하세요!", Toast.LENGTH_SHORT).show();
                    etEmail.requestFocus();
                    return;
                }
                UserIdCheckTask ch = new UserIdCheckTask(etEmail.getText().toString(), btnCheck);
                ch.execute();
            }
        });
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    /**
     * 로그인 확인 백그라운드 작업
     */
    public class UserSingUpTask extends AsyncTask<Void, Void, Boolean> {
        private final String mEmail;
        private final String mPassword;
        private final String mNickname;

        ProgressDialog asyncDialog = new ProgressDialog(
                RegistActivity.this);


        UserSingUpTask(String email, String password, String nickname) {
            mEmail = email;
            mPassword = password;
            mNickname = nickname;
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

        @Override
        protected void onPostExecute(final Boolean success) {
            asyncDialog.dismiss();

            if (success) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                //mPasswordView.setError(getString(R.string.error_incorrect_password));
                //mPasswordView.requestFocus();
            }
        }

    }

    /**
     * 중복확인 백그라운드 작업
     */
    public class UserIdCheckTask extends AsyncTask<Void, Void, Boolean> {
        private final String mEmail;
        private Button btn;

        UserIdCheckTask(String email, Button button) {
            mEmail = email;
            btn = button;
        }

        @Override
        protected void onPreExecute() {
            btn.setText("확인중");
            super.onPreExecute();
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

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                btn.setText("성공");
                btn.setTextColor(Color.CYAN);
                isCheck = true;
            } else {
                btn.setText("실패");
                btn.setTextColor(Color.RED);
            }
        }

    }
}