package com.studyHard.teamnova_android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Locale;

public class timer_layout extends AppCompatActivity {
    static long start_time;
    static long end_time;

    static TextView timeText;
    Button timer_reset;
    static ToggleButton timer_toggle;
    preferenceManager_TimeRecord pref;
    Toolbar timer_toolbar;

    int hour, minute, second;

    String timerSet;
    String end_timerSet;
    static String Result_Time;

    Handler timerHandler;

    Vibrator vibrator;

    static boolean timer_start = false, is_timerCompleted = false, is_timer_state = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer_layout);

        timer_toolbar = (Toolbar) findViewById(R.id.timer_toolbar);
        setSupportActionBar(timer_toolbar);

        //추가된 소스코드, Toolbar의 왼쪽에 버튼을 추가하고 버튼의 아이콘을 바꾼다.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_home_24);

        getSupportActionBar().setTitle("타이머");  //해당 액티비티의 툴바에 있는 타이틀을 공백으로 처리

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        pref = new preferenceManager_TimeRecord();
        timerHandler = new Handler();

        // 타이머 실행 시간 구하기.
        start_time = System.currentTimeMillis();

        timeText = (TextView) findViewById(R.id.text_view_stopwatch);
        timer_toggle = findViewById(R.id.toggle_Timer);
        timer_reset = findViewById(R.id.timer_reset);

        timer_reset.setVisibility(View.GONE);

        Intent intent = new Intent(this.getIntent());

        // 00:00:00 꼴로 전송.
        String timeSet = intent.getStringExtra("timeSet");

        String[] array = timeSet.split(":");

        hour = Integer.parseInt(array[0]);
        minute = Integer.parseInt(array[1]);
        second = Integer.parseInt(array[2]);

        if(hour == 0) {
            timerSet = String.format(Locale.getDefault(), "%02d:%02d",minute, second);
        } else {
            timerSet = String.format(Locale.getDefault(), "%02d:%02d:%02d", hour, minute, second);
        }
        end_timerSet = timerSet;


        timeText.setText(timerSet);

        timer_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(timer_toggle.isChecked()) {
                    if(timer_start == false) {
                        timer_start = true;
                        timer_toggle.setTextOff("다시 시작");
                    }

                    // 핸들러가 변화하는 시간값을 UI 에 그려주는 역할을 수행함.
                    timerHandler.postDelayed(runnable, 1000);

                    timer_reset.setVisibility(View.GONE);

//                    stopwatch_reset.setEnabled(false);

                } else {

                    // 스탑워치 일시정지 버튼 눌렀을 때의 총 시간

                    timerHandler.removeCallbacks(runnable);

                    timer_reset.setVisibility(View.VISIBLE);

                    // 일시정지 버튼 클릭 시 리셋 버튼을 활성화 시켜준다.
//                    stopwatch_reset.setEnabled(true);
                }

            }
        });

        timer_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer_start = false;

                timer_toggle.setTextOff("시작");

                start_time = System.currentTimeMillis();

                end_timerSet = null;

                timerHandler.removeCallbacks(runnable);

                hour = Integer.parseInt(array[0]);
                minute = Integer.parseInt(array[1]);
                second = Integer.parseInt(array[2]);

                if(timerSet.length()==5) {
                    timerSet = String.format(Locale.getDefault(), "%02d:%02d",minute, second);
                } else {
                    timerSet = String.format(Locale.getDefault(), "%02d:%02d:%02d", hour, minute, second);
                }
                timeText.setText(timerSet);
            }
        });

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        MenuInflater menuInflater = getMenuInflater();
//
//        menuInflater.inflate(R.menu.timer_menu, menu);
//
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.timer_settings:
//                // User chose the "Settings" item, show the app settings UI...
//                startActivity(new Intent(this, SettingActivity.class));
//                return true;

            case android.R.id.home: //홈 버튼을 누를 경우,
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // 시간값을 갱신하는 스레드
    public Runnable runnable = new Runnable() {

        public void run() {

            if (second != 0) {
                second--;

            } else if (minute != 0) {
                second = 60;
                second--;
                minute--;
            } else if (hour != 0) {
                second = 60;
                minute = 60;
                second--;
                minute--;
                hour--;
            }

            if(hour == 0) {
                end_timerSet = String.format(Locale.getDefault(), "%02d:%02d", minute, second);
            } else {
                end_timerSet = String.format(Locale.getDefault(), "%02d:%02d:%02d", hour, minute, second);
            }

            timeText.setText(end_timerSet);

            // 타이머 종료시, dialog 발생.
            if(end_timerSet.equals("00:00")) {

                end_time = System.currentTimeMillis();
                timerHandler.removeCallbacks(runnable);
                vibrator.vibrate(500);
                is_timerCompleted = true;
                getStudyTime ();
                timer_exit_ConcenRate_dialog concenDialog = new timer_exit_ConcenRate_dialog();

                Bundle bundle = new Bundle();
                bundle.putLong("Start_time", start_time);
                bundle.putLong("End_time", end_time);
                bundle.putString("watchTime", Result_Time);
                concenDialog.setArguments(bundle);

                concenDialog.setCancelable(false);
                try {
                    concenDialog.show(getSupportFragmentManager(), "timer_exit_dialog");
                } catch (Exception e) {
                    //Exception is ignored.
                }
            } else {
                timerHandler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    public void onBackPressed() {
        timer_toggle.setChecked(false);
        timerHandler.removeCallbacks(runnable);

        if (!timer_start) {
            Intent intent = new Intent(this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        } else {
            getStudyTime ();
            timer_exit_dialog dialog = new timer_exit_dialog();

            Bundle bundle = new Bundle();
            bundle.putLong("Start_time", start_time);
            bundle.putString("watchTime", Result_Time);
            dialog.setArguments(bundle);

            dialog.show(getSupportFragmentManager(), "timer_exit_dialog");
        }

    }


    public void getStudyTime () {

        String[] init_array = timerSet.split(":");
        String[] end_array = end_timerSet.split(":");

        if(end_timerSet.length() == 5) {
            if(timerSet.length() == 5) {
                int init_minute = Integer.parseInt(init_array[0]) * 60;
                int init_second = Integer.parseInt(init_array[1]);

                int end_minute = Integer.parseInt(end_array[0])* 60;
                int end_second = Integer.parseInt(end_array[1]);

                int StudyTime = (init_minute+init_second)-(end_minute+end_second);
                int resultMinute = StudyTime / 60;
                int resultSecond = StudyTime % 60;

                Result_Time = String.format("00:%02d:%02d",resultMinute,resultSecond);
            } else {
                int init_hour = Integer.parseInt(init_array[0]) * 3600;
                int init_minute = Integer.parseInt(init_array[1]) * 60;
                int init_second = Integer.parseInt(init_array[2]);

                int end_minute = Integer.parseInt(end_array[0])* 60;
                int end_second = Integer.parseInt(end_array[1]);

                int StudyTime = (init_hour+init_minute+init_second)-(end_minute+end_second);
                int resultHour = StudyTime / 3600;
                int resultMinute = (StudyTime - resultHour*3600) / 60;
                int resultSecond = StudyTime % 60;

                Result_Time = String.format("%02d:%02d:%02d",resultHour, resultMinute,resultSecond);
            }
        } else {
            int init_hour = Integer.parseInt(init_array[0]) * 3600;
            int init_minute = Integer.parseInt(init_array[1]) * 60;
            int init_second = Integer.parseInt(init_array[2]);

            int end_hour = Integer.parseInt(end_array[0])* 3600;
            int end_minute = Integer.parseInt(end_array[1])* 60;
            int end_second = Integer.parseInt(end_array[2]);

            int StudyTime = (init_hour+init_minute+init_second)-(end_hour+end_minute+end_second);
            int resultHour = StudyTime / 3600;
            int resultMinute = (StudyTime - resultHour*3600) / 60;
            int resultSecond = StudyTime % 60;

            Result_Time = String.format("%02d:%02d:%02d",resultHour, resultMinute,resultSecond);
        }
    }
}
