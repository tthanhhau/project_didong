package com.example.fashionstoreapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.fashionstoreapp.Model.Address;
import com.example.fashionstoreapp.Model.User;
import com.example.fashionstoreapp.R;
import com.example.fashionstoreapp.Retrofit.UserAPI;
import com.example.fashionstoreapp.Somethings.ObjectSharedPreferences;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText etPassword, etUserName;
    Button btnLogin;
    TextView tvRegister, tvForgotPassword, tvAdmin;
    User user = new User();
    ProgressBar progressBar;
    ConstraintLayout clGoogle;

    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;

    private static final int RC_SIGN_IN = 1000;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        anhXa();
        initGoogleSignIn();
        btnLoginClick();
        tvRegisterClick();
        tvForgotPasswordClick();
        clGoogleClick();
        tvAdminClick();
    }

    private void initGoogleSignIn() {
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            navigateToSecondActivity();
        }
    }

    private void tvAdminClick() {
        if (tvAdmin != null) {
            tvAdmin.setOnClickListener(v -> {
                Log.d(TAG, "Navigating to LoginAdminActivity");
                startActivity(new Intent(LoginActivity.this, LoginAdminActivity.class));
            });
        } else {
            Log.e(TAG, "tvAdmin is null. Check R.id.tvAdmin in activity_login.xml");
            Toast.makeText(this, "Error: Admin link not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void clGoogleClick() {
        if (clGoogle != null) {
            clGoogle.setOnClickListener(v -> {
                if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);
                }
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            });
        } else {
            Log.e(TAG, "clGoogle is null. Check R.id.clGoogle in activity_login.xml");
            Toast.makeText(this, "Error: Google Sign-In button not found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
                navigateToSecondActivity();
            } catch (ApiException e) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                Toast.makeText(this, "Google Sign-In failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Google Sign-In error: " + e.getStatusCode());
            }
        }
    }

    private void navigateToSecondActivity() {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String id = acct.getId();
            String name = acct.getDisplayName();
            String email = acct.getEmail();
            String avatar = acct.getPhotoUrl() != null ? acct.getPhotoUrl().toString() : null;
            UserAPI.userApi.LoginWitGoogle(id, name, email, avatar).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }
                    user = response.body();
                    if (user != null) {
                        if (user.getRole() == null) {
                            user.setRole("user");
                        }
                        user.setLoginType("google");
                        Log.d(TAG, "Google login user: " + user.toString());
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        saveUserAndNavigate(user);
                    } else {
                        Toast.makeText(LoginActivity.this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Google Sign-In: User response null");
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }
                    Toast.makeText(LoginActivity.this, "Connection error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Google Sign-In API call failed: " + t.getMessage());
                }
            });
        } else {
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
            Toast.makeText(this, "Google Sign-In account not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void tvForgotPasswordClick() {
        if (tvForgotPassword != null) {
            tvForgotPassword.setOnClickListener(v -> {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            });
        } else {
            Log.e(TAG, "tvForgotPassword is null. Check R.id.tvForgotPassword in activity_login.xml");
            Toast.makeText(this, "Error: Forgot Password link not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void tvRegisterClick() {
        if (tvRegister != null) {
            tvRegister.setOnClickListener(v -> {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            });
        } else {
            Log.e(TAG, "tvRegister is null. Check R.id.tvRegister in activity_login.xml");
            Toast.makeText(this, "Error: Register link not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void btnLoginClick() {
        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> Login());
        } else {
            Log.e(TAG, "btnLogin is null. Check R.id.btnSignUp in activity_login.xml");
            Toast.makeText(this, "Error: Login button not found", Toast.LENGTH_LONG).show();
        }
    }

    private void Login() {
        if (etUserName == null || etPassword == null) {
            Toast.makeText(this, "Error: Username or Password field not found", Toast.LENGTH_SHORT).show();
            return;
        }
        String username = etUserName.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            etUserName.setError("Please enter your username");
            etUserName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Please enter your password");
            etPassword.requestFocus();
            return;
        }

        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        UserAPI.userApi.Login(username, password).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                user = response.body();
                if (user != null) {
                    if (user.getRole() == null) {
                        user.setRole("user");
                    }
                    Log.d(TAG, "User login: " + user.toString());
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    saveUserAndNavigate(user);
                } else {
                    Toast.makeText(LoginActivity.this, "Incorrect Username or Password", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Login failed: Invalid credentials, code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                Toast.makeText(LoginActivity.this, "Connection error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Login API call failed: " + t.getMessage());
            }
        });
    }

    private void saveUserAndNavigate(User user) {
        ObjectSharedPreferences.saveObjectToSharedPreference(LoginActivity.this, "User", "MODE_PRIVATE", user);
        if (user.getAddress() != null && user.getPhoneNumber() != null) {
            Address address = new Address(user.getUserName(), user.getPhoneNumber(), user.getAddress());
            ObjectSharedPreferences.saveObjectToSharedPreference(LoginActivity.this, "address", "MODE_PRIVATE", address);
        }

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("object", user);
        startActivity(intent);
        finish();
    }

    private void anhXa() {
        etUserName = findViewById(R.id.etUserName);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnSignUp); // Sửa thành btnSignUp
        tvRegister = findViewById(R.id.tvRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        progressBar = findViewById(R.id.progressBar);
        clGoogle = findViewById(R.id.clGoogle);
        tvAdmin = findViewById(R.id.tvAdmin);

        // Kiểm tra null để debug
        if (etUserName == null) Log.e(TAG, "etUserName is null. Check R.id.etUserName in activity_login.xml");
        if (etPassword == null) Log.e(TAG, "etPassword is null. Check R.id.etPassword in activity_login.xml");
        if (btnLogin == null) Log.e(TAG, "btnLogin is null. Check R.id.btnSignUp in activity_login.xml");
        if (tvRegister == null) Log.e(TAG, "tvRegister is null. Check R.id.tvRegister in activity_login.xml");
        if (tvForgotPassword == null) Log.e(TAG, "tvForgotPassword is null. Check R.id.tvForgotPassword in activity_login.xml");
        if (progressBar == null) Log.e(TAG, "progressBar is null. Check R.id.progressBar in activity_login.xml");
        if (clGoogle == null) Log.e(TAG, "clGoogle is null. Check R.id.clGoogle in activity_login.xml");
        if (tvAdmin == null) Log.e(TAG, "tvAdmin is null. Check R.id.tvAdmin in activity_login.xml");
    }
}