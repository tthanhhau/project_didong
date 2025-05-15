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
	private String product_name;
	@SerializedName("description")
	private String description;
	@SerializedName("sold")
	private int sold;
	@SerializedName("is_active")
	private int is_active;
	@SerializedName("is_selling")
	private int is_selling;
	@SerializedName("created_at")
	private String created_at;
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
		this.product_name = productName;
		this.description = description;
		this.sold = sold;
		this.is_active = isActive;
		this.is_selling = isSelling;
		this.created_at = createdAt;
		this.price = price;
		this.quantity = quantity;
		this.productImages = productImages;
		this.categoryId = categoryId;

	}

	// Constructor mặc định
	public Product() {}

	public Product(int id) {
		this.id=id;
	}


	// Getters và setters
	public int getId() { return id; }
	public void setId(int id) { this.id = id; }
	public String getProductName() { return product_name; }
	public void setProductName(String productName) { this.product_name = productName; }
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	public int getSold() { return sold; }
	public void setSold(int sold) { this.sold = sold; }
	public int getIsActive() { return is_active; }
	public void setIsActive(int isActive) { this.is_active = isActive; }
	public int getIsSelling() { return is_selling; }
	public void setIsSelling(int isSelling) { this.is_selling = isSelling; }
	public String getCreatedAt() { return created_at; }
	public void setCreatedAt(String createdAt) { this.created_at = createdAt; }
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