package com.example.fashionstoreapp.Retrofit;

import com.example.fashionstoreapp.Model.Rating;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RatingAPI {
    RetrofitService retrofitService = new RetrofitService();
    RatingAPI ratingAPI = retrofitService.getRetrofit().create(RatingAPI.class);

    @GET("/getRating")
    Call<List<Rating>> getRating(@Query("id") int id);

    @GET("/checkRating")
    Call<Map<String, Object>> checkRating(@Query("user_id") String user_id, @Query("id") int id);

    @POST("/addRating")
    Call<Map<String, Object>> addRating(@Body Rating rating);
}