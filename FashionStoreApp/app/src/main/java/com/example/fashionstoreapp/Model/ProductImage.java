package com.example.fashionstoreapp.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProductImage implements Serializable {
	@SerializedName("id")
	private int id;
	@SerializedName("image_url")
	private String imageUrl;

	// Getters và setters
	public int getId() { return id; }
	public void setId(int id) { this.id = id; }
	public String getImageUrl() { return imageUrl; }
	public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

	public ProductImage(int id, String imageUrl) {
		this.id = id;
		this.imageUrl = imageUrl;
	}
}