package com.example.csr.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.csr.databinding.ActivitySelectLoginOrFingerprintBinding;

public class SelectLoginOrFingerprintActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivitySelectLoginOrFingerprintBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectLoginOrFingerprintBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("로그인 방법 선택");

        binding.btnLoginWithId.setOnClickListener(this);
        binding.btnLoginWithFingerprint.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if(view == binding.btnLoginWithId){
            startActivity(new Intent(this, LoginActivity.class));
        }else{
            startActivity(new Intent(this, RecognizeFingerPrintActivity.class));
        }
    }
}
