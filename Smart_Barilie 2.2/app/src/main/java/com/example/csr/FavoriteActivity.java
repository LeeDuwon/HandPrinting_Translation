package com.example.csr;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.example.csr.adapter.FavoriteAdapter;
import com.example.csr.entity.Word;
import com.example.csr.entity.WordItem;
import com.example.csr.listener.IAdapterOnClickListener;
import com.example.csr.util.Constants;
import com.example.csr.util.GlobalVariable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class FavoriteActivity extends AppCompatActivity {
    private static String TAG = FavoriteActivity.class.getSimpleName();

    private LinearLayout layLoading;                // 로딩 레이아웃
    private LinearLayout layNoData;                 // 데이터 없을때 표시할 레이아웃

    private RecyclerView recyclerView;              // 검색기록 리스트 구성할 RecyclerView
    private FavoriteAdapter adapter;                // 검색기록 표시할 Adapter

    private ArrayList<WordItem> items;              // 검색기록 item array

    private int selectedPosition;                   // 리스트 선택 위치

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        // 제목 표시
        setTitle(getString(R.string.activity_title_favorite));

        // 홈버튼(<-) 표시
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // 로딩 레이아웃
        this.layLoading = findViewById(R.id.layLoading);
        ((ProgressBar) findViewById(R.id.progressBar)).setIndeterminateTintList(ColorStateList.valueOf(Color.WHITE));

        this.recyclerView = findViewById(R.id.recyclerView);
        this.recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        this.layNoData = findViewById(R.id.layNoData);

        layLoading.setVisibility(View.VISIBLE);
        // (딜레이 가동)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 즐겨찾기 내역
                listFavorite();
            }
        }, Constants.LoadingDelay.SHORT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.RequestCode.WORD_INFO) {
                // 즐겨찾기 추가 및 제거 후
                if (data != null) {
                    boolean favorite = data.getBooleanExtra("favorite", false);
                    if (!favorite) {
                        // 즐겨찾기 삭제
                        adapter.remove(this.selectedPosition);

                        // 모두 삭제되었으면
                        if (this.items.size() == 0) {
                            this.layNoData.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* 즐겨찾기 내역 */
    private void listFavorite() {
        // 파이어스토어
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // 즐겨찾기 내역 가져오기 (번역된 단어순으로 정렬)
        Query query = db.collection(Constants.FirestoreCollectionName.USER)
                .document(GlobalVariable.documentId).collection(Constants.FirestoreCollectionName.WORD)
                .whereEqualTo("favorite", true)
                .orderBy("translatedWord");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    layLoading.setVisibility(View.GONE);

                    if (task.getResult() != null) {
                        items = new ArrayList<>();

                        // 즐겨찾기
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Word item = document.toObject(Word.class);
                            items.add(new WordItem(document.getId(), item));
                        }

                        if (items.size() == 0) {
                            layNoData.setVisibility(View.VISIBLE);
                        } else {
                            layNoData.setVisibility(View.GONE);
                        }

                        // 리스트에 어뎁터 설정
                        adapter = new FavoriteAdapter(mAdapterListener, items);
                        recyclerView.setAdapter(adapter);
                    }
                } else {
                    // 오류
                    layLoading.setVisibility(View.GONE);
                    Log.d(TAG, "error:" + task.getException().toString());
                }
            }
        });
    }

    private IAdapterOnClickListener mAdapterListener = new IAdapterOnClickListener() {
        @Override
        public void onItemClick(Bundle bundle, int id) {
            // 단어 정보보기
            selectedPosition = bundle.getInt("position");

            Intent intent = new Intent(FavoriteActivity.this, WordInfoActivity.class);
            intent.putExtra("word_item", items.get(selectedPosition));
            startActivityForResult(intent, Constants.RequestCode.WORD_INFO);
        }
    };
}
