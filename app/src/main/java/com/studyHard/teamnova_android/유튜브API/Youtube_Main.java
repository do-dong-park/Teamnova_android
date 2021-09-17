package com.studyHard.teamnova_android.유튜브API;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.studyHard.teamnova_android.MainActivity;
import com.studyHard.teamnova_android.Papago.Papago;
import com.studyHard.teamnova_android.R;
import com.studyHard.teamnova_android.RecyclerViewEmptySupport;
import com.studyHard.teamnova_android.memo;
import com.studyHard.teamnova_android.todo;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Youtube_Main extends AppCompatActivity {

    androidx.appcompat.widget.Toolbar Toolbar;
    private BottomNavigationView bottomNavigationView; // 바텀 네비게이션 뷰
    private long backKeyPressedTime = 0; // 마지막으로 뒤로가기 버튼 눌렀던 시간 저장
    private Toast toast; // 첫번째 뒤로가기 버튼 누를 때 표시 문구
    RecyclerViewEmptySupport recyclerView;
    private Intent intent;
    public Youtube_Adapter youtube_adapter;
    Youtube_API youtube_api;
    AsyncTask<?, ?, ?> searchTask;

    final String serverKey = "AIzaSyDuNPlJeVW8gt0EGieAEoCH3ZnAQ6mhuos";
    String vodid, change_chTitle, changString, viewCount, date = "";
    ArrayList<Youtube_Data> youtube_data = new ArrayList<Youtube_Data>();



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        searchTask = new searchTask().execute();
    }



    private class searchTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                paringJsonData(getUtube());
            } catch (IOException | JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            youtube_adapter = new Youtube_Adapter(Youtube_Main.this, R.layout.youtube_item,youtube_data);
            recyclerView.setAdapter(youtube_adapter);
        }
    }

    public void init() {
        setContentView(R.layout.youtube_layout);
        recyclerView = findViewById(R.id.youtube_recyclerview);
        bottomNavigationView = findViewById(R.id.youtube_bottomNavigationView);
        Toolbar = findViewById(R.id.youtube_toolbar);

        setSupportActionBar(Toolbar);
        getSupportActionBar().setTitle("동기부여");

        recyclerView.setEmptyView(findViewById(R.id.list_empty));
        setRecyclerView();

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
                        intent = new Intent(getApplicationContext(), MainActivity.class);
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


    }

    //레이아웃 및 어뎁터 결정.
    public void setRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setHasFixedSize(true);
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
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0, 0);
    }

    public JSONObject getUtube() throws IOException {

        String originUrl = "https://www.googleapis.com/youtube/v3/search?"
                + "part=snippet&q=" + "동기부여 영상"
                + "&key=" + serverKey + "&maxResults=6";

        String myUrl = String.format(originUrl);

        URL url = new URL(myUrl);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setReadTimeout(10000);
        connection.setConnectTimeout(15000);
        connection.connect();

        String line;
        String result = "";
        InputStream inputStream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer response = new StringBuffer();

        // 다 읽을 때까지 무한루프
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        // String 형태에서 jsonObject로 만들기
        result = response.toString();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(result);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsonObject;

    }

    //json 객체를 가지고 와서 필요한 데이터를 파싱한다.
    //파싱을 하면 여러가지 값을 얻을 수 있는데 필요한 값들을 세팅하셔서 사용하시면 됩니다.
    private void paringJsonData(JSONObject jsonObject) throws JSONException {

        JSONArray contacts = jsonObject.getJSONArray("items");

        for (int i = 0; i < contacts.length(); i++) {
            JSONObject c = contacts.getJSONObject(i);
            String kind = c.getJSONObject("id").getString("kind"); // 종류를 체크하여 playlist도 저장
            //파싱해서, 비디오만 추출.
            if (kind.equals("youtube#video")) {
                vodid = c.getJSONObject("id").getString("videoId"); // 유튜브
                // 동영상
                // 아이디
                // 값입니다.
                // 재생시
                // 필요합니다.
            } else {
                vodid = c.getJSONObject("id").getString("playlistId"); // 유튜브
            }

            String title = c.getJSONObject("snippet").getString("title"); //유튜브 제목을 받아옵니다
            changString = stringToHtmlSign(title);

            String channelTitle = c.getJSONObject("snippet").getString("channelTitle"); //유튜브 제목을 받아옵니다
            change_chTitle = stringToHtmlSign(channelTitle);


            date = c.getJSONObject("snippet").getString("publishedAt") //등록날짜
                    .substring(0, 10);
//            viewCount = c.getJSONObject("statistics").getString("viewCount");  //조회수
            viewCount = "";

            youtube_data.add(new Youtube_Data(vodid,changString,change_chTitle,date,viewCount));
        }
    }


    //영상 제목을 받아올때 " ' 문자가 그대로 출력되기 때문에 다른 문자로 대체 해주기 위해 사용하는 메서드
    private String stringToHtmlSign(String str) {

        return str.replaceAll("&quot;", "\"")

                .replaceAll("&#39;", "")

                .replaceAll("[<]", "<")

                .replaceAll("[>]", ">");
    }
}
