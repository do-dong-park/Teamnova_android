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

public class todo_modify_dialog extends DialogFragment {
    private static final String TAG = "todo_modify_dialog";
    preferenceManager_ToDo pref;

    //widgets
    private EditText mInput;
    public TextView mActionOk;
    public static boolean is_modify_todo = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.todo_modify_dialog, container, false);
        mActionOk = view.findViewById(R.id.modifyNewTaskButton);
        mInput = view.findViewById(R.id.newTaskText);

        pref = new preferenceManager_ToDo();

        Bundle bundle = getArguments();

        //key 값과 내용을 받음.
        String Text = bundle.getString("todo_content","");
        String key = bundle.getString("key","");
        int pos = bundle.getInt("position",0);
        mInput.setText(Text);

        mActionOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick : capturing input.");
                is_modify_todo = true;

                String todo_content = mInput.getText().toString();

                if (todo_content.isEmpty()) {
                    is_modify_todo = !is_modify_todo;
                    Toast toast = Toast.makeText(getContext(), "수정할 내용이 없습니다.", Toast.LENGTH_SHORT);
                    ViewGroup group = (ViewGroup) toast.getView();
                    TextView msgTextView = (TextView) group.getChildAt(0);
                    msgTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                    toast.show();
                    getDialog().dismiss();
                    return;
                }

                String modify_todo = "{\"todo_content\":\""+todo_content+"\"}";

                // pref에 수정된 값 저장.
                pref.setString(getContext(), key, modify_todo);

                Intent intent = new Intent(getContext(),todo.class);
                intent.putExtra("todo_content", todo_content);
                intent.putExtra("position",pos);
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

}
