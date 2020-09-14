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

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private static final String TAG = HistoryAdapter.class.getSimpleName();

    private IAdapterOnClickListener listener;
    private ArrayList<WordItem> items;

    public HistoryAdapter(IAdapterOnClickListener listener, ArrayList<WordItem> items) {
        this.listener = listener;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, null);

        // Item 사이즈 조절
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtTranslatedWord.setText(this.items.get(position).word.getTranslatedWord());    // 번역된 단어

        if (this.items.get(position).word.isFavorite()) {
            // 즐겨찾기에 추가됨
            holder.imgFavorite.setVisibility(View.VISIBLE);
        } else {
            holder.imgFavorite.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView imgFavorite;
        public TextView txtTranslatedWord;

        public ViewHolder(View view) {
            super(view);

            this.imgFavorite = view.findViewById(R.id.imgFavorite);
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
