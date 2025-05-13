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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.fashionstoreapp.Activity.ShowDetailActivity;
import com.example.fashionstoreapp.Model.Cart;
import com.example.fashionstoreapp.Model.Product;
import com.example.fashionstoreapp.Model.ProductImage;
import com.example.fashionstoreapp.Model.User;
import com.example.fashionstoreapp.R;
import com.example.fashionstoreapp.Retrofit.CartAPI;
import com.example.fashionstoreapp.Retrofit.ProductAPI;
import com.example.fashionstoreapp.Retrofit.ProductImageAPI;
import com.example.fashionstoreapp.Retrofit.RetrofitService;
import com.example.fashionstoreapp.Somethings.ObjectSharedPreferences;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final Context context;
    private final boolean hasUser;
    private final List<Product> products;
    private final ProductImageAPI productImageAPI;

    public ProductAdapter(List<Product> products, Context context, boolean hasUser) {
        this.products = (products != null) ? new ArrayList<>(products) : new ArrayList<>();
        this.context = context;
        this.hasUser = hasUser;
        this.productImageAPI = new RetrofitService().getRetrofit().create(ProductImageAPI.class);
    }

    public void updateProducts(List<Product> newProducts) {
        products.clear();
        if (newProducts != null) {
            for (Product product : newProducts) {
                if (product != null && product.getProductName() != null && product.getIsActive() == 1) {
                    products.add(product);
                } else {
                    Log.w("ProductAdapter", "Invalid product skipped: " + (product != null ? product.getId() : "null"));
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_products, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);

        if (product == null) {
            holder.title.setText("Unknown Product");
            holder.fee.setText("N/A");
            holder.addBtn.setVisibility(View.GONE);
            Log.e("ProductAdapter", "Product is null at position: " + position);
            return;
        }

        // Tên sản phẩm
        String productName = product.getProductName();
        holder.title.setText(productName != null ? productName : "Unknown Product");

        // Giá sản phẩm (format theo locale)
        String priceText = NumberFormat.getInstance(new Locale("vi", "VN")).format(product.getPrice()) + " ₫";
        holder.fee.setText(priceText);

        // Hình ảnh sản phẩm
        holder.ivImage.setImageDrawable(null); // Clear hình cũ
        productImageAPI.getImageByProduct(product.getId()).enqueue(new Callback<List<ProductImage>>() {
            @Override
            public void onResponse(Call<List<ProductImage>> call, Response<List<ProductImage>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    String imageUrl = response.body().get(0).getImageUrl();
                    Glide.with(context)
                            .load(imageUrl)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)

                            .into(holder.ivImage);
                } else {
                    Log.w("ProductAdapter", "No images for product ID: " + product.getId());
                }
            }

            @Override
            public void onFailure(Call<List<ProductImage>> call, Throwable t) {
                Log.e("ProductAdapter", "Failed to load image for product ID: " + product.getId() + ": " + t.getMessage());
            }
        });

        // Nút thêm giỏ hàng
        if (hasUser) {
            holder.addBtn.setVisibility(View.VISIBLE);
            holder.addBtn.setOnClickListener(v -> addToCart(product));
        } else {
            holder.addBtn.setVisibility(View.GONE);
        }

        // Click item để xem chi tiết
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ShowDetailActivity.class);
            intent.putExtra("product", product);
            context.startActivity(intent);
        });
    }

    private void addToCart(Product product) {
        User user = ObjectSharedPreferences.getSavedObjectFromPreference(context, "User", "MODE_PRIVATE", User.class);
        if (user == null) {
            Toast.makeText(context, "Vui lòng đăng nhập để thêm vào giỏ", Toast.LENGTH_SHORT).show();
            return;
        }

        CartAPI.cartAPI.addToCart(user.getId(), product.getId(), 1).enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(@NonNull Call<Cart> call, @NonNull Response<Cart> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(context, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Không thể thêm vào giỏ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Cart> call, @NonNull Throwable t) {
                Toast.makeText(context, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ProductAdapter", "Add to cart failed", t);
            }
        });
    }

    public void deleteProduct(Product product, int position) {
        ProductAPI.productApi.removeProduct(product.getId()).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Object>> call, @NonNull Response<Map<String, Object>> response) {
                if (response.isSuccessful() && Boolean.TRUE.equals(response.body().get("success"))) {
                    products.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, products.size());
                    Toast.makeText(context, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
                } else {
                    String msg = (response.body() != null && response.body().get("message") != null)
                            ? response.body().get("message").toString()
                            : "Xóa thất bại";
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, Object>> call, @NonNull Throwable t) {
                Toast.makeText(context, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ProductAdapter", "Delete failed", t);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView title, fee;
        ImageView ivImage, addBtn;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            fee = itemView.findViewById(R.id.fee);
            ivImage = itemView.findViewById(R.id.ivImage);
            addBtn = itemView.findViewById(R.id.addBtn);
        }
    }
}