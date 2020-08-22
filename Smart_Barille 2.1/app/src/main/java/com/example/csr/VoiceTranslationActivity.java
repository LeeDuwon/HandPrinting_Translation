package com.example.csr;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.csr.util.AudioWriterPCM;
import com.example.csr.util.Constants;
import com.example.csr.util.NaverRecognizer;
import com.naver.speech.clientapi.SpeechRecognitionResult;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;

public class VoiceTranslationActivity extends TranslationActivity {
    private static String TAG = VoiceTranslationActivity.class.getSimpleName();

    private NaverRecognizer naverRecognizer;        // 네이버 음성인식 리스너
    private AudioWriterPCM writer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_translation);

        // 제목 표시
        setTitle(getString(R.string.activity_title_voice_translation));

        // 홈버튼(<-) 표시
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // 초기화
        init();

        this.imgIcon.setOnClickListener(mClickListener);
        this.btnWord.setOnClickListener(mClickListener);
        findViewById(R.id.btnTranslation).setOnClickListener(mClickListener);

        // 네이버 음성인식 리스너
        this.naverRecognizer = new NaverRecognizer(this, new RecognitionHandler(this), Constants.NaverSpeechApi.CLIENT_ID);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // 음성인식 서버 초기화를 진행합니다.
        this.naverRecognizer.getSpeechRecognizer().initialize();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // 음성인식 서버를 종료합니다.
        this.naverRecognizer.getSpeechRecognizer().release();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* 음성인식 시작 */
    private void startRecognize() {
        this.imgIcon.setEnabled(false);
        this.imgIcon.setImageResource(R.drawable.ic_microphone_96_gray);
        this.layWord.setVisibility(View.VISIBLE);

        if (!naverRecognizer.getSpeechRecognizer().isRunning()) {
            this.result = "";
            this.translated = "";
            this.txtWord.setText("Connecting...");
            this.btnWord.setText("");

            // 분석할때 true 이면 한국어로 false 이면 영어로
            this.startKo = this.rd1.isChecked();
            this.naverRecognizer.recognize(this.startKo);
        } else {
            Log.d(TAG, "stop and wait Final Result");
            this.naverRecognizer.getSpeechRecognizer().stop();
        }
    }

    /* 음성 인식 메시지를 처리합니다. */
    private void handleMessage(Message msg) {
        switch (msg.what) {
            // 음성 인식을 시작할 준비가 완료된 경우
            case R.id.clientReady:
                this.txtWord.setText("Connected");
                this.writer = new AudioWriterPCM(Environment.getExternalStorageDirectory().getAbsolutePath() + "/NaverSpeechTest");
                this.writer.open("Test");
                break;
            // 현재 음성 인식이 진행되고 있는 경우
            case R.id.audioRecording:
                this.writer.write((short[]) msg.obj);
                break;
            // 처리가 되고 있는 도중에 결과를 받은 경우
            case R.id.partialResult:
                this.result = (String) (msg.obj);
                this.txtWord.setText(this.result);
                break;
            // 최종 인식이 완료되면 유사 결과를 모두 보여줍니다.
            case R.id.finalResult:
                SpeechRecognitionResult speechRecognitionResult = (SpeechRecognitionResult) msg.obj;
                List<String> results = speechRecognitionResult.getResults();

                //StringBuilder strBuf = new StringBuilder();
                // 첫번째 단어만 체크
                for (String result : results) {
                    //strBuf.append(result);
                    //strBuf.append("\n");
                    this.result = result;
                    break;
                }

                //this.result = strBuf.toString();
                this.txtWord.setText(this.result);
                this.imgIcon.setEnabled(true);
                this.imgIcon.setImageResource(R.drawable.ic_microphone_96_blue);

                // 한글 <-> 영어 번역
                new BackgroundTask(this.result).execute();
                break;
            // 인식 오류가 발생한 경우
            case R.id.recognitionError:
                if (writer != null) {
                    writer.close();
                }
                this.result = "";
                this.txtWord.setText("Error code : " + msg.obj.toString());
                this.imgIcon.setEnabled(true);
                this.imgIcon.setImageResource(R.drawable.ic_microphone_96_blue);
                break;
            // 음성 인식 비활성화 상태인 경우
            case R.id.clientInactive:
                if (writer != null) {
                    writer.close();
                }
                this.imgIcon.setEnabled(true);
                this.imgIcon.setImageResource(R.drawable.ic_microphone_96_blue);
                break;
        }
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.imgIcon:
                    // 음성녹음
                    startRecognize();
                    break;
                case R.id.btnWord:
                    // 단어를 음성으로 듣기
                    speechWord(btnWord.getText().toString());
                    break;
                case R.id.btnTranslation:
                    // 점자 변환하기 (저장 후 점자로 변환)
                    final String word = btnWord.getText().toString();

                    if (TextUtils.isEmpty(word)) {
                        Toast.makeText(VoiceTranslationActivity.this, getString(R.string.msg_word_empty), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    layLoading.setVisibility(View.VISIBLE);
                    // 로딩 레이아웃을 표시하기 위해 딜레이 적용
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // (저장 후 점자로 변환)
                            saveWord(word);
                        }
                    }, Constants.LoadingDelay.SHORT);
                    break;
            }
        }
    };

    /* SpeechRecognizer 쓰레드의 메시지를 처리하는 핸들러를 정의합니다. */
    class RecognitionHandler extends Handler {
        private final WeakReference<VoiceTranslationActivity> mActivity;

        private RecognitionHandler(VoiceTranslationActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            VoiceTranslationActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }
}
