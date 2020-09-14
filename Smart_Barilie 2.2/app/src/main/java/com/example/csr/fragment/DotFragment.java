package com.example.csr.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.csr.DotActivity;
import com.example.csr.R;

public class DotFragment extends Fragment {
    private static String TAG = DotFragment.class.getSimpleName();

    private TextToSpeech textToSpeech;            // 텍스트를 음성으로 변환 객체

    // // n번째 값과 각 점자에 대한 인덱스를 매칭해줌 (아이콘)
    private int[] arr = {
            R.id.imgDot1,
            R.id.imgDot2,
            R.id.imgDot3,
            R.id.imgDot4,
            R.id.imgDot5,
            R.id.imgDot6
    };

    // // n번째 값과 각 점자에 대한 인덱스를 매칭해줌 (번호)
    private int[] arrText = {
            R.id.txtDot1,
            R.id.txtDot2,
            R.id.txtDot3,
            R.id.txtDot4,
            R.id.txtDot5,
            R.id.txtDot6
    };

    private boolean[] filled;           // 해당 점이 채워졌는가 true 면 채워짐

    private String dotText;             // 점자로 나타내는 텍스트

    public static DotFragment getInstance(boolean[] filled, String dotText) {
        DotFragment fragment = new DotFragment();

        Bundle bundle = new Bundle();
        bundle.putBooleanArray("filled", filled);
        bundle.putString("dot_text", dotText);

        // Argument 에 값 설정
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Argument 에서 값 얻기
        Bundle bundle = getArguments();

        if (bundle != null) {
            this.filled = bundle.getBooleanArray("filled");
            this.dotText = bundle.getString("dot_text");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dot, container, false);

        Button btnDot = view.findViewById(R.id.btnDot);
        btnDot.setText(this.dotText);
        btnDot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 음성으로 듣기
                ((DotActivity) getActivity()).speechText(dotText);
            }
        });

        if (filled != null) {
            // 진동 서비스
            final Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);

            for (int i=0; i<this.arr.length; i++) {
                final int number = i + 1;       // 점자 번호

                ImageView imgDot = view.findViewById(this.arr[i]);
                TextView txtDot = view.findViewById(this.arrText[i]);
                txtDot.setText(String.valueOf(number));

                final long milliseconds;            // 진동 ms

                if (filled[i]) {
                    // 만약 점자가 채워져있는 상태에서 터치시 500ms간 진동
                    milliseconds = 500;
                    imgDot.setImageResource(R.drawable.circle_full);
                    txtDot.setTextColor(Color.WHITE);
                } else {
                    milliseconds = 100;
                    imgDot.setImageResource(R.drawable.circle_empty);
                    txtDot.setTextColor(Color.BLACK);
                }
                imgDot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (vibrator != null) {
                            // 진동
                            vibrator.vibrate(milliseconds);
                        }

                        // 점자번호를 음성으로 듣기
                        ((DotActivity) getActivity()).speechText(String.valueOf(number), true);
                    }
                });
            }
        } else {
            // 등록되지 않은 문자 (점자로 변환 못함)
            view.findViewById(R.id.layDot).setVisibility(View.INVISIBLE);
        }

        return view;
    }

}
