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
import com.example.fashionstoreapp.Model.Cart;
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

public class CheckOutAdapter extends RecyclerView.Adapter<CheckOutAdapter.ViewHolder> {
    private final List<Cart> listCart;
    private final Context context;
    private final ProductImageAPI productImageAPI;

    public CheckOutAdapter(List<Cart> listCart, Context context) {
        this.listCart = (listCart != null) ? new ArrayList<>(listCart) : new ArrayList<>();
        this.context = context;
        this.productImageAPI = new RetrofitService().getRetrofit().create(ProductImageAPI.class);
    }

    @NonNull
    @Override
    public CheckOutAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_checkout_item, parent, false);
        return new CheckOutAdapter.ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckOutAdapter.ViewHolder holder, int position) {
        Cart cart = listCart.get(position);

        if (cart == null || cart.getProduct() == null) {
            holder.tvProductName.setText("Sản phẩm không xác định");
            holder.tvProductPrice.setText("0 ₫");
            holder.tvCount.setText("0");
            holder.tvTotalPrice.setText("0 ₫");
            Log.e("CheckOutAdapter", "Cart or product is null at position: " + position);
            return;
        }

        Locale localeVN = new Locale("vi", "VN");
        NumberFormat formatter = NumberFormat.getInstance(localeVN);

        // Tên sản phẩm
        String productName = cart.getProduct().getProductName();
        holder.tvProductName.setText(productName != null ? productName : "Sản phẩm không xác định");

        // Giá sản phẩm và tổng giá
        holder.tvProductPrice.setText(formatter.format(cart.getProduct().getPrice()) + " ₫");
        holder.tvCount.setText(String.valueOf(cart.getCount()));
        holder.tvTotalPrice.setText(formatter.format(cart.getCount() * cart.getProduct().getPrice()) + " ₫");

        // Tải hình ảnh sản phẩm
        holder.ivProductImage.setImageDrawable(null); // Xóa hình cũ
        productImageAPI.getImageByProduct(cart.getProduct().getId()).enqueue(new Callback<List<ProductImage>>() {
            @Override
            public void onResponse(Call<List<ProductImage>> call, Response<List<ProductImage>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    String imageUrl = response.body().get(0).getImageUrl();
                    Glide.with(context)
                            .load(imageUrl)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(holder.ivProductImage);
                } else {
                    Log.w("CheckOutAdapter", "No images for product ID: " + cart.getProduct().getId());
                }
            }

            @Override
            public void onFailure(Call<List<ProductImage>> call, Throwable t) {
                Log.e("CheckOutAdapter", "Failed to load image for product ID: " + cart.getProduct().getId() + ": " + t.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return listCart.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvProductPrice, tvCount, tvTotalPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvCount = itemView.findViewById(R.id.tvCount);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
        }
    }
}