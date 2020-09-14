package com.example.csr;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.csr.entity.WordItem;
import com.example.csr.util.Constants;
import com.example.csr.util.GlobalVariable;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;
import java.util.Objects;

public class WordInfoActivity extends AppCompatActivity {
    private static String TAG = WordInfoActivity.class.getSimpleName();

    private TextToSpeech textToSpeech;              // 텍스트를 음성으로 변환 객체

    private LinearLayout layLoading;                // 로딩 레이아웃
    private ImageView imgFavorite;                  // 즐겨찾기 아이콘

    private WordItem wordItem;                      // 단어 item 객체

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_info);

        Intent intent = getIntent();
        this.wordItem = intent.getParcelableExtra("word_item");

        // 제목 표시
        setTitle(getString(R.string.activity_title_word_info));

        // 홈버튼(<-) 표시
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // 로딩 레이아웃
        this.layLoading = findViewById(R.id.layLoading);
        ((ProgressBar) findViewById(R.id.progressBar)).setIndeterminateTintList(ColorStateList.valueOf(Color.WHITE));

        this.imgFavorite = findViewById(R.id.imgFavorite);
        // 즐겨찾기 표시
        displayFavorite();

        Log.d(TAG, this.wordItem.word.getWord());
        Log.d(TAG, this.wordItem.word.getTranslatedWord());

        ((TextView) findViewById(R.id.txtWord)).setText(this.wordItem.word.getWord());          // 번역전 단어
        ((Button) findViewById(R.id.btnWord)).setText(this.wordItem.word.getTranslatedWord());  // 번역된 단어

        this.imgFavorite.setOnClickListener(mClickListener);
        findViewById(R.id.btnWord).setOnClickListener(mClickListener);
        findViewById(R.id.btnTranslation).setOnClickListener(mClickListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* 즐겨찾기 표시 */
    private void displayFavorite() {
        if (this.wordItem.word.isFavorite()) {
            // 즐겨찾기에 추가됨
            this.imgFavorite.setImageResource(R.drawable.ic_star_36_blue);
        } else {
            this.imgFavorite.setImageResource(R.drawable.ic_star_outline_36_gray);
        }
    }

    /* 즐겨찾기 추가 및 제거 */
    private void modifyFavorite(final boolean favorite) {
        // 파이어스토어
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // 단어 document 참조
        DocumentReference reference = db.collection(Constants.FirestoreCollectionName.USER)
                .document(GlobalVariable.documentId).collection(Constants.FirestoreCollectionName.WORD)
                .document(this.wordItem.id);
        // 즐겨찾기 수정
        reference.update("favorite", favorite)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // 성공
                        layLoading.setVisibility(View.GONE);

                        wordItem.word.setFavorite(favorite);
                        // 즐겨찾기 표시
                        displayFavorite();

                        // Activity 에 값 전달
                        Intent intent = new Intent();
                        intent.putExtra("favorite", favorite);
                        setResult(Activity.RESULT_OK, intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 실패
                        layLoading.setVisibility(View.GONE);
                        Toast.makeText(WordInfoActivity.this, getString(R.string.msg_error), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /* 텍스트를 음성으로 */
    private void speechWord(final String word) {
        if (TextUtils.isEmpty(word)) {
            Toast.makeText(this, getString(R.string.msg_word_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        this.textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int state) {
                if (state == TextToSpeech.SUCCESS) {
                    if (wordItem.word.getLanguage().equals(Constants.WordLanguageKind.KOREAN)) {
                        // 한글
                        textToSpeech.setLanguage(Locale.KOREAN);
                    } else {
                        // 영어
                        textToSpeech.setLanguage(Locale.ENGLISH);
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

    /* 점자 번역하기 */
    private void goDot(String translatedWord, String language) {
        Intent intent = new Intent(this, DotActivity.class);
        intent.putExtra("word", translatedWord);
        intent.putExtra("language", language);
        startActivity(intent);
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.imgFavorite:
                    // 즐겨찾기
                    layLoading.setVisibility(View.VISIBLE);
                    // 로딩 레이아웃을 표시하기 위해 딜레이 적용
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // 즐겨찾기 추가 및 제거
                            modifyFavorite(!wordItem.word.isFavorite());
                        }
                    }, Constants.LoadingDelay.SHORT);
                    break;
                case R.id.btnWord:
                    // 단어를 음성으로 듣기
                    if (wordItem == null) {
                        return;
                    }

                    speechWord(wordItem.word.getTranslatedWord());
                    break;
                case R.id.btnTranslation:
                    // 점자 변환하기
                    if (wordItem == null) {
                        return;
                    }

                    goDot(wordItem.word.getTranslatedWord(), wordItem.word.getLanguage());
                    break;
            }
        }
    };
}
