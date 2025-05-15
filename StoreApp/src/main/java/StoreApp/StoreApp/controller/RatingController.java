package StoreApp.StoreApp.controller;

import java.awt.print.Printable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import StoreApp.StoreApp.entity.Product;
import StoreApp.StoreApp.entity.Rating;
import StoreApp.StoreApp.model.RatingDto;
import StoreApp.StoreApp.service.RatingService;

@RestController
public class RatingController {
	@Autowired
	RatingService ratingService;
	@Autowired
	private ModelMapper modelMapper;
	@GetMapping("/getRating")
	public ResponseEntity<?> getRatingByID(@RequestParam int id) {
	    try {
	        List<Rating> ratings = ratingService.getRatingByIdProduct(id);
	        return ResponseEntity.ok(ratings);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Map.of("success", false, "message", "Failed to get ratings"));
	    }
	}




	
	@PostMapping("/addRating")
	public ResponseEntity<?> addRating(@RequestBody RatingDto ratingDto) {
	    try {
            Rating rating = modelMapper.map(ratingDto, Rating.class);

	        ratingService.addRating(rating);
	        return ResponseEntity.ok(Map.of("success", true, "message", "Rating added successfully"));
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Map.of("success", false, "message", "Failed to add rating"));
	    }
	}

}
