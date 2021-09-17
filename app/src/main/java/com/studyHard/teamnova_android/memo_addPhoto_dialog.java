package com.studyHard.teamnova_android;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;

public class memo_addPhoto_dialog extends DialogFragment {
    private static final String TAG = "memo_photo_dialog";
    Uri image;
    preferenceManager_MEMO pref;

    //widget
    public TextView mActionOk, mActionCancel;
    ImageView imageView;
    public static boolean is_add_photo = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.memo_photo_add_dialog, container, false);
        imageView = view.findViewById(R.id.add_photo);

        Bundle bundle = getArguments();
        image = bundle.getParcelable("image");
        Glide.with(getContext()).load(image).into(imageView);

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
