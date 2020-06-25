package com.example.csr.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.csr.databinding.ActivitySearchHistoryOrTranslateBinding;

public class SearchHistoryOrTranslateActivity extends AppCompatActivity implements View.OnClickListener {
    ActivitySearchHistoryOrTranslateBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchHistoryOrTranslateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //setTitle("");
        binding.btnSearchHistory.setOnClickListener(this);
        binding.btnTranslate.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        if(v == binding.btnSearchHistory){
            startActivity(new Intent(SearchHistoryOrTranslateActivity.this, FavoriteOrSearchHistoryActivity.class));
        }else{
            startActivity(new Intent(SearchHistoryOrTranslateActivity.this, MainActivity.class));
        }
    }
}
