package com.example.fashionstoreapp.Activity;

import static java.lang.Integer.parseInt;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.fashionstoreapp.Adapter.SliderAdapter;
import com.example.fashionstoreapp.Model.Cart;
import com.example.fashionstoreapp.Model.Product;
import com.example.fashionstoreapp.Model.ProductImage;
import com.example.fashionstoreapp.Model.User;
import com.example.fashionstoreapp.R;
import com.example.fashionstoreapp.Retrofit.CartAPI;
import com.example.fashionstoreapp.Retrofit.ProductImageAPI;
import com.example.fashionstoreapp.Retrofit.RetrofitService;
import com.example.fashionstoreapp.Somethings.ObjectSharedPreferences;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowDetailActivity extends AppCompatActivity {
    TextView tvTitle, tvdescription, tvPrice, tvTotalPrice, tvSold, tvAvailable, tvNumber, tvAddToCart;
    ImageView ivMinus, ivPlus;
    Product product;
    ConstraintLayout clBack;
    SliderView sliderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_detail);
        product = (Product) getIntent().getSerializableExtra("product");
        AnhXa();
        LoadProduct();
        ivMinusClick();
        ivPlusClick();
        tvAddToCartClick();
        clBackClick();
    }

    private void clBackClick() {
        clBack.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void tvAddToCartClick() {
        tvAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = ObjectSharedPreferences.getSavedObjectFromPreference(ShowDetailActivity.this, "User", "MODE_PRIVATE", User.class);
                if (user == null) {
                    Toast.makeText(ShowDetailActivity.this, "Vui lòng đăng nhập để thêm vào giỏ", Toast.LENGTH_SHORT).show();
                    return;
                }
                int count;
                try {
                    count = parseInt(tvNumber.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(ShowDetailActivity.this, "Số lượng không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }
                CartAPI.cartAPI.addToCart(user.getId(), product.getId(), count).enqueue(new Callback<Cart>() {
                    @Override
                    public void onResponse(Call<Cart> call, Response<Cart> response) {
                        Cart cart = response.body();
                        if (cart != null) {
                            Toast.makeText(ShowDetailActivity.this, "Thêm vào giỏ thành công", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ShowDetailActivity.this, "Thêm vào giỏ thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Cart> call, Throwable t) {
                        Toast.makeText(ShowDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("ShowDetailActivity", "Add to cart failed: " + t.getMessage());
                    }
                });
            }
        });
    }

    private void LoadProduct() {
        tvTitle.setText(product.getProductName() != null ? product.getProductName() : "Unknown Product");
        tvdescription.setText(product.getDescription() != null ? product.getDescription() : "No description");
        Locale localeEN = new Locale("en", "EN");
        NumberFormat en = NumberFormat.getInstance(localeEN);
        tvPrice.setText(en.format(product.getPrice()));
        tvSold.setText(String.valueOf(product.getSold()));
        tvAvailable.setText(String.valueOf(product.getQuantity()));
        tvTotalPrice.setText(en.format(product.getPrice()));
        tvNumber.setText("1");
        LoadImage();
    }

    private void LoadImage() {
        sliderView = findViewById(R.id.imageSlider);
        List<ProductImage> productImages = product.getProductImages();

        if (productImages != null && !productImages.isEmpty()) {
            // Sử dụng danh sách hình ảnh đã preload
            SliderAdapter adapter = new SliderAdapter(ShowDetailActivity.this, productImages);
            sliderView.setSliderAdapter(adapter);
            setupSliderView();
            Log.d("ShowDetailActivity", "Loaded " + productImages.size() + " preloaded images for product ID: " + product.getId());
        } else {
            // Gọi API để lấy hình ảnh
            ProductImageAPI productImageAPI = new RetrofitService().getRetrofit().create(ProductImageAPI.class);
            productImageAPI.getImageByProduct(product.getId()).enqueue(new Callback<List<ProductImage>>() {
                @Override
                public void onResponse(Call<List<ProductImage>> call, Response<List<ProductImage>> response) {
                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        List<ProductImage> images = response.body();
                        product.setProductImages(images);
                        SliderAdapter adapter = new SliderAdapter(ShowDetailActivity.this, images);
                        sliderView.setSliderAdapter(adapter);
                        setupSliderView();
                        Log.d("ShowDetailActivity", "Loaded " + images.size() + " images from API for product ID: " + product.getId());
                    } else {
                        SliderAdapter adapter = new SliderAdapter(ShowDetailActivity.this, new ArrayList<>());
                        sliderView.setSliderAdapter(adapter);
                        setupSliderView();
                        Toast.makeText(ShowDetailActivity.this, "Không có hình ảnh cho sản phẩm", Toast.LENGTH_SHORT).show();
                        Log.w("ShowDetailActivity", "No images for product ID: " + product.getId());
                    }
                }

                @Override
                public void onFailure(Call<List<ProductImage>> call, Throwable t) {
                    SliderAdapter adapter = new SliderAdapter(ShowDetailActivity.this, new ArrayList<>());
                    sliderView.setSliderAdapter(adapter);
                    setupSliderView();
                    Toast.makeText(ShowDetailActivity.this, "Lỗi tải hình ảnh: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("ShowDetailActivity", "Failed to load images for product ID: " + product.getId() + ": " + t.getMessage());
                }
            });
        }
    }

    private void setupSliderView() {
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(4);
        sliderView.startAutoCycle();
    }

    private void ivPlusClick() {
        ivPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int number = parseInt(tvNumber.getText().toString()) + 1;
                if (number <= product.getQuantity()) {
                    tvNumber.setText(String.valueOf(number));
                    Locale localeEN = new Locale("en", "EN");
                    NumberFormat en = NumberFormat.getInstance(localeEN);
                    tvTotalPrice.setText(en.format(product.getPrice() * number));
                } else {
                    Toast.makeText(ShowDetailActivity.this, "Số lượng vượt quá hàng tồn", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void ivMinusClick() {
        ivMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int number = parseInt(tvNumber.getText().toString()) - 1;
                if (number >= 1) {
                    tvNumber.setText(String.valueOf(number));
                    Locale localeEN = new Locale("en", "EN");
                    NumberFormat en = NumberFormat.getInstance(localeEN);
                    tvTotalPrice.setText(en.format(product.getPrice() * number));
                }
            }
        });
    }

    private void AnhXa() {
        tvAddToCart = findViewById(R.id.tvAddToCart);
        tvTitle = findViewById(R.id.tvTitle);
        tvdescription = findViewById(R.id.tvDescription);
        tvPrice = findViewById(R.id.tvPrice);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvSold = findViewById(R.id.tvSold);
        tvAvailable = findViewById(R.id.tvAvailable);
        tvNumber = findViewById(R.id.tvNumber);
        ivMinus = findViewById(R.id.ivMinus);
        ivPlus = findViewById(R.id.ivPlus);
        clBack = findViewById(R.id.clBack);
    }
}