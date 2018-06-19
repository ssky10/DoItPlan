package com.teamsix.doitplan;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.FormBody;

public class RegistActivity extends AppCompatActivity {


    private EditText etEmail; //이메일 입력 부분
    private EditText etPassword; //비밀번호 입력 부분
    private EditText etPasswordConfirm; //비밀번호 입력 확인
    private Button btnDone; //확인버튼
    private Button btnCancel; //취소 버튼
    private EditText etNickname; //닉네입 입력 팡
    private Button btnCheck; //중복 확인 버튼
    private Boolean isCheck = true; //중복 확인 여부
    private int type;
    private String token="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        Intent intent = getIntent();
        type = intent.getIntExtra("type",0); //0:회원가입 1:카카오톡가입 2:페이스북가입 3:정보수정

        //각 요소와 자바 객체 연결
        etEmail = (EditText) findViewById(R.id.etEmail);
        etNickname = (EditText) findViewById(R.id.etNick);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etPasswordConfirm = (EditText) findViewById(R.id.etPasswordConfirm);
        btnDone = (Button) findViewById(R.id.btnDone);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCheck = (Button) findViewById(R.id.btnCheck);


        switch (type){
            case 0:
                isCheck = false;
                break;
            case 1:
                btnCheck.setVisibility(View.GONE);
                etEmail.setText(intent.getStringExtra("email"));
                token = intent.getStringExtra("Ktoken");
                etEmail.setClickable(false);
                etEmail.setFocusable(false);
                break;
            case 2:
                btnCheck.setVisibility(View.GONE);
                etEmail.setText(intent.getStringExtra("email"));
                token = intent.getStringExtra("Ftoken");
                etEmail.setClickable(false);
                etEmail.setFocusable(false);
                break;
            case 3:
                getSupportActionBar().setTitle("회원정보수정");
                btnCheck.setVisibility(View.GONE);
                etNickname.setText(ApplicationController.getNickname());
                etEmail.setText(ApplicationController.getEmailId());
                etEmail.setClickable(false);
                etEmail.setFocusable(false);
                btnDone.setText("수정하기");
                break;
        }

        //이메일 창의 값이 변경된 경우 실행
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //값이 변경되기전
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //값이 변경될때
                if (isCheck & type==0) { //중복확인 후 이메일 값 변경시 중복확인 취소
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
            @SuppressLint("StaticFieldLeak")
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
                new ConnectServer.UserSingUpTask(type ,etEmail.getText().toString(), etPassword.getText().toString(), etNickname.getText().toString(),RegistActivity.this,token){
                    @Override
                    protected void onPostExecute(final Boolean success) {
                        asyncDialog.dismiss();

                        if (success) {
                            switch (type){
                                case 0:
                                case 1:
                                case 2:
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                    break;
                                case 3:
                                    Toast.makeText(RegistActivity.this,"수정이 정상적으로 완료되었습니다.",Toast.LENGTH_SHORT).show();
                                    break;
                            }

                        } else {
                            Toast.makeText(RegistActivity.this,"오류가 발생하였습니다.",Toast.LENGTH_SHORT).show();
                        }
                    }
                }.execute();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View view) {
                if (isCheck) return;
                if ((etEmail.getText().toString().length() <= 4) || (!isEmailValid(etEmail.getText().toString()))) {
                    Toast.makeText(RegistActivity.this, "올바른 Email을 입력하세요!", Toast.LENGTH_SHORT).show();
                    etEmail.requestFocus();
                    return;
                }

                new ConnectServer.ConnectServerTask(new FormBody.Builder()
                        .add("email", etEmail.getText().toString())
                        .build(),"checkId.php"){
                    @Override
                    protected void onPreExecute() {
                        btnCheck.setText("확인중");
                        super.onPreExecute();
                    }

                    @Override
                    protected void onPostExecute(final String success) {
                        try {
                            JSONObject jsonObject = new JSONObject(success);
                            if (jsonObject.getBoolean("result")) {
                                btnCheck.setText("성공");
                                btnCheck.setTextColor(Color.CYAN);
                                isCheck = true;
                            } else {
                                btnCheck.setText("실패");
                                btnCheck.setTextColor(Color.RED);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }.execute();
            }
        });
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

}