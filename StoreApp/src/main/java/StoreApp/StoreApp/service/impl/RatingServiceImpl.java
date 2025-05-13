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
	public List<Object[]> checkPurchaseAndRating(String user_id,int id) {
		return ratingRepository.checkPurchaseAndRatingNative(user_id,id);
	}
	@Override
	public void addRating(Rating rating) {
		 ratingRepository.save(rating);
	}
}
