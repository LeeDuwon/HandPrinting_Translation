package com.example.csr.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.csr.R;
import com.example.csr.databinding.ActivityRecognizeFingerprintBinding;
import com.example.csr.databinding.ActivityRegisterBinding;
import com.example.csr.ui.main.SearchHistoryOrTranslateActivity;

public class RecognizeFingerPrintActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityRecognizeFingerprintBinding binding;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecognizeFingerprintBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("지문인식");

        binding.imageView.setOnClickListener(this);

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
        if(v==binding.imageView){
            Intent i = new Intent(RecognizeFingerPrintActivity.this, SearchHistoryOrTranslateActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }
    }
}
