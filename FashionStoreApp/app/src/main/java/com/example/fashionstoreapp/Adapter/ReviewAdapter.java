package com.example.fashionstoreapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionstoreapp.Model.Rating;
import com.example.fashionstoreapp.R;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private List<Rating> reviews;

    public ReviewAdapter(List<Rating> reviews) {
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Rating rating = reviews.get(position);
        holder.tvReviewerName.setText(rating.getUserId());
        holder.rbReviewRating.setRating(rating.getRate());
        holder.tvReviewDescription.setText(rating.getDescription());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView tvReviewerName, tvReviewDescription;
        RatingBar rbReviewRating;

        ReviewViewHolder(View itemView) {
            super(itemView);
            tvReviewerName = itemView.findViewById(R.id.tvReviewerName);
            rbReviewRating = itemView.findViewById(R.id.rbRating);
            tvReviewDescription = itemView.findViewById(R.id.tvReviewComment);
        }
    }
}