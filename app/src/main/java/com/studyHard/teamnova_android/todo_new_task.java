package com.studyHard.teamnova_android;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
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

public class todo_new_task extends DialogFragment {
    private static final String TAG = "todo_dialog";

    //widgets
    private preferenceManager_ToDo pref;
    private EditText mInput;
    public TextView mActionOk;
    public static boolean is_add_todo = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.todo_generate_dialog, container, false);
        mActionOk = view.findViewById(R.id.saveNewTaskButton);
        mInput = view.findViewById(R.id.newTaskText);
        pref = new preferenceManager_ToDo();

        mActionOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick : capturing input.");
                is_add_todo = true;
                String todo_content = mInput.getText().toString();

                if (todo_content.isEmpty()) {
                    is_add_todo = !is_add_todo;
                    Toast toast = Toast.makeText(getContext(), "저장할 내용이 없어 할 일을 저장하지 않았습니다.", Toast.LENGTH_SHORT);
                    ViewGroup group = (ViewGroup) toast.getView();
                    TextView msgTextView = (TextView) group.getChildAt(0);
                    msgTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                    toast.show();
                    getDialog().dismiss();
                    return;
                }

                // key값은 고유한 값, 시간으로 부여
                long now = System.currentTimeMillis();
                Date mDate = new Date(now);
                SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy--MM-dd HH:mm:ss");
                String getTime = simpleDate.format(mDate).toString();

                // String 값을 json 형태로 저장. (value)
                String save_todo = "{\"todo_content\":\""+todo_content+"\"}";

                pref.setString(getContext(),getTime,save_todo);

                Intent intent = new Intent(getContext(),todo.class);
                intent.putExtra("todo_date", getTime);
                intent.putExtra("todo_content", todo_content);
                intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
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



    //    @NonNull
//    @Override
//    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle("Notice")
//                .setMessage("This is a message to you")
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                });
//        return builder.create();
//    }

}
