package com.studyHard.teamnova_android;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class chart2 extends AppCompatActivity {
    public ArrayList<Integer> daySumTimeArray;
    public ArrayList<Float> timeArray;
    public ArrayList<Float> rateArray;
    public ArrayList<String> timeLabelArray;
    private TextView weekStudy, weekRate;
    Toolbar Toolbar;
    private CombinedChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart2);

        weekStudy = findViewById(R.id.totalStudy);
        weekRate = findViewById(R.id.ratingValue);
        chart = findViewById(R.id.combinedChart);

        daySumTimeArray = new ArrayList<>();
        timeArray = new ArrayList<>();
        rateArray = new ArrayList<>();
        timeLabelArray = new ArrayList<>();

        Toolbar = (Toolbar) findViewById(R.id.chart_toolbar);
        setSupportActionBar(Toolbar);

        //추가된 소스코드, Toolbar의 왼쪽에 버튼을 추가하고 버튼의 아이콘을 바꾼다.
        getSupportActionBar().setTitle("통계");  //해당 액티비티의 툴바에 있는 타이틀을 공백으로 처리

        loadRecord();

        chart.setDrawOrder(new CombinedChart.DrawOrder[] {CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE});

        Legend l = chart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        final ArrayList<String> xAxisLabel = new ArrayList<>();

        if(timeLabelArray.size() < 10){
            for (int i = 0; i < timeLabelArray.size(); i++){
                xAxisLabel.add(timeLabelArray.get(i));
            }
        }else{
            for (int i = timeLabelArray.size() - 10 ; i < timeLabelArray.size(); i++){
                xAxisLabel.add(timeLabelArray.get(i));
            }
        }

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


        CombinedData data = new CombinedData();

        data.setData(generateLineData());
        data.setData(generateBarData());

        chart.setData(data);
        chart.invalidate();
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(false);
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);



    }



    private BarData generateBarData () {
        List<BarEntry> entries = new ArrayList<BarEntry>();

        if(rateArray.size() < 10){
            for (int i = 0; i < rateArray.size(); i++){
                entries.add(new BarEntry(i,rateArray.get(i)));
            }
        }else{
            for (int i = rateArray.size() - 10 ; i < rateArray.size(); i++){
                entries.add(new BarEntry(i,rateArray.get(i)));
            }
        }


        // 생성한 데이터를 데이터 셋에 저장.

        BarDataSet dataSet = new BarDataSet(entries, "집중도");
        dataSet.setColor(Color.rgb(142, 150, 175));
        dataSet.setValueTextColor(Color.rgb(142, 150, 175));
        dataSet.setValueTextSize(10f);
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        BarData data = new BarData(dataSet);

        return data;
    }

    private LineData generateLineData() {

        ArrayList<Entry> entries = new ArrayList<>();

        if(timeArray.size() < 10){
            for (int i = 0; i < timeArray.size(); i++){
                entries.add(new BarEntry(i,timeArray.get(i)));
            }
        }else{
            for (int i =timeArray.size() - 10 ; i < timeArray.size(); i++){
                entries.add(new BarEntry(i,timeArray.get(i)));
            }
        }


        LineDataSet set = new LineDataSet(entries, "공부시간");
        set.setColor(Color.rgb(60, 220, 78));
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.rgb(60, 220, 78));
        set.setCircleRadius(5f);
        set.setFillColor(Color.rgb(60, 220, 78));
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.rgb(60, 220, 78));

        set.setAxisDependency(YAxis.AxisDependency.RIGHT);
        LineData d = new LineData();
        d.addDataSet(set);

        return d;
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

            try {
                JSONObject jsonObject = new JSONObject(value);
                String total_time = jsonObject.getString("total_time");
                String rate_average = jsonObject.getString("rate_average");

                // 저장된 값을 onCreate시점에 recyclerview에 뿌리기.

                float time1 = Float.parseFloat(total_time);
                float hour = (float) time1 / (60 * 60);
                String Time = String.format("%.1f", hour);
                float getTime = Float.parseFloat(Time);

                float rate = Float.parseFloat(rate_average);

                daySumTimeArray.add(Integer.parseInt(total_time));
                timeArray.add(getTime);
                rateArray.add(rate);

                String subKey = key.substring(5,10);
                timeLabelArray.add(subKey);

            } catch (JSONException e) {

            }

        }

        int dayStudyTime = 0;
        for (int i = 0; i < daySumTimeArray.size(); i++){
            dayStudyTime += daySumTimeArray.get(i);
        }

        int aValue = (int) dayStudyTime;
        int hour = (int) aValue / (60 * 60);
        int minute = (int) aValue / (60) - hour * 60;
        String weekTime = String.format(Locale.getDefault(), "%dh %dm", hour,minute);

        float weekRateAverage = 0;
        for (int i = 0; i < rateArray.size(); i++){
            weekRateAverage += rateArray.get(i);
        }
        float rateAverage = weekRateAverage/rateArray.size();

        String stRateAverage = String.format("%.1f", rateAverage);

        weekStudy.setText(weekTime);
        weekRate.setText( String.valueOf(stRateAverage));
    }



}
