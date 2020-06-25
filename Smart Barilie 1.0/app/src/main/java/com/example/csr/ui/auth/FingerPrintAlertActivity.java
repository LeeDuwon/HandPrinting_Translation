package com.example.csr.ui.auth;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.csr.databinding.ActivityFingerprintAlertBinding;

public class FingerPrintAlertActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityFingerprintAlertBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityFingerprintAlertBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle("경고");

        binding.btnCancel.setOnClickListener(this);
        binding.btnConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == binding.btnCancel) {
            setResult(RESULT_CANCELED);
        }else{
            setResult(RESULT_OK);
        }
        finish();
    }
}
