
package com.example.fashionstoreapp.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.example.fashionstoreapp.Model.Order;
import com.example.fashionstoreapp.Model.User;
import com.example.fashionstoreapp.R;
import com.example.fashionstoreapp.Retrofit.UserAPI;
import com.example.fashionstoreapp.Somethings.ObjectSharedPreferences;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserActivity extends AppCompatActivity {
    ImageView ivHome, ivUser, ivCart, ivHistory, ivAvatar;
    Button btnEditProfile, btnLogout;
    TextView tvFullName, tvId, tvTotalOrder, tvTotalPrice, tvChangePassword, tvEmail, tvPhone, tvAddress;
    User user;
    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        AnhXa();
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        appBarClick();
        LoadData();
        btnLogoutClick();
        tvChangePasswordClick();
        btnEditProfileClick();
    }

    private void btnEditProfileClick() {
        btnEditProfile.setOnClickListener(v -> {
            startActivity(new Intent(UserActivity.this, EditProfileActivity.class));
        });
    }

    private void tvChangePasswordClick() {
        tvChangePassword.setOnClickListener(v -> {
            Dialog dialog = new Dialog(UserActivity.this);
            dialog.setContentView(R.layout.dialog_change_password);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setGravity(Gravity.BOTTOM);
            dialog.show();
            // Anh xa dialog
            ConstraintLayout clChangePassword = dialog.findViewById(R.id.clChangePassword);
            ConstraintLayout clChangePasswordSuccess = dialog.findViewById(R.id.clChangePasswordSuccess);
            EditText etOldPassword = dialog.findViewById(R.id.etOldPassword);
            EditText etNewPassword = dialog.findViewById(R.id.etNewPassword);
            EditText etReNewPassword = dialog.findViewById(R.id.etReNewPassword);
            TextView tvErrorChangePassword = dialog.findViewById(R.id.tvErrorChangePassword);
            Button btnChangePassword = dialog.findViewById(R.id.btnChangePassword);
            Button btnOK = dialog.findViewById(R.id.btnOk);
            // ====
            btnChangePassword.setOnClickListener(v1 -> {
                String password = etOldPassword.getText().toString();
                String newPassword = etNewPassword.getText().toString();
                String reNewPassword = etReNewPassword.getText().toString();
                user = ObjectSharedPreferences.getSavedObjectFromPreference(UserActivity.this, "User", "MODE_PRIVATE", User.class);
                if (user == null) {
                    tvErrorChangePassword.setText("Vui lòng đăng nhập lại");
                    return;
                }
                if (password.equals(user.getPassword())) {
                    if (newPassword.equals(reNewPassword)) {
                        UserAPI.userApi.changePassword(user.getId(), newPassword).enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                String pass = response.body();
                                if (pass != null) {
                                    clChangePassword.setVisibility(View.GONE);
                                    clChangePasswordSuccess.setVisibility(View.VISIBLE);
                                    user.setPassword(pass);
                                    ObjectSharedPreferences.saveObjectToSharedPreference(UserActivity.this, "User", "MODE_PRIVATE", user);
                                    btnOK.setOnClickListener(v2 -> dialog.dismiss());
                                } else {
                                    tvErrorChangePassword.setText("Lỗi đổi mật khẩu, vui lòng thử lại");
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                tvErrorChangePassword.setText("Lỗi kết nối: " + t.getMessage());
                            }
                        });
                    } else {
                        tvErrorChangePassword.setText("Mật khẩu mới và xác nhận mật khẩu không khớp!");
                    }
                } else {
                    tvErrorChangePassword.setText("Mật khẩu hiện tại không đúng");
                }
            });
        });
    }

    private void btnLogoutClick() {
        btnLogout.setOnClickListener(v -> {
            user = ObjectSharedPreferences.getSavedObjectFromPreference(UserActivity.this, "User", "MODE_PRIVATE", User.class);
            if (user == null) {
                startActivity(new Intent(UserActivity.this, LoginActivity.class));
                finish();
                return;
            }
            if (Objects.equals(user.getLoginType(), "google")) {
                googleSignInClient.signOut().addOnCompleteListener(task -> {
                    ObjectSharedPreferences.saveObjectToSharedPreference(UserActivity.this, "User", "MODE_PRIVATE", null);
                    startActivity(new Intent(UserActivity.this, LoginActivity.class));
                    finish();
                });
            } else {
                ObjectSharedPreferences.saveObjectToSharedPreference(UserActivity.this, "User", "MODE_PRIVATE", null);
                startActivity(new Intent(UserActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void LoadData() {
        user = ObjectSharedPreferences.getSavedObjectFromPreference(UserActivity.this, "User", "MODE_PRIVATE", User.class);
        if (user == null || user.getId() == null || user.getPassword() == null) {
            Toast.makeText(UserActivity.this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(UserActivity.this, LoginActivity.class));
            finish();
            return;
        }
        UserAPI.userApi.Login(user.getId(), user.getPassword()).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    user = response.body();
                    ObjectSharedPreferences.saveObjectToSharedPreference(UserActivity.this, "User", "MODE_PRIVATE", user);

                    tvFullName.setText(user.getUserName() != null ? user.getUserName() : "N/A");
                    tvId.setText(user.getId() != null ? user.getId() : "N/A");
                    tvTotalOrder.setText(user.getOrder() != null ? String.valueOf(user.getOrder().size()) : "0");

                    int totalPrice = 0;
                    if (user.getOrder() != null) {
                        for (Order o : user.getOrder()) {
                            totalPrice += o.getTotal();
                        }
                    }
                    Locale localeEN = new Locale("en", "EN");
                    NumberFormat en = NumberFormat.getInstance(localeEN);
                    tvTotalPrice.setText(en.format(totalPrice));

                    tvPhone.setText(user.getPhoneNumber() != null ? user.getPhoneNumber() : "N/A");
                    tvAddress.setText(user.getAddress() != null ? user.getAddress() : "N/A");
                    tvEmail.setText(user.getEmail() != null ? user.getEmail() : "N/A");

                    // Kiểm tra loginType
                    if (Objects.equals(user.getLoginType(), "google")) {
                        tvChangePassword.setVisibility(View.GONE);
                    } else {
                        tvChangePassword.setVisibility(View.VISIBLE);
                    }

                    // Load avatar
                    if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                        Glide.with(UserActivity.this)
                                .load(user.getAvatar())
                                .into(ivAvatar);
                    }
                } else {
                    Toast.makeText(UserActivity.this, "Không thể tải thông tin người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(UserActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void appBarClick() {
        ivHome.setOnClickListener(v -> {
            startActivity(new Intent(UserActivity.this, MainActivity.class));
            finish();
        });
        ivUser.setOnClickListener(v -> {
            startActivity(new Intent(UserActivity.this, UserActivity.class));
            finish();
        });
        ivCart.setOnClickListener(v -> {
            startActivity(new Intent(UserActivity.this, CartActivity.class));
            finish();
        });
        ivHistory.setOnClickListener(v -> {
            startActivity(new Intent(UserActivity.this, OrderActivity.class));
            finish();
        });
    }

    private void AnhXa() {
        ivHome = findViewById(R.id.ivHome);
        ivUser = findViewById(R.id.ivUser);
        ivCart = findViewById(R.id.ivCart);
        ivHistory = findViewById(R.id.ivHistory);
        tvFullName = findViewById(R.id.tvFullName);
        tvId = findViewById(R.id.tvId);
        tvTotalOrder = findViewById(R.id.tvTotalOrder);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvChangePassword = findViewById(R.id.tvChangePassword);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvAddress = findViewById(R.id.tvAddress);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnLogout = findViewById(R.id.btnLogout);
        ivAvatar = findViewById(R.id.ivAvatar);
    }
}
