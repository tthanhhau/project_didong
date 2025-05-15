package com.example.fashionstoreapp.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
	@SerializedName("id")
	@Expose
	private String id;

	@SerializedName("login_type")
	@Expose
	private String loginType;

	@SerializedName("role")
	@Expose
	private String role;

	@SerializedName("password")
	@Expose
	private String password;

	@SerializedName("user_name")
	@Expose
	private String userName;

	@SerializedName("avatar")
	@Expose
	private String avatar;

	@SerializedName("email")
	@Expose
	private String email;

	@SerializedName("phone_number")
	@Expose
	private String phoneNumber;

	@SerializedName("address")
	@Expose
	private String address;

	@SerializedName("order")
	@Expose
	private List<Order> order;

	@SerializedName("cart")
	@Expose
	private List<Cart> cart;

	@SerializedName("rating")
	@Expose
	private List<Object> rating;

	// Default constructor
	public User() {
	}

	// Full constructor
	public User(String id, String loginType, String role, String password, String userName,
				String avatar, String email, String phoneNumber, String address,
				List<Order> order, List<Cart> cart, List<Object> rating) {
		this.id = id;
		this.loginType = loginType;
		this.role = role;
		this.password = password;
		this.userName = userName;
		this.avatar = avatar;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.address = address;
		this.order = order;
		this.cart = cart;
		this.rating = rating;
	}

	public User(String id) {
		this.id=id;
	}


	// Getters
	public String getId() {
		return id;
	}

	public String getLoginType() {
		return loginType;
	}

	public String getRole() {
		return role;
	}

	public String getPassword() {
		return password;
	}

	public String getUserName() {
		return userName;
	}

	public String getAvatar() {
		return avatar;
	}

	public String getEmail() {
		return email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getAddress() {
		return address;
	}

	public List<Order> getOrder() {
		return order;
	}

	public List<Cart> getCart() {
		return cart;
	}

	public List<Object> getRating() {
		return rating;
	}

	// Setters
	public void setId(String id) {
		this.id = id;
	}

	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setOrder(List<Order> order) {
		this.order = order;
	}

	public void setCart(List<Cart> cart) {
		this.cart = cart;
	}

	public void setRating(List<Object> rating) {
		this.rating = rating;
	}

	// Helper method to check admin status
	public boolean isAdmin() {
		return "admin".equalsIgnoreCase(role);
	}

	@Override
	public String toString() {
		return "User{" +
				"id='" + id + '\'' +
				", loginType='" + loginType + '\'' +
				", role='" + role + '\'' +
				", password='" + password + '\'' +
				", userName='" + userName + '\'' +
				", avatar='" + avatar + '\'' +
				", email='" + email + '\'' +
				", phoneNumber='" + phoneNumber + '\'' +
				", address='" + address + '\'' +
				", order=" + order +
				", cart=" + cart +
				", rating=" + rating +
				'}';
	}
}