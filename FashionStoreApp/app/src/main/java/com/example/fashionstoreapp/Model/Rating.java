package com.example.fashionstoreapp.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Rating {
    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("rate")
    @Expose
    private int rate;
    @SerializedName("description")
    @Expose
    private String description;

    private User user;
    private Product product;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Rating(int id, int rate, String description, User user, Product product) {
        this.id = id;
        this.rate = rate;
        this.description = description;
        this.user = user;
        this.product = product;
    }
}
