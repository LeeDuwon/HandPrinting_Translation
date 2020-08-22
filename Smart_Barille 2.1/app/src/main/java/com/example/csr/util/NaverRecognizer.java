package com.example.csr.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.WorkerThread;

import com.example.csr.R;
import com.naver.speech.clientapi.SpeechConfig;
import com.naver.speech.clientapi.SpeechRecognitionException;
import com.naver.speech.clientapi.SpeechRecognitionListener;
import com.naver.speech.clientapi.SpeechRecognitionResult;
import com.naver.speech.clientapi.SpeechRecognizer;

public class NaverRecognizer implements SpeechRecognitionListener {
    private static String TAG = NaverRecognizer.class.getSimpleName();

    private Handler handler;
    private SpeechRecognizer recognizer;

    public NaverRecognizer(Context context, Handler handler, String clientId) {
        this.handler = handler;

        try {
            this.recognizer = new SpeechRecognizer(context, clientId);
        } catch (SpeechRecognitionException e) {
            e.printStackTrace();
        }
        this.recognizer.setSpeechRecognitionListener(this);
    }

    public SpeechRecognizer getSpeechRecognizer() {
        return this.recognizer;
    }

    public void recognize(boolean startKo) {
        try {
            // 시작을 한국어로 했으면 한국어, 영어로 했으면 언어 타입을 영어로 바꿔준다.
            this.recognizer.recognize(new SpeechConfig(startKo ? SpeechConfig.LanguageType.KOREAN : SpeechConfig.LanguageType.ENGLISH, SpeechConfig.EndPointDetectType.AUTO));
        } catch (SpeechRecognitionException e) {
            e.printStackTrace();
        }
    }

    @Override
    @WorkerThread
    public void onInactive() {
        Log.d(TAG, "Event occurred : Inactive");
        Message msg = Message.obtain(this.handler, R.id.clientInactive);
        msg.sendToTarget();
    }

    @Override
    @WorkerThread
    public void onReady() {
        Log.d(TAG, "Event occurred : Ready");
        Message msg = Message.obtain(this.handler, R.id.clientReady);
        msg.sendToTarget();
    }

    @Override
    @WorkerThread
    public void onRecord(short[] speech) {
        Log.d(TAG, "Event occurred : Record");
        Message msg = Message.obtain(this.handler, R.id.audioRecording, speech);
        msg.sendToTarget();
    }

    @Override
    @WorkerThread
    public void onPartialResult(String result) {
        Log.d(TAG, "Partial Result!! (" + result + ")");
        Message msg = Message.obtain(this.handler, R.id.partialResult, result);
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
        Log.d(TAG, "Final Result!! (" + result.getResults().get(0) + ")");
        Message msg = Message.obtain(this.handler, R.id.finalResult, result);
        msg.sendToTarget();
    }

    @Override
    @WorkerThread
    public void onError(int errorCode) {
        Log.d(TAG, "Error!! (" + errorCode + ")");
        Message msg = Message.obtain(this.handler, R.id.recognitionError, errorCode);
        msg.sendToTarget();
    }

    @Override
    @WorkerThread
    public void onEndPointDetectTypeSelected(SpeechConfig.EndPointDetectType epdType) {
        Log.d(TAG, "EndPointDetectType is selected!! (" + epdType.toInteger() + ")");
        Message msg = Message.obtain(this.handler, R.id.endPointDetectTypeSelected, epdType);
        msg.sendToTarget();
    }
}
