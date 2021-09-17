    package com.studyHard.teamnova_android;

    import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import androidx.recyclerview.widget.LinearLayoutManager;

    import com.studyHard.teamnova_android.Papago.Papago;
    import com.studyHard.teamnova_android.유튜브API.Youtube_Main;
    import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.TreeMap;

import static com.studyHard.teamnova_android.memo_modify_dialog.is_modify_memo;
import static com.studyHard.teamnova_android.memo_write.is_add_memo;

public class memo extends AppCompatActivity {
    private FloatingActionButton add_memo;
    private BottomNavigationView bottomNavigationView;
    private RecyclerViewEmptySupport recyclerView;
    private Memo_Adapter memo_adapter;
    private long backKeyPressedTime = 0; // 마지막으로 뒤로가기 버튼 눌렀던 시간 저장
    private Toast toast;
    private Intent intent;
    private String memo_title, memo_content, key;
    private int position;
    private Uri image, myUri;
    private androidx.appcompat.widget.Toolbar Toolbar;
    private static boolean is_created = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memo);

        Toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(Toolbar);
        getSupportActionBar().setTitle("메모장");

        recyclerView = (RecyclerViewEmptySupport) findViewById(R.id.recyclerview);
        recyclerView.setEmptyView(findViewById(R.id.list_empty));
        setRecyclerView();

        if (is_created == false) {
            loadMemo();
        }


        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_todo:
                        intent = new Intent(getApplicationContext(), todo.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent);
                        break;
                    case R.id.action_memo:
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

        add_memo = findViewById(R.id.floating_action_button_memo);
        add_memo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                memo_write memo_write_dialog = new memo_write();
                memo_write_dialog.show(getSupportFragmentManager(), "memo_dialog");

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


        memo_adapter = new Memo_Adapter(this, R.layout.memo_layout);
        recyclerView.setAdapter(memo_adapter);
        recyclerView.setHasFixedSize(true);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (null != intent && is_add_memo == true) {
            // key값과 메모 값을 가져옴.
            memo_title = intent.getStringExtra("memo_title");
            memo_content = intent.getStringExtra("memo_content");
            image = intent.getParcelableExtra("image");
            key = intent.getStringExtra("key");
            setIntent(intent);
        } else if (null != intent && is_modify_memo == true) {
            image = intent.getParcelableExtra("image");
            memo_title = intent.getStringExtra("memo_title");
            memo_content = intent.getStringExtra("memo_content");
            key = intent.getStringExtra("key");
            position = intent.getIntExtra("position", 0);
            setIntent(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0, 0);
        if (is_add_memo == true) {
            is_add_memo = false;
            memo_adapter.addItem(new Memo_Data(key, memo_title, memo_content, image));
            memo_adapter.notifyDataSetChanged();
        } else if (is_modify_memo == true) {
            is_modify_memo = false;
            memo_adapter.memo_data.remove(position);
            memo_adapter.modifyItem(new Memo_Data(key, memo_title, memo_content, image));
            memo_adapter.notifyDataSetChanged();
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

    public void loadMemo() {

        SharedPreferences prefb = getSharedPreferences("Memo_preference", MODE_PRIVATE);
        // sharedpreference는 순서가 없는 map상태로 저장됨, 얘를 순서가 있는 treemap으로 저장형식을 바꿈.
        // keys는 데이터 셋의 집합 entry 변수는 데이터 한 세트를 의미.

        //Treemap에 역순으로 저장되게 만듦;
        TreeMap<String, Object> keys = new TreeMap<String, Object>(prefb.getAll());
//        keys.putAll();

        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            Log.i("map key", entry.getKey());
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            try {
                JSONObject jsonObject = new JSONObject(value);
                String title = jsonObject.getString("memo_title");
                String content = jsonObject.getString("memo_content");
                String image = jsonObject.getString("photo");

                myUri = Uri.parse(image);

                memo_adapter.addItem(new Memo_Data(key, title, content, myUri));
                memo_adapter.notifyDataSetChanged();

            } catch (JSONException e) {

            }

        }
        is_created = true;
    }

}






