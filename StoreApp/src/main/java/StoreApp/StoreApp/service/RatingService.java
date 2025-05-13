package StoreApp.StoreApp.service;

import java.util.List;

import org.springframework.stereotype.Service;

import StoreApp.StoreApp.entity.Cart;
import StoreApp.StoreApp.entity.Product;
import StoreApp.StoreApp.entity.Rating;
@Service
public interface RatingService {
	
	List<Rating> getRatingByIdProduct(int id);

	List<Object[]> checkPurchaseAndRating(String user_id, int id);
	
	void addRating(Rating rating);
	
	
}
