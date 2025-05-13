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
import com.example.fashionstoreapp.R;
import com.example.fashionstoreapp.Retrofit.ProductAPI;
import com.example.fashionstoreapp.Retrofit.ProductImageAPI;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

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
        productAdapter = new ProductAdapter(new ArrayList<>(), this, true); // Truyền isAdmin
        rcProduct.setAdapter(productAdapter);
        rcProduct.post(() -> {
            Log.d("RecyclerView", "Adapter attached");
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
            Log.w("ProductsActivity", "No category or search content provided");
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
                    Log.e("APIError", "Get all products response is null or unsuccessful: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                tvResult.setText("Failed to load products");
                Log.e("APIError", "Get all products API failed: " + t.getMessage());
            }
        });
    }

    private void loadProductCategory(Category category) {
        if (category == null) {
            tvResult.setText("Invalid category");
            Log.e("ProductsActivity", "Category is null");
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
                if (product == null || product.getProductName() == null || product.getProductImages() == null || product.getProductImages().isEmpty()) {
                    Log.e("ProductsActivity", "Invalid product: " + (product != null ? "ID: " + product.getId() : "null"));
                    continue;
                }

                // Fetch image for each product
                ProductImageAPI.productImageApi.getImageByProduct(product.getId()).enqueue(new Callback<List<ProductImage>>() {
                    @Override
                    public void onResponse(Call<List<ProductImage>> call, Response<List<ProductImage>> response) {
                        loadedCount[0]++;
                        if (response.isSuccessful() && response.body() != null) {
                            product.setProductImages(response.body());
                        } else {
                            product.setProductImages(new ArrayList<>());
                        }

                        validProducts.add(product);

                        if (loadedCount[0] == total) {
                            updateUIWithProducts(validProducts);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ProductImage>> call, Throwable t) {
                        loadedCount[0]++;
                        Log.e("ImageLoad", "Failed to load image for product ID: " + product.getId(), t);
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
            Log.e("CategoryError", "Product list is null or empty");
        }
    }

    private void updateUIWithProducts(List<Product> products) {
        List<Product> filtered = new ArrayList<>();
        for (Product p : products) {
            if (p.getProductImages() != null && !p.getProductImages().isEmpty()) {
                Log.d("ImageLoad", "Loaded image for: " + p.getProductName());
            } else {
                Log.w("ImageLoad", "No image for: " + p.getProductName());
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
            Log.w("ProductsActivity", "Empty search query");
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
                    Log.e("APIError", "Search response is null or unsuccessful: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                tvResult.setText("Failed to load products");
                Log.e("APIError", "Search API failed: " + t.getMessage());
            }
        });
    }

    private void navigateTo(Class<?> targetActivity) {
        Intent intent = new Intent(ProductsActivity.this, targetActivity);
        startActivity(intent);
        finish();
    }
}
