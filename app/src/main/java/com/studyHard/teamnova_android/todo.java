package com.studyHard.teamnova_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.studyHard.teamnova_android.Papago.Papago;
import com.studyHard.teamnova_android.유튜브API.Youtube_Main;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.TreeMap;

import static com.studyHard.teamnova_android.todo_modify_dialog.is_modify_todo;
import static com.studyHard.teamnova_android.todo_new_task.is_add_todo;

public class todo extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton add_todo;
    private long backKeyPressedTime = 0; // 마지막으로 뒤로가기 버튼 눌렀던 시간 저장
    private Toast toast;
    private Intent intent;
    private String todo_content, getTime;
    private int pos;
    private Toolbar Toolbar;
    RecyclerViewEmptySupport recyclerView;
    Todo_Adapter todo_adapter;
    preferenceManager_ToDo pref;
    private static boolean is_created = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo);

        pref = new preferenceManager_ToDo();
        Toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(Toolbar);
        getSupportActionBar().setTitle("To Do List");

        recyclerView = findViewById(R.id.todo_recyclerview);
        recyclerView.setEmptyView(findViewById(R.id.list_empty));
        setRecyclerView();

        if(is_created == false) {
            loadTodo();
        }


        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_todo:
                        break;
                    case R.id.action_memo:
                        intent = new Intent(getApplicationContext(), memo.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent);
                        break;
                    case R.id.action_timer:
                        intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent);
                        break;
                    case R.id.action_youtube:
                        intent = new Intent(getApplicationContext(), Youtube_Main.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent);
                    case R.id.action_papago:
                        intent = new Intent(getApplicationContext(), Papago.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent);
                }
                return true;
            }
        });

        add_todo = findViewById(R.id.floating_action_button_todo);
        add_todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                todo_new_task dialog = new todo_new_task();
                dialog.show(getSupportFragmentManager(), "todo_dialog");
            }
        });

    }

    //레이아웃 및 어뎁터 결정.
    public void setRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        todo_adapter = new Todo_Adapter(this, R.layout.todo_task_layout);
        recyclerView.setAdapter(todo_adapter);
        recyclerView.setHasFixedSize(true);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (null != intent && is_add_todo == true) {
            todo_content = intent.getStringExtra("todo_content");
            getTime = intent.getStringExtra("todo_date");

            setIntent(intent);
        } else if (null != intent && is_modify_todo == true) {
            todo_content = intent.getStringExtra("todo_content");
            pos = intent.getIntExtra("position", 0);
            setIntent(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0, 0);
        if (is_add_todo == true) {
            is_add_todo = false;
            todo_adapter.addItem(new Todo_Data(getTime, todo_content));
            todo_adapter.notifyDataSetChanged();
        } else if (is_modify_todo == true) {
            is_modify_todo = false;
            todo_adapter.todo_data.remove(pos);
            todo_adapter.modifyItem(new Todo_Data(getTime ,todo_content));
            todo_adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) { // 2초 내로 한 번 더 뒤로가기 입력 없으면 문구 출력
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
            ViewGroup group = (ViewGroup) toast.getView();
            TextView msgTextView = (TextView) group.getChildAt(0);
            msgTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            toast.show();
            return;

        }

        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) { // 2초 내로 한 번 더 뒤로가기 입력 있으면 종료
            finishAffinity();
            toast.cancel();
        }
    }

    public void loadTodo() {
        SharedPreferences prefb = getSharedPreferences("ToDo_preference", MODE_PRIVATE);
        // sharedpreference는 순서가 없는 map상태로 저장됨, 얘를 순서가 있는 treemap으로 저장형식을 바꿈.
        // keys는 데이터 셋의 집합 entry 변수는 데이터 한 세트를 의미.
        TreeMap<String, ?> keys = new TreeMap<String, Object>(prefb.getAll());
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            Log.i("map key", entry.getKey());
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();

            try {
                // String으로 된 value를 JSONObject로 변환하여 key-value로 데이터 추출
                JSONObject jsonObject = new JSONObject(value);
                String content = jsonObject.getString("todo_content");
                // 저장된 값을 onCreate시점에 recyclerview에 뿌리기.
                todo_adapter.addItem(new Todo_Data(key, content));
            } catch (JSONException e) {

            }
            todo_adapter.notifyDataSetChanged();

        }
        is_created = true;


    }
}

//        // 할 일 정보가 들어있는 db 호출
//        // key value 값을 리스트 형태로 추출
//        SharedPreferences prefb = getSharedPreferences("ToDo_preference", MODE_PRIVATE);
//        Collection<?> col_val = prefb.getAll().values();
//        Iterator<?> it_val = col_val.iterator();
//        Collection<?> col_key = prefb.getAll().keySet();
//        Iterator<?> it_key = col_key.iterator();
//
//
//        while (it_val.hasNext() && it_key.hasNext()) {
//            // DB 내 dataSet을 조회
//            String key = (String) it_key.next();
//            String value = (String) it_val.next();
//
//            // value 값은 다음과 같이 저장되어있다
//            // "{\"content\":\"hi content\"}"
//            try {
//                // String으로 된 value를 JSONObject로 변환하여 key-value로 데이터 추출
//                JSONObject jsonObject = new JSONObject(value);
//                String content = jsonObject.getString("todo_content");
//                // 저장된 값을 onCreate시점에 recyclerview에 뿌리기.
//                todo_adapter.addItem(new Todo_Data(key, content));
//            } catch (JSONException e) {
//
//            }
//            is_created = true;
//            // 목록 갱신하여 뷰에 띄어줌
//            todo_adapter.notifyDataSetChanged();
//        }