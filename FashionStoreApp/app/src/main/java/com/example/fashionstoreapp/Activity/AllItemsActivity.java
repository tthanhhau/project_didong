package com.example.fashionstoreapp.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionstoreapp.Adapter.ProductAdapter;
import com.example.fashionstoreapp.Model.Product;
import com.example.fashionstoreapp.R;
import com.example.fashionstoreapp.Retrofit.ProductAPI;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllItemsActivity extends AppCompatActivity {

    private RecyclerView rvAllItems;
    private ProductAdapter productAdapter;
    private ImageView ivBack;

    private ProductAPI productAPI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_item_admin); // Make sure the XML file is named activity_all_items.xml

        // Initialize views
        rvAllItems = findViewById(R.id.rvAllItems);
        ivBack = findViewById(R.id.ivBack);

        // Set up RecyclerView
        rvAllItems.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columns grid
        rvAllItems.setHasFixedSize(true);


        // Load products
        loadProducts();

        // Back button click listener
        ivBack.setOnClickListener(v -> finish());
    }

    private void loadProducts() {
        ProductAPI.productApi.getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body();
                    productAdapter = new ProductAdapter(products, AllItemsActivity.this,true);
                    rvAllItems.setAdapter(productAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(AllItemsActivity.this,
                        "Failed to load products: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

}