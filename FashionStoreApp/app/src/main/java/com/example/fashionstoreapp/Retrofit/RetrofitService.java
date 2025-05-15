package com.example.fashionstoreapp.Retrofit;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {

    public static final String IPAddress = "192.168.1.13";
    private static final String CLOUDINARY_BASE_URL = "https://api.cloudinary.com/v1_1/da4f83k0v/";
    private static final String TAG = "RetrofitService";

    private final OkHttpClient okHttpClientBackend;
    private final OkHttpClient okHttpClientCloudinary;
    private final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .setLenient()
            .create();

    private Retrofit retrofitBackend;
    private Retrofit retrofitCloudinary;

    public RetrofitService() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message -> Log.d(TAG, message));
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        okHttpClientBackend = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        okHttpClientCloudinary = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        initializeRetrofit();
        Log.d(TAG, "Cloudinary Base URL: " + CLOUDINARY_BASE_URL);
    }

    private void initializeRetrofit() {
        retrofitBackend = new Retrofit.Builder()
                .client(okHttpClientBackend)
                .baseUrl("http://" + IPAddress + ":8080/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        retrofitCloudinary = new Retrofit.Builder()
                .client(okHttpClientCloudinary)
                .baseUrl(CLOUDINARY_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public Retrofit getRetrofit() {
        return retrofitBackend;
    }

    public Retrofit getCloudinaryRetrofit() {
        return retrofitCloudinary;
    }
}