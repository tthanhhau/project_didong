package com.example.fashionstoreapp.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionstoreapp.Adapter.OrderAdapter;
import com.example.fashionstoreapp.Model.Order;
import com.example.fashionstoreapp.Model.User;
import com.example.fashionstoreapp.R;
import com.example.fashionstoreapp.Retrofit.OrderAPI;
import com.example.fashionstoreapp.Somethings.ObjectSharedPreferences;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllOrderFragment extends Fragment {

    RecyclerView rvAllOrder;
    OrderAdapter orderAdapter;
    List<Order> orderList;

    public AllOrderFragment() {
        // Constructor rỗng bắt buộc
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AnhXa();
        LoadData();
    }

    private void LoadData() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvAllOrder.setLayoutManager(linearLayoutManager);

        // Lấy thông tin quyền admin từ Bundle
        Bundle args = getArguments();
        boolean isAdmin = args != null && args.getBoolean("isAdmin", false);
        User loggedInUser = ObjectSharedPreferences.getSavedObjectFromPreference(
                getContext(), "User", "MODE_PRIVATE", User.class);

        if (isAdmin) {
            // Trường hợp admin: Lấy tất cả đơn hàng của mọi khách hàng
            OrderAPI.orderAPI.getAllOrder().enqueue(new Callback<List<Order>>() {
                @Override
                public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        orderList = response.body();
                        orderAdapter = new OrderAdapter(orderList, getContext().getApplicationContext());
                        rvAllOrder.setAdapter(orderAdapter);
                        Log.d("APIResponse", "Đã tải " + orderList.size() + " đơn hàng (admin)");
                    } else {
                        Log.e("APIError", "Không thể tải tất cả đơn hàng: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<List<Order>> call, Throwable t) {
                    Log.e("APIError", "Lỗi gọi API getAllOrder: " + t.getMessage());
                }
            });
        } else if (loggedInUser != null) {
            // Trường hợp user: Lấy đơn hàng của người dùng hiện tại
            OrderAPI.orderAPI.getOrderByUserId(loggedInUser.getId()).enqueue(new Callback<List<Order>>() {
                @Override
                public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        orderList = response.body();
                        orderAdapter = new OrderAdapter(orderList, getContext().getApplicationContext());
                        rvAllOrder.setAdapter(orderAdapter);
                        Log.d("APIResponse", "Đã tải " + orderList.size() + " đơn hàng (user)");
                    } else {
                        Log.e("APIError", "Không thể tải đơn hàng cho người dùng: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<List<Order>> call, Throwable t) {
                    Log.e("APIError", "Lỗi gọi API getOrderByUserId: " + t.getMessage());
                }
            });
        }
    }

    private void AnhXa() {
        rvAllOrder = getView().findViewById(R.id.rvAllOrder);
    }
}