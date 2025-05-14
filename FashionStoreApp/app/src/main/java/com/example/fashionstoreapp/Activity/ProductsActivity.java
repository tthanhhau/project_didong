
package com.example.fashionstoreapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionstoreapp.Adapter.ProductAdapter;
import com.example.fashionstoreapp.Model.Category;
import com.example.fashionstoreapp.Model.Product;
import com.example.fashionstoreapp.Model.ProductImage;
import com.example.fashionstoreapp.Model.User;
import com.example.fashionstoreapp.R;
import com.example.fashionstoreapp.Retrofit.ProductAPI;
import com.example.fashionstoreapp.Retrofit.ProductImageAPI;
import com.example.fashionstoreapp.Somethings.ObjectSharedPreferences;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductsActivity extends AppCompatActivity {

    private ProductAdapter productAdapter;
    private EditText etSearch;
    private TextView tvResult;
    private ImageView ivSearch, ivHome, ivUser, ivCart, ivHistory;
    private RecyclerView rcProduct;
    private User user;
    private boolean hasUser;

    private static final String TAG = "ProductsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        // Load user data
        user = ObjectSharedPreferences.getSavedObjectFromPreference(
                this, "User", "MODE_PRIVATE", User.class
        );
        hasUser = user != null;

        initViews();
        setupAppBar();
        setupRecyclerView();
        setupSearch();
        loadInitialData();
    }

    private void initViews() {
        rcProduct = findViewById(R.id.view);
        etSearch = findViewById(R.id.etSearch);
        ivSearch = findViewById(R.id.ivSearch);
        ivHome = findViewById(R.id.ivHome);
        ivCart = findViewById(R.id.ivCart);
        ivHistory = findViewById(R.id.ivHistory);
        ivUser = findViewById(R.id.ivUser);
        tvResult = findViewById(R.id.tvResult);
    }

    private void setupRecyclerView() {
        rcProduct.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        rcProduct.setLayoutManager(layoutManager);
        productAdapter = new ProductAdapter(new ArrayList<>(), this, hasUser, user != null && user.isAdmin());
        rcProduct.setAdapter(productAdapter);
        rcProduct.post(() -> {
            Log.d(TAG, "Product adapter attached with hasUser: " + hasUser + ", isAdmin: " + (user != null && user.isAdmin()));
            productAdapter.notifyDataSetChanged();
        });
    }

    private void setupAppBar() {
        ivHome.setOnClickListener(v -> navigateTo(MainActivity.class));
        ivUser.setOnClickListener(v -> navigateTo(UserActivity.class));
        ivCart.setOnClickListener(v -> navigateTo(CartActivity.class));
        ivHistory.setOnClickListener(v -> navigateTo(OrderActivity.class));
    }

    private void setupSearch() {
        ivSearch.setOnClickListener(v -> performSearch());
        etSearch.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                performSearch();
                return true;
            }
            return false;
        });
    }

    private void loadInitialData() {
        Category category = (Category) getIntent().getSerializableExtra("category");
        String searchContent = getIntent().getStringExtra("searchContent");

        if (category != null) {
            loadProductCategory(category);
        } else if (searchContent != null && !searchContent.isEmpty()) {
            etSearch.setText(searchContent);
            performSearch();
        } else {
            tvResult.setText("No data to display");
            Log.w(TAG, "No category or search content provided");
            fetchAllProducts();
        }
    }

    private void fetchAllProducts() {
        ProductAPI.productApi.getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body();
                    processProducts(products);
                } else {
                    tvResult.setText("Failed to load products");
                    Log.e(TAG, "Get all products response is null or unsuccessful: " + response.message());
                    Toast.makeText(ProductsActivity.this, "Error loading products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                tvResult.setText("Failed to load products");
                Log.e(TAG, "Get all products API failed: " + t.getMessage());
                Toast.makeText(ProductsActivity.this, "Connection error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProductCategory(Category category) {
        if (category == null) {
            tvResult.setText("Invalid category");
            Log.e(TAG, "Category is null");
            Toast.makeText(this, "Invalid category", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Product> products = category.getProduct();
        processProducts(products);
    }

    private void processProducts(List<Product> products) {
        if (products != null && !products.isEmpty()) {
            List<Product> validProducts = new ArrayList<>();
            int total = products.size();
            final int[] loadedCount = {0};

            for (Product product : products) {
                if (product == null || product.getProductName() == null || product.getId() == 0) {
                    Log.w(TAG, "Invalid product: " + (product != null ? "ID: " + product.getId() : "null"));
                    loadedCount[0]++;
                    if (loadedCount[0] == total) {
                        updateUIWithProducts(validProducts);
                    }
                    continue;
                }

                // Fetch image for each product
                ProductImageAPI.productImageApi.getImageByProduct(product.getId()).enqueue(new Callback<List<ProductImage>>() {
                    @Override
                    public void onResponse(Call<List<ProductImage>> call, Response<List<ProductImage>> response) {
                        loadedCount[0]++;
                        if (response.isSuccessful() && response.body() != null) {
                            product.setProductImages(response.body());
                            Log.d(TAG, "Loaded " + response.body().size() + " images for product ID: " + product.getId());
                        } else {
                            product.setProductImages(new ArrayList<>());
                            Log.w(TAG, "No images for product ID: " + product.getId());
                        }

                        validProducts.add(product);

                        if (loadedCount[0] == total) {
                            updateUIWithProducts(validProducts);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ProductImage>> call, Throwable t) {
                        loadedCount[0]++;
                        Log.e(TAG, "Failed to load image for product ID: " + product.getId() + ": " + t.getMessage());
                        product.setProductImages(new ArrayList<>());
                        validProducts.add(product);

                        if (loadedCount[0] == total) {
                            updateUIWithProducts(validProducts);
                        }
                    }
                });
            }
        } else {
            tvResult.setText("No products found");
            Log.w(TAG, "Product list is null or empty");
            Toast.makeText(this, "No products found", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUIWithProducts(List<Product> products) {
        List<Product> filtered = new ArrayList<>();
        for (Product p : products) {
            if (p.getProductImages() != null && !p.getProductImages().isEmpty()) {
                Log.d(TAG, "Loaded image for: " + p.getProductName());
            } else {
                Log.w(TAG, "No image for: " + p.getProductName());
            }
            filtered.add(p);
        }

        tvResult.setText(filtered.size() + " Results");
        productAdapter.updateProducts(filtered);
        productAdapter.notifyDataSetChanged();
    }

    private void performSearch() {
        String query = etSearch.getText().toString().trim();
        if (query.isEmpty()) {
            tvResult.setText("Please enter a search query");
            Log.w(TAG, "Empty search query");
            Toast.makeText(this, "Please enter a search query", Toast.LENGTH_SHORT).show();
            return;
        }

        ProductAPI.productApi.search(query).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body();
                    processProducts(products);
                } else {
                    tvResult.setText("No results found");
                    Log.e(TAG, "Search response is null or unsuccessful: " + response.message());
                    Toast.makeText(ProductsActivity.this, "No results found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                tvResult.setText("Failed to load products");
                Log.e(TAG, "Search API failed: " + t.getMessage());
                Toast.makeText(ProductsActivity.this, "Connection error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateTo(Class<?> targetActivity) {
        Intent intent = new Intent(ProductsActivity.this, targetActivity);
        startActivity(intent);
        finish();
    }
}
