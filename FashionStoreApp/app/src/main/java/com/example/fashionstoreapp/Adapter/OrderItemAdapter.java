package com.example.fashionstoreapp.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.fashionstoreapp.Model.Order_Item;
import com.example.fashionstoreapp.Model.ProductImage;
import com.example.fashionstoreapp.R;
import com.example.fashionstoreapp.Retrofit.ProductImageAPI;
import com.example.fashionstoreapp.Retrofit.RetrofitService;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder> {

    private final List<Order_Item> orderItems;
    private final Context context;
    private final ProductImageAPI productImageAPI;

    public OrderItemAdapter(List<Order_Item> orderItems, Context context) {
        this.orderItems = (orderItems != null) ? new ArrayList<>(orderItems) : new ArrayList<>();
        this.context = context;
        this.productImageAPI = new RetrofitService().getRetrofit().create(ProductImageAPI.class);
        Log.d("OrderItemAdapter", "Initialized with " + this.orderItems.size() + " items");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_order_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order_Item orderItem = orderItems.get(position);

        if (orderItem == null || orderItem.getProduct() == null) {
            holder.tvProductName.setText("Sản phẩm không xác định");
            holder.tvUnits.setText("0");
            holder.tvPrice.setText("0 ₫");
            holder.tvTotalPrice.setText("0 ₫");
            Log.e("OrderItemAdapter", "OrderItem or product is null at position: " + position);
            return;
        }

        // Lấy thông tin sản phẩm
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat formatter = NumberFormat.getInstance(localeVN);

        String productName = orderItem.getProduct().getProductName();
        holder.tvProductName.setText(productName != null ? productName : "Sản phẩm không xác định");
        holder.tvUnits.setText(String.valueOf(orderItem.getCount()));
        double price = orderItem.getProduct().getPrice();
        holder.tvPrice.setText(formatter.format(price) + " ₫");
        double total = price * orderItem.getCount();
        holder.tvTotalPrice.setText(formatter.format(total) + " ₫");

        // Tải hình ảnh sản phẩm
        holder.ivProductImage.setImageDrawable(null); // Xóa hình cũ
        int productId = orderItem.getProduct().getId();
        Log.d("OrderItemAdapter", "Calling getImageByProduct for product ID: " + productId);
        productImageAPI.getImageByProduct(productId).enqueue(new Callback<List<ProductImage>>() {
            @Override
            public void onResponse(Call<List<ProductImage>> call, Response<List<ProductImage>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    String imageUrl = response.body().get(0).getImageUrl();
                    Log.d("OrderItemAdapter", "Loaded image URL: " + imageUrl + " for product ID: " + productId);
                    Glide.with(context)
                            .load(imageUrl)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(holder.ivProductImage);
                } else {
                    Log.w("OrderItemAdapter", "No images for product ID: " + productId);
                }
            }

            @Override
            public void onFailure(Call<List<ProductImage>> call, Throwable t) {
                Log.e("OrderItemAdapter", "Failed to load image for product ID: " + productId + ": " + t.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvUnits, tvPrice, tvTotalPrice;
        ImageView ivProductImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvUnits = itemView.findViewById(R.id.tvUnits);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
        }
    }
}