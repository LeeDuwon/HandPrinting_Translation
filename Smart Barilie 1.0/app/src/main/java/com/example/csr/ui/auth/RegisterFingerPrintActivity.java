package com.example.csr.ui.auth;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.csr.R;
import com.example.csr.databinding.ActivityRegisterFingerprintBinding;

public class RegisterFingerPrintActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityRegisterFingerprintBinding binding;
    private static final int REGISTER_FINGER_PRINT_CANCEL=4444;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterFingerprintBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnCancel.setOnClickListener(this);

        binding.imageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnCancel) {
          Intent i = new Intent(this, FingerPrintAlertActivity.class);
          startActivityForResult(i, REGISTER_FINGER_PRINT_CANCEL);


        }else if(v ==binding.imageView){
            Toast.makeText(RegisterFingerPrintActivity.this, "회원가입 완료! 첫 화면으로 돌아갑니다.", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode == REGISTER_FINGER_PRINT_CANCEL){
                Toast.makeText(RegisterFingerPrintActivity.this, "회원가입 완료! 첫 화면으로 돌아갑니다.", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }
        }
    }
}
