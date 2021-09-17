package com.studyHard.teamnova_android;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class SettingActivity extends AppCompatActivity {
    private SettingAdapter settingAdapter;
    private ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);

        listView = findViewById(R.id.settingListView);
        settingAdapter = new SettingAdapter();

        // 아이템 추가 + 리스트 뷰에 지정
        settingAdapter.addItem(ContextCompat.getDrawable(this,R.drawable.ic_baseline_add_24),"아이콘1번", "24dp Black");
        settingAdapter.addItem(ContextCompat.getDrawable(this,R.drawable.ic_baseline_calendar_today_24),"아이콘2번", "24dp Black");
        settingAdapter.addItem(ContextCompat.getDrawable(this,R.drawable.ic_baseline_change_circle_24),"아이콘3번", "24dp Black");

        listView.setAdapter(settingAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(SettingActivity.this, settingAdapter.getItem(position).getTitle(), Toast.LENGTH_SHORT).show();
            }
        });


    }
}
