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

public class memo_modify_dialog extends DialogFragment {
    private static final String TAG = "memo_modify_dialog";
    preferenceManager_MEMO pref;

    //widget
    private EditText mInput, mTitleInput;
    public TextView mActionOk, mActionCancel, mImageAdd;
    private ImageView imageView;
    public static boolean is_modify_memo = false;
    boolean is_image_null = false;
    Uri image;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.memo_modify_layout,container, false);
        mActionOk = view.findViewById(R.id.memo_modify_save);
        mActionCancel = view.findViewById(R.id.memo_modify_cancel);
        mImageAdd = view.findViewById(R.id.memo_modifyImage);
        mInput = view.findViewById(R.id.memo_modify_editText);
        mTitleInput = view.findViewById(R.id.memo_modify_editTitle);
        imageView = view.findViewById(R.id.memo_modifyImageView);
        pref = new preferenceManager_MEMO();

        Bundle bundle = getArguments();
        image = bundle.getParcelable("image");
        String key = bundle.getString("key", "");
        String Title = bundle.getString("memo_title", "");
        String Text = bundle.getString("memo_content","");
        int pos = bundle.getInt("position",0);


        mTitleInput.setText(Title);
        mInput.setText(Text);

        Glide.with(getContext()).load(image).override(300,400).into(imageView);

        if(imageView.getDrawable() == null){

        }else{
            mImageAdd.setVisibility(View.GONE);
        }


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setImageDrawable(null);
                mImageAdd.setVisibility(View.VISIBLE);
                is_image_null = true;
                image = null;
            }
        });

        mImageAdd.setOnClickListener(new View.OnClickListener() {
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

                String memo_title = mTitleInput.getText().toString();
                String memo_content = mInput.getText().toString();
                is_modify_memo = true;

                if (memo_title.isEmpty() && memo_content.isEmpty() && is_image_null == true) {
                    is_modify_memo = !is_modify_memo;
                    Toast toast = Toast.makeText(getContext(), "수정할 내용이 없어 메모를 수정하지 않았습니다.", Toast.LENGTH_SHORT);
                    ViewGroup group = (ViewGroup) toast.getView();
                    TextView msgTextView = (TextView) group.getChildAt(0);
                    msgTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                    toast.show();
                    getDialog().dismiss();
                    return;

                } else if (memo_title.isEmpty() && is_image_null == true) {
                    memo_title = "이미지";
                }

                // shared 에 원래 있던 키값 삭제 후 수정시점의 key값 생성.
                pref.removeKey(getContext(), key);

                // key 값 생성 (현재시간)
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy--MM-dd HH:mm:ss");
                String getTime = simpleDate.format(date);

                // 수정된 값을 shared에 갱신.
                String modify_memo = "{\"memo_title\" : \""+memo_title+"\", \"memo_content\":\""+memo_content+"\", \"photo\" : \""+image+"\"}";
                pref.setString(getContext(), getTime, modify_memo);

                // 값입력_액티비티에서 사용
                Intent intent = new Intent(getContext(), memo.class); //액티비티 전환
                // 전달할 값 ( 첫번째 인자 : key, 두번째 인자 : 실제 전달할 값 )
                intent.putExtra("image", image);
                intent.putExtra("memo_title", memo_title);
                intent.putExtra("memo_content", memo_content);
                intent.putExtra("position",pos);
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
                mImageAdd.setVisibility(View.GONE);
                is_image_null = false;
            }
        }
    }


}
