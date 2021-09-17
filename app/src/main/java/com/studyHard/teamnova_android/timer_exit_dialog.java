package com.studyHard.teamnova_android;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class timer_exit_dialog extends DialogFragment {

    private static final String TAG = "timer_exit_dialog";
    private Long end_time;
    public static boolean is_timer_end = false;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        // 시작시간 전달 받기
        Bundle bundle = getArguments();

        String watchTime = bundle.getString("watchTime");
        Long start_time = bundle.getLong("Start_time");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.timer_exit)
                .setPositiveButton(R.string.end, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        is_timer_end = true;
                        // 끝시간 저장
                        end_time = System.currentTimeMillis();

                        Bundle bundle = new Bundle();
                        bundle.putLong("Start_time", start_time);
                        bundle.putLong("End_time", end_time);
                        bundle.putString("watchTime",watchTime);

                        timer_exit_ConcenRate_dialog goto_rate = new timer_exit_ConcenRate_dialog();
                        goto_rate.setArguments(bundle);
                        goto_rate.show(getFragmentManager(), "timer_rate_dialog");
                    }
                })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getDialog().dismiss();
            }
        });

        return builder.create();


    }
}
