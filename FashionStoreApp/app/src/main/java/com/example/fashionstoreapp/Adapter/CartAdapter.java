package com.example.fashionstoreapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
        return new ViewHolder(inflate);
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
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat vn = NumberFormat.getInstance(localeVN);
        holder.tvCount.setText(String.valueOf(cart.getCount()));
        holder.tvProductName.setText(cart.getProduct().getProductName());
        holder.tvPrice.setText(vn.format(cart.getProduct().getPrice()) + " ₫");
        holder.tvTotalPrice.setText(vn.format(cart.getProduct().getPrice() * cart.getCount()) + " ₫");

        // Kiểm tra và tải hình ảnh sản phẩm
        if (cart.getProduct().getProductImages() != null && !cart.getProduct().getProductImages().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(cart.getProduct().getProductImages().get(0).getImageUrl())
                    .into(holder.ivImage);
        }

        // Sự kiện xóa sản phẩm
        holder.layout_delete.setOnClickListener(v -> {
            double totalPrice = cart.getProduct().getPrice() * cart.getCount() * (-1); // Tính giá âm để trừ
            CartAPI.cartAPI.deleteCart(cart.getId(), user.getId()).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        cartItemInterface.onClickUpdatePrice(-999); // Gửi tín hiệu tính lại tổng
                        notifyItemRemoved(holder.getAdapterPosition());
                        carts.remove(cart);
                    } else {
                        Log.e("CartAdapter", "Delete cart failed: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("CartAdapter", "Delete cart failed: " + t.getMessage());
                }
            });
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
                int newCount = cart.getCount() + 1;
                cart.setCount(newCount);
                CartAPI.cartAPI.addToCart(user.getId(), cart.getProduct().getId(), 1).enqueue(new Callback<Cart>() {
                    @Override
                    public void onResponse(Call<Cart> call, Response<Cart> response) {
                        if (response.isSuccessful()) {
                            double price = cart.getProduct().getPrice();
                            // Cập nhật số lượng và tổng giá sản phẩm trên giao diện
                            holder.tvCount.setText(String.valueOf(newCount));
                            holder.tvTotalPrice.setText(vn.format(price * newCount) + " ₫");
                            // Gửi tín hiệu tính lại tổng
                            cartItemInterface.onClickUpdatePrice(-999);
                        } else {
                            // Hoàn tác nếu API thất bại
                            cart.setCount(cart.getCount() - 1);
                            Log.e("CartAdapter", "Add to cart failed: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<Cart> call, Throwable t) {
                        // Hoàn tác nếu gọi API thất bại
                        cart.setCount(cart.getCount() - 1);
                        Log.e("CartAdapter", "Add to cart failed: " + t.getMessage());
                    }
                });
            } else {
                Toast.makeText(context, "Số lượng vượt quá tồn kho", Toast.LENGTH_SHORT).show();
            }
        });

        // Sự kiện giảm số lượng
        holder.ivMinus.setOnClickListener(v -> {
            if (cart.getCount() > 1) {
                int newCount = cart.getCount() - 1;
                cart.setCount(newCount);
                CartAPI.cartAPI.addToCart(user.getId(), cart.getProduct().getId(), -1).enqueue(new Callback<Cart>() {
                    @Override
                    public void onResponse(Call<Cart> call, Response<Cart> response) {
                        if (response.isSuccessful()) {
                            double price = cart.getProduct().getPrice();
                            // Cập nhật số lượng và tổng giá sản phẩm trên giao diện
                            holder.tvCount.setText(String.valueOf(newCount));
                            holder.tvTotalPrice.setText(vn.format(price * newCount) + " ₫");
                            // Gửi tín hiệu tính lại tổng
                            cartItemInterface.onClickUpdatePrice(-999);
                        } else {
                            // Hoàn tác nếu API thất bại
                            cart.setCount(cart.getCount() + 1);
                            Log.e("CartAdapter", "Reduce cart failed: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<Cart> call, Throwable t) {
                        // Hoàn tác nếu gọi API thất bại
                        cart.setCount(cart.getCount() + 1);
                        Log.e("CartAdapter", "Reduce cart failed: " + t.getMessage());
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