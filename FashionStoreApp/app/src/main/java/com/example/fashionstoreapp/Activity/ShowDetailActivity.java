package com.example.fashionstoreapp.Activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionstoreapp.Adapter.ReviewAdapter;
import com.example.fashionstoreapp.Adapter.SliderAdapter;
import com.example.fashionstoreapp.Model.Cart;
import com.example.fashionstoreapp.Model.Product;
import com.example.fashionstoreapp.Model.ProductImage;
import com.example.fashionstoreapp.Model.Rating;
import com.example.fashionstoreapp.Model.User;
import com.example.fashionstoreapp.R;
import com.example.fashionstoreapp.Retrofit.CartAPI;
import com.example.fashionstoreapp.Retrofit.ProductImageAPI;
import com.example.fashionstoreapp.Retrofit.RatingAPI;
import com.example.fashionstoreapp.Retrofit.RetrofitService;
import com.example.fashionstoreapp.Somethings.ObjectSharedPreferences;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowDetailActivity extends AppCompatActivity {
    TextView tvTitle, tvDescription, tvPrice, tvTotalPrice, tvSold, tvAvailable, tvNumber, tvAddToCart, tvReviewsTitle;
    ImageView ivMinus, ivPlus;
    ConstraintLayout clBack;
    SliderView sliderView;
    RecyclerView rvReviews;
    RatingBar rbUserRating;
    EditText etReviewDescription;
    Button btnSubmitReview;
    TextView tvNoReviews; // Placeholder khi không có đánh giá
    Product product;
    User user;
    RatingAPI ratingAPI;
    private final Locale localeVN = new Locale("vi", "VN");
    private final NumberFormat formatter = NumberFormat.getInstance(localeVN);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_detail);
        product = (Product) getIntent().getSerializableExtra("product");
        user = ObjectSharedPreferences.getSavedObjectFromPreference(this, "User", "MODE_PRIVATE", User.class);
        ratingAPI = new RetrofitService().getRetrofit().create(RatingAPI.class);
        AnhXa();
        LoadProduct();
        ivMinusClick();
        ivPlusClick();
        tvAddToCartClick();
        clBackClick();
        setupReviews();
        setupSubmitReview();
    }

    private void setupReviews() {
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        loadReviews(); // Tải danh sách đánh giá trước, luôn hiển thị rvReviews

        if (user == null) {
            rbUserRating.setVisibility(View.GONE);
            etReviewDescription.setVisibility(View.GONE);
            btnSubmitReview.setVisibility(View.GONE);
            findViewById(R.id.tvAddReviewTitle).setVisibility(View.GONE);
            Toast.makeText(this, "Vui lòng đăng nhập để đánh giá", Toast.LENGTH_SHORT).show();
            return;
        }

        ratingAPI.checkRating(user.getId(), product.getId()).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> body = response.body();
                    Log.d("ShowDetailActivity", "CheckRating response: " + body.toString());

                    boolean canRate = false;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        canRate = (boolean) body.getOrDefault("canRate", false);
                    }
                    boolean hasPurchased = false;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        hasPurchased = (boolean) body.getOrDefault("hasPurchased", false);
                    }
                    boolean hasRated = false;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        hasRated = (boolean) body.getOrDefault("hasRated", false);
                    }

                    Log.d("ShowDetailActivity", "canRate: " + canRate + ", hasPurchased: " + hasPurchased + ", hasRated: " + hasRated);

                    if (canRate) {
                        // Hiển thị form đánh giá
                        rbUserRating.setVisibility(View.VISIBLE);
                        etReviewDescription.setVisibility(View.VISIBLE);
                        btnSubmitReview.setVisibility(View.VISIBLE);
                        findViewById(R.id.tvAddReviewTitle).setVisibility(View.VISIBLE);
                        Log.d("ShowDetailActivity", "Showing rating form for user: " + user.getId() + ", product: " + product.getId());
                    } else {
                        // Ẩn form đánh giá
                        rbUserRating.setVisibility(View.GONE);
                        etReviewDescription.setVisibility(View.GONE);
                        btnSubmitReview.setVisibility(View.GONE);
                        findViewById(R.id.tvAddReviewTitle).setVisibility(View.GONE);

                        // Thông báo lý do
                        if (!hasPurchased) {
                            Toast.makeText(ShowDetailActivity.this, "Bạn cần mua sản phẩm để đánh giá", Toast.LENGTH_SHORT).show();
                        } else if (hasRated) {
                            Toast.makeText(ShowDetailActivity.this, "Bạn đã đánh giá sản phẩm này", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    // Ẩn form đánh giá nếu API thất bại
                    rbUserRating.setVisibility(View.GONE);
                    etReviewDescription.setVisibility(View.GONE);
                    btnSubmitReview.setVisibility(View.GONE);
                    findViewById(R.id.tvAddReviewTitle).setVisibility(View.GONE);
                    Toast.makeText(ShowDetailActivity.this, "Lỗi kiểm tra trạng thái đánh giá", Toast.LENGTH_SHORT).show();
                    Log.e("ShowDetailActivity", "CheckRating failed: response code = " + response.code() + ", message = " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                // Ẩn form đánh giá nhưng không ảnh hưởng đến rvReviews
                rbUserRating.setVisibility(View.GONE);
                etReviewDescription.setVisibility(View.GONE);
                btnSubmitReview.setVisibility(View.GONE);
                findViewById(R.id.tvAddReviewTitle).setVisibility(View.GONE);
                Toast.makeText(ShowDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ShowDetailActivity", "CheckRating failed: " + t.getMessage());
            }
        });
    }

    private void loadReviews() {
        // Luôn hiển thị tiêu đề và RecyclerView
        tvReviewsTitle.setVisibility(View.VISIBLE);
        rvReviews.setVisibility(View.VISIBLE);

        ratingAPI.getRating(product.getId()).enqueue(new Callback<List<Rating>>() {
            @Override
            public void onResponse(Call<List<Rating>> call, Response<List<Rating>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    List<Rating> reviews = response.body();
                    ReviewAdapter adapter = new ReviewAdapter(reviews);
                    rvReviews.setAdapter(adapter);
                    tvNoReviews.setVisibility(View.GONE); // Ẩn placeholder nếu có đánh giá
                    Log.d("ShowDetailActivity", "Loaded " + reviews.size() + " reviews for product ID: " + product.getId());
                } else {
                    // Hiển thị placeholder khi không có đánh giá
                    rvReviews.setAdapter(new ReviewAdapter(new ArrayList<>()));
                    tvNoReviews.setVisibility(View.VISIBLE);
                    Log.d("ShowDetailActivity", "No reviews for product ID: " + product.getId());
                }
            }

            @Override
            public void onFailure(Call<List<Rating>> call, Throwable t) {
                // Hiển thị placeholder khi API thất bại
                rvReviews.setAdapter(new ReviewAdapter(new ArrayList<>()));
                tvNoReviews.setVisibility(View.VISIBLE);
                Toast.makeText(ShowDetailActivity.this, "Lỗi tải đánh giá: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ShowDetailActivity", "Failed to load reviews: " + t.getMessage());
            }
        });
    }

    private void setupSubmitReview() {
        btnSubmitReview.setOnClickListener(v -> {
            if (user == null) {
                Toast.makeText(this, "Vui lòng đăng nhập để gửi đánh giá", Toast.LENGTH_SHORT).show();
                return;
            }

            float ratingValue = rbUserRating.getRating();
            String description = etReviewDescription.getText().toString().trim();

            if (ratingValue == 0) {
                Toast.makeText(this, "Vui lòng chọn số sao", Toast.LENGTH_SHORT).show();
                return;
            }
            if (description.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập mô tả đánh giá", Toast.LENGTH_SHORT).show();
                return;
            }
            if (product == null || product.getId() == 0) {
                Toast.makeText(this, "Dữ liệu sản phẩm không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            Rating rating = new Rating();
            rating.setRate((int) ratingValue);
            rating.setDescription(description);
            rating.setProductId(product.getId());
            rating.setUserId(user.getId());

            ratingAPI.addRating(rating).enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    if (response.isSuccessful() && response.body() != null && (boolean) response.body().get("success")) {
                        Toast.makeText(ShowDetailActivity.this, "Gửi đánh giá thành công", Toast.LENGTH_SHORT).show();
                        rbUserRating.setVisibility(View.GONE);
                        etReviewDescription.setVisibility(View.GONE);
                        btnSubmitReview.setVisibility(View.GONE);
                        findViewById(R.id.tvAddReviewTitle).setVisibility(View.GONE);
                        loadReviews();
                        rbUserRating.setRating(0);
                        etReviewDescription.setText("");
                    } else {
                        String message = response.body() != null ? (String) response.body().get("message") : "Lỗi không xác định";
                        Toast.makeText(ShowDetailActivity.this, "Gửi đánh giá thất bại: " + message, Toast.LENGTH_SHORT).show();
                        Log.e("ShowDetailActivity", "Add rating failed: " + message);
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    Toast.makeText(ShowDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("ShowDetailActivity", "Add rating failed: " + t.getMessage());
                }
            });
        });
    }

    private void clBackClick() {
        clBack.setOnClickListener(v -> onBackPressed());
    }

    private void tvAddToCartClick() {
        tvAddToCart.setOnClickListener(v -> {
            if (user == null) {
                Toast.makeText(this, "Vui lòng đăng nhập để thêm vào giỏ", Toast.LENGTH_SHORT).show();
                return;
            }
            int count;
            try {
                count = Integer.parseInt(tvNumber.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Số lượng không hợp lệ", Toast.LENGTH_SHORT).show();
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
        });
    }

    private void LoadProduct() {
        tvTitle.setText(product.getProductName() != null ? product.getProductName() : "Unknown Product");
        tvDescription.setText(product.getDescription() != null ? product.getDescription() : "No description");
        tvPrice.setText(formatter.format(product.getPrice()) + " ₫");
        tvSold.setText(String.valueOf(product.getSold()));
        tvAvailable.setText(String.valueOf(product.getQuantity()));
        tvTotalPrice.setText(formatter.format(product.getPrice()) + " ₫");
        tvNumber.setText("1");
        LoadImage();
    }

    private void LoadImage() {
        sliderView = findViewById(R.id.imageSlider);
        List<ProductImage> productImages = product.getProductImages();

        if (productImages != null && !productImages.isEmpty()) {
            SliderAdapter adapter = new SliderAdapter(ShowDetailActivity.this, productImages);
            sliderView.setSliderAdapter(adapter);
            setupSliderView();
            Log.d("ShowDetailActivity", "Loaded " + productImages.size() + " preloaded images for product ID: " + product.getId());
        } else {
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
        ivPlus.setOnClickListener(v -> {
            int number = Integer.parseInt(tvNumber.getText().toString()) + 1;
            if (number <= product.getQuantity()) {
                tvNumber.setText(String.valueOf(number));
                tvTotalPrice.setText(formatter.format(product.getPrice() * number) + " ₫");
            } else {
                Toast.makeText(this, "Số lượng vượt quá hàng tồn", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ivMinusClick() {
        ivMinus.setOnClickListener(v -> {
            int number = Integer.parseInt(tvNumber.getText().toString()) - 1;
            if (number >= 1) {
                tvNumber.setText(String.valueOf(number));
                tvTotalPrice.setText(formatter.format(product.getPrice() * number) + " ₫");
            }
        });
    }

    private void AnhXa() {
        tvAddToCart = findViewById(R.id.tvAddToCart);
        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        tvPrice = findViewById(R.id.tvPrice);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvSold = findViewById(R.id.tvSold);
        tvAvailable = findViewById(R.id.tvAvailable);
        tvNumber = findViewById(R.id.tvNumber);
        ivMinus = findViewById(R.id.ivMinus);
        ivPlus = findViewById(R.id.ivPlus);
        clBack = findViewById(R.id.clBack);
        rvReviews = findViewById(R.id.rvReviews);
        rbUserRating = findViewById(R.id.rbUserRating);
        etReviewDescription = findViewById(R.id.etReviewDescription);
        btnSubmitReview = findViewById(R.id.btnSubmitReview);
        tvReviewsTitle = findViewById(R.id.tvReviewsTitle);
        tvNoReviews = findViewById(R.id.tvNoReviews); // Khởi tạo placeholder
    }
}