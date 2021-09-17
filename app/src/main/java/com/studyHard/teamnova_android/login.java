package com.studyHard.teamnova_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Iterator;

public class login extends AppCompatActivity {
    private EditText Email, Password;
    private Button Login;
    private TextView findId, findPassword, signUp;
    private Toast toast;
    private String email,password, loginEmail, loginPassword;
    preferenceManager_login memberPref;
    public static boolean is_login = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Email = (EditText)findViewById(R.id.Email);
        Password = (EditText)findViewById(R.id.Password);
        Login = (Button)findViewById(R.id.login);
        findId = (TextView)findViewById(R.id.findId);
        findPassword = (TextView)findViewById(R.id.findPassword);
        signUp = (TextView)findViewById(R.id.signUp);
        memberPref = new preferenceManager_login();



        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginEmail = Email.getText().toString();
                loginPassword = Password.getText().toString();

                loadMember();

                if (is_login == true ) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else if (loginEmail.isEmpty() == true){
                    // Email 입력해주세요.
                    toast = Toast.makeText(getApplicationContext(), "이메일을 입력해주세요.", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (loginPassword.isEmpty() == true){
                    // 비밀번호 입력해주세요.
                    toast = Toast.makeText(getApplicationContext(), "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    // 로그인 실패
                    Email.setText("");
                    Password.setText("");
                    toast = Toast.makeText(getApplicationContext(), "등록되지 않은 이메일이거나 \n이메일 또는 비밀번호를 잘못 입력했습니다.", Toast.LENGTH_SHORT);
                    ViewGroup group = (ViewGroup)toast.getView();
                    TextView msgTextView = (TextView)group.getChildAt(0);
                    msgTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,14);
                    toast.show();
                }
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), login_signUp.class));
            }
        });
    }

    public void loadMember() {
        //쉐어드 모든 키 벨류 가져오기
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
                password = (String) jsonObject.getString("password");
            } catch (JSONException e) {
                Log.d("MainActivity", "JSONObject : " + e);
            }
            if (loginEmail.equals(email) && loginPassword.equals(password)) {
                is_login = true;
                break;
            }
        }
    }

}
