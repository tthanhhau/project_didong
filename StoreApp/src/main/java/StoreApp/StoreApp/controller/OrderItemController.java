package StoreApp.StoreApp.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import StoreApp.StoreApp.service.Order_ItemService;
import StoreApp.StoreApp.service.RatingService; // Thêm RatingService

@RestController
public class OrderItemController {
    @Autowired
    Order_ItemService order_ItemService;

    @Autowired
    RatingService ratingService; // Inject RatingService

    @GetMapping("/checkRating")
    public Map<String, Object> checkRating(@RequestParam("user_id") String userId, @RequestParam("id") Integer productId) {
        boolean hasPurchased = order_ItemService.existsByProduct_IdAndUser_Id(productId, userId);
        boolean hasRated = ratingService.existsByUserIdAndProductId(userId, productId); // Sử dụng RatingService
        Map<String, Object> response = new HashMap<>();
        response.put("hasPurchased", hasPurchased);
        response.put("hasRated", hasRated);
        response.put("canRate", hasPurchased && !hasRated);
        return response;
    }
}