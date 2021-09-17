package com.studyHard.teamnova_android;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class timer_select_dialog extends DialogFragment {

    private static final String TAG = "timer_dialog";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.select)
                .setItems(R.array.timer_kind_array, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if( which == 0) {
                            startActivity(new Intent(getActivity(), timer_preset.class));
                        }
                        else {
                            startActivity(new Intent(getActivity(), timer_stopwatch.class ));
                        }
                    }
                });
        return builder.create();
    }
}
