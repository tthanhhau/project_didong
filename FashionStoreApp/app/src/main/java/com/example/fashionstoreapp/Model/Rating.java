package com.example.fashionstoreapp.Model;

import com.google.gson.annotations.SerializedName;

public class Rating {
    @SerializedName("id")
    private int id;

    @SerializedName("rate")
    private int rate;

    @SerializedName("description")
    private String description;

    @SerializedName("product_id")
    private Integer productId; // Sử dụng Integer để khớp với Product.id

    @SerializedName("user_id")
    private String userId;

    public Rating() {
    }

    public Rating(int rate, String description, Integer productId, String userId) {
        this.rate = rate;
        this.description = description;
        this.productId = productId;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}