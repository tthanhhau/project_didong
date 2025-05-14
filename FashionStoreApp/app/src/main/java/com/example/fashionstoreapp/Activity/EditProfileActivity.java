
        package com.example.fashionstoreapp.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.fashionstoreapp.Model.User;
import com.example.fashionstoreapp.R;
import com.example.fashionstoreapp.Retrofit.UserAPI;
import com.example.fashionstoreapp.Somethings.ObjectSharedPreferences;
import com.example.fashionstoreapp.Somethings.RealPathUtil;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    ImageView ivAvatar, ivBack;
    EditText etFullName, etEmail, etPhoneNumber, etAddress;
    Button btnUpdate;
    User user;
    TextView tvChangePicture, tvError;
    ProgressDialog progressDialog;

    // Upload Image
    private Uri mUri;
    public static final int MY_REQUEST_CODE = 100;
    public static final String TAG = EditProfileActivity.class.getName();
    public static String[] storage_permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String[] storage_permissions_33 = {
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_VIDEO
    };

    public static String[] permissions() {
        String[] p;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            p = storage_permissions_33;
        } else {
            p = storage_permissions;
        }
        return p;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        AnhXa();
        LoadData();
        ivBackClick();
        tvChangePictureClick();
        btnUpdateClick();
    }

    private void btnUpdateClick() {
        btnUpdate.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phoneNumber = etPhoneNumber.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            if (fullName.isEmpty()) {
                tvError.setText("Please enter your name!");
                return;
            }
            if (email.isEmpty()) {
                tvError.setText("Please enter your email!");
                return;
            }
            if (phoneNumber.isEmpty()) {
                tvError.setText("Please enter your phone number!");
                return;
            }
            if (address.isEmpty()) {
                tvError.setText("Please enter your address!");
                return;
            }

            user = ObjectSharedPreferences.getSavedObjectFromPreference(
                    EditProfileActivity.this, "User", "MODE_PRIVATE", User.class);
            if (user == null) {
                tvError.setText("Please log in again!");
                startActivity(new Intent(EditProfileActivity.this, LoginActivity.class));
                finish();
                return;
            }

            RequestBody userId = RequestBody.create(MediaType.parse("multipart/form-data"), user.getId());
            RequestBody fullNameBody = RequestBody.create(MediaType.parse("multipart/form-data"), fullName);
            RequestBody emailBody = RequestBody.create(MediaType.parse("multipart/form-data"), email);
            RequestBody phoneNumberBody = RequestBody.create(MediaType.parse("multipart/form-data"), phoneNumber);
            RequestBody addressBody = RequestBody.create(MediaType.parse("multipart/form-data"), address);
            MultipartBody.Part avatar = null;

            if (mUri != null) {
                String imagePath = RealPathUtil.getRealPath(this, mUri);
                Log.d(TAG, "Image path: " + imagePath);
                File file = new File(imagePath);
                if (file.exists()) {
                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                    avatar = MultipartBody.Part.createFormData("avatar", file.getName(), requestFile);
                } else {
                    tvError.setText("Selected image file is invalid!");
                    return;
                }
            }

            progressDialog = new ProgressDialog(EditProfileActivity.this);
            progressDialog.setMessage("Updating profile...");
            progressDialog.setTitle("Please wait");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();

            UserAPI.userApi.update(userId, avatar, fullNameBody, emailBody, phoneNumberBody, addressBody)
                    .enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            progressDialog.dismiss();
                            if (response.isSuccessful() && response.body() != null) {
                                User userUpdate = response.body();
                                ObjectSharedPreferences.saveObjectToSharedPreference(
                                        EditProfileActivity.this, "User", "MODE_PRIVATE", userUpdate);
                                Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(EditProfileActivity.this, UserActivity.class));
                                finish();
                            } else {
                                tvError.setText("Failed to update profile. Please try again.");
                                Log.e(TAG, "Update failed: " + response.message());
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            progressDialog.dismiss();
                            tvError.setText("Connection error: " + t.getMessage());
                            Log.e(TAG, "Update API call failed: " + t.getMessage());
                        }
                    });
        });
    }

    private void tvChangePictureClick() {
        tvChangePicture.setOnClickListener(v -> CheckPermissions());
    }

    private void ivBackClick() {
        ivBack.setOnClickListener(v -> finish());
    }

    private void LoadData() {
        user = ObjectSharedPreferences.getSavedObjectFromPreference(
                EditProfileActivity.this, "User", "MODE_PRIVATE", User.class);
        if (user == null) {
            Toast.makeText(this, "Please log in again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(EditProfileActivity.this, LoginActivity.class));
            finish();
            return;
        }
        etFullName.setText(user.getUserName() != null ? user.getUserName() : "");
        etEmail.setText(user.getEmail() != null ? user.getEmail() : "");
        etPhoneNumber.setText(user.getPhoneNumber() != null ? user.getPhoneNumber() : "");
        etAddress.setText(user.getAddress() != null ? user.getAddress() : "");
        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            Glide.with(this).load(user.getAvatar()).into(ivAvatar);
        } else {
            ivAvatar.setImageResource(R.drawable.avatar_admin); // Default avatar if none
        }
    }

    private void AnhXa() {
        ivAvatar = findViewById(R.id.ivAvatar);
        ivBack = findViewById(R.id.ivBack);
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etAddress = findViewById(R.id.etAddress);
        btnUpdate = findViewById(R.id.btnUpdate);
        tvChangePicture = findViewById(R.id.tvChangePicture);
        tvError = findViewById(R.id.tvError);
    }

    // Image Upload
    private void CheckPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            openGallery();
        } else {
            requestPermissions(permissions(), MY_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            Toast.makeText(this, "Permission denied to access gallery", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        mActivityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    private final ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Log.d(TAG, "onActivityResult");
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getData() != null) {
                        mUri = data.getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mUri);
                            ivAvatar.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            Log.e(TAG, "Failed to load image: " + e.getMessage());
                            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
    );
}
