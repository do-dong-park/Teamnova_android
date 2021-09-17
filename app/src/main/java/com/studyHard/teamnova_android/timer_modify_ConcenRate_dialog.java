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

public class timer_modify_ConcenRate_dialog extends DialogFragment {

    private static final String TAG = "modify_rate_dialog";
    public static String value;
    public static int REQUEST_CODE = 1;
    public static boolean is_modify_rate = false;
    preferenceManager_TimeRecord pref;

    //widget
    public Button ActionSave;
    public RatingBar ratingBar;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.timer_modify_rating, container, false);
        ActionSave = view.findViewById(R.id.rating_save);
        ratingBar = view.findViewById(R.id.rating);

        pref = new preferenceManager_TimeRecord();

        Bundle bundle = getArguments();
        int pos = bundle.getInt("position",0);
        String studyTime = bundle.getString("studyTime");
        String key = bundle.getString("key");
        long StartTime = bundle.getLong("startTime",0);
        long EndTime = bundle.getLong("endTime",0);


        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                value = String.valueOf(rating);
            }
        });

        ActionSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                is_modify_rate = true;

                String modify_rate = "{\"startTime\":\""+StartTime+"\",\"endTime\":\""+EndTime+"\",\"studyTime\":\""+studyTime+"\",\"rate\":\""+value+"\"}";

                pref.setString(getContext(),key,modify_rate);

                Log.d(TAG, "onClick : rating save");
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("rating", value);
                intent.putExtra("position", pos);
                intent.putExtra("studyTime", studyTime);
                startActivity(intent);
                getDialog().dismiss();
            }
        });

        return view;
    }
}
