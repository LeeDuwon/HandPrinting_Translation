package com.example.csr;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.csr.entity.Word;
import com.example.csr.util.Constants;
import com.example.csr.util.GlobalVariable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;

public abstract class TranslationActivity extends AppCompatActivity {
    private static String TAG = TranslationActivity.class.getSimpleName();

    protected TextToSpeech textToSpeech;            // 텍스트를 음성으로 변환 객체

    protected LinearLayout layLoading;              // 로딩 레이아웃
    protected LinearLayout layWord;                 // (음성/사진) 인식 결과 레이아웃
    protected RadioButton rd1;                      // (한글 -> 영어) RadioButton
    protected ImageView imgIcon;                    // (음성인식 또는 사진촬영) 아이콘
    protected TextView txtWord;                     // (음성/사진) 인식 결과 표시할 TextView
    protected Button btnWord;                       // 번역(한글 <-> 영어) 결과 표시할 Button

    protected String result;                        // (음성/사진) 인식 결과값
    protected String translated;
    protected boolean startKo = true;

    /* 초기화 */
    protected void init() {
        // 로딩 레이아웃
        this.layLoading = findViewById(R.id.layLoading);
        ((ProgressBar) findViewById(R.id.progressBar)).setIndeterminateTintList(ColorStateList.valueOf(Color.WHITE));

        // 한글 -> 영어를 디폴트로 설정
        this.rd1 = findViewById(R.id.rd1);
        this.rd1.setChecked(true);

        this.imgIcon = findViewById(R.id.imgIcon);
        this.layWord = findViewById(R.id.layWord);
        this.txtWord = findViewById(R.id.txtWord);
        this.btnWord = findViewById(R.id.btnWord);
        this.btnWord.setText("");

        // 결과 단어 레이아웃 숨김
        this.layWord.setVisibility(View.INVISIBLE);
    }

    /* 텍스트를 음성으로 */
    protected void speechWord(final String word) {
        if (TextUtils.isEmpty(word)) {
            Toast.makeText(this, getString(R.string.msg_word_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        this.textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int state) {
                if (state == TextToSpeech.SUCCESS) {
                    // 한글로 설정
                    if (startKo) {
                        // 한글 -> 영어
                        textToSpeech.setLanguage(Locale.ENGLISH);
                    } else {
                        // 영어 -> 한글
                        textToSpeech.setLanguage(Locale.KOREAN);
                    }

                    // QUEUE_ADD : 재생 대기열 끝에 새 항목이 추가되는 대기열 모드입니다.
                    // QUEUE_FLUSH : 재생 대기열의 모든 항목 (재생할 미디어 및 합성 할 텍스트)이 삭제되고 새 항목으로 대체되는 대기열 모드입니다. 주어진 호출 앱과 관련하여 큐가 플러시됩니다. 다른 수신자의 대기열에있는 항목은 삭제되지 않습니다.
                    // utteranceId : 이 요청의 고유 식별자입니다.
                    textToSpeech.speak(word, TextToSpeech.QUEUE_ADD, null, "id");
                } else {
                    Log.d(TAG, "오류:" + state);
                }
            }
        });
    }

    /* 단어 저장 (중복인 경우 등록일으로 변경) */
    protected void saveWord(final String translatedWord) {
        if (TextUtils.isEmpty(translatedWord)) {
            return;
        }

        // 파이어스토어
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        // 단어 Collection 참조
        CollectionReference reference = db.collection(Constants.FirestoreCollectionName.USER)
                .document(GlobalVariable.documentId).collection(Constants.FirestoreCollectionName.WORD);

        // 단어 중복 체크
        Query query = reference.whereEqualTo("translatedWord", translatedWord).limit(1);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() != null) {
                        if (task.getResult().size() == 0) {
                            // 중복 아님
                            // 등록
                            Word item = new Word(result, translatedWord, (startKo ? Constants.WordLanguageKind.ENGLISH : Constants.WordLanguageKind.KOREAN),
                                    System.currentTimeMillis());
                            db.collection(Constants.FirestoreCollectionName.USER)
                                    .document(GlobalVariable.documentId).collection(Constants.FirestoreCollectionName.WORD)
                                    .add(item)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            // 성공
                                            layLoading.setVisibility(View.GONE);

                                            // 점자 번역하기
                                            goDot(translatedWord, startKo ? Constants.WordLanguageKind.ENGLISH : Constants.WordLanguageKind.KOREAN);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // 등록 실패
                                            layLoading.setVisibility(View.GONE);
                                            Toast.makeText(TranslationActivity.this, getString(R.string.msg_error), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            // 중복
                            String wordId = "";
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                wordId = document.getId();
                                break;
                            }

                            if (TextUtils.isEmpty(wordId)) {
                                layLoading.setVisibility(View.GONE);
                                return;
                            }

                            // 등록일만 수정
                            db.collection(Constants.FirestoreCollectionName.USER)
                                    .document(GlobalVariable.documentId).collection(Constants.FirestoreCollectionName.WORD)
                                    .document(wordId)
                                    .update("createTimeMillis", System.currentTimeMillis())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // 성공
                                            layLoading.setVisibility(View.GONE);

                                            // 점자 번역하기
                                            goDot(translatedWord, startKo ? Constants.WordLanguageKind.ENGLISH : Constants.WordLanguageKind.KOREAN);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // 실패
                                            layLoading.setVisibility(View.GONE);
                                            Toast.makeText(TranslationActivity.this, getString(R.string.msg_error), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        layLoading.setVisibility(View.GONE);
                        Toast.makeText(TranslationActivity.this, getString(R.string.msg_error), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 오류
                    layLoading.setVisibility(View.GONE);
                    Toast.makeText(TranslationActivity.this, getString(R.string.msg_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /* 점자 번역하기 */
    private void goDot(String translatedWord, String language) {
        Intent intent = new Intent(this, DotActivity.class);
        intent.putExtra("word", translatedWord);
        intent.putExtra("language", language);
        startActivity(intent);
    }

    /* 한글 <-> 영어 번역 */
    private String request(String textForTranslated) {
        StringBuilder output = new StringBuilder();

        try {
            String text = URLEncoder.encode(textForTranslated, "UTF-8");
            URL url = new URL(Constants.NaverTranslationApi.ADDRESS);           // 번역 API 주소
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("X-Naver-Client-Id", Constants.NaverTranslationApi.CLIENT_ID);
            con.setRequestProperty("X-Naver-Client-Secret", Constants.NaverTranslationApi.CLIENT_SECRET);

            // 포스트 방식으로 파라미터를 전달합니다.

            // 시작 지점이 한국어이냐 아니냐에 따라 번역 시작과 끝 부분을 달리함
            String source = this.startKo ? "ko" : "en";
            String dest = !this.startKo ? "ko" : "en";
            String postParams = "source=" + source + "&target=" + dest + "&text=" + text;

            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                output.append(inputLine);
            }
            br.close();
        } catch (Exception ex) {
            Log.e("SampleHTTP", "Exception in processing response.", ex);
            ex.printStackTrace();
        }

        return output.toString();
    }

    /* 한글 <-> 영어 번역 AsyncTask */
    protected class BackgroundTask extends AsyncTask<Integer, Integer, Integer> {
        private String forTranslated;

        // forTranslated 는 번역이 완료된 TextView 에 띄우기 위한 용도, 이 번역 결과를 점자를 만드는 데 사용함
        BackgroundTask(String forTranslated) {
            this.forTranslated = forTranslated;
        }

        @Override
        protected Integer doInBackground(Integer... arg0) {
            // 한글 <-> 영어 번역하기
            translated = request(forTranslated);
            return null;
        }

        protected void onPostExecute(Integer a) {
            JsonElement element = JsonParser.parseString(translated).getAsJsonObject();
            if (element.getAsJsonObject().get("errorMessage") != null) {
                System.out.println("번역 오류가 발생했습니다. " +
                        "[오류 코드: " + element.getAsJsonObject().get("errorCode").getAsString() + "]");
            } else if (element.getAsJsonObject().get("message") != null) {
                // 번역 결과 출력
                String result = element.getAsJsonObject().get("message").getAsJsonObject().get("result")
                        .getAsJsonObject().get("translatedText").getAsString();

                btnWord.setText(result);
            }
        }
    }
}
