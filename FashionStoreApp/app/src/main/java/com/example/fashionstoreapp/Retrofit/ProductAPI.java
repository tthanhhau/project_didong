package com.example.fashionstoreapp.Retrofit;

import com.example.fashionstoreapp.Model.Cart;
import com.example.fashionstoreapp.Model.Product;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProductAPI {

    RetrofitService retrofitService = new RetrofitService();
    ProductAPI productApi = retrofitService.getRetrofit().create(ProductAPI.class);
    @GET("/newproduct")
    Call<List<Product>> getNewProduct();

    @GET("bestsellers")
    Call<List<Product>> getBestSellers();
//    @FormUrlEncoded
    @GET("/search")
    Call<List<Product>> search(@Query("searchContent") String searchContent);

    @GET("/getAll")
    Call<List<Product>> getProducts();

    @POST("/add")
    Call<Map<String, Object>> addProduct(@Body Product product);

    @POST("/remove")
    Call<Map<String, Object>> removeProduct(@Query("id") int id);
}
