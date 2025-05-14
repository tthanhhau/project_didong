package com.example.fashionstoreapp.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fashionstoreapp.Adapter.ProductImagesAdapter;
import com.example.fashionstoreapp.Model.Category;
import com.example.fashionstoreapp.Model.CloudinaryResponse;
import com.example.fashionstoreapp.Model.Product;
import com.example.fashionstoreapp.Model.ProductImage;
import com.example.fashionstoreapp.R;
import com.example.fashionstoreapp.Retrofit.CategoryAPI;
import com.example.fashionstoreapp.Retrofit.CloudinaryAPI;
import com.example.fashionstoreapp.Retrofit.ProductAPI;
import com.example.fashionstoreapp.Retrofit.RetrofitService;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddProductActivity extends AppCompatActivity {

    private Spinner categorySpinner;
    private EditText productNameEditText, priceEditText, quantityEditText, descriptionEditText;
    private Switch isActiveSwitch, isSellingSwitch;
    private RecyclerView productImagesRecyclerView;
    private Button addImageButton, addProductButton;
    private ImageButton backButton;
    private List<Uri> imageUris = new ArrayList<>();
    private ProductImagesAdapter imagesAdapter;
    private List<Category> categories;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<String> permissionLauncher;
    private static final String UPLOAD_PRESET = "upload_hao";
    private RetrofitService retrofitService;
    private static final String TAG = "AddProductActivity";
    private ExecutorService executorService;
    private boolean isActivityDestroyed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        retrofitService = new RetrofitService();
        executorService = Executors.newSingleThreadExecutor();

        // Initialize views with null check
        categorySpinner = findViewById(R.id.categorySpinner);
        productNameEditText = findViewById(R.id.productNameEditText);
        priceEditText = findViewById(R.id.priceEditText);
        quantityEditText = findViewById(R.id.quantityEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        isActiveSwitch = findViewById(R.id.isActiveSwitch);
        isSellingSwitch = findViewById(R.id.isSellingSwitch);
        productImagesRecyclerView = findViewById(R.id.productImagesRecyclerView);
        addImageButton = findViewById(R.id.addImageButton);
        backButton = findViewById(R.id.backButton);
        addProductButton = findViewById(R.id.addProductButton);

        if (categorySpinner == null || productNameEditText == null || priceEditText == null ||
                quantityEditText == null || descriptionEditText == null || isActiveSwitch == null ||
                isSellingSwitch == null || productImagesRecyclerView == null || addImageButton == null ||
                backButton == null || addProductButton == null) {
            Toast.makeText(this, "Error initializing views", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize RecyclerView
        productImagesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imagesAdapter = new ProductImagesAdapter(imageUris, position -> {
            if (position >= 0 && position < imageUris.size()) {
                imageUris.remove(position);
                imagesAdapter.notifyDataSetChanged();
            }
        });
        productImagesRecyclerView.setAdapter(imagesAdapter);

        // Initialize image picker
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri imageUri = result.getData().getData();
                if (imageUri != null && !imageUris.contains(imageUri)) {
                    imageUris.add(imageUri);
                    imagesAdapter.notifyDataSetChanged();
                    requestStoragePermission();
                } else {
                    Toast.makeText(this, "Image already selected or invalid", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Initialize permission launcher
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (!isGranted) {
                Toast.makeText(this, "Storage permission is required to upload images", Toast.LENGTH_SHORT).show();
            }
        });

        // Load categories
        loadCategories();

        // Button events
        backButton.setOnClickListener(v -> finish());
        addImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });
        addProductButton.setOnClickListener(v -> addProduct());
    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    private void loadCategories() {
        CategoryAPI categoryAPI = retrofitService.getRetrofit().create(CategoryAPI.class);
        categoryAPI.GetAllCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (!isActivityDestroyed && response.isSuccessful() && response.body() != null) {
                    categories = response.body();
                    List<String> categoryNames = new ArrayList<>();
                    for (Category category : categories) {
                        if (category != null && category.getCategory_Name() != null) {
                            categoryNames.add(category.getCategory_Name());
                        }
                    }
                    if (!categoryNames.isEmpty()) {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(AddProductActivity.this,
                                android.R.layout.simple_spinner_item, categoryNames);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        categorySpinner.setAdapter(adapter);
                    } else {
                        Toast.makeText(AddProductActivity.this, "No categories available", Toast.LENGTH_SHORT).show();
                    }
                } else if (!isActivityDestroyed) {
                    Toast.makeText(AddProductActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                if (!isActivityDestroyed) {
                    Toast.makeText(AddProductActivity.this, "Error loading categories: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addProduct() {
        if (productNameEditText == null || priceEditText == null || quantityEditText == null ||
                descriptionEditText == null || categorySpinner == null || addProductButton == null) {
            Toast.makeText(this, "Error initializing input fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String productName = productNameEditText.getText().toString().trim();
        String priceStr = priceEditText.getText().toString().trim();
        String quantityStr = quantityEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        boolean isActive = isActiveSwitch.isChecked();
        boolean isSelling = isSellingSwitch.isChecked();
        int categoryIndex = categorySpinner.getSelectedItemPosition();

        // Validate input
        if (productName.isEmpty()) {
            productNameEditText.setError("Product name is required");
            return;
        }
        if (priceStr.isEmpty()) {
            priceEditText.setError("Price is required");
            return;
        }
        if (quantityStr.isEmpty()) {
            quantityEditText.setError("Quantity is required");
            return;
        }
        if (categories == null || categoryIndex < 0 || categoryIndex >= categories.size()) {
            Toast.makeText(this, "Please select a valid category", Toast.LENGTH_SHORT).show();
            return;
        }
        if (imageUris.isEmpty()) {
            Toast.makeText(this, "Please select at least one image", Toast.LENGTH_SHORT).show();
            return;
        }

        int price, quantity;
        try {
            price = Integer.parseInt(priceStr);
            quantity = Integer.parseInt(quantityStr);
            if (price <= 0 || quantity <= 0) {
                Toast.makeText(this, "Price and quantity must be positive", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price or quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create Product object with createdAt in yyyy-MM-dd format
        String createdAt = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        Product product = new Product(
                0, // id (generated by backend)
                productName,
                description,
                0, // sold
                isActive ? 1 : 0,
                isSelling ? 1 : 0,
                createdAt,
                price,
                quantity,
                new ArrayList<>(), // Empty product images list initially
                categories.get(categoryIndex).getId()
        );
        product.setOrderItem(new ArrayList<>());
        product.setCart(new ArrayList<>());

        // Show loading dialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Adding product...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Upload images to Cloudinary
        uploadImagesToCloudinary(product, progressDialog);
    }

    private void uploadImagesToCloudinary(Product product, ProgressDialog progressDialog) {
        if (imageUris.isEmpty()) {
            if (!isActivityDestroyed) {
                progressDialog.dismiss();
                Toast.makeText(this, "No images selected", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        List<ProductImage> productImages = new ArrayList<>();
        final int[] uploadCount = {0};
        final int totalImages = imageUris.size();

        for (Uri imageUri : imageUris) {
            executorService.execute(() -> {
                InputStream inputStream = null;
                try {
                    inputStream = getContentResolver().openInputStream(imageUri);
                    if (inputStream == null) {
                        Log.e(TAG, "Failed to open input stream for URI: " + imageUri);
                        return;
                    }
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2; // Reduce image size to optimize upload
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
                    byte[] imageBytes = baos.toByteArray();
                    bitmap.recycle();

                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageBytes);
                    MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", "image_" + System.currentTimeMillis() + ".jpg", requestFile);
                    RequestBody presetPart = RequestBody.create(MediaType.parse("text/plain"), UPLOAD_PRESET);

                    CloudinaryAPI cloudinaryAPI = retrofitService.getCloudinaryRetrofit().create(CloudinaryAPI.class);
                    Call<CloudinaryResponse> call = cloudinaryAPI.uploadImage(filePart, presetPart);
                    call.enqueue(new Callback<CloudinaryResponse>() {
                        @Override
                        public void onResponse(Call<CloudinaryResponse> call, Response<CloudinaryResponse> response) {
                            if (!isActivityDestroyed) {
                                if (response.isSuccessful() && response.body() != null) {
                                    String imageUrl = response.body().getSecure_url();
                                    synchronized (productImages) {
                                        productImages.add(new ProductImage(0, imageUrl));
                                        uploadCount[0]++;
                                        if (uploadCount[0] == totalImages) {
                                            product.setProductImages(productImages);
                                            addProductToServer(product, progressDialog);
                                        }
                                    }
                                } else {
                                    progressDialog.dismiss();
                                    String errorMessage = response.message();
                                    try {
                                        if (response.errorBody() != null) {
                                            errorMessage = response.errorBody().string();
                                        }
                                    } catch (IOException e) {
                                        Log.e(TAG, "Error reading errorBody: ", e);
                                    }
                                    Toast.makeText(AddProductActivity.this, "Failed to upload image: " + errorMessage, Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "Image upload error: " + errorMessage);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<CloudinaryResponse> call, Throwable t) {
                            if (!isActivityDestroyed) {
                                progressDialog.dismiss();
                                Toast.makeText(AddProductActivity.this, "Image upload error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Image upload error: ", t);
                            }
                        }
                    });
                } catch (IOException e) {
                    if (!isActivityDestroyed) {
                        progressDialog.dismiss();
                        Toast.makeText(AddProductActivity.this, "Error processing image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error processing image: ", e);
                    }
                } finally {
                    try {
                        if (inputStream != null) inputStream.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Error closing stream: ", e);
                    }
                }
            });
        }
    }

    private void addProductToServer(Product product, ProgressDialog progressDialog) {
        ProductAPI productAPI = retrofitService.getRetrofit().create(ProductAPI.class);
        Gson gson = new Gson();
        Log.d(TAG, "Product JSON: " + gson.toJson(product));
        Call<Map<String, Object>> call = productAPI.addProduct(product);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (!isActivityDestroyed) {
                    progressDialog.dismiss();
                    if (response.isSuccessful() && response.body() != null) {
                        Map<String, Object> result = response.body();
                        Boolean success = (Boolean) result.get("success");
                        String message = (String) result.get("message");
                        if (success != null && success) {
                            Toast.makeText(AddProductActivity.this, message, Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AddProductActivity.this, message != null ? message : "Failed to add product", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Server error: " + message);
                        }
                    } else {
                        String errorMessage = response.message();
                        try {
                            if (response.errorBody() != null) {
                                errorMessage = response.errorBody().string();
                            }
                        } catch (IOException e) {
                            Log.e(TAG, "Error reading errorBody: ", e);
                        }
                        Toast.makeText(AddProductActivity.this, "Failed to add product: " + errorMessage, Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Server error: " + errorMessage);
                    }
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                if (!isActivityDestroyed) {
                    progressDialog.dismiss();
                    Toast.makeText(AddProductActivity.this, "Error adding product: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "API addProduct failed: ", t);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isActivityDestroyed = true;
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }
        // Clear Glide cache and resources
        if (imagesAdapter != null) {
            imagesAdapter.clear();
        }
    }
}
