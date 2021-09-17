package com.studyHard.teamnova_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import static com.studyHard.teamnova_android.timer_set_dialog.add_timerSet;
import static com.studyHard.teamnova_android.timer_set_modify_dialog.modify_timerSet;

public class timer_preset extends AppCompatActivity {
    androidx.appcompat.widget.Toolbar Toolbar;
    public static final String TAG = "TimerSet_Activity";

    private ArrayList<timer_preset_data> item = new ArrayList<>();
    private timer_preset_adapter itemAdapter;
    private RecyclerViewEmptySupport recyclerView;
    private int position;
    private String title,timeSet, key;
    private boolean is_created = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer_grid_layout);

        Toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar_timer_grid);
        setSupportActionBar(Toolbar);
        getSupportActionBar().setTitle("타이머");  //해당 액티비티의 툴바에 있는 타이틀을 공백으로 처리

        recyclerView = findViewById(R.id.timer_preset_recyclerview);
        recyclerView.setEmptyView(findViewById(R.id.list_empty));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        itemAdapter = new timer_preset_adapter(this, item);

        recyclerView.setAdapter(itemAdapter);

        if (is_created == false) {
            loadTimerSet();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater =  getMenuInflater();
        menuInflater.inflate(R.menu.timer_preset_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_timer:
                Log.d(TAG, "onclick : opening timerSet dialog");

                timer_set_dialog dialog = new timer_set_dialog();
                dialog.show(getSupportFragmentManager(), "timer_set_dialog");
        }
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(null != intent && add_timerSet == true) {
            key = intent.getStringExtra("key");
            title = intent.getStringExtra("title");
            timeSet = intent.getStringExtra("timeSet");
            setIntent(intent);
        } else if (null != intent && modify_timerSet == true) {
            key = intent.getStringExtra("key");
            position = intent.getIntExtra("position",0);
            timeSet = intent.getStringExtra("timeSet");
            title = intent.getStringExtra("title");
            setIntent(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(add_timerSet == true) {
            add_timerSet = false;
            Log.d(TAG, "onclick : 아이템 추가하기");
            itemAdapter.addItem(new timer_preset_data (key, title, timeSet));
            itemAdapter.notifyDataSetChanged();
        } else if (modify_timerSet == true) {
            modify_timerSet = false;
            Log.d(TAG, "onclick : 아이템 수정하기");
            itemAdapter.modifyItem(position, new timer_preset_data (key, title, timeSet));
            itemAdapter.notifyDataSetChanged();
        }
    }

    public void loadTimerSet() {

        SharedPreferences prefb = getSharedPreferences("timerSet_info", MODE_PRIVATE);
        // sharedpreference는 순서가 없는 map상태로 저장됨, 얘를 순서가 있는 treemap으로 저장형식을 바꿈.
        // keys는 데이터 셋의 집합 entry 변수는 데이터 한 세트를 의미.
        TreeMap<String, ?> keys = new TreeMap<String, Object>(prefb.getAll());
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            Log.i("map key", entry.getKey());
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();

            try {
                JSONObject jsonObject = new JSONObject(value);
                String title = jsonObject.getString("title");
                String timeSet = jsonObject.getString("timeSet");
                // 저장된 값을 onCreate시점에 recyclerview에 뿌리기.
                itemAdapter.addItem(new timer_preset_data(key, title, timeSet));
            } catch (JSONException e) {

            }
            itemAdapter.notifyDataSetChanged();
        }
        is_created = true;
    }
}

