package com.studyHard.teamnova_android;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class memo_write extends DialogFragment {
    private static final String TAG = "memo_dialog";
    preferenceManager_MEMO pref;

    //widget
    private EditText mInputContent, mInputTitle;
    public TextView mActionOk, mActionCancel, mAddPhoto;
    private ImageView imageView;
    public static boolean is_add_memo = false;
    private Uri image;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.memo_write_layout,container, false);

        mAddPhoto = view.findViewById(R.id.memo_addImage);
        mActionOk = view.findViewById(R.id.memo_save);
        mActionCancel = view.findViewById(R.id.memo_cancel);
        imageView = view.findViewById(R.id.memo_imageView);
        mInputTitle = view.findViewById(R.id.memo_editTitle);
        mInputContent = view.findViewById(R.id.memo_editText);
        pref = new preferenceManager_MEMO();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setImageDrawable(null);
                mAddPhoto.setVisibility(View.VISIBLE);
            }
        });

        mAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 0);
            }
        });

        mActionOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick : memo save");
                is_add_memo = true;

                String memo_title = mInputTitle.getText().toString();
                String memo_content = mInputContent.getText().toString();

                if (memo_title.isEmpty() && memo_content.isEmpty() && image == null) {
                    is_add_memo = !is_add_memo;
                    Toast toast = Toast.makeText(getContext(), "저장할 내용이 없어 메모를 저장하지 않았습니다.", Toast.LENGTH_SHORT);
                    ViewGroup group = (ViewGroup) toast.getView();
                    TextView msgTextView = (TextView) group.getChildAt(0);
                    msgTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                    toast.show();
                    getDialog().dismiss();
                    return;
                } else if (memo_title.isEmpty() && image != null) {
                    memo_title = "이미지";
                }

                //key(생성 시간) value(메모 내용)값 생성하여, pref에 저장.

                // key 값 생성 (현재시간)
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy--MM-dd HH:mm:ss");
                String getTime = simpleDate.format(date);

                // value값 구하기. json 형태로 저장. (key : value 형태로 또 들어갈거임. 시간 : {표지1 : 값} 형식으로 ㅇㅇ)
                String save_memo = "{\"memo_title\" : \""+memo_title+"\", \"memo_content\":\""+memo_content+"\", \"photo\" : \""+image+"\"}";

                //pref에 저장
                pref.setString(getContext(), getTime, save_memo);

                // 값입력_액티비티에서 사용
                Intent intent = new Intent(getContext(), memo.class); //액티비티 전환
                // 전달할 값 ( 첫번째 인자 : key, 두번째 인자 : 실제 전달할 값 )
                intent.putExtra("memo_title", memo_title);
                intent.putExtra("memo_content", memo_content);
                intent.putExtra("image", image);
                intent.putExtra("key", getTime);
                intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                getDialog().dismiss();
            }
        });

        mActionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick : memo cancel");
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                // Glide 사용해서 이미지 출력 (load: 이미지 경로, override: 이미지 가로,세로 크기 조정, into: 이미지를 출력할 ImageView 객체)
                image = data.getData();
                Glide.with(getContext()).load(data.getData()).override(300,400).into(imageView);
                mAddPhoto.setVisibility(View.GONE);
            }
        }
    }


}
