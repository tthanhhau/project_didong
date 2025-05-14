
package com.example.fashionstoreapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fashionstoreapp.Activity.MainActivity;
import com.example.fashionstoreapp.Model.Address;
import com.example.fashionstoreapp.Model.User;
import com.example.fashionstoreapp.R;
import com.example.fashionstoreapp.Retrofit.UserAPI;
import com.example.fashionstoreapp.Somethings.ObjectSharedPreferences;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.HttpException;

public class LoginAdminActivity extends AppCompatActivity {

    private EditText etAdminId, etAdminPassword;
    private Button btnAdminLogin;
    private ProgressBar progressBar;
    private User user;

    private static final String TAG = "LoginAdminActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_admin);

        initViews();
        setupListeners();
    }

    private void initViews() {
        etAdminId = findViewById(R.id.edtUsername);
        etAdminPassword = findViewById(R.id.edtPassword);
        btnAdminLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupListeners() {
        btnAdminLogin.setOnClickListener(v -> loginAdmin());
    }

    private void loginAdmin() {
        String adminId = etAdminId.getText().toString().trim();
        String password = etAdminPassword.getText().toString().trim();

        if (TextUtils.isEmpty(adminId)) {
            etAdminId.setError("Vui lòng nhập ID Admin");
            etAdminId.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etAdminPassword.setError("Vui lòng nhập mật khẩu");
            etAdminPassword.requestFocus();
            return;
        }

        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        UserAPI.userApi.LoginAdmin(adminId, password).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                if (response.isSuccessful() && response.body() != null) {
                    user = response.body();
                    Toast.makeText(LoginAdminActivity.this, "Đăng nhập admin thành công", Toast.LENGTH_SHORT).show();
                    saveUserAndNavigate(user);
                } else {
                    String errorMsg = "Không có phản hồi từ máy chủ";
                    if (response.errorBody() != null) {
                        try {
                            errorMsg = response.errorBody().string();
                        } catch (IOException e) {
                            Log.e(TAG, "Lỗi khi đọc errorBody: " + e.getMessage(), e);
                        }
                    }
                    Toast.makeText(LoginAdminActivity.this, "ID hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Đăng nhập admin thất bại: " + response.code() + " - " + errorMsg);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                String errorMsg = t.getMessage() != null ? t.getMessage() : "Lỗi kết nối không xác định";
                Toast.makeText(LoginAdminActivity.this, "Lỗi kết nối: " + errorMsg, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Gọi API đăng nhập admin thất bại: " + errorMsg, t);
                if (t instanceof HttpException) {
                    HttpException exception = (HttpException) t;
                    Log.e(TAG, "Mã lỗi HTTP: " + exception.code());
                }
            }
        });
    }

    private void saveUserAndNavigate(User user) {
        ObjectSharedPreferences.saveObjectToSharedPreference(LoginAdminActivity.this, "User", "MODE_PRIVATE", user);
        if (user.getAddress() != null && user.getPhoneNumber() != null) {
            Address address = new Address(user.getUserName(), user.getPhoneNumber(), user.getAddress());
            ObjectSharedPreferences.saveObjectToSharedPreference(LoginAdminActivity.this, "address", "MODE_PRIVATE", address);
        }

        Intent intent = new Intent(LoginAdminActivity.this, MainActivity.class);
        intent.putExtra("object", user);
        startActivity(intent);
        finish();
    }
}
