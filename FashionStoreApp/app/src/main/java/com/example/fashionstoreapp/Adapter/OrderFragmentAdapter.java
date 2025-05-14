package com.example.fashionstoreapp.Adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.fashionstoreapp.Fragment.AllOrderFragment;
import com.example.fashionstoreapp.Fragment.PayOnDeliveryFragment;
import com.example.fashionstoreapp.Fragment.PayWithZalopayFragment;

public class OrderFragmentAdapter extends FragmentStateAdapter {

    private final Bundle fragmentArgs; // Lưu Bundle chứa thông tin quyền admin

    public OrderFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, Bundle args) {
        super(fragmentManager, lifecycle);
        this.fragmentArgs = args;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        if (position == 0) {
            fragment = new AllOrderFragment();
        } else if (position == 1) {
            fragment = new PayOnDeliveryFragment();
        } else {
            fragment = new PayWithZalopayFragment();
        }
        // Truyền Bundle vào Fragment
        if (fragmentArgs != null) {
            fragment.setArguments(fragmentArgs);
        }
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}