package StoreApp.StoreApp.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import StoreApp.StoreApp.entity.Order;
import StoreApp.StoreApp.entity.Rating;
import StoreApp.StoreApp.repository.OrderRepository;
import StoreApp.StoreApp.repository.RatingRepository;
import StoreApp.StoreApp.service.RatingService;

@Service
public class RatingServiceImpl implements RatingService{
	@Autowired
	RatingRepository ratingRepository;
	
	@Override
	public List<Rating> getRatingByIdProduct(int id) {
		return ratingRepository.getRatingsByProductIdNative(id);
	}
	@Override
	public boolean checkPurchaseAndRating(String userId, Integer productId) {
	    return ratingRepository.existsByProduct_IdAndUser_Id(productId, userId);
	}

	@Override
	public void addRating(Rating rating) {
		 ratingRepository.save(rating);
	}
	@Override
	public boolean existsByUserIdAndProductId(String userId,Integer productId) {
		return ratingRepository.existsByProduct_IdAndUser_Id(productId,userId);
	}
}
