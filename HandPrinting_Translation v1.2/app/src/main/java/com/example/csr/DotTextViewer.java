package com.example.csr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.csr.utils.WordClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class DotTextViewer extends AppCompatActivity {

    private static HashMap<Character, int[]> map = new HashMap<>();
    private static HashMap<Character, int[]> mapJong = new HashMap<>();

    private static int[] fortis = new int[]
            {
                    0, 0,
                    0, 0,
                    0, 1
            };

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

    }

    private List<String> decomposed = new ArrayList<>();
    private List<DotFragment> fragments = new ArrayList<>();

    private ViewPager viewPager;
    private DotPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dot_text_viewer);

        Intent intent = getIntent();

        // 자모 분리
        decomposed = WordClass.decomposeList(intent.getStringExtra("str"), false);

        Log.d("handprinting", decomposed.toString());

        // decomposed된 단어의 제대로된 길이 구하기
        for(int i = 0; i < decomposed.size(); i++) {
            for(int k = 0; k < decomposed.get(i).length(); k++) {
                char ch = decomposed.get(i).charAt(k);

                Log.d("handprinting", "ch: " + ch + ", index: " + i + ", " + k);

                if(k == 0) { // 초성인 경우
                     if(WordClass.getJongDecompose(ch) != null) { // 만약 두 글자가 합쳐진 경우?
                         String decomposedMore = WordClass.getJongDecompose(ch); // 쪼개본다
                         if(decomposedMore.charAt(0) == decomposedMore.charAt(1)) { // 된소리인 경우(앞뒤가 똑같음)
                             fragments.add(createDotFragment(fortis)); // 된소리 전용 점자 추가
                             Log.d("handprinting", "add: fortis");
                         } else { // 된소리가 아닌 경우
                             fragments.add(createDotFragment(map.get(decomposedMore.charAt(0)))); // 첫번째 추가
                             Log.d("handprinting", "add: " + decomposedMore.charAt(0));
                         }
                         fragments.add(createDotFragment(map.get(decomposedMore.charAt(1)))); // 두번째 추가
                         Log.d("handprinting", "add: " + decomposedMore.charAt(1));
                     } else { // 합쳐진거 아니면
                         fragments.add(createDotFragment(map.get(ch))); // 그대로 추가
                         Log.d("handprinting", "add: " + ch);
                     }
                } else if(k == 1) {
                    if(toSplit.contains(ch)) { // 만약 분해해야하는 모음인 경우
                        String decomposedMore = WordClass.getVowelDecompose(ch); // 쪼갠다
                        fragments.add(createDotFragment(map.get(decomposedMore.charAt(0))));
                        fragments.add(createDotFragment(map.get(decomposedMore.charAt(1)))); // 각각 추가
                        Log.d("handprinting", "add: " + decomposedMore.charAt(0));
                        Log.d("handprinting", "add: " + decomposedMore.charAt(1));
                    } else {
                        fragments.add(createDotFragment(map.get(ch))); // 그대로 추가
                        Log.d("handprinting", "add: " + ch);
                    }
                } else if(k == 2) {
                    if(WordClass.getJongDecompose(ch) != null) { // 만약 두 글자가 합쳐진 경우?
                        String decomposedMore = WordClass.getJongDecompose(ch); // 쪼개본다
                        if(decomposedMore.charAt(0) == decomposedMore.charAt(1)) { // 된소리인 경우(앞뒤가 똑같음)
                            fragments.add(createDotFragment(fortis)); // 된소리 전용 점자 추가
                            Log.d("handprinting", "add: fortis");
                        } else { // 된소리가 아닌 경우
                            fragments.add(createDotFragment(mapJong.get(decomposedMore.charAt(0)))); // 첫번째 추가
                            Log.d("handprinting", "add: " + decomposedMore.charAt(0));
                        }
                        fragments.add(createDotFragment(mapJong.get(decomposedMore.charAt(1)))); // 두번째 추가
                        Log.d("handprinting", "add: " + decomposedMore.charAt(1));
                        // 종성이니깐 종성에 맞는 점자를 가져옴
                    } else { // 합쳐진거 아니면
                        fragments.add(createDotFragment(map.get(ch))); // 그대로 추가
                        Log.d("handprinting", "add: " + ch);
                    }
                }
            }
        }

        // ViewPager를 가져와서 adapter를 직접 만든 어댑터로 설정
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        pagerAdapter = new DotPagerAdapter();
        viewPager.setAdapter(pagerAdapter);
    }

    public class DotPagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            DotFragment fragment = fragments.get(position);
            container.addView(fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // 뷰페이저에서 삭제.
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            // 전체 페이지 수는 decomposed된 문자열 길이로
            return fragments.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return (view == (View)object);
        }
    }

    private DotFragment createDotFragment(int[] arr) {
        // DotFragment fragment를 view에 추가하는 과정
        DotFragment fragment = new DotFragment(getApplicationContext());
        // 뷰페이저에 추가.

        if (arr != null) {
            // 지정한 인덱스에 따라서 점자를 채워낸다
            for (int i = 0; i < 6; i++) {
                fragment.setFill(i, arr[i] == 1);
            }
        } else { // 등록되지 않은 문자가 나오면 ViewPager에 점자를 추가하지 않고 로그만 띄움
            Log.d("handprinting", "null 존재");
        }

        return fragment;
    }
}
