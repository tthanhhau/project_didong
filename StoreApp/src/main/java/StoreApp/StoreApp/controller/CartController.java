package StoreApp.StoreApp.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import StoreApp.StoreApp.entity.Cart;
import StoreApp.StoreApp.entity.Product;
import StoreApp.StoreApp.entity.User;
import StoreApp.StoreApp.model.CartDto;
import StoreApp.StoreApp.service.CartService;
import StoreApp.StoreApp.service.ProductService;
import StoreApp.StoreApp.service.UserService;

@RestController
public class CartController {

	@Autowired
	CartService cartService;
	@Autowired
	ProductService productService;
	@Autowired
	UserService userService;
	
	@Autowired
    private ModelMapper modelMapper;

	@PostMapping(path = "/addtocart")
	public ResponseEntity<Cart> addToCart(String user_id, int product_id, int count) {
		System.out.println(user_id + product_id + count);
		User user = userService.findByIdAndRole(user_id, "user");
		List<Cart> listCart = cartService.GetAllCartByUser_id(user_id);
		Product product = productService.getProductById(product_id);
		int flag = 0;
		Cart cart = new Cart();
		for (Cart y : listCart) {
			if (y.getProduct().getId() == product_id) {
				y.setCount(y.getCount() + count);
				cartService.saveCart(y);
				cart = y;
				flag = 1;
			}
		}
		if (flag == 0) {
			Cart newCart = new Cart();
			newCart.setCount(count);
			newCart.setProduct(product);
			newCart.setUser(user);
			cart = cartService.saveCart(newCart);
		}
		return new ResponseEntity<>(cart, HttpStatus.OK);
	}

	@GetMapping(path = "/cartofuser")
	public ResponseEntity<List<Cart>> cartOfUser(@RequestParam("id") String userId) {
	    List<Cart> listCart = cartService.GetAllCartByUser_id(userId);
	    return new ResponseEntity<>(listCart, HttpStatus.OK);
	}

	
	@PostMapping(path = "/deletecart", consumes = "application/x-www-form-urlencoded")
	public ResponseEntity<String> DeleteCart(int cart_id, String user_id) {
		List<Cart> carts = cartService.GetAllCartByUser_id(user_id);
		System.out.println(cart_id + user_id);
		for(Cart y:carts) {
			if(cart_id == y.getId())
				cartService.deleteById(cart_id);
		}
		return new ResponseEntity<>("successfully", HttpStatus.OK);
	}
}
