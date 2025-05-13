package com.example.fashionstoreapp.Adapter;

import static java.lang.Integer.parseInt;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fashionstoreapp.Activity.ShowDetailActivity;
import com.example.fashionstoreapp.Interface.CartItemInterface;
import com.example.fashionstoreapp.Model.Cart;
import com.example.fashionstoreapp.Model.User;
import com.example.fashionstoreapp.R;
import com.example.fashionstoreapp.Retrofit.CartAPI;
import com.example.fashionstoreapp.Somethings.ObjectSharedPreferences;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    final CartItemInterface cartItemInterface;
    List<Cart> carts;
    Context context;

    public CartAdapter(CartItemInterface cartItemInterface, List<Cart> carts, Context context) {
        this.cartItemInterface = cartItemInterface;
        this.carts = carts;
        this.context = context;
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_cart, parent, false);
        return new CartAdapter.ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        if (carts == null || carts.isEmpty()) {
            return; // Thoát nếu danh sách carts null hoặc rỗng
        }

        Cart cart = carts.get(position);
        if (cart == null || cart.getProduct() == null) {
            return; // Thoát nếu cart hoặc product null
        }

        User user = ObjectSharedPreferences.getSavedObjectFromPreference(context, "User", "MODE_PRIVATE", User.class);
        if (user == null) {
            Log.e("CartAdapter", "User is null");
            return; // Thoát nếu user null
        }

        // Thiết lập các giá trị giao diện
        holder.tvCount.setText(String.valueOf(cart.getCount()));
        holder.tvProductName.setText(cart.getProduct().getProductName());
        Locale localeEN = new Locale("en", "EN");
        NumberFormat en = NumberFormat.getInstance(localeEN);
        holder.tvPrice.setText(en.format(cart.getProduct().getPrice()));
        holder.tvTotalPrice.setText(en.format(cart.getProduct().getPrice() * cart.getCount()));

        // Kiểm tra và tải hình ảnh sản phẩm
        if (cart.getProduct().getProductImages() != null && !cart.getProduct().getProductImages().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(cart.getProduct().getProductImages().get(0).getImageUrl())
                    .into(holder.ivImage);
        }

        // Sự kiện xóa sản phẩm
        holder.layout_delete.setOnClickListener(v -> {
            String totalPriceText = holder.tvTotalPrice.getText().toString().replace(",", "");
            try {
                int price = parseInt(totalPriceText) * (-1);
                CartAPI.cartAPI.deleteCart(cart.getId(), user.getId()).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()) {
                            cartItemInterface.onClickUpdatePrice(price);
                            notifyItemRemoved(holder.getAdapterPosition());
                            carts.remove(cart);
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.e("CartAdapter", "Delete cart failed: " + t.getMessage());
                    }
                });
            } catch (NumberFormatException e) {
                Log.e("CartAdapter", "Invalid total price format: " + totalPriceText);
            }
        });

        // Sự kiện nhấn vào hình ảnh sản phẩm
        holder.ivImage.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), ShowDetailActivity.class);
            intent.putExtra("product", cart.getProduct());
            holder.itemView.getContext().startActivity(intent);
        });

        // Sự kiện tăng số lượng
        holder.ivPlus.setOnClickListener(v -> {
            if (cart.getCount() < cart.getProduct().getQuantity()) {
                cart.setCount(cart.getCount() + 1);
                CartAPI.cartAPI.addToCart(user.getId(), cart.getProduct().getId(), 1).enqueue(new Callback<Cart>() {
                    @Override
                    public void onResponse(Call<Cart> call, Response<Cart> response) {
                        if (response.isSuccessful()) {
                            double price = cart.getProduct().getPrice();
                            holder.tvCount.setText(String.valueOf(parseInt(holder.tvCount.getText().toString()) + 1));
                            holder.tvTotalPrice.setText(en.format(price * parseInt(holder.tvCount.getText().toString())));
                            cartItemInterface.onClickUpdatePrice(price);
                        }
                    }

                    @Override
                    public void onFailure(Call<Cart> call, Throwable t) {
                        Log.e("CartAdapter", "Update cart failed: " + t.getMessage());
                    }
                });
            }
        });

        // Sự kiện giảm số lượng
        holder.ivMinus.setOnClickListener(v -> {
            if (cart.getCount() > 1) {
                cart.setCount(cart.getCount() - 1);
                CartAPI.cartAPI.addToCart(user.getId(), cart.getProduct().getId(), -1).enqueue(new Callback<Cart>() {
                    @Override
                    public void onResponse(Call<Cart> call, Response<Cart> response) {
                        if (response.isSuccessful()) {
                            double price = cart.getProduct().getPrice();
                            holder.tvCount.setText(String.valueOf(parseInt(holder.tvCount.getText().toString()) - 1));
                            holder.tvTotalPrice.setText(en.format(price * parseInt(holder.tvCount.getText().toString())));
                            cartItemInterface.onClickUpdatePrice(price * -1);
                        }
                    }

                    @Override
                    public void onFailure(Call<Cart> call, Throwable t) {
                        Log.e("CartAdapter", "Update cart failed: " + t.getMessage());
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return (carts != null) ? carts.size() : 0; // Trả về 0 nếu carts null
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTotalPrice, tvPrice, tvProductName, tvCount;
        ImageView ivImage, ivPlus, ivMinus;
        ConstraintLayout layout_delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layout_delete = itemView.findViewById(R.id.layout_delete);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvCount = itemView.findViewById(R.id.tvCount);
            ivImage = itemView.findViewById(R.id.ivImage);
            ivPlus = itemView.findViewById(R.id.ivPlus);
            ivMinus = itemView.findViewById(R.id.ivMinus);
        }
    }
}