package com.studyHard.teamnova_android;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class timer_set_dialog extends DialogFragment {
    private static final String TAG = "timer_set_dialog";
    preferenceManager_timer_PreSet pref;

    //widget
    private EditText Input_title, Input_hour,Input_minute,Input_second;
    private TextView ActionOk, ActionCancel;
    private Toast toast;
    public static boolean add_timerSet = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.timer_setting_dialog, container, false);
        Input_title = view.findViewById(R.id.input_timer_set_title);
        Input_hour = view.findViewById(R.id.hour);
        Input_minute = view.findViewById(R.id.minute);
        Input_second = view.findViewById(R.id.second);
        ActionOk = view.findViewById(R.id.save_timer_set);
        ActionCancel = view.findViewById(R.id.cancel_timer_set);
        pref = new preferenceManager_timer_PreSet();


        ActionOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = Input_title.getText().toString();
                String hour = Input_hour.getText().toString();
                String minute = Input_minute.getText().toString();
                String second = Input_second.getText().toString();

                if(hour.isEmpty() || minute.isEmpty() || second.isEmpty()) {
                    toast = Toast.makeText(getContext(), "시간을 입력해주세요.", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (title.isEmpty()) {
                    toast = Toast.makeText(getContext(), "제목을 입력해주세요.", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    int intHour = Integer.parseInt(hour);
                    int intMinute = Integer.parseInt(minute);
                    int intSecond = Integer.parseInt(second);

                    if (intMinute > 59 || intSecond > 59) {
                        toast = Toast.makeText(getContext(), "60 미만의 값만 입력해주세요.", Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        Log.d(TAG, "onClick : timer_set save");

                        add_timerSet = true;

                        String timeSet = String.format(Locale.getDefault(), "%02d:%02d:%02d",intHour,intMinute,intSecond);

                        //shared에 key값 value(json)값을 저장.

                        // key값 만들기.
                        long now = System.currentTimeMillis();
                        Date date = new Date(now);
                        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy--MM-dd HH:mm:ss");
                        String getTime = simpleDate.format(date).toString();

                        //value 값은 json형태로 구성.
                        String save_form = "{\"title\":\""+title+"\",\"timeSet\":\""+timeSet+"\"}";

                        // shared에 key : value 저장
                        pref.setString(getContext(), getTime, save_form);

                        Intent intent = new Intent(getActivity(), timer_preset.class);

                        intent.putExtra("key", getTime);
                        intent.putExtra("title", title);
                        intent.putExtra("timeSet", timeSet);

                        // 다시 preset으로 돌아가면서 grid layout에 시간 값을 줌.
                        intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                        startActivity(intent);

                        getDialog().dismiss();
                    }
                }

            }
        });

        ActionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick : timer_set cancel");
                getDialog().dismiss();
            }
        });
        return view;


    }

    @Override
    public void onResume() {
        // Sets the height and the width of the DialogFragment
        int width = ConstraintLayout.LayoutParams.MATCH_PARENT;
        int height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setLayout(width, height);

        super.onResume();
    }


}
