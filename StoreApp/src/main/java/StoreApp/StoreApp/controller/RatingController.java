package StoreApp.StoreApp.controller;

import java.awt.print.Printable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import StoreApp.StoreApp.entity.Rating;
import StoreApp.StoreApp.service.RatingService;

@RestController
public class RatingController {
	@Autowired
	RatingService ratingService;
	
	@GetMapping("/getRating")
	public List<Rating> getRatingByID(int id) {
	    try {
	    	List<Rating> ratings= ratingService.getRatingByIdProduct(id);
	    
	        return ratings;
	    } catch (Exception e) {
	        return (List<Rating>) ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Map.of("success", false, "message", "Failed to add product"));
	    }
	}
	@GetMapping("/checkRating")
	public ResponseEntity<List<Object[]>> checkPurchaseAndRating(
	        String userId,
	        Integer productId) {
	    try {
	        List<Object[]> result = ratingService.checkPurchaseAndRating(userId, productId);
	        
	        return ResponseEntity.ok(result);
	    } catch (Exception e) {
	        // Trả về mặc định [0, 0] nếu có lỗi
	        List<Object[]> defaultResult = new ArrayList<>();
	        defaultResult.add(new Object[]{0}); // Correctly initialize the default result
	        return ResponseEntity.ok(defaultResult);
	    }
	}

	
	@PostMapping(value = "/addRating")
	public ResponseEntity<?> addRating(@RequestBody Rating rating) {
	    try {
	        System.out.println("Received rating: " + rating);
	        ratingService.addRating(rating);
	        return ResponseEntity.ok(Map.of("success", true, "message", "Rating added successfully"));
	    } catch (Exception e) {
	        System.err.println("Error adding rating: " + e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Map.of("success", false, "message", "Failed to add rating: " + e.getMessage()));
	    }
	}

}
