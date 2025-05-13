package com.example.fashionstoreapp.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fashionstoreapp.Adapter.CategoryAdapter;
import com.example.fashionstoreapp.Adapter.ImagePagerAdapter;
import com.example.fashionstoreapp.Adapter.ProductAdapter;
import com.example.fashionstoreapp.Model.Category;
import com.example.fashionstoreapp.Model.Product;
import com.example.fashionstoreapp.Model.User;
import com.example.fashionstoreapp.R;
import com.example.fashionstoreapp.Retrofit.CategoryAPI;
import com.example.fashionstoreapp.Retrofit.ProductAPI;
import com.example.fashionstoreapp.Somethings.ObjectSharedPreferences;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private CategoryAdapter categoryAdapter;
    private ProductAdapter newProductsAdapter, bestSellersAdapter, allProductsAdapter;
    private RecyclerView recyclerViewCategoryList, recyclerViewNewProductList, recyclerViewBestSellersList, recyclerViewAllProductsList;
    private TextView tvHiName;
    private EditText etSearch;
    private ImageView ivAvatar, ivHome, ivUser, ivCart, ivHistory, ivSearch;
    private User user;
    private boolean hasUser;

    // ViewPager2 and TabLayout
    ViewPager2 viewPager;
    TabLayout tabLayout;

    // Auto-scroll
    private Handler autoScrollHandler;
    private Runnable autoScrollRunnable;
    private static final long AUTO_SCROLL_INTERVAL = 2000; // 2 giây
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check admin rights
        user = ObjectSharedPreferences.getSavedObjectFromPreference(
                MainActivity.this, "User", "MODE_PRIVATE", User.class
        );
        hasUser = user != null;
        AnhXa();
        appBarClick();
        LoadUserInfor();
        ivSearchClick();
        initRecyclerViews();
        LoadCategories();
        LoadNewProducts();
        LoadBestSellers();
        LoadAllProducts();
        setupViewPager();
    }

    private void initRecyclerViews() {
        // Initialize RecyclerView for Categories
        recyclerViewCategoryList = findViewById(R.id.view1);
        recyclerViewCategoryList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new CategoryAdapter(new ArrayList<>(), MainActivity.this);
        recyclerViewCategoryList.setAdapter(categoryAdapter);
        Log.d("RecyclerView", "Categories adapter initialized with empty list");

        // Initialize RecyclerView for New Products
        recyclerViewNewProductList = findViewById(R.id.view2);
        recyclerViewNewProductList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        newProductsAdapter = new ProductAdapter(new ArrayList<>(), MainActivity.this, hasUser);
        recyclerViewNewProductList.setAdapter(newProductsAdapter);
        Log.d("RecyclerView", "NewProducts adapter initialized with empty list");

        // Initialize RecyclerView for Best Sellers
        recyclerViewBestSellersList = findViewById(R.id.view3);
        recyclerViewBestSellersList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        bestSellersAdapter = new ProductAdapter(new ArrayList<>(), MainActivity.this, hasUser);
        recyclerViewBestSellersList.setAdapter(bestSellersAdapter);
        Log.d("RecyclerView", "BestSellers adapter initialized with empty list");

// In initRecyclerViews()
        recyclerViewAllProductsList = findViewById(R.id.view4); // Replace with your actual ID
        recyclerViewAllProductsList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        allProductsAdapter = new ProductAdapter(new ArrayList<>(), MainActivity.this, hasUser);
        recyclerViewAllProductsList.setAdapter(allProductsAdapter);
        Log.d("RecyclerView", "AllProducts adapter initialized with empty list");
    }

    private void LoadCategories() {
        CategoryAPI.categoryAPI.GetAllCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categoriesList = response.body();
                    Log.d("APIResponse", "Categories loaded: " + categoriesList.size() + " items");
                    for (Category category : categoriesList) {
                        Log.d("APIResponse", "Category: " + category.getCategory_Name() + ", Image: " + category.getCategory_Image());
                    }
                    categoryAdapter.updateCategories(categoriesList);
                    recyclerViewCategoryList.post(() -> {
                        Log.d("RecyclerView", "Categories adapter updated with " + categoriesList.size() + " items");
                        categoryAdapter.notifyDataSetChanged();
                    });
                    Toast.makeText(MainActivity.this, "Loaded " + categoriesList.size() + " categories", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("APIError", "Get Categories response is null or unsuccessful: " + response.message());
                    Toast.makeText(MainActivity.this, "Error loading categories: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.e("APIError", "Call API Get Categories fail: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Connection error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void LoadNewProducts() {
        ProductAPI.productApi.getNewProduct().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> newProductsList = response.body();
                    Log.d("APIResponse", "New Products loaded: " + newProductsList.size() + " items");
                    newProductsAdapter.updateProducts(newProductsList);
                    recyclerViewNewProductList.post(() -> {
                        Log.d("RecyclerView", "NewProducts adapter updated with " + newProductsList.size() + " items");
                        newProductsAdapter.notifyDataSetChanged();
                    });
                    Toast.makeText(MainActivity.this, "Loaded new products", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("APIError", "Get New Products response is null or unsuccessful: " + response.message());
                    Toast.makeText(MainActivity.this, "Error loading new products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e("APIError", "Call API Get New Products fail: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Connection error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void LoadBestSellers() {
        ProductAPI.productApi.getBestSellers().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> bestSellersList = response.body();
                    Log.d("APIResponse", "Best Sellers loaded: " + bestSellersList.size() + " items");
                    bestSellersAdapter.updateProducts(bestSellersList);
                    recyclerViewBestSellersList.post(() -> {
                        Log.d("RecyclerView", "BestSellers adapter updated with " + bestSellersList.size() + " items");
                        bestSellersAdapter.notifyDataSetChanged();
                    });
                    Toast.makeText(MainActivity.this, "Loaded best sellers", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("APIError", "Get Best Sellers response is null or unsuccessful: " + response.message());
                    Toast.makeText(MainActivity.this, "Error loading best sellers", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e("APIError", "Call API Get Best Sellers fail: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Connection error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void LoadAllProducts() {
        ProductAPI.productApi.getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> allProductsList = response.body();
                    Log.d("APIResponse", "All Products loaded: " + allProductsList.size() + " items");
                    allProductsAdapter.updateProducts(allProductsList);
                    recyclerViewAllProductsList.post(() -> {
                        Log.d("RecyclerView", "AllProducts adapter updated with " + allProductsList.size() + " items");
                        allProductsAdapter.notifyDataSetChanged();
                    });
                    Toast.makeText(MainActivity.this, "Loaded all products", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("APIError", "Get All Products response is null or unsuccessful: " + response.message());
                    Toast.makeText(MainActivity.this, "Error loading all products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e("APIError", "Call API Get All Products fail: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Connection error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void LoadUserInfor() {
        if (user != null) {
            tvHiName.setText("Hi " + user.getUser_Name());
        } else {
            tvHiName.setText("Hi Guest");
        }
    }

    private void ivSearchClick() {
        ivSearch.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProductsActivity.class);
            intent.putExtra("searchContent", etSearch.getText().toString());
            startActivity(intent);
        });

        etSearch.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                Intent intent = new Intent(MainActivity.this, ProductsActivity.class);
                intent.putExtra("searchContent", etSearch.getText().toString());
                intent.putExtra("category_id", "-1");
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    private void setupViewPager() {
        // Danh sách ảnh (ID tài nguyên)
        List<Integer> imageList = Arrays.asList(
                R.drawable.banner,
                R.drawable.banner2,
                R.drawable.banner3
        );

        // Thiết lập ViewPager2
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new ImagePagerAdapter(imageList));

        // Kết nối TabLayout với ViewPager2
        tabLayout = findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    // Tùy chỉnh tab nếu cần (để trống vì chỉ cần dot indicator)
                }).attach();

        // Thiết lập auto-scroll
        autoScrollHandler = new Handler(Looper.getMainLooper());
        autoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                if (viewPager.getAdapter() != null) {
                    int totalItems = viewPager.getAdapter().getItemCount();
                    int nextItem = (viewPager.getCurrentItem() + 1) % totalItems; // Quay lại đầu nếu đến cuối
                    viewPager.setCurrentItem(nextItem, true); // Chuyển trang với hiệu ứng mượt
                    autoScrollHandler.postDelayed(this, AUTO_SCROLL_INTERVAL); // Lặp lại sau 2 giây
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Bắt đầu auto-scroll khi Activity được hiển thị
        if (autoScrollHandler != null && autoScrollRunnable != null) {
            autoScrollHandler.postDelayed(autoScrollRunnable, AUTO_SCROLL_INTERVAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Dừng auto-scroll khi Activity bị tạm dừng
        if (autoScrollHandler != null && autoScrollRunnable != null) {
            autoScrollHandler.removeCallbacks(autoScrollRunnable);
        }
    }

    private void appBarClick() {
        ivHome.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, MainActivity.class));
            finish();
        });
        ivUser.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, UserActivity.class));
            finish();
        });
        ivCart.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, CartActivity.class));
            finish();
        });
        ivHistory.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, OrderActivity.class));
            finish();
        });
    }

    private void AnhXa() {
        tvHiName = findViewById(R.id.tvHiName);
        ivAvatar = findViewById(R.id.ivAvatar);
        ivHome = findViewById(R.id.ivHome);
        ivUser = findViewById(R.id.ivUser);
        ivCart = findViewById(R.id.ivCart);
        ivHistory = findViewById(R.id.ivHistory);
        etSearch = findViewById(R.id.etSearch);
        ivSearch = findViewById(R.id.ivSearch);
    }
}