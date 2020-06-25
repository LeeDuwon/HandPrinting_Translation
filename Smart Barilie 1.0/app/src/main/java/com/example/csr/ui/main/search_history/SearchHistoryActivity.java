package com.example.csr.ui.main.search_history;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.csr.databinding.ActivityFavoriteAndSearchHistoryBinding;
import com.example.csr.ui.main.DotTextViewer;
import com.example.csr.ui.main.TranslateViewPagerAdapter;
import com.example.csr.model.StringValues;

import java.util.ArrayList;
import java.util.List;

public class SearchHistoryActivity extends AppCompatActivity {
    private ActivityFavoriteAndSearchHistoryBinding binding;
    private TranslateViewPagerAdapter translateViewPagerAdapter;
    private List<StringValues> translatedWordList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoriteAndSearchHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("즐겨찾기");

        translatedWordList = new ArrayList<>();

        translateViewPagerAdapter = new TranslateViewPagerAdapter(translatedWordList, this::onChangeToBrailleClicked);
        binding.viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        binding.viewPager2.setAdapter(translateViewPagerAdapter);

        fetchDataFromServer();
    }

    /** 네트워크 통신으로 데이터 가져오기 **/
    private void fetchDataFromServer(){
        //sample data
        translatedWordList.add(new StringValues("hello", "안녕"));
        translatedWordList.add(new StringValues("world", "세계"));
        translatedWordList.add(new StringValues("android", "안드로이드"));
        translatedWordList.add(new StringValues("google", "구글"));
        translateViewPagerAdapter.notifyDataSetChanged();
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

    private void onChangeToBrailleClicked(String stringValue){
        Intent intent = new Intent(getApplicationContext(), DotTextViewer.class);
        intent.putExtra("str", stringValue);
        startActivity(intent);
    }
}