package com.example.csr;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import static android.os.Build.VERSION.SDK_INT;

public class DotFragment extends LinearLayout {
    private View view;

    private static int[] arr = {
            R.id.dot_circle_1,
            R.id.dot_circle_2,
            R.id.dot_circle_3,
            R.id.dot_circle_4,
            R.id.dot_circle_5,
            R.id.dot_circle_6
    };

    private boolean[] filled = new boolean[] {
            false, false, false, false, false, false
    };

    public DotFragment(Context context) {
        super(context);
        init(context);
    }

    public DotFragment(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void setFill(int index, boolean fill) {
        View circle = view.findViewById(arr[index]);
        if(fill) {
            circle.setBackground(getDrawable(getResources(), R.drawable.circle_full, null));
        } else {
            circle.setBackground(getDrawable(getResources(), R.drawable.circle_empty, null));
        }
        filled[index] = fill;
    }

    public DotFragment(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.fragment_dot, this);

        if(view == null) {
            Log.d("handprinting", "view is null");
        }

        final Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);

        for(int i = 0; i < 6; i++) {
            final int index = i;
            view.findViewById(arr[i]).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(filled[index]) {
                        vibrator.vibrate(1000);
                    }
                }
            });
        }
    }

    @Nullable
    @SuppressWarnings("deprecation")
    public static Drawable getDrawable(@NonNull Resources res, @DrawableRes int id,
                                       @Nullable Resources.Theme theme) throws Resources.NotFoundException {
        if (SDK_INT >= 21) {
            return ResourcesCompat.getDrawable(res, id, theme);
        } else {
            return res.getDrawable(id);
        }
    }
}
