package com.example.csr;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.csr.util.Constants;
import com.example.csr.util.GlobalVariable;
import com.example.csr.util.SharedPreferencesUtils;

public class MainActivity extends AppCompatActivity {
    private static String TAG = MainActivity.class.getSimpleName();

    private BackPressHandler backPressHandler;      // 백키(종료) 핸들러

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 제목 표시
        setTitle(getString(R.string.activity_title_main));

        // 회원정보
        ((TextView) findViewById(R.id.txtName)).setText(GlobalVariable.user.getName());
        ((TextView) findViewById(R.id.txtPhone)).setText(GlobalVariable.user.getPhone());

        findViewById(R.id.layFavorite).setOnClickListener(mClickListener);
        findViewById(R.id.layHistory).setOnClickListener(mClickListener);
        findViewById(R.id.layVoice).setOnClickListener(mClickListener);
        findViewById(R.id.layPhoto).setOnClickListener(mClickListener);

        // 종료 핸들러
        this.backPressHandler = new BackPressHandler(this);
    }

    @Override
    public void onBackPressed() {
        this.backPressHandler.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // main 메뉴 생성
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                // 로그아웃
                new AlertDialog.Builder(this)
                        .setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(@NonNull DialogInterface dialog, int which) {
                                // 로그아웃
                                logout();
                            }
                        })
                        .setNegativeButton(getString(R.string.dialog_cancel), null)
                        .setCancelable(false)
                        .setTitle(getString(R.string.dialog_title_logout))
                        .setMessage(getString(R.string.dialog_msg_logout))
                        .show();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* 로그아웃 */
    private void logout() {
        // Document Id 값 clear
        SharedPreferencesUtils.getInstance(this)
                .put(Constants.SharedPreferencesName.USER_DOCUMENT_ID, "");

        // 로그인화면으로 이동
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /* 종료 */
    public void end() {
        moveTaskToBack(true);
        finish();
        // 프로세스까지 강제 종료
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent;

            switch (view.getId()) {
                case R.id.layFavorite:
                    // 즐거찾기

                    break;
                case R.id.layHistory:
                    // 검색기록

                    break;
                case R.id.layVoice:
                    // 음성녹음
                    intent = new Intent(MainActivity.this, VoiceTranslationActivity.class);
                    startActivity(intent);
                    break;
                case R.id.layPhoto:
                    // 사진촬영
                    intent = new Intent(MainActivity.this, PhotoTranslationActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };

    /* Back Press Class */
    private class BackPressHandler {
        private Context context;
        private Toast toast;

        private final long FINISH_INTERVAL_TIME = 2000;
        private long backPressedTime = 0;

        public BackPressHandler(Context context) {
            this.context = context;
        }

        public void onBackPressed() {
            if (System.currentTimeMillis() > this.backPressedTime + FINISH_INTERVAL_TIME) {
                this.backPressedTime = System.currentTimeMillis();

                this.toast = Toast.makeText(this.context, R.string.msg_back_press_end, Toast.LENGTH_SHORT);
                this.toast.show();

                return;
            }

            if (System.currentTimeMillis() <= this.backPressedTime + FINISH_INTERVAL_TIME) {
                // 종료
                end();
                this.toast.cancel();
            }
        }
    }
}