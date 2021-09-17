package com.studyHard.teamnova_android;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class chart extends AppCompatActivity {
    public ArrayList<Float> timeArray;
    public ArrayList<Float> rateArray;
    private TextView weekStudy, weekRate;
    Toolbar Toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart);

        weekStudy = findViewById(R.id.totalStudy);
        weekRate = findViewById(R.id.ratingValue);

        timeArray = new ArrayList<>();
        rateArray = new ArrayList<>();

        Toolbar = (Toolbar) findViewById(R.id.chart_toolbar);
        setSupportActionBar(Toolbar);

        //추가된 소스코드, Toolbar의 왼쪽에 버튼을 추가하고 버튼의 아이콘을 바꾼다.
        getSupportActionBar().setTitle("통계");  //해당 액티비티의 툴바에 있는 타이틀을 공백으로 처리

        loadRecord();

        BarChart chart = (BarChart) findViewById(R.id.barchart);

        List<BarEntry> entries = new ArrayList<BarEntry>();
        entries.add(new BarEntry(0f, 0f));
        entries.add(new BarEntry(1f, rateArray.get(0)));
        entries.add(new BarEntry(2f, 0f));
        entries.add(new BarEntry(3f, 0f));
        entries.add(new BarEntry(4f, 0f));
        entries.add(new BarEntry(5f, 0f));
        entries.add(new BarEntry(6f, 0f));

        final ArrayList<String> xAxisLabel = new ArrayList<>();
        xAxisLabel.add("Mon");
        xAxisLabel.add("Tue");
        xAxisLabel.add("Wed");
        xAxisLabel.add("Thu");
        xAxisLabel.add("Fri");
        xAxisLabel.add("Sat");
        xAxisLabel.add("Sun");


        // 생성한 데이터를 데이터 셋에 저장.
        BarDataSet dataSet = new BarDataSet(entries, "Numbers");
        BarData data = new BarData(dataSet);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return xAxisLabel.get((int) value);
            }
        });

        YAxis YAxis = chart.getAxisLeft();
        YAxis.setAxisMinimum (0f) ;
        YAxis.setAxisMaximum (5f);
        YAxis.setGranularity(1f);
        YAxis.setLabelCount(5);

        YAxis YAxisR = chart.getAxisRight();
        YAxisR.setAxisMinimum (0f) ;
        YAxisR.setAxisMaximum (20f);
        YAxisR.setGranularity(4f);
        YAxisR.setLabelCount(5);

        //차트에 데이터를 뿌림.
        chart.setData(data);
        chart.invalidate();

        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(false);
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);
    }

    public void loadRecord() {
        SharedPreferences prefb = getSharedPreferences("chart_preference", MODE_PRIVATE);
        // sharedpreference는 순서가 없는 map상태로 저장됨, 얘를 순서가 있는 treemap으로 저장형식을 바꿈.
        // keys는 데이터 셋의 집합 entry 변수는 데이터 한 세트를 의미.
        TreeMap<String, ?> keys = new TreeMap<String, Object>(prefb.getAll());
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            Log.i("map key", entry.getKey());
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();

//            String save_chart = "{\"total_time\" : \""+timeDiffer2+"\", \"rate_average\":\""+rateAverage+"\"}";

            try {
                JSONObject jsonObject = new JSONObject(value);
                String total_time = jsonObject.getString("total_time");
                String rate_average = jsonObject.getString("rate_average");
                // 저장된 값을 onCreate시점에 recyclerview에 뿌리기.

                float time1 = Float.parseFloat(total_time);
                float hour = (float) time1 / (1000 * 60 * 60);
                String Time = String.format("%.1f", hour);
                float getTime = Float.parseFloat(Time);

                float rate = Float.parseFloat(rate_average);

                timeArray.add(getTime);
                rateArray.add(rate);

            } catch (JSONException e) {

            }

        }

        float weekStudyTime = 0;
        for (int i = 0; i < timeArray.size(); i++){
            weekStudyTime += timeArray.get(i);
        }
        long aValue = (long) weekStudyTime;
        int hour = (int) aValue / (1000 * 60 * 60);
        int minute = (int) aValue / (1000 * 60) - hour * 60;
        String weekTime = String.format(Locale.getDefault(), "%dh %dm", hour,minute);

        float weekRateAverage = 0;
        for (int i = 0; i < rateArray.size(); i++){
            weekRateAverage += rateArray.get(i);
        }
        float rateAverage = weekRateAverage/rateArray.size();

        weekStudy.setText(weekTime);
        weekRate.setText( String.valueOf(rateAverage));

    }

}
