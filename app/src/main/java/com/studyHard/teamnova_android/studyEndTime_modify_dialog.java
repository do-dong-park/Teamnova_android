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

import java.util.Locale;

public class studyEndTime_modify_dialog extends DialogFragment {
    private static final String TAG = "modify_endTime_dialog";

    //widget
    private EditText Input_title, Input_hour,Input_minute,Input_second;
    private TextView ActionOk, ActionCancel;
    private Toast toast;
    public static boolean is_modify_studyTime = false;
    preferenceManager_TimeRecord pref;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.timer_modify_endtime, container, false);
        Input_hour = view.findViewById(R.id.hour);
        Input_minute = view.findViewById(R.id.minute);
        Input_second = view.findViewById(R.id.second);
        ActionOk = view.findViewById(R.id.save_timer_set);
        ActionCancel = view.findViewById(R.id.cancel_timer_set);
        pref = new preferenceManager_TimeRecord();

        Bundle bundle = getArguments();

        int pos = bundle.getInt("position",0);
        int startHour = bundle.getInt("hour",0);
        int startMinute = bundle.getInt("minute",0);
        int startSecond = bundle.getInt("second",0);
        String key = bundle.getString("key");
        String rate = bundle.getString("rate");

        ActionOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String hour = Input_hour.getText().toString();
                String minute = Input_minute.getText().toString();
                String second = Input_second.getText().toString();

                if(hour.isEmpty() || minute.isEmpty() || second.isEmpty()) {
                    toast = Toast.makeText(getContext(), "시간을 입력해주세요.", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    int intHour = Integer.parseInt(hour);
                    int intMinute = Integer.parseInt(minute);
                    int intSecond = Integer.parseInt(second);

                    if ( intHour > 23 ||intMinute > 59 || intSecond > 59) {
                        toast = Toast.makeText(getContext(), "정상 범위 내 값만 입력해주세요.", Toast.LENGTH_SHORT);
                        toast.show();
                    } else {

                        long endTime = (long) (intHour * 3600000 + intMinute *  60000 + intSecond * 1000 + 15 * 3600 * 1000 );
                        long startTime = (long) (startHour * 3600000 + startMinute *  60000 + startSecond * 1000 + 15 * 3600 * 1000);

                        if (startTime > endTime) {
                            endTime = endTime + 1000 * 60 * 60 * 24;
                        }

                        long timeDiffer = endTime - startTime;
                        int studyHour = (int) timeDiffer / (1000 * 60 * 60);
                        int studyMinute = (int) timeDiffer / (1000 * 60) - studyHour * 60;
                        int studySecond = (int) timeDiffer / (1000) - studyHour * 3600 - studyMinute * 60;
                        String itemStudyTime = String.format(Locale.getDefault(), "%02d:%02d:%02d", studyHour, studyMinute, studySecond);


                        Log.d(TAG, "onClick : timer_set save");

                        is_modify_studyTime = true;

                        String modify_time = "{\"startTime\":\""+startTime+"\",\"endTime\":\""+endTime+"\",\"studyTime\":\""+itemStudyTime+"\",\"rate\":\""+rate+"\"}";

                        pref.setString(getContext(),key, modify_time);

                        Intent intent = new Intent(getActivity(), MainActivity.class);

                        intent.putExtra("position", pos);
                        intent.putExtra("studyTime", itemStudyTime);
                        intent.putExtra("endTime", endTime);
                        intent.putExtra("startTime", startTime);

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
