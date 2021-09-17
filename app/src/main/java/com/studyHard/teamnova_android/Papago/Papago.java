package com.studyHard.teamnova_android.Papago;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.studyHard.teamnova_android.MainActivity;
import com.studyHard.teamnova_android.R;
import com.studyHard.teamnova_android.memo;
import com.studyHard.teamnova_android.todo;
import com.studyHard.teamnova_android.유튜브API.Youtube_Main;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Papago extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView; // 바텀 네비게이션 뷰

    private long backKeyPressedTime = 0; // 마지막으로 뒤로가기 버튼 눌렀던 시간 저장

    Toast toast;
    Intent intent;

    //view

    androidx.appcompat.widget.Toolbar Toolbar;


    TextView toLanguageTV;
    TextView fromLanguageTV;
    TextView changedTextTV;
    ImageView m_ivImage;
    ImageButton languageChangeIB;

    Button m_btnOCR;

    EditText whatTranslateET;

    // 기본 언어 한글
    String language = "ko";

    // tesseract 변수
    Context mContext;

    private String mDataPath = ""; //언어데이터가 있는 경로
    private String mCurrentPhotoPath; // 사진 경로
    private final String[] mLanguageList = {"eng", "kor"}; // 언어
    private Bitmap image; //사용되는 이미지

    // true  : Camera On  : 카메라로 직접 찍어 문자 인식
    // false : Camera Off : 샘플이미지를 로드하여 문자 인식
    private boolean CameraOnOffFlag = true;

    private TessBaseAPI m_Tess; //Tess API reference
    private ProgressCircleDialog m_objProgressCircle = null; // 원형 프로그레스바
    private MessageHandler m_messageHandler;

    private static final String TAG = "Papago";

    private boolean ProgressFlag = false; // 프로그레스바 상태 플래그

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.papago_activity);

        m_objProgressCircle = new ProgressCircleDialog(this);
        m_messageHandler = new MessageHandler();



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
                        break;
                }
                return true;
            }
        });

        // 초기화
        //레이아웃에 들어갈 view와 id를 잇는 역할
        init();
        //각 button 별로 리스너를 설정 가능하다.
        buttonClickListenr();

        setSupportActionBar(Toolbar);
        getSupportActionBar().setTitle("번역");

        if (CameraOnOffFlag) {
            try{
                PermissionCheck();
                Tesseract();
            }catch (Exception e){

            }
        } else {
            //이미지 디코딩을 위한 초기화
            image = BitmapFactory.decodeResource(getResources(), R.drawable.sampledata); //샘플이미지파일
        }

        whatTranslateET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d(TAG, "beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d(TAG, "onTextChanged");
            }

            @Override
            public void afterTextChanged(Editable editable) {

                new Thread() {
                    @Override
                    public void run() {
                        String word = whatTranslateET.getText().toString();
                        // Papago는 3번에서 만든 자바 코드이다.
                        Translate papago = new Translate();
                        AutoDetect papago_autoDetect = new AutoDetect();

                        String resultWord;
                        language = papago_autoDetect.getDetectLanguage(word);

                        if (language.equals("ko")) {
                            resultWord = papago.getTranslation(word, "ko", "en");
                        } else {
                            resultWord = papago.getTranslation(word, "en", "ko");
                        }

                        Bundle papagoBundle = new Bundle();
                        papagoBundle.putString("resultWord", resultWord);

                        Log.d(TAG, "resultWord" + resultWord);

                        Message msg = papago_handler.obtainMessage();
                        msg.setData(papagoBundle);
                        papago_handler.sendMessage(msg);

                        Log.d(TAG, "msg" + msg);
                    }
                }.start();

            }
        });
    }


    private void init() {
        fromLanguageTV = findViewById(R.id.FromLanguageTV);
        toLanguageTV = findViewById(R.id.ToLanguageTV);
        languageChangeIB = findViewById(R.id.languageChangeIB);
        whatTranslateET = findViewById(R.id.whatTranslateET);
        changedTextTV = findViewById(R.id.changedTextTV);
        m_btnOCR = findViewById(R.id.btn_OCR);
        m_ivImage = findViewById(R.id.iv_image);
        Toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
    }

    @SuppressLint("HandlerLeak")
    Handler papago_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String resultWord = bundle.getString("resultWord");
            changedTextTV.setText(resultWord);
            if (language.equals("ko")) {

                toLanguageTV.setText("영어");
                fromLanguageTV.setText("한국어");

            } else {

                toLanguageTV.setText("한국어");
                fromLanguageTV.setText("영어");
            }

        }
    };


    private void buttonClickListenr() {

        languageChangeIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "languageChangeIB onClick");

                //버튼을 누를 때마다 번역할 언어가 바뀜

                if (language.equals("ko")) {
                    language = "en";

                    toLanguageTV.setText("영어");
                    fromLanguageTV.setText("한국어");

                } else {
                    language = "ko";

                    toLanguageTV.setText("한국어");
                    fromLanguageTV.setText("영어");
                }
            }
        });

        m_btnOCR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CameraOnOffFlag) {
                    dispatchTakePictureIntent();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult) {
        if (requestCode == 0) {

        } else {

        }
    }

    // 이미지를 원본과 같게 회전시킨다.
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public void PermissionCheck() {
        /**
         * 6.0 마시멜로우 이상일 경우에는 권한 체크후 권한을 요청한다.
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED &&
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                // 권한 없음
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        ConstantDefine.PERMISSION_CODE);
            } else {
                // 권한 있음
            }
        }
    }

    /**
     * 기본카메라앱을 실행 시킨다.
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // 사진파일을 생성한다.
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // 사진파일이 정상적으로 생성되었을때
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        this.getApplicationContext().getPackageName() + ".fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, ConstantDefine.ACT_TAKE_PIC);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ConstantDefine.PERMISSION_CODE:
                Toast.makeText(this, "권한이 허용되었습니다.", Toast.LENGTH_SHORT).show();
                break;
            case ConstantDefine.ACT_TAKE_PIC:
                //카메라로 찍은 사진을 받는다.
                if ((resultCode == RESULT_OK)) {

                    try {
                        //카메라로 찍은 사진의 url 경로를 이용해 bitmap으로 만든다.
                        File file = new File(mCurrentPhotoPath);
                        Bitmap rotatedBitmap = null;
                        Bitmap bitmap = null;
                        if (Build.VERSION.SDK_INT >= 29) {
                            ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), Uri.fromFile(file));
                            try {
                                bitmap = ImageDecoder.decodeBitmap(source);
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),
                                        FileProvider.getUriForFile(this,
                                                getApplicationContext().getPackageName() + ".fileprovider", file));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        // 회전된 사진을 원래대로 돌려 표시한다.
                        if (bitmap != null) {
                            ExifInterface ei = new ExifInterface(mCurrentPhotoPath);
                            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                    ExifInterface.ORIENTATION_UNDEFINED);
                            switch (orientation) {

                                case ExifInterface.ORIENTATION_ROTATE_90:
                                    rotatedBitmap = rotateImage(bitmap, 90);
                                    break;

                                case ExifInterface.ORIENTATION_ROTATE_180:
                                    rotatedBitmap = rotateImage(bitmap, 180);
                                    break;

                                case ExifInterface.ORIENTATION_ROTATE_270:
                                    rotatedBitmap = rotateImage(bitmap, 270);
                                    break;

                                case ExifInterface.ORIENTATION_NORMAL:
                                default:
                                    rotatedBitmap = bitmap;
                            }
                            OCRThread ocrThread = new OCRThread(rotatedBitmap);
                            ocrThread.setDaemon(true);
                            ocrThread.start();
                            m_ivImage.setVisibility(View.VISIBLE);
                            m_ivImage.setImageBitmap(rotatedBitmap);// 카메라로 찍은 사진을 뷰에 표시한다.
                            whatTranslateET.setText(getResources().getString(R.string.LoadingMessage)); //인식된텍스트 표시
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    //check file on the device
    private void checkFile(File dir, String Language) {
        //디렉토리가 없으면 디렉토리를 만들고 그후에 파일을 카피
        if (!dir.exists() && dir.mkdirs()) {
            copyFiles(Language);
        }
        //디렉토리가 있지만 파일이 없으면 파일카피 진행
        if (dir.exists()) {
            String datafilepath = mDataPath + "tessdata/" + Language + ".traineddata";
            File datafile = new File(datafilepath);
            if (!datafile.exists()) {
                copyFiles(Language);
            }
        }
    }

    //copy file to device
    private void copyFiles(String Language) {
        try {
            String filepath = mDataPath + "/tessdata/" + Language + ".traineddata";
            AssetManager assetManager = getAssets();
            InputStream instream = assetManager.open("tessdata/" + Language + ".traineddata");
            OutputStream outstream = new FileOutputStream(filepath);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }
            outstream.flush();
            outstream.close();
            instream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Tesseract() {

        //언어파일 경로
        mDataPath = getFilesDir() + "/tesseract/";

        //트레이닝데이터가 카피되어 있는지 체크
        String lang = "";
        for (String Language : mLanguageList) {
            checkFile(new File(mDataPath + "tessdata/"), Language);
            lang += Language + "+";
        }
        m_Tess = new TessBaseAPI();
        m_Tess.init(mDataPath, lang);
    }

    //region Thread
    public class OCRThread extends Thread {
        private Bitmap rotatedImage;

        OCRThread(Bitmap rotatedImage) {
            this.rotatedImage = rotatedImage;
//            if (!ProgressFlag)
//                m_objProgressCircle = ProgressCircleDialog.show(getApplicationContext(), "", "", true);
//            ProgressFlag = true;
        }

        @Override
        public void run() {
            super.run();
            // 사진의 글자를 인식해서 옮긴다
            String OCRresult = null;
            m_Tess.setImage(rotatedImage);
            OCRresult = m_Tess.getUTF8Text();

            Message message = Message.obtain();
            message.what = ConstantDefine.RESULT_OCR;
            message.obj = OCRresult;
            m_messageHandler.sendMessage(message);

        }
    }
//    endregion

    //    region Handler
    public class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case ConstantDefine.RESULT_OCR:
                    whatTranslateET.setText(String.valueOf(msg.obj)); //텍스트 변경
//                     원형 프로그레스바 종료
//                    if (m_objProgressCircle.isShowing() && m_objProgressCircle != null)
//                        m_objProgressCircle.dismiss();
//                    ProgressFlag = false;
                    Toast.makeText(getApplicationContext(), "문자인식이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
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
}