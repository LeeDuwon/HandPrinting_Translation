package com.example.csr.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.csr.R;
import com.example.csr.entity.WordItem;
import com.example.csr.listener.IAdapterOnClickListener;

import java.util.ArrayList;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {
    private static final String TAG = HistoryAdapter.class.getSimpleName();

    private IAdapterOnClickListener listener;
    private ArrayList<WordItem> items;

    public FavoriteAdapter(IAdapterOnClickListener listener, ArrayList<WordItem> items) {
        this.listener = listener;
        this.items = items;
    }

    /* 삭제 */
    public WordItem remove(int position){
        WordItem data = null;

        if (position < getItemCount()) {
            data = this.items.get(position);
            // 즐겨찾기 삭제
            this.items.remove(position);
            // 삭제된 즐겨찾기를 리스트에 적용하기 위함
            notifyItemRemoved(position);
        }

        return data;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, null);

        // Item 사이즈 조절
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtTranslatedWord.setText(this.items.get(position).word.getTranslatedWord());    // 번역된 단어
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtTranslatedWord;

        public ViewHolder(View view) {
            super(view);

            this.txtTranslatedWord = view.findViewById(R.id.txtTranslatedWord);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // 단어 정보 보기
            Bundle bundle = new Bundle();
            bundle.putInt("position", getAdapterPosition());

            listener.onItemClick(bundle, view.getId());
        }
    }
}
