package com.example.csr.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.csr.R;
import com.example.csr.ui.auth.AuthActivity;
import com.example.csr.ui.main.MainActivity;

public class IntroSplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_splash);
        getSupportActionBar().hide();

        new Handler().postDelayed(() -> {
                    startActivity(new Intent(IntroSplashActivity.this, AuthActivity.class));
                    finish();
                }
                , 2000);
    }
}
