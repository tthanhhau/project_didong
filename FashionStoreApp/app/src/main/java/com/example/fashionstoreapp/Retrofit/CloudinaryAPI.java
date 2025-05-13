package com.example.fashionstoreapp.Retrofit;

import com.example.fashionstoreapp.Model.CloudinaryResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface CloudinaryAPI {
    @Multipart
    @POST("image/upload")
    Call<CloudinaryResponse> uploadImage(
            @Part MultipartBody.Part file,
            @Part("upload_preset") RequestBody uploadPreset
    );
}