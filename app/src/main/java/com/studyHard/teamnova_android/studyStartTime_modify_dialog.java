package com.studyHard.teamnova_android;

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

public class studyStartTime_modify_dialog extends DialogFragment {
    private static final String TAG = "modify_startTime_dialog";

    //widget
    private EditText Input_title, Input_hour,Input_minute,Input_second;
    private TextView ActionOk, ActionCancel;
    private Toast toast;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.timer_modify_starttime, container, false);
        Input_hour = view.findViewById(R.id.hour);
        Input_minute = view.findViewById(R.id.minute);
        Input_second = view.findViewById(R.id.second);
        ActionOk = view.findViewById(R.id.save_timer_set);
        ActionCancel = view.findViewById(R.id.cancel_timer_set);

        Bundle bundle = getArguments();

        int pos = bundle.getInt("position",0);
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


                    if (intHour > 23 || intMinute > 59 || intSecond > 59) {
                        toast = Toast.makeText(getContext(), "정상 범위 내 값만 입력해주세요.", Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        Log.d(TAG, "onClick : timer_set save");

                        studyEndTime_modify_dialog modified_endTime = new studyEndTime_modify_dialog();

                        Bundle bundle = new Bundle();

                        bundle.putInt("position", pos);
                        bundle.putString("key", key);
                        bundle.putString("rate", rate);


                        bundle.putInt("hour", intHour);
                        bundle.putInt("minute", intMinute);
                        bundle.putInt("second", intSecond);

                        modified_endTime.setArguments(bundle);
                        modified_endTime.show(getFragmentManager(), "modify_endTime_dialog");

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


