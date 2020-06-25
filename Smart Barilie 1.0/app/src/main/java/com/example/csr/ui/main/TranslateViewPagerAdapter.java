package com.example.csr.ui.main;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.csr.databinding.ItemLayoutRecyclerviewBinding;
import com.example.csr.model.StringValues;

import java.util.List;


public class TranslateViewPagerAdapter extends RecyclerView.Adapter<TranslateViewPagerAdapter.FavoriteViewHolder> {
    private List<StringValues> favoriteStringList;
    private ChangeToBraileClickListener changeToBraileClickListener;

    public TranslateViewPagerAdapter(List<StringValues> favoriteStringList, ChangeToBraileClickListener changeToBraileClickListener) {
        this.favoriteStringList = favoriteStringList;
        this.changeToBraileClickListener = changeToBraileClickListener;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLayoutRecyclerviewBinding binding = ItemLayoutRecyclerviewBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new FavoriteViewHolder(binding, changeToBraileClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        holder.bind(favoriteStringList.get(position));
    }

    @Override
    public int getItemCount() {
        return favoriteStringList.size();
    }

    static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        private ItemLayoutRecyclerviewBinding binding;
        private ChangeToBraileClickListener changeToBraileClickListener;

        FavoriteViewHolder(ItemLayoutRecyclerviewBinding binding, ChangeToBraileClickListener changeToBraileClickListener) {
            super(binding.getRoot());
            this.binding = binding;
            this.changeToBraileClickListener = changeToBraileClickListener;
        }

        void bind(StringValues stringValue) {
            binding.tvPreTranslate.setText(stringValue.getBeforeTranslation());
            binding.tvAfterTranslate.setText(stringValue.getAfterTranslation());
            this.binding.btnChangeToBraille.setOnClickListener(v -> changeToBraileClickListener.onChangeToBraileClicked(stringValue.getAfterTranslation()));
        }
    }
}
