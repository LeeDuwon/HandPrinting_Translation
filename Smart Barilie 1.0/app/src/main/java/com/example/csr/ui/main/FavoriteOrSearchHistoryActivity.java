package com.example.csr.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.csr.databinding.ActivityFavoriteOrSearchBinding;
import com.example.csr.ui.main.favorite.FavoriteActivity;
import com.example.csr.ui.main.search_history.SearchHistoryActivity;

public class FavoriteOrSearchHistoryActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityFavoriteOrSearchBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoriteOrSearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("검색 기록");

        binding.btnFavorite.setOnClickListener(this);
        binding.btnSearchHistory.setOnClickListener(this);

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
    public void onClick(View v) {
        if(v == binding.btnFavorite){
            startActivity(new Intent(FavoriteOrSearchHistoryActivity.this, FavoriteActivity.class));
        }else{
            startActivity(new Intent(FavoriteOrSearchHistoryActivity.this, SearchHistoryActivity.class));
        }
    }
}