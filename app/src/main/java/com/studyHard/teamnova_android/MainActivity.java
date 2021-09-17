package com.studyHard.teamnova_android;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.studyHard.teamnova_android.Papago.Papago;
import com.studyHard.teamnova_android.유튜브API.Youtube_Main;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import static com.studyHard.teamnova_android.Weather_TransLocalPoint.TO_GRID;
import static com.studyHard.teamnova_android.studyEndTime_modify_dialog.is_modify_studyTime;
import static com.studyHard.teamnova_android.timer_exit_ConcenRate_dialog.is_add_timer;
import static com.studyHard.teamnova_android.timer_layout.timer_start;
import static com.studyHard.teamnova_android.timer_modify_ConcenRate_dialog.is_modify_rate;
import static com.studyHard.teamnova_android.timer_stopwatch.is_start;


public class MainActivity extends AppCompatActivity {
    androidx.appcompat.widget.Toolbar Toolbar;
    private BottomNavigationView bottomNavigationView; // 바텀 네비게이션 뷰
    private FragmentManager fm;
    private FragmentTransaction ft;
    private todo todo;
    private memo memo;
    private TextView tv_outPut, totalStudy, motivation, totalStudyLabel, ratingValueLabel, timerText, timerType, ratingValue;
    private Button timer_reset;
    private ToggleButton timer_toggle;
    private long backKeyPressedTime = 0; // 마지막으로 뒤로가기 버튼 눌렀던 시간 저장
    private Toast toast; // 첫번째 뒤로가기 버튼 누를 때 표시 문구
    private String rate, key, studyTime, nowTimer, getToday;
    private int position, count;
    private Long start_time, end_time;
    private Intent intent;
    private FloatingActionButton add_timer;
    private boolean is_created = false;
    public ArrayList<Integer> timeArray;
    public ArrayList<Double> rateArray;
    RecyclerViewEmptySupport recyclerView;
    Timer_Record_Adapter timer_record_adapter;
    preferenceManager_chart prefChart;
    Handler motiveHandler, TimerHandler;
    public Weather_TransLocalPoint weatherTransLocalPoint;
    private Weather_GpsTracker weatherGpsTracker;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private JSONObject json = null;
    String formatTime;
    ImageView weatherImageView;
    ArrayList<String> weatherData = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Random rand = new Random();

        prefChart = new preferenceManager_chart();

        timeArray = new ArrayList<>();
        rateArray = new ArrayList<>();
        tv_outPut = findViewById(R.id.tv_outPut);
        ratingValue = (TextView) findViewById(R.id.ratingValue);
        totalStudy = findViewById(R.id.totalStudy);
        totalStudyLabel = findViewById(R.id.총공부시간);
        ratingValueLabel = findViewById(R.id.평균집중도);
        timerText = findViewById(R.id.timerText);
        timer_toggle = findViewById(R.id.timer_mToggle);
        timer_reset = findViewById(R.id.timer_mReset);
        timerType = (TextView) findViewById(R.id.timer_type);
        weatherImageView = findViewById(R.id.weatherImage);

        motivation = findViewById(R.id.motivation);

        recyclerView = (RecyclerViewEmptySupport) findViewById(R.id.main_recyclerview);
        recyclerView.setEmptyView(findViewById(R.id.list_empty));
        setRecyclerView();

        // GPS 체크 로직
        if (!checkLocationServicesStatus()) {

            showDialogForLocationServiceSetting();
        } else {

            checkRunTimePermission();
        }

        weatherGpsTracker = new Weather_GpsTracker(MainActivity.this);

        double latitude = weatherGpsTracker.getLatitude();
        double longitude = weatherGpsTracker.getLongitude();

        //String address = getCurrentAddress(latitude, longitude);
        //Log.d("adress","주소는? "+address);

        Log.d("gps", "위도는? " + latitude);
        Log.d("gps", "경도는? " + longitude);
        // GPS END

        // 기상청 격자 좌표 변환
        weatherTransLocalPoint = new Weather_TransLocalPoint();
        Weather_TransLocalPoint.LatXLngY tmp = weatherTransLocalPoint.convertGRID_GPS(TO_GRID, latitude, longitude);
//        Weather_TransLocalPoint.LatXLngY tmp = Weather_TransLocalPoint.convertGRID_GPS(TO_GRID, latitude, longitude);
        Log.e(">>", "x = " + tmp.x + "y = " + tmp.y);

        long mNow = System.currentTimeMillis();
        Date mReDate = new Date(mNow);
        SimpleDateFormat mFormatYDM = new SimpleDateFormat("yyyyMMdd"); // 현재 날짜 데이터 포맷 : 20210520
        String formatYDM = mFormatYDM.format(mReDate);
        SimpleDateFormat mFormatTime = new SimpleDateFormat("HH00"); // 현재 시간 포맷 : 1100 -> 11시
        int toformat = (Integer.parseInt(mFormatTime.format(mReDate)));
        formatTime = String.format("%04d", toformat); // 현재 시간을 넣었을 때 갱신된 정보가 안나오므로 1시간 이전 데이터를 조회
        Log.d("date", "현재 날짜? :" + formatYDM);
        Log.d("time", "현재 시간 대? :" + formatTime);

        if (formatTime.equals("0000") || formatTime.equals("0100")) {
            mNow = (System.currentTimeMillis() - 1000 * 3600 * 24);
            mReDate = new Date(mNow);
            formatYDM = mFormatYDM.format(mReDate);
        }

        // URL 설정.
        String service_key = "kAWVZXW6zC3cJrlzxprCIAjPa5Lwe81yhWsmdoE9qcKD4C4yAbq9yc6EVsS3CV3kEbMQwtOO9bZlOMp%2FI9WhzQ%3D%3D";
        String num_of_rows = "10";
        String page_no = "1";
        String date_type = "JSON";
        String base_date = formatYDM;
        String base_time = fn_timeChange();
        Log.e("베이스 시간",base_time);
        String nx = String.format("%.0f", tmp.x);
        String ny = String.format("%.0f", tmp.y);


        // 요청할 정보를 조합해 url 만들기!
//        "http://apis.data.go.kr/1360000/VilageFcstInfoService/getUltraSrtNcst?"
        String url = "http://apis.data.go.kr/1360000/VilageFcstInfoService/getVilageFcst?" +
                "serviceKey=" + service_key +
                "&numOfRows=" + num_of_rows +
                "&pageNo=" + page_no +
                "&dataType=" + date_type +
                "&base_date=" + base_date +
                "&base_time=" + base_time +
                "&nx=" + nx +
                "&ny=" + ny;

        // AsyncTask를 통해 HttpURLConnection 수행.
        NetworkTask networkTask = new NetworkTask(url, null);
        networkTask.execute();

        if (is_created == false) {
            loadRecord();
        }

        timerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_start) {
                    Intent intent = new Intent(getApplicationContext(), timer_stopwatch.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                } else if (timer_start) {
                    Intent intent = new Intent(getApplicationContext(), timer_layout.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                }

            }
        });

        Toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(Toolbar);

        //추가된 소스코드, Toolbar의 왼쪽에 버튼을 추가하고 버튼의 아이콘을 바꾼다.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_insert_chart_outlined_24);

        getSupportActionBar().setTitle("오늘의 공부량");  //해당 액티비티의 툴바에 있는 타이틀을 공백으로 처리

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
                        intent = new Intent(getApplicationContext(), memo.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent);
                        break;
                    case R.id.action_timer:
                        fm = getSupportFragmentManager();
                        ft = fm.beginTransaction();
                        break;
                    case R.id.action_youtube:
                        intent = new Intent(getApplicationContext(), Youtube_Main.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent);
                        break;
                    case R.id.action_papago:
                        intent = new Intent(getApplicationContext(), Papago.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent);
                }
                return true;
            }
        });

        add_timer = findViewById(R.id.floating_action_button_timer);
        add_timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer_select_dialog dialog = new timer_select_dialog();
                dialog.show(getSupportFragmentManager(), "timer_dialog");
            }
        });

        motiveHandler = new Handler();

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Resources res = getResources();
                String[] motiveList = res.getStringArray(R.array.motivation);
                int i = rand.nextInt(motiveList.length);
                motivation.setText(motiveList[i]);
            }
        };

        class MotiveRunnable implements Runnable {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(3000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(runnable);
                }
            }
        }

        MotiveRunnable mr = new MotiveRunnable();
        Thread t = new Thread(mr);
        t.start();

    }

    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public NetworkTask(String url, ContentValues values) {

            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {

            String result; // 요청 결과를 저장할 변수.
            Weather_RequestHttpConnection requestHttpURLConnection = new Weather_RequestHttpConnection();
            result = requestHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.
            Log.e("날씨",result);

            return result;
        }

        // response값에 변화가 생길 때마다 다시 갱신해준다.
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
            // String page - url의 출력물을 string으로 받는 역할을 수행함.
            // String -> json으로 바꾸고,
            Log.e("날씨",s);

            fn_JsonParsing(s);
            String weatherIcon = weatherData.get(1);

            // Glide 사용해서 이미지 출력 (load: 이미지 경로, override: 이미지 가로,세로 크기 조정, into: 이미지를 출력할 ImageView 객체)
            Glide.with(getApplicationContext()).load(weatherIcon).override(30, 30).into(weatherImageView);
            tv_outPut.setText(weatherData.get(2));
        }
    }


    void checkRunTimePermission() {

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음


        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(MainActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }

    public String fn_timeChange() {
        String hh = formatTime;
        Log.e("입력시간",hh);
        String baseTime = "";

        // 현재 시간에 따라 데이터 시간 설정(3시간 마다 업데이트) //
        switch (hh) {

            case "0200":
            case "0300":
            case "0400":
                baseTime = "0200";
                break;
            case "0500":
            case "0600":
            case "0700":
                baseTime = "0500";
                break;
            case "0800":
            case "0900":
            case "1000":
                baseTime = "0800";
                break;
            case "1100":
            case "1200":
            case "1300":
                baseTime ="1100";
                break;
            case "1400":
            case "1500":
            case "1600":
                baseTime = "1400";
                break;
            case "1700":
            case "1800":
            case "1900":
                baseTime = "1700";
                break;
            case "2000":
            case "2100":
            case "2200":
                baseTime = "2000";
                break;
            default:
                baseTime = "2300";

        }
        Log.e("시간",baseTime);
        return baseTime;
    }

    public void fn_JsonParsing(String Data) {

        JSONObject WeatherData;
        String VALUE = "";
        String date = "";
        String time = "";
        String DataValue = "";
        String info = "";

        try {
            JSONObject obj = new JSONObject(Data);
            JSONObject response = obj.getJSONObject("response");
            JSONObject body = response.getJSONObject("body");
            JSONObject items = body.getJSONObject("items");
            JSONArray item = items.getJSONArray("item");

            for (int i = 0; i < item.length(); i++) {
                WeatherData = (JSONObject) item.get(i);


                time = WeatherData.get("baseTime").toString();
                int realTime = Integer.parseInt(time);
                DataValue = WeatherData.get("fcstValue").toString();
                info = WeatherData.get("category").toString();

                if (info.equals("SKY") || info.equals("PTY") || info.equals("T3H")) {
                    if (700 < realTime && realTime < 1900) {
                        if (info.equals("SKY")) {
                            info = "하늘상태";
                            if (DataValue.equals("1")) {
                                DataValue = "https://www.weatherbit.io/static/img/icons/c01d.png";
                            } else if (DataValue.equals("2")) {
                                DataValue = "https://www.weatherbit.io/static/img/icons/r01n.png";
                            } else if (DataValue.equals("3")) {
                                DataValue = "https://www.weatherbit.io/static/img/icons/c02d.png";
                            } else if (DataValue.equals("4")) {
                                DataValue = "https://www.weatherbit.io/static/img/icons/c04d.png";
                            }
                        }
                    } else {
                        if (info.equals("SKY")) {
                            info = "하늘상태";
                            if (DataValue.equals("1")) {
                                DataValue = "https://www.weatherbit.io/static/img/icons/c01n.png";
                            } else if (DataValue.equals("2")) {
                                DataValue = "https://www.weatherbit.io/static/img/icons/r01n.png";
                            } else if (DataValue.equals("3")) {
                                DataValue = "https://www.weatherbit.io/static/img/icons/c02n.png";
                            } else if (DataValue.equals("4")) {
                                DataValue = "https://www.weatherbit.io/static/img/icons/c04d.png";
                            }
                        }

                    }

                    if (info.equals("T3H")) {
                        info = "기온";
                        DataValue = DataValue + " ℃";
                    }
                    Log.e("날씨",DataValue);
                    weatherData.add(DataValue);
                }

            }


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    public String getCurrentAddress(double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString() + "\n";

    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    //레이아웃 및 어뎁터 결정.
    public void setRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        timer_record_adapter = new Timer_Record_Adapter(this, R.layout.timer_record_list_item);
        recyclerView.setAdapter(timer_record_adapter);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        //return super.onCreateOptionsMenu(menu);
//        MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.menu, menu);
//        return true;
//    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
//            case R.id.action_settings:
//                // User chose the "Settings" item, show the app settings UI...
//                startActivity(new Intent(this, SettingActivity.class));
//                return true;

            case android.R.id.home: //로그인 버튼 클릭시,

                Intent intent = new Intent(this, chart2.class);
                startActivity(intent);

                return true;
        }
        return super.onOptionsItemSelected(item);

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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (null != intent && is_add_timer == true) {
            key = intent.getStringExtra("key");
            rate = intent.getStringExtra("rating");
            start_time = intent.getLongExtra("Start_time", 0);
            end_time = intent.getLongExtra("End_time", 0);
            studyTime = intent.getStringExtra("studyTime");
            setIntent(intent);
        } else if (null != intent && is_modify_rate == true) {
            rate = intent.getStringExtra("rating");
            position = intent.getIntExtra("position", 0);
            studyTime = intent.getStringExtra("studyTime");
            setIntent(intent);
        } else if (null != intent && is_modify_studyTime == true) {
            position = intent.getIntExtra("position", 0);
            start_time = intent.getLongExtra("startTime", 0);
            end_time = intent.getLongExtra("endTime", 0);
            studyTime = intent.getStringExtra("studyTime");
            setIntent(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0, 0);

        TimerHandler = new Handler();

        if (is_start || timer_start) {
            TimerHandler.post(timerRunnable);
            add_timer.hide();
            motivation.setVisibility(View.VISIBLE);
            ratingValue.setVisibility(View.GONE);
            totalStudy.setVisibility(View.GONE);
            totalStudyLabel.setVisibility(View.GONE);
            ratingValueLabel.setVisibility(View.GONE);
            timerText.setVisibility(View.VISIBLE);
            timer_toggle.setVisibility(View.GONE);
            timer_reset.setVisibility(View.GONE);
            timerType.setVisibility(View.VISIBLE);
            if (is_start) {
                timerType.setText("스탑워치 실행 중");
            } else {
                timerType.setText("타이머 실행 중");
            }

        } else {
            add_timer.show();
            TimerHandler.removeCallbacks(timerRunnable);
            motivation.setVisibility(View.VISIBLE);
            ratingValue.setVisibility(View.VISIBLE);
            totalStudy.setVisibility(View.VISIBLE);
            totalStudyLabel.setVisibility(View.VISIBLE);
            ratingValueLabel.setVisibility(View.VISIBLE);
            timerText.setVisibility(View.GONE);
            timer_toggle.setVisibility(View.GONE);
            timer_reset.setVisibility(View.GONE);
            timerType.setVisibility(View.GONE);
        }

        if (is_add_timer == true) {
            is_add_timer = false;
            timer_record_adapter.addItem(new Timer_Data(key, start_time, end_time, studyTime, rate));
            timer_record_adapter.notifyDataSetChanged();

        } else if (is_modify_rate == true) {
            is_modify_rate = false;
            long start_time = timer_record_adapter.timer_record_data.get(position).getStart_time();
            long end_time = timer_record_adapter.timer_record_data.get(position).getEnd_time();

            timer_record_adapter.modifyItem(position, new Timer_Data(key, start_time, end_time, studyTime, rate));
            timer_record_adapter.notifyDataSetChanged();

        } else if (is_modify_studyTime == true) {
            is_modify_studyTime = false;
            String rate = timer_record_adapter.timer_record_data.get(position).getRate();
            timer_record_adapter.modifyItem(position, new Timer_Data(key, start_time, end_time, studyTime, rate));
        }

        getDay_Time_Rate();
    }

    public void loadRecord() {
        nowTime();

        SharedPreferences prefb = getSharedPreferences("Time_Record", MODE_PRIVATE);
        // sharedpreference는 순서가 없는 map상태로 저장됨, 얘를 순서가 있는 treemap으로 저장형식을 바꿈.
        // keys는 데이터 셋의 집합 entry 변수는 데이터 한 세트를 의미.
        TreeMap<String, ?> keys = new TreeMap<String, Object>(prefb.getAll());
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            Log.i("map key", entry.getKey());
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            //오늘 것만 recyclerview에 보여줄거야.
            if (key.substring(0, 11).equals(getToday)) {
                try {
                    JSONObject jsonObject = new JSONObject(value);
                    String startTime = jsonObject.getString("startTime");
                    String endTime = jsonObject.getString("endTime");
                    String rate = jsonObject.getString("rate");
                    String studyTime = jsonObject.getString("studyTime");
                    long start_time = Long.parseLong(startTime);
                    long end_time = Long.parseLong(endTime);

                    // 저장된 값을 onCreate시점에 recyclerview에 뿌리기.

                    timer_record_adapter.addItem(new Timer_Data(key, start_time, end_time, studyTime, rate));
                } catch (JSONException e) {

                }
                timer_record_adapter.notifyDataSetChanged();
            } else {
                // preference에서 지우기?

            }
        }
        is_created = true;
    }

    // 일일 공부량 및 평균 집중도 산출 메소드
    public void getDay_Time_Rate() {
        int hour, minute, second;
        int itemStudyTime;
        int dayStudyTime = 0;
        rateArray.clear();
        timeArray.clear();

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");
        String key = simpleDate.format(date);

        // 오늘의 공부기록을 다 더해서 ㅇㅇ
        for (int i = 0; i < timer_record_adapter.timer_record_data.size(); i++) {
            String sum_studyTime = timer_record_adapter.timer_record_data.get(i).getStudyTime();
            String[] array = sum_studyTime.split(":");

            hour = Integer.parseInt(array[0]) * (60 * 60);
            minute = Integer.parseInt(array[1]) * (60);
            second = Integer.parseInt(array[2]);
            itemStudyTime = hour + minute + second;
            timeArray.add(itemStudyTime);
        }
        for (int i = 0; i < timeArray.size(); i++) {
            dayStudyTime += timeArray.get(i);
        }

        int dayHour = (int) dayStudyTime / (60 * 60);
        int dayMinute = (int) dayStudyTime / (60) - dayHour * 60;
        studyTime = String.format(Locale.getDefault(), "%dh %dm", dayHour, dayMinute);

        double rateDouble1 = 0;
        double rateDouble2 = 0;
        for (int i = 0; i < timer_record_adapter.timer_record_data.size(); i++) {
            String rate = timer_record_adapter.timer_record_data.get(i).getRate();
            rateDouble1 = Double.parseDouble(rate);
            rateArray.add(rateDouble1);
        }
        for (int i = 0; i < rateArray.size(); i++) {
            rateDouble2 += rateArray.get(i);
        }
        double rateAverage = rateDouble2 / rateArray.size();
        if (Double.isNaN(rateAverage)) {
            rateAverage = 0.0;
        }
        String getRate = String.format("%.1f", rateAverage);

        String save_chart = "{\"total_time\" : \"" + dayStudyTime + "\", \"rate_average\":\"" + getRate + "\"}";

        //pref에 저장
        prefChart.setString(this, key, save_chart);

        ratingValue.setText(getRate);
        totalStudy.setText(studyTime);
    }

    public Runnable timerRunnable = new Runnable() {

        public void run() {

            if (is_start) {
                nowTimer = "SomeThing~";
                String nowStopwatch = timer_stopwatch.stopwatchText.getText().toString();
                timerText.setText(nowStopwatch);

            } else if (timer_start) {
                nowTimer = timer_layout.timeText.getText().toString();
                timerText.setText(nowTimer);
            }

            // 타이머 종료시, dialog 발생.
            if (nowTimer.equals("00:00")) {
                TimerHandler.removeCallbacks(timerRunnable);
                is_start = false;
                timer_start = false;

                timer_exit_ConcenRate_dialog concenDialog = new timer_exit_ConcenRate_dialog();
                Bundle bundle = new Bundle();
                bundle.putLong("Start_time", timer_layout.start_time);
                bundle.putLong("End_time", timer_layout.end_time);
                bundle.putString("watchTime", timer_layout.Result_Time);
                concenDialog.setArguments(bundle);

                concenDialog.setCancelable(false);
                try {
                    concenDialog.show(getSupportFragmentManager(), "timer_exit_dialog");
                } catch (Exception e) {
                }
            } else {
                TimerHandler.postDelayed(timerRunnable, 1000);
            }
        }
    };

    public void nowTime() {
        // key값은 고유한 값, 시간으로 부여
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy--MM-dd HH:mm:ss");
        getToday = simpleDate.format(mDate).substring(0, 11);
    }
}
