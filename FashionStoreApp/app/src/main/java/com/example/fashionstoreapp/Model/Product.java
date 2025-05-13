package com.example.fashionstoreapp.Model;

import android.widget.ImageView;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Product implements Serializable {
	@SerializedName("id")
	private int id;
	@SerializedName("product_name")
	private String productName;
	@SerializedName("description")
	private String description;
	@SerializedName("sold")
	private int sold;
	@SerializedName("is_active")
	private int isActive;
	@SerializedName("is_selling")
	private int isSelling;
	@SerializedName("created_at")
	private String createdAt;
	@SerializedName("price")
	private double price;
	@SerializedName("quantity")
	private int quantity;
	@SerializedName("product_images")
	private List<ProductImage> productImages;
	@SerializedName("category_id")
	private int categoryId;
	@SerializedName("order_item")
	private List<Object> orderItem; // Thêm để khớp với JSON
	@SerializedName("cart")
	private List<Object> cart; // Thêm để khớp với JSON
	@SerializedName("rating")
	private List<Object> rating; // Thêm để khớp với JSON

	// Constructor 11 tham số
	public Product(int id, String productName, String description, int sold, int isActive, int isSelling,
				   String createdAt, double price, int quantity, List<ProductImage> productImages, int categoryId) {
		this.id = id;
		this.productName = productName;
		this.description = description;
		this.sold = sold;
		this.isActive = isActive;
		this.isSelling = isSelling;
		this.createdAt = createdAt;
		this.price = price;
		this.quantity = quantity;
		this.productImages = productImages;
		this.categoryId = categoryId;

	}

	// Constructor mặc định
	public Product() {}



	// Getters và setters
	public int getId() { return id; }
	public void setId(int id) { this.id = id; }
	public String getProductName() { return productName; }
	public void setProductName(String productName) { this.productName = productName; }
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	public int getSold() { return sold; }
	public void setSold(int sold) { this.sold = sold; }
	public int getIsActive() { return isActive; }
	public void setIsActive(int isActive) { this.isActive = isActive; }
	public int getIsSelling() { return isSelling; }
	public void setIsSelling(int isSelling) { this.isSelling = isSelling; }
	public String getCreatedAt() { return createdAt; }
	public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
	public double getPrice() { return price; }
	public void setPrice(double price) { this.price = price; }
	public int getQuantity() { return quantity; }
	public void setQuantity(int quantity) { this.quantity = quantity; }
	public List<ProductImage> getProductImages() { return productImages; }
	public void setProductImages(List<ProductImage> productImages) { this.productImages = productImages; }
	public int getCategoryId() { return categoryId; }
	public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
	public List<Object> getOrderItem() { return orderItem; }
	public void setOrderItem(List<Object> orderItem) { this.orderItem = orderItem; }
	public List<Object> getCart() { return cart; }
	public void setCart(List<Object> cart) { this.cart = cart; }

}