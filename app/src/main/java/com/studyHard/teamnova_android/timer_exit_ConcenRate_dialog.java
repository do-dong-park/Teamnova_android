package com.studyHard.teamnova_android;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.studyHard.teamnova_android.timer_layout.timer_start;
import static com.studyHard.teamnova_android.timer_stopwatch.is_start;

public class timer_exit_ConcenRate_dialog extends DialogFragment {

    private static final String TAG = "timer_rate_dialog";
    public static String value;
    public static int REQUEST_CODE = 1;
    public static boolean is_add_timer = false;
    preferenceManager_TimeRecord pref;

    //widget
    public Button ActionSave;
    public RatingBar ratingBar;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.timer_rating, container, false);
        ActionSave = view.findViewById(R.id.rating_save);
        ratingBar = view.findViewById(R.id.rating);

        pref = new preferenceManager_TimeRecord();

        // 시작 및 끝시간 전달 받기
        Bundle bundle = getArguments();

        String watchTime = bundle.getString("watchTime");
        Long startTime = bundle.getLong("Start_time");
        Long endTime = bundle.getLong("End_time");


        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                value = String.valueOf(rating);
            }
        });

        ActionSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                is_add_timer = true;
                is_start = false;
                timer_start = false;

                //shared에 key : value 저장

                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy--MM-dd HH:mm:ss");
                String getTime = simpleDate.format(date);

                String save_record = "{\"startTime\":\""+startTime+"\",\"endTime\":\""+endTime+"\",\"studyTime\" : \""+watchTime+"\",\"rate\":\""+value+"\"}";

                pref.setString(getContext(),getTime,save_record);

                Log.d(TAG, "onClick : rating save");
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("key", getTime);
                intent.putExtra("studyTime", watchTime);
                intent.putExtra("rating", value);
                intent.putExtra("Start_time", startTime);
                intent.putExtra("End_time",endTime);
                startActivity(intent);

                getDialog().dismiss();
            }
        });

        return view;
    }
}
