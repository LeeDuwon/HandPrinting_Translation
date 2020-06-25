package com.example.csr.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.csr.databinding.ActivitySelectLanguageBinding;

public class SelectLanguageActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivitySelectLanguageBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectLanguageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("언어 선택");

        binding.btnEnglishToKorean.setOnClickListener(this);
        binding.btnKoreanToEnglish.setOnClickListener(this);


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
        Intent i = new Intent();
        if(view == binding.btnEnglishToKorean){
            i.putExtra("startKo", false);
        }else{
            i.putExtra("startKo", true);
        }
        setResult(RESULT_OK);
        finish();
    }
}
