package com.example.fashionstoreapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.fashionstoreapp.Adapter.OrderFragmentAdapter;
import com.example.fashionstoreapp.Model.Order;
import com.example.fashionstoreapp.Model.User;
import com.example.fashionstoreapp.R;
import com.example.fashionstoreapp.Retrofit.OrderAPI;
import com.example.fashionstoreapp.Somethings.ObjectSharedPreferences;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    OrderFragmentAdapter orderFragmentAdapter;
    ImageView ivHome, ivUser, ivCart, ivHistory;
    ConstraintLayout clOrder, clEmptyOrder;
    User loggedInUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        AnhXa();
        appBarClick();

        // Lấy thông tin người dùng đang đăng nhập
        loggedInUser = ObjectSharedPreferences.getSavedObjectFromPreference(
                OrderActivity.this, "User", "MODE_PRIVATE", User.class);

        // Khởi tạo FragmentManager và Adapter
        FragmentManager fragmentManager = getSupportFragmentManager();
        Bundle bundle = new Bundle();
        // Truyền thông tin quyền admin qua Bundle
        bundle.putBoolean("isAdmin", loggedInUser != null && loggedInUser.isAdmin());
        orderFragmentAdapter = new OrderFragmentAdapter(fragmentManager, getLifecycle(), bundle);
        viewPager2.setAdapter(orderFragmentAdapter);

        // Kiểm tra đơn hàng trống (chỉ áp dụng cho user)
        CheckEmpty();

        // Thiết lập TabLayout
        tabLayout.addTab(tabLayout.newTab().setText("Tất cả đơn hàng"));
        tabLayout.addTab(tabLayout.newTab().setText("Thanh toán khi nhận hàng"));
        tabLayout.addTab(tabLayout.newTab().setText("Thanh toán bằng ZaloPay"));

        // Sự kiện chọn tab
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // Đồng bộ ViewPager2 với TabLayout
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
    }

    private void CheckEmpty() {
        if (loggedInUser == null || loggedInUser.isAdmin()) {
            // Với admin hoặc không đăng nhập, không kiểm tra trống
            clOrder.setVisibility(View.VISIBLE);
            clEmptyOrder.setVisibility(View.GONE);
            return;
        }

        // Kiểm tra đơn hàng của user
        OrderAPI.orderAPI.getOrderByUserId(loggedInUser.getId()).enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.body() == null || response.body().isEmpty()) {
                    clOrder.setVisibility(View.GONE);
                    clEmptyOrder.setVisibility(View.VISIBLE);
                } else {
                    clOrder.setVisibility(View.VISIBLE);
                    clEmptyOrder.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Log.e("APIError", "Không thể kiểm tra đơn hàng: " + t.getMessage());
                Toast.makeText(OrderActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void appBarClick() {
        ivHome.setOnClickListener(v -> {
            startActivity(new Intent(OrderActivity.this, MainActivity.class));
            finish();
        });
        ivUser.setOnClickListener(v -> {
            startActivity(new Intent(OrderActivity.this, UserActivity.class));
            finish();
        });
        ivCart.setOnClickListener(v -> {
            startActivity(new Intent(OrderActivity.this, CartActivity.class));
            finish();
        });
        ivHistory.setOnClickListener(v -> {
            startActivity(new Intent(OrderActivity.this, OrderActivity.class));
            finish();
        });
    }

    private void AnhXa() {
        ivHome = findViewById(R.id.ivHome);
        ivUser = findViewById(R.id.ivUser);
        ivCart = findViewById(R.id.ivCart);
        ivHistory = findViewById(R.id.ivHistory);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager2 = findViewById(R.id.viewPager2);
        clOrder = findViewById(R.id.clOrder);
        clEmptyOrder = findViewById(R.id.clEmptyOrder);
    }
}