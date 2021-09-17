package com.studyHard.teamnova_android.Papago;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class AutoDetect {

    public String getDetectLanguage(String word) {

        // 클라이언트 ID / PW
        String clientId = "_GYisNxhu3b1mXAIs2zD";
        String clientSecret = "lwLLY2QRrG";

        try {
            // 번역할 문자, 번역할 언어는 String 상태, 컴퓨터가 이해할 수 있도록 UTF-8을 이용해 encode한다.
            String text = URLEncoder.encode(word, "UTF-8");             //word

            // API가 담겨 있는 코드.
            String apiURL = "https://openapi.naver.com/v1/papago/detectLangs";
            URL url = new URL(apiURL);

            //HttpURLConnection -> url에 연결된 서버를 열음.
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("X-Naver-Client-Id", clientId);
            con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
            // post request (id, pw를 통해, 서버 접속 성공. / 번역할 단어 전송.)
            String postParams = "query=" + text;
            con.setDoOutput(true);
            //wr에 입력 받는 값을 기록 후 저장.
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) { // 정상 호출
                //정상적으로 호출이 완료되었을 경우에, 서버로부터 결과값을 받음.
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();

//            System.out.println(response.toString());

            String s = response.toString();

            s = s.split("\"")[3];

//            Log.e("언어 감지", s);

            return s;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }
}
