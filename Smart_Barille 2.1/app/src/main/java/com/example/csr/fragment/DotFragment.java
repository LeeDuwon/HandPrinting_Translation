package com.example.csr.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.csr.R;

public class DotFragment extends Fragment {
    private static String TAG = DotFragment.class.getSimpleName();

    // // n번째 값과 각 점자에 대한 인덱스를 매칭해줌
    private static int[] arr = {
            R.id.imgDot1,
            R.id.imgDot2,
            R.id.imgDot3,
            R.id.imgDot4,
            R.id.imgDot5,
            R.id.imgDot6
    };

    private boolean[] filled;           // 해당 점이 채워졌는가 true 면 채워짐

    public static DotFragment getInstance(boolean[] filled) {
        DotFragment fragment = new DotFragment();

        Bundle bundle = new Bundle();
        bundle.putBooleanArray("filled", filled);

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
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
