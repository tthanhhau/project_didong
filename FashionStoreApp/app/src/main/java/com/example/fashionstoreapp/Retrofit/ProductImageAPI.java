package com.example.fashionstoreapp.Retrofit;

import com.example.fashionstoreapp.Model.Product;
import com.example.fashionstoreapp.Model.ProductImage;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ProductImageAPI {
    RetrofitService retrofitService = new RetrofitService();
    ProductImageAPI productImageApi = retrofitService.getRetrofit().create(ProductImageAPI.class);

    @GET("/getImageByProductID")
    Call<List<ProductImage>> getImageByProduct(@Query("id")int id);
}
