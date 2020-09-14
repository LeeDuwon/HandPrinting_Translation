package com.example.csr;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.csr.adapter.MyFragmentPagerAdapter;
import com.example.csr.fragment.DotFragment;
import com.example.csr.util.Constants;
import com.example.csr.util.WordClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class DotActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    private static String TAG = DotActivity.class.getSimpleName();

    private TextToSpeech textToSpeech;      // 텍스트를 음성으로 변환 객체

    private List<Fragment> fragments;       // 점자 Fragment list
    private List<String> texts;             // 점자 텍스트 list (페이징하면 음성으로 들려주기 위함)

    private TextView txtPagePosition;       // 페이지 위치

    private String language;                // 한글(KOR) / 영어(ENG)

    private int pagePosition = 0;           // 페이지 위치

    // 점자 정보를 담는 Map
    private static HashMap<Character, int[]> map = new HashMap<>();

    // 점자 종성 정보를 담는 Map
    private static HashMap<Character, int[]> mapJong = new HashMap<>();

    // 된소리를 표기하기 위한 점자
    private static int[] fortis = new int[]
            {
                    0, 0,
                    0, 0,
                    0, 1
            };

    // 따로 나눠서 표시해야 하는 모음 점자
    private static HashSet<Character> toSplit = new HashSet<>(Arrays.asList('ㅟ', 'ㅒ', 'ㅙ', 'ㅞ'));

    static {
        // 초성
        map.put('ㄱ', new int[]
                {
                        0, 1,
                        0, 0,
                        0, 0
                });
        map.put('ㄴ', new int[]
                {
                        1, 1,
                        0, 0,
                        0, 0
                });
        map.put('ㄷ', new int[]
                {
                        0, 1,
                        1, 0,
                        0, 0
                });
        map.put('ㄹ', new int[]
                {
                        0, 0,
                        0, 1,
                        0, 0
                });
        map.put('ㅁ', new int[]
                {
                        1, 0,
                        0, 1,
                        0, 0
                });
        map.put('ㅂ', new int[]
                {
                        0, 1,
                        0, 1,
                        0, 0
                });
        map.put('ㅅ', new int[]
                {
                        0, 0,
                        0, 0,
                        0, 1
                });
//        map.put('ㅇ', new int[]
//                {
//                        0, 0,
//                        1, 1,
//                        1, 1
//                });
        map.put('ㅈ', new int[]
                {
                        0, 1,
                        0, 0,
                        1, 1
                });
        map.put('ㅊ', new int[]
                {
                        0, 0,
                        0, 1,
                        0, 1
                });
        map.put('ㅋ', new int[]
                {
                        1, 1,
                        1, 0,
                        0, 0
                });
        map.put('ㅌ', new int[]
                {
                        1, 0,
                        1, 1,
                        0, 0
                });
        map.put('ㅎ', new int[]
                {
                        0, 1,
                        1, 1,
                        0, 0
                });
        // 중성
        map.put('ㅏ', new int[]
                {
                        1, 0,
                        1, 0,
                        0, 1
                });
        map.put('ㅑ', new int[]
                {
                        0, 1,
                        0, 1,
                        1, 0
                });
        map.put('ㅓ', new int[]
                {
                        0, 1,
                        1, 0,
                        1, 0
                });
        map.put('ㅕ', new int[]
                {
                        1, 0,
                        0, 1,
                        0, 1
                });
        map.put('ㅗ', new int[]
                {
                        1, 0,
                        0, 0,
                        1, 1
                });
        map.put('ㅛ', new int[]
                {
                        0, 1,
                        0, 0,
                        1, 1
                });
        map.put('ㅜ', new int[]
                {
                        1, 1,
                        0, 0,
                        1, 0
                });
        map.put('ㅠ', new int[]
                {
                        1, 1,
                        0, 0,
                        0, 1
                });
        map.put('ㅡ', new int[]
                {
                        0, 1,
                        1, 0,
                        0, 1
                });
        map.put('ㅣ', new int[]
                {
                        1, 0,
                        0, 1,
                        1, 0
                });
        map.put('ㅐ', new int[]
                {
                        1, 0,
                        1, 1,
                        1, 0
                });
        map.put('ㅔ', new int[]
                {
                        1, 1,
                        0, 1,
                        1, 0
                });
        map.put('ㅚ', new int[]
                {
                        1, 1,
                        0, 1,
                        1, 1
                });
        map.put('ㅘ', new int[]
                {
                        1, 0,
                        1, 0,
                        1, 1
                });
        map.put('ㅝ', new int[]
                {
                        1, 1,
                        1, 0,
                        1, 0
                });
        map.put('ㅢ', new int[]
                {
                        0, 1,
                        1, 1,
                        0, 1
                });
        map.put('ㅖ', new int[]
                {
                        0, 1,
                        0, 0,
                        1, 0
                });
        // 여기부턴 종성, 종성만 따로 다른 맵에 넣어서 초성과 겹치는 일을 방지
        //     private static  List<Character> 종성 = new ArrayList<>(Arrays.asList(' ', 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ', 'ㄺ', 'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ',
        //            'ㄿ', 'ㅀ', 'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ',
        //            'ㅍ', 'ㅎ'));
        mapJong.put('ㄱ', new int[]
                {
                        1, 0,
                        0, 0,
                        0, 0
                });
        mapJong.put('ㄴ', new int[]
                {
                        0, 0,
                        1, 1,
                        0, 0
                });
        mapJong.put('ㄷ', new int[]
                {
                        0, 0,
                        0, 1,
                        1, 0
                });
        mapJong.put('ㄹ', new int[]
                {
                        0, 0,
                        1, 0,
                        0, 0
                });
        mapJong.put('ㅁ', new int[]
                {
                        0, 0,
                        1, 0,
                        0, 1
                });
        mapJong.put('ㅂ', new int[]
                {
                        1, 0,
                        1, 0,
                        0, 0
                });
        mapJong.put('ㅅ', new int[]
                {
                        0, 0,
                        0, 0,
                        1, 0
                });
        mapJong.put('ㅇ', new int[]
                {
                        0, 0,
                        1, 1,
                        1, 1
                });
        mapJong.put('ㅈ', new int[]
                {
                        1, 0,
                        0, 0,
                        1, 0
                });
        mapJong.put('ㅊ', new int[]
                {
                        0, 0,
                        1, 0,
                        1, 0
                });
        mapJong.put('ㅋ', new int[]
                {
                        0, 0,
                        1, 1,
                        1, 0
                });
        mapJong.put('ㅌ', new int[]
                {
                        0, 0,
                        1, 0,
                        1, 1
                });
        mapJong.put('ㅍ', new int[]
                {
                        0, 0,
                        1, 1,
                        0, 1
                });
        mapJong.put('ㅎ', new int[]
                {
                        0, 0,
                        0, 1,
                        1, 1
                });

        // 알파벳 등록
        map.put('a', new int[]
                {
                        1, 0,
                        0, 0,
                        0, 0
                });
        map.put('b', new int[]
                {
                        1, 0,
                        1, 0,
                        0, 0
                });
        map.put('c', new int[]
                {
                        1, 1,
                        0, 0,
                        0, 0
                });
        map.put('d', new int[]
                {
                        1, 1,
                        0, 1,
                        0, 0
                });
        map.put('e', new int[]
                {
                        1, 0,
                        0, 1,
                        0, 0
                });
        map.put('f', new int[]
                {
                        1, 1,
                        1, 0,
                        0, 0
                });
        map.put('g', new int[]
                {
                        1, 1,
                        1, 1,
                        0, 0
                });
        map.put('h', new int[]
                {
                        1, 0,
                        1, 1,
                        0, 0
                });
        map.put('i', new int[]
                {
                        0, 1,
                        1, 0,
                        0, 0
                });
        map.put('j', new int[]
                {
                        0, 1,
                        1, 1,
                        0, 0
                });
        map.put('k', new int[]
                {
                        1, 0,
                        0, 0,
                        1, 0
                });
        map.put('l', new int[]
                {
                        1, 0,
                        1, 0,
                        1, 0
                });
        map.put('m', new int[]
                {
                        1, 1,
                        0, 0,
                        1, 0
                });
        map.put('n', new int[]
                {
                        1, 1,
                        0, 1,
                        1, 0
                });
        map.put('o', new int[]
                {
                        1, 0,
                        0, 1,
                        1, 0
                });
        map.put('p', new int[]
                {
                        1, 1,
                        1, 0,
                        1, 0
                });
        map.put('q', new int[]
                {
                        1, 1,
                        1, 1,
                        1, 0
                });
        map.put('r', new int[]
                {
                        1, 0,
                        1, 1,
                        1, 0
                });
        map.put('s', new int[]
                {
                        0, 1,
                        1, 0,
                        1, 0
                });
        map.put('t', new int[]
                {
                        0, 1,
                        1, 1,
                        1, 0
                });
        map.put('u', new int[]
                {
                        1, 0,
                        0, 0,
                        1, 1
                });
        map.put('v', new int[]
                {
                        1, 0,
                        1, 0,
                        1, 1
                });
        map.put('w', new int[]
                {
                        0, 1,
                        1, 1,
                        0, 1
                });
        map.put('x', new int[]
                {
                        1, 1,
                        0, 0,
                        1, 1
                });
        map.put('y', new int[]
                {
                        1, 1,
                        0, 1,
                        1, 1
                });
        map.put('z', new int[]
                {
                        1, 0,
                        0, 1,
                        1, 1
                });

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dot);

        Intent intent = getIntent();
        String word = intent.getStringExtra("word");            // 점자로 변환할 단어
        this.language = intent.getStringExtra("language");      // 한글(KOR) / 영어(ENG)

        // 제목 표시
        setTitle(getString(R.string.activity_title_dot));

        // 홈버튼(<-) 표시
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        ViewPager viewPager = findViewById(R.id.viewPager);
        this.txtPagePosition = findViewById(R.id.txtPagePosition);

        // 자모 분리
        final List<String> decomposed = WordClass.decomposeList(word, false);
        Log.d(TAG, decomposed.toString());

        this.fragments = new ArrayList<>();
        this.texts = new ArrayList<>();

        // decomposed 된 단어의 제대로된 길이 구하기
        for(int i = 0; i < decomposed.size(); i++) {
            for(int k = 0; k < decomposed.get(i).length(); k++) {
                char ch = decomposed.get(i).charAt(k);

                Log.d(TAG, "ch: " + ch + ", index: " + i + ", " + k);

                if (k == 0) { // 초성인 경우
                    if(WordClass.getJongDecompose(ch) != null) { // 만약 두 글자가 합쳐진 경우?
                        String decomposedMore = WordClass.getJongDecompose(ch); // 쪼개본다 ㄳ -> ㄱㅅ, ㅃ -> ㅂㅂ
                        if (!TextUtils.isEmpty(decomposedMore)) {
                            if(decomposedMore.charAt(0) == decomposedMore.charAt(1)) { // 된소리인 경우(앞뒤가 똑같음) ㄱ == ㅅ -> false, ㅂ == ㅂ -> true
                                this.fragments.add(createDotFragment("된소리 " + decomposedMore.charAt(0), fortis)); // 된소리 전용 점자 추가
                                Log.d(TAG, "add: fortis");
                            } else { // 된소리가 아닌 경우
                                this.fragments.add(createDotFragment(String.valueOf(decomposedMore.charAt(0)), map.get(decomposedMore.charAt(0)))); // 첫번째 추가
                                Log.d(TAG, "add: " + decomposedMore.charAt(0));
                            }
                            this.fragments.add(createDotFragment(String.valueOf(decomposedMore.charAt(1)), map.get(decomposedMore.charAt(1)))); // 두번째 추가
                            Log.d(TAG, "add: " + decomposedMore.charAt(1));
                        }
                    } else { // 합쳐진거 아니면
                        if('A' <= ch && ch <= 'Z') { // 만약 대문자 알파벳인 경우
                            ch += 32; // 소문자로 변경 아스키코드상 a = 97, A = 65 따라서 32를 더하면 소문자로 바뀜
                        }
                        this.fragments.add(createDotFragment(String.valueOf(ch), map.get(ch))); // 그대로 추가
                        Log.d(TAG, "add: " + ch);
                    }
                } else if(k == 1) { // 중성인 경우
                    if(toSplit.contains(ch)) { // 만약 분해해야하는 모음인 경우
                        String decomposedMore = WordClass.getVowelDecompose(ch); // 쪼갠다
                        if (!TextUtils.isEmpty(decomposedMore)) {
                            this.fragments.add(createDotFragment(String.valueOf(decomposedMore.charAt(0)), map.get(decomposedMore.charAt(0))));
                            this.fragments.add(createDotFragment(String.valueOf(decomposedMore.charAt(1)), map.get(decomposedMore.charAt(1)))); // 각각 추가
                            Log.d(TAG, "add: " + decomposedMore.charAt(0));
                            Log.d(TAG, "add: " + decomposedMore.charAt(1));
                        }
                    } else {
                        this.fragments.add(createDotFragment(String.valueOf(ch), map.get(ch))); // 그대로 추가
                        Log.d(TAG, "add: " + ch);
                    }
                } else if(k == 2) { // 종성인 경우
                    if(WordClass.getJongDecompose(ch) != null) { // 만약 두 글자가 합쳐진 경우?
                        String decomposedMore = WordClass.getJongDecompose(ch); // 쪼개본다
                        if (!TextUtils.isEmpty(decomposedMore)) {
                            if(decomposedMore.charAt(0) == decomposedMore.charAt(1)) { // 된소리인 경우(앞뒤가 똑같음)
                                this.fragments.add(createDotFragment("된소리 " + decomposedMore.charAt(0), fortis)); // 된소리 전용 점자 추가
                                Log.d(TAG, "add: fortis");
                            } else { // 된소리가 아닌 경우
                                this.fragments.add(createDotFragment(String.valueOf(decomposedMore.charAt(0)), mapJong.get(decomposedMore.charAt(0)))); // 첫번째 추가
                                Log.d(TAG, "add: " + decomposedMore.charAt(0));
                            }
                            this.fragments.add(createDotFragment(String.valueOf(decomposedMore.charAt(1)), mapJong.get(decomposedMore.charAt(1)))); // 두번째 추가
                            Log.d(TAG, "add: " + decomposedMore.charAt(1));
                            // 종성이니깐 종성에 맞는 점자를 가져옴
                        }
                    } else { // 합쳐진거 아니면
                        this.fragments.add(createDotFragment(String.valueOf(ch), map.get(ch))); // 그대로 추가
                        Log.d(TAG, "add: " + ch);
                    }
                }
            }
        }

        // 만약 추가한 fragment 가 아무런 내용도 담고 있지 않다면 제거함 (Map 에 해당하는 문자에 대해 점자를 등록하지 않은 경우가 해당한다)
        for (int i=0; i<this.fragments.size(); i++) {
            if (this.fragments.get(i) == null) {
                this.fragments.remove(i);
                i--;
            }
        }

        // pager adapter 설정
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), this.fragments);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);

        // 첫 텍스트를 음성으로
        speechText(this.texts.get(this.pagePosition));

        // 페이지 표시
        displayPage();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // 스크롤이 정지되면
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            // 텍스트를 음성으로
            speechText(this.texts.get(this.pagePosition));

            // 페이지 표시
            displayPage();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        this.pagePosition = position;
    }

    /* 페이지 표시 */
    private void displayPage() {
        // 페이지 표시
        this.txtPagePosition.setText((this.pagePosition + 1) + " / " + this.fragments.size());
    }

    /* 점자 Fragment 생성 */
    private DotFragment createDotFragment(String ch, int[] arr) {
        // DotFragment fragment 를 viewPager 에 추가하는 과정

        if (arr != null) {
            // 텍스트 담기
            this.texts.add(ch);

            boolean[] filled = new boolean[6];
            // 지정한 인덱스에 따라서 점자를 채워낸다
            for (int i=0; i<arr.length; i++) {
                Log.d(TAG, "dot" + (i + 1) + ":" + arr[i]);
                filled[i] = (arr[i] == 1);
            }

            return DotFragment.getInstance(filled, ch);
        } else {
            // 등록되지 않은 문자
            Log.d(TAG, "null 존재");
            //return DotFragment.getInstance(null, ch);
            return null;
        }
    }

    /* 텍스트를 음성으로 */
    public void speechText(String text) {
        speechText(text, false);
    }

    /* 텍스트를 음성으로 */
    public void speechText(final String text, final boolean numeric) {
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(this, getString(R.string.msg_word_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        this.textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int state) {
                if (state == TextToSpeech.SUCCESS) {
                    // 점자 번호이면
                    if (numeric) {
                        // 한글
                        textToSpeech.setLanguage(Locale.KOREAN);
                    } else {
                        if (language.equals(Constants.WordLanguageKind.KOREAN)) {
                            // 한글
                            textToSpeech.setLanguage(Locale.KOREAN);
                        } else {
                            // 영어
                            textToSpeech.setLanguage(Locale.ENGLISH);
                        }
                    }

                    // QUEUE_ADD : 재생 대기열 끝에 새 항목이 추가되는 대기열 모드입니다.
                    // QUEUE_FLUSH : 재생 대기열의 모든 항목 (재생할 미디어 및 합성 할 텍스트)이 삭제되고 새 항목으로 대체되는 대기열 모드입니다. 주어진 호출 앱과 관련하여 큐가 플러시됩니다. 다른 수신자의 대기열에있는 항목은 삭제되지 않습니다.
                    // utteranceId : 이 요청의 고유 식별자입니다.
                    textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null, "id");
                } else {
                    Log.d(TAG, "오류:" + state);
                }
            }
        });
    }
}
