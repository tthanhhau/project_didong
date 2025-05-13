package com.example.fashionstoreapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionstoreapp.Adapter.CartAdapter;
import com.example.fashionstoreapp.Interface.CartItemInterface;
import com.example.fashionstoreapp.Model.Cart;
import com.example.fashionstoreapp.Model.ProductImage;
import com.example.fashionstoreapp.Model.User;
import com.example.fashionstoreapp.R;
import com.example.fashionstoreapp.Retrofit.CartAPI;
import com.example.fashionstoreapp.Retrofit.ProductImageAPI;
import com.example.fashionstoreapp.Retrofit.RetrofitService;
import com.example.fashionstoreapp.Somethings.ObjectSharedPreferences;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity implements CartItemInterface {
    CartAdapter cartAdapter;
    RecyclerView recyclerViewCart;
    Button btnCheckout;
    TextView tvTotalPrice;

    ImageView ivHome, ivUser, ivCart, ivHistory;
    ConstraintLayout clCartIsEmpty, clCart;
    List<Cart> listCart = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        AnhXa();
        LoadCart();
        appBarClick();
        btnCheckoutClick();
    }

    private void btnCheckoutClick() {
        btnCheckout.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, CheckOutActivity.class);
            startActivity(intent);
        });
    }

    private void appBarClick() {
        ivHome.setOnClickListener(v -> {
            startActivity(new Intent(CartActivity.this, MainActivity.class));
            finish();
        });
        ivUser.setOnClickListener(v -> {
            startActivity(new Intent(CartActivity.this, UserActivity.class));
            finish();
        });
        ivCart.setOnClickListener(v -> {
            startActivity(new Intent(CartActivity.this, CartActivity.class));
            finish();
        });

        ivHistory.setOnClickListener(v -> {
            startActivity(new Intent(CartActivity.this, OrderActivity.class));
            finish();
        });
    }

    private void LoadCart() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewCart = findViewById(R.id.view);
        recyclerViewCart.setLayoutManager(linearLayoutManager);

        User user = ObjectSharedPreferences.getSavedObjectFromPreference(CartActivity.this, "User", "MODE_PRIVATE", User.class);
        if (user == null) {
            clCartIsEmpty.setVisibility(View.VISIBLE);
            clCart.setVisibility(View.GONE);
            Log.e("CartActivity", "User is null, showing empty cart");
            Toast.makeText(this, "Vui lòng đăng nhập để xem giỏ hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        CartItemInterface cartItemInterface = this;

        // Gọi API để lấy danh sách giỏ hàng
        CartAPI.cartAPI.cartOfUser(user.getId()).enqueue(new Callback<List<Cart>>() {
            @Override
            public void onResponse(Call<List<Cart>> call, Response<List<Cart>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listCart = response.body();
                    if (listCart.isEmpty()) {
                        clCartIsEmpty.setVisibility(View.VISIBLE);
                        clCart.setVisibility(View.GONE);
                        Log.d("CartActivity", "Cart is empty");
                    } else {
                        // Tải trước hình ảnh cho mỗi sản phẩm trong giỏ hàng
                        ProductImageAPI productImageAPI = new RetrofitService().getRetrofit().create(ProductImageAPI.class);
                        for (Cart cart : listCart) {
                            if (cart.getProduct() != null) {
                                productImageAPI.getImageByProduct(cart.getProduct().getId()).enqueue(new Callback<List<ProductImage>>() {
                                    @Override
                                    public void onResponse(Call<List<ProductImage>> call, Response<List<ProductImage>> response) {
                                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                                            cart.getProduct().setProductImages(response.body());
                                            cartAdapter.notifyDataSetChanged(); // Cập nhật adapter khi hình ảnh được tải
                                            Log.d("CartActivity", "Loaded images for product ID: " + cart.getProduct().getId());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<List<ProductImage>> call, Throwable t) {
                                        Log.e("CartActivity", "Failed to load images for product ID: " + cart.getProduct().getId() + ": " + t.getMessage());
                                    }
                                });
                            }
                        }

                        // Cập nhật adapter
                        cartAdapter = new CartAdapter(cartItemInterface, listCart, CartActivity.this);
                        recyclerViewCart.setAdapter(cartAdapter);

                        // Tính tổng giá
                        int total = 0;
                        for (Cart cart : listCart) {
                            if (cart.getProduct() != null) {
                                total += cart.getCount() * cart.getProduct().getPrice();
                            }
                        }
                        Locale localeVN = new Locale("vi", "VN"); // Sử dụng locale Việt Nam
                        NumberFormat formatter = NumberFormat.getInstance(localeVN);
                        tvTotalPrice.setText(formatter.format(total) + " ₫"); // Hiển thị giá với ký hiệu ₫
                        clCartIsEmpty.setVisibility(View.GONE);
                        clCart.setVisibility(View.VISIBLE);
                        Log.d("CartActivity", "Cart loaded with " + listCart.size() + " items, total price: " + total);
                    }
                } else {
                    clCartIsEmpty.setVisibility(View.VISIBLE);
                    clCart.setVisibility(View.GONE);
                    Log.e("CartActivity", "Cart API response failed: " + response.message());
                    Toast.makeText(CartActivity.this, "Không thể tải giỏ hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Cart>> call, Throwable t) {
                clCartIsEmpty.setVisibility(View.VISIBLE);
                clCart.setVisibility(View.GONE);
                Log.e("CartActivity", "Call API cartOfUser failed: " + t.getMessage());
                Toast.makeText(CartActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void AnhXa() {
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        ivHome = findViewById(R.id.ivHome);
        ivUser = findViewById(R.id.ivUser);
        ivCart = findViewById(R.id.ivCart);
        ivHistory = findViewById(R.id.ivHistory);
        clCart = findViewById(R.id.clCart);
        clCartIsEmpty = findViewById(R.id.clCartIsEmpty);
        btnCheckout = findViewById(R.id.btnCheckout);
    }

    @Override
    public void onClickUpdatePrice(int price) {
        Locale localeEN = new Locale("en", "EN");
        NumberFormat en = NumberFormat.getInstance(localeEN);
        int total = Integer.parseInt(tvTotalPrice.getText().toString().replace(",",""));
        total += price;
        if (total ==0){
            clCartIsEmpty.setVisibility(View.VISIBLE);
            clCart.setVisibility(View.GONE);
        }
        tvTotalPrice.setText(en.format(total));
    }

    @Override
    public void onClickUpdatePrice(double price) {

    }
}
