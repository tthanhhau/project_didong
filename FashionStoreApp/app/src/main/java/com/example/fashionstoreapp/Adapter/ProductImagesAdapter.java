package com.example.fashionstoreapp.Adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fashionstoreapp.R;

import java.util.List;

public class ProductImagesAdapter extends RecyclerView.Adapter<ProductImagesAdapter.ImageViewHolder> {
    private List<Uri> imageUris;
    private OnImageRemoveListener removeListener;

    public interface OnImageRemoveListener {
        void onImageRemove(int position);
    }

    public ProductImagesAdapter(List<Uri> imageUris, OnImageRemoveListener listener) {
        this.imageUris = imageUris;
        this.removeListener = listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Uri imageUri = imageUris.get(position);
        Glide.with(holder.imageView.getContext())
                .load(imageUri)
                .thumbnail(0.25f)
                .override(100, 100)
                .into(holder.imageView);

        holder.removeButton.setOnClickListener(v -> removeListener.onImageRemove(position));
    }

    @Override
    public int getItemCount() {
        return imageUris.size();
    }

    public void clear() {
        imageUris.clear();
        notifyDataSetChanged();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton removeButton;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            removeButton = itemView.findViewById(R.id.removeImageButton);
        }
    }
}