package com.studyHard.teamnova_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

public class login_signUp extends AppCompatActivity {

    private EditText signUpEmail, signUpPassword, signUpConfirm;
    private Button signUp, doubleCheck;
    private Toast toast;
    private String email, password;
    preferenceManager_login memberPref;
    public static boolean is_valid_email = false, is_doubleChecked = false;
    private ArrayList<String> doubleCheckEmail = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_sign_up);

        memberPref = new preferenceManager_login();

        signUpEmail = (EditText) findViewById(R.id.sign_up_email);
        signUpPassword = (EditText) findViewById(R.id.sign_up_password);
        signUpConfirm = (EditText) findViewById(R.id.sign_up_confirm_password);
        signUp = (Button) findViewById(R.id.sign_up);
        doubleCheck = findViewById(R.id.doubleCheck);

        doubleCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefb = getSharedPreferences("member_info", MODE_PRIVATE);
                Collection<?> col_val = prefb.getAll().values();
                Iterator<?> it_val = col_val.iterator();
                Collection<?> col_key = prefb.getAll().keySet();
                Iterator<?> it_key = col_key.iterator();

                while (it_val.hasNext() && it_key.hasNext()) {
                    String key = (String) it_key.next();
                    String value = (String) it_val.next();
                    try {
                        JSONObject jsonObject = new JSONObject(value);
                        email = (String) jsonObject.getString("email");
                    } catch (JSONException e) {
                        Log.d("MainActivity", "JSONObject : " + e);
                    }
                    doubleCheckEmail.add(email);
                }
                if (doubleCheckEmail.contains(signUpEmail.getText().toString())) {
                    toast = Toast.makeText(getApplicationContext(), "이미 등록된 이메일입니다.", Toast.LENGTH_SHORT);
                    is_valid_email = false;
                    is_doubleChecked = true;
                } else {
                    is_valid_email = true;
                    is_doubleChecked = true;
                    toast = Toast.makeText(getApplicationContext(), "이용 가능한 이메일입니다.", Toast.LENGTH_SHORT);
                }
                toast.show();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 회원가입 조건 파악이 필요해요!

                String correctEmail = signUpEmail.getText().toString();
                String correctPassword = signUpPassword.getText().toString();
                String correctConfirm = signUpConfirm.getText().toString();

                if (is_doubleChecked == true) {
                    if (is_valid_email == true) {
                        if (correctEmail.isEmpty() == false && correctPassword.isEmpty() == false && correctPassword.equals(correctConfirm) == true) {
                            Intent intent = new Intent(getApplicationContext(), login.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 회원가입창 닫기, 로그인 창으로

                            // sharedPreference에 이메일 비밀번호 저장 / 키값은 시간
                            String save_memberInfo = "{\"email\":\"" + correctEmail + "\",\"password\":\"" + correctPassword + "\"}";
                            long now = System.currentTimeMillis();
                            Date mDate = new Date(now);
                            SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                            String getTime = simpleDate.format(mDate).toString();
                            memberPref.setString(getApplication(), getTime, save_memberInfo);
                            startActivity(intent);
                        } else if (correctEmail.isEmpty() == true || !correctEmail.contains("@")) {
                            //이메일을 입력해주세요.
                            signUpEmail.setText("");
                            toast = Toast.makeText(getApplicationContext(), "이메일을 입력해주세요.", Toast.LENGTH_SHORT);
                            toast.show();

                        } else if (correctPassword.isEmpty() == true) {
                            signUpPassword.setText("");
                            signUpConfirm.setText("");
                            toast = Toast.makeText(getApplicationContext(), "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT);
                            toast.show();

                        } else if (correctPassword.equals(correctConfirm) == false) {
                            signUpPassword.setText("");
                            signUpConfirm.setText("");
                            // 비밀번호와 비밀번호 확인이 일치하지 않습니다.
                            toast = Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    } else {
                        signUpEmail.setText("");
                        is_doubleChecked = false;
                        // 이미 등록된 이메일입니다.
                        toast = Toast.makeText(getApplicationContext(), "이미 등록된 이메일입니다.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                } else {
                    //유효성 검사를 진행해주세요.
                    toast = Toast.makeText(getApplicationContext(), "이메일 중복확인을 진행해주세요.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }


        });
    }
}

