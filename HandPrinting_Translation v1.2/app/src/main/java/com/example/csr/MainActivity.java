package com.example.csr;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.WorkerThread;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.csr.utils.AudioWriterPCM;
import com.example.csr.utils.WordClass;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.naver.speech.clientapi.SpeechConfig;
import com.naver.speech.clientapi.SpeechRecognitionException;
import com.naver.speech.clientapi.SpeechRecognitionListener;
import com.naver.speech.clientapi.SpeechRecognitionResult;
import com.naver.speech.clientapi.SpeechRecognizer;

import java.lang.ref.WeakReference;
import java.util.List;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.URLDecoder;


public class MainActivity extends Activity  {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String CLIENT_ID = "udvt44fz7r";

    private RecognitionHandler handler;
    private NaverRecognizer naverRecognizer;
    private TextView txtResult;
    private Button btnStart;
    private String mResult;
    private AudioWriterPCM writer;

    /* 텍스트를 영어로 변환하기 위한 소스코드입니다. */
    private TextView translatedView;
    private String textForTranslated;
    private String translated;
    private String address = "https://openapi.naver.com/v1/papago/n2mt";

    class BackgroundTask extends AsyncTask<Integer, Integer, Integer> {
        protected void onPreExecute() {
        }

        @Override
        protected Integer doInBackground(Integer... arg0) {
            translated = request(address);
            return null;
        }

        protected void onPostExecute(Integer a) {
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(translated);
            if(element.getAsJsonObject().get("errorMessage") != null) {
                System.out.println("번역 오류가 발생했습니다. " +
                        "[오류 코드: " + element.getAsJsonObject().get("errorCode").getAsString() + "]");
            } else if(element.getAsJsonObject().get("message") != null) {
                // 번역 결과 출력
                translatedView.setText(element.getAsJsonObject().get("message").getAsJsonObject().get("result")
                        .getAsJsonObject().get("translatedText").getAsString());
            }

        }

    }

    private String request(String urlStr) {
        StringBuilder output = new StringBuilder();
        String clientId = "wVCRXR41BQ3xx0VDCW65"; // 애플리케이션 클라이언트 아이디 값";
        String clientSecret = "m7mEqSecUi"; // 애플리케이션 클라이언트 시크릿 값";
        try {
            String text = URLEncoder.encode(textForTranslated, "UTF-8");
            URL url = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("X-Naver-Client-Id", clientId);
            con.setRequestProperty("X-Naver-Client-Secret", clientSecret);

            // 포스트 방식으로 파라미터를 전달합니다.
            String postParams = "source=ko&target=en&text=" + text;
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if(responseCode==200) {
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                output.append(inputLine);
            }
            br.close();
        } catch(Exception ex) {
            Log.e("SampleHTTP", "Exception in processing response.", ex);
            ex.printStackTrace();
        }
        return output.toString();
    }
    /* 여기까지 텍스트를 영어로 변환 */

    // 음성 인식 메시지를 처리합니다.
    private void handleMessage(Message msg) {
                switch (msg.what) {
                    // 음성 인식을 시작할 준비가 완료된 경우
                    case R.id.clientReady:
                        txtResult.setText("Connected");
                        writer = new AudioWriterPCM(Environment.getExternalStorageDirectory().getAbsolutePath() + "/NaverSpeechTest");
                        writer.open("Test");
                        break;
                    // 현재 음성 인식이 진행되고 있는 경우
                    case R.id.audioRecording:
                        writer.write((short[]) msg.obj);
                        break;
                    // 처리가 되고 있는 도중에 결과를 받은 경우
                    case R.id.partialResult:
                        mResult = (String) (msg.obj);
                        txtResult.setText(mResult);
                break;
            // 최종 인식이 완료되면 유사 결과를 모두 보여줍니다.
            case R.id.finalResult:
                SpeechRecognitionResult speechRecognitionResult = (SpeechRecognitionResult) msg.obj;
                List<String> results = speechRecognitionResult.getResults();
                StringBuilder strBuf = new StringBuilder();
                // 전달 받은 모든 문자열을 차례대로 출력합니다.
                for(String result : results) {
                    strBuf.append(result);
                    strBuf.append(" ");
                    strBuf.append(WordClass.decompose(result));
                    strBuf.append("\n");
                }
                mResult = strBuf.toString();
                txtResult.setText(mResult);
                textForTranslated = mResult;
                new BackgroundTask().execute();
                break;
            // 인식 오류가 발생한 경우
            case R.id.recognitionError:
                if (writer != null) {
                    writer.close();
                }
                mResult = "Error code : " + msg.obj.toString();
                txtResult.setText(mResult);
                btnStart.setText(R.string.str_start);
                btnStart.setEnabled(true);
                break;
            // 음성 인식 비활성화 상태인 경우
            case R.id.clientInactive:
                if (writer != null) {
                    writer.close();
                }
                btnStart.setText(R.string.str_start);
                btnStart.setEnabled(true);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtResult = (TextView) findViewById(R.id.txt_result);
        translatedView = (TextView)findViewById(R.id.translated);
        btnStart = (Button) findViewById(R.id.btn_start);
        handler = new RecognitionHandler(this);
        naverRecognizer = new NaverRecognizer(this, handler, CLIENT_ID);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 사용자의 OS 버전이 마시멜로우 이상인지 체크합니다. */
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    /* 사용자 단말기의 권한 중 권한이 허용되어 있는지 체크합니다. */
                    int permissionResult = checkSelfPermission(Manifest.permission.RECORD_AUDIO);
                    /* 권한이 없을 때 */
                    if (permissionResult == PackageManager.PERMISSION_DENIED) {
                        /* 사용자가 권한을 한번이라도 거부한 적이 있는 지 확인합니다. */
                        if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                            dialog.setTitle("권한이 필요합니다.")
                                    .setMessage("이 기능을 사용하기 위해서는 권한이 필요합니다. 계속하시겠습니까?")
                                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 1000);
                                            }
                                        }
                                    })
                                    .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(MainActivity.this, "기능을 취소했습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .create()
                                    .show();
                        }
                        // 최초로 권한을 요청하는 경우
                        else {
                            // 권한을 요청합니다.
                            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 1000);
                        }
                    }
                    /* 권한이 있는 경우 */
                    else {
                        /* 음성 인식 기능을 처리합니다. */
                        if(!naverRecognizer.getSpeechRecognizer().isRunning()) {
                            mResult = "";
                            txtResult.setText("Connecting...");
                            btnStart.setText(R.string.str_stop);
                            naverRecognizer.recognize();
                        } else {
                            Log.d(TAG, "stop and wait Final Result");
                            btnStart.setEnabled(false);
                            naverRecognizer.getSpeechRecognizer().stop();
                        }
                    }
                }
                /* 사용자의 OS 버전이 마시멜로우 이하일 떄 */
                else {
                    /* 음성 인식 기능을 처리합니다. */
                    if(!naverRecognizer.getSpeechRecognizer().isRunning()) {
                        mResult = "";
                        txtResult.setText("Connecting...");
                        btnStart.setText(R.string.str_stop);
                        naverRecognizer.recognize();
                    } else {
                        Log.d(TAG, "stop and wait Final Result");
                        btnStart.setEnabled(false);
                        naverRecognizer.getSpeechRecognizer().stop();
                    }
                }
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        // 음성인식 서버 초기화를 진행합니다.
        naverRecognizer.getSpeechRecognizer().initialize();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mResult = "";
        txtResult.setText("");
        btnStart.setText(R.string.str_start);
        btnStart.setEnabled(true);
    }
    @Override
    protected void onStop() {
        super.onStop();
        // 음성인식 서버를 종료합니다.
        naverRecognizer.getSpeechRecognizer().release();
    }
    // SpeechRecognizer 쓰레드의 메시지를 처리하는 핸들러를 정의합니다.
    static class RecognitionHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;
        RecognitionHandler(MainActivity activity) {
            mActivity = new WeakReference<MainActivity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }
}


// 2. SpeechRecognitionListener를 상속한 클래스를 정의합니다.
class NaverRecognizer implements SpeechRecognitionListener {
    /* 텍스트를 영어로 변환하기 위한 소스코드입니다. */
    public TextView translatedView;
    public String textForTranslated;
    public String translated;
    public String address = "https://openapi.naver.com/v1/papago/n2mt";

    private final static String TAG = NaverRecognizer.class.getSimpleName();
    private Handler mHandler;
    private SpeechRecognizer mRecognizer;
    public NaverRecognizer(Context context, Handler handler, String clientId) {
        this.mHandler = handler;
        try {
            mRecognizer = new SpeechRecognizer(context, clientId);
        } catch (SpeechRecognitionException e) {
            e.printStackTrace();
        }
        mRecognizer.setSpeechRecognitionListener(this);
    }
    public SpeechRecognizer getSpeechRecognizer() {
        return mRecognizer;
    }
    public void recognize() {
        try {
            mRecognizer.recognize(new SpeechConfig(SpeechConfig.LanguageType.KOREAN, SpeechConfig.EndPointDetectType.AUTO));
        } catch (SpeechRecognitionException e) {
            e.printStackTrace();
        }
    }
    @Override
    @WorkerThread
    public void onInactive() {
        Message msg = Message.obtain(mHandler, R.id.clientInactive);
        msg.sendToTarget();
    }
    @Override
    @WorkerThread
    public void onReady() {
        Message msg = Message.obtain(mHandler, R.id.clientReady);
        msg.sendToTarget();
    }
    @Override
    @WorkerThread
    public void onRecord(short[] speech) {
        Message msg = Message.obtain(mHandler, R.id.audioRecording, speech);
        msg.sendToTarget();
    }
    @Override
    @WorkerThread
    public void onPartialResult(String result) {
        Message msg = Message.obtain(mHandler, R.id.partialResult, result);
        msg.sendToTarget();
    }
    @Override
    @WorkerThread
    public void onEndPointDetected() {
        Log.d(TAG, "Event occurred : EndPointDetected");
    }
    @Override
    @WorkerThread
    public void onResult(SpeechRecognitionResult result) {
        Message msg = Message.obtain(mHandler, R.id.finalResult, result);
        msg.sendToTarget();
    }
    @Override
    @WorkerThread
    public void onError(int errorCode) {
        Message msg = Message.obtain(mHandler, R.id.recognitionError, errorCode);
        msg.sendToTarget();
    }
    @Override
    @WorkerThread
    public void onEndPointDetectTypeSelected(SpeechConfig.EndPointDetectType epdType) {
        Message msg = Message.obtain(mHandler, R.id.endPointDetectTypeSelected, epdType);
        msg.sendToTarget();
    }
}

