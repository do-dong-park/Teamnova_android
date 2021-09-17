package com.studyHard.teamnova_android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class timer_stopwatch extends AppCompatActivity {

    static TextView stopwatchText;
    static Button stopwatch_reset;
    static ToggleButton stopWatch_toggle;
    Toolbar stopwatch_toolbar;
    public static Context context_stopwatch;

    String Time, setTime;

    long start_time;
    long MillisecondTime = 0L;  // 스탑워치 시작 버튼을 누르고 흐른 시간
    long StartTime = 0L;        // 스탑워치 시작 버튼 누르고 난 이후 부터의 시간
    long TimeBuff = 0L;         // 스탑워치 일시정지 버튼 눌렀을 때의 총 시간
    long UpdateTime = 0L;       // 스탑워치 일시정지 버튼 눌렀을 때의 총 시간 + 시작 버튼 누르고 난 이후 부터의 시간 = 총 시간

    public static boolean is_start, is_stopwatch_state, is_reset = false;

    int Hour, Seconds, Minutes;

    public Handler stopwatchHandler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer_stopwatch_layout);

        context_stopwatch = this;

        stopwatch_toolbar = (Toolbar) findViewById(R.id.timer_toolbar);
        setSupportActionBar(stopwatch_toolbar);

        //추가된 소스코드, Toolbar의 왼쪽에 버튼을 추가하고 버튼의 아이콘을 바꾼다.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_home_24);

        getSupportActionBar().setTitle("스탑워치");  //해당 액티비티의 툴바에 있는 타이틀을 공백으로 처리

        start_time = System.currentTimeMillis();

        stopwatchText = findViewById(R.id.text_view_stopwatch);
        stopWatch_toggle = findViewById(R.id.toggle_Timer);
        stopwatch_reset = findViewById(R.id.reset);

        stopwatchHandler = new Handler();

        stopwatch_reset.setVisibility(View.GONE);

        stopWatch_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stopWatch_toggle.isChecked()) {
                    // 타이머 작동 시작.
                    if(is_start == false) {
                        is_start = true;
                        is_stopwatch_state = true;
                        stopWatch_toggle.setTextOff("다시 시작");
                    }

                    StartTime = SystemClock.uptimeMillis();
                    // 핸들러가 변화하는 시간값을 UI 에 그려주는 역할을 수행함.
                    stopwatchHandler.postDelayed(runnable, 1000);

                    stopwatch_reset.setVisibility(View.GONE);

                } else {
                    is_stopwatch_state = false;
                    stopwatch_reset.setVisibility(View.VISIBLE);
                    stopwatch_stop ();
                }
            }
        });

        stopwatch_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                is_start = false;

                start_time = System.currentTimeMillis();

                Time = null;

                // 측정 시간을 모두 0으로 리셋시켜준다.
                MillisecondTime = 0L ;
                StartTime = 0L ;
                TimeBuff = 0L ;
                UpdateTime = 0L ;
                Seconds = 0 ;
                Minutes = 0 ;
                Hour = 0;

                // 초를 나타내는 TextView를 0초로 갱신시켜준다.
                stopwatchText.setText("00:00");
            }
        });

    }

    public  void stopwatch_stop () {
        // 스탑워치 일시정지 버튼 눌렀을 때의 총 시간
        TimeBuff += MillisecondTime;

        stopwatch_reset.setVisibility(View.VISIBLE);

        // Runnable 객체 제거
        stopwatchHandler.removeCallbacks(runnable);

        // 일시정지 버튼 클릭 시 리셋 버튼을 활성화 시켜준다.
        stopwatch_reset.setEnabled(true);
    }

    // 시간값을 갱신하는 스레드
    public Runnable runnable = new Runnable() {

        public void run() {

            //시작 버튼 누른 이후 총 흐른 시간.
            MillisecondTime = SystemClock.uptimeMillis() - StartTime;

            // 여지껏 흘렀던 시간 + 시작버튼 눌렀을 때부터 흐른시간. = 총 시간.
            UpdateTime = TimeBuff + MillisecondTime;

            Seconds = (int) (UpdateTime / 1000);

            Hour = Minutes / 60;

            Minutes = Seconds / 60;

            Seconds = Seconds % 60;



            // TextView에 UpdateTime을 갱신해준다

            if(Hour == 0){
                Time = String.format("%02d:%02d",Minutes,Seconds);
                setTime = String.format("00:%02d:%02d",Minutes,Seconds);
            }else{
                Time = String.format("%02d:%02d:%02d",Hour, Minutes,Seconds);
                setTime = Time;

            }

            stopwatchText.setText(Time);

            stopwatchHandler.postDelayed(this, 1000);
        }

    };

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



    @Override
    public void onBackPressed() {
        stopWatch_toggle.setChecked(false);
        stopwatchHandler.removeCallbacks(runnable);

        if(Time == null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        } else {
//            stopWatch_toggle.setChecked(false);
//            stopwatch_stop ();
            timer_exit_dialog dialog = new timer_exit_dialog();

            Bundle bundle = new Bundle();
            bundle.putLong("Start_time", start_time);
            bundle.putString("watchTime", setTime);

            dialog.setArguments(bundle);

            dialog.show(getSupportFragmentManager(), "timer_exit_dialog");
        }
    }
}
