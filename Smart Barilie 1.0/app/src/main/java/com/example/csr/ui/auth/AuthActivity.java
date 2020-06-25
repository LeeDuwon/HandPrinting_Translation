package com.example.csr.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.csr.databinding.ActivityAuthBinding;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityAuthBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView( binding.getRoot());
        getSupportActionBar().hide();

        binding.btnLogin.setOnClickListener(this);
        binding.btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==binding.btnLogin){
            startActivity(new Intent(AuthActivity.this, SelectLoginOrFingerprintActivity.class));
        }else{
            startActivity(new Intent(AuthActivity.this, RegisterActivity.class));
        }
    }
}
