package StoreApp.StoreApp.service;

import java.util.List;

import org.springframework.stereotype.Service;

import StoreApp.StoreApp.entity.Cart;
import StoreApp.StoreApp.entity.Product;
import StoreApp.StoreApp.entity.Rating;

public interface RatingService {
	
	List<Rating> getRatingByIdProduct(int id);

	
	void addRating(Rating rating);

	boolean checkPurchaseAndRating(String userId, Integer productId);
	boolean existsByUserIdAndProductId(String userId,Integer productId);
	
}
