package StoreApp.StoreApp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RatingDto {
    private int id;
    private int rate;
    private String description;
    @JsonProperty("product_id")
    private Integer productId; // Sửa thành Integer để khớp với Product.id
    @JsonProperty("user_id")
    private String userId;    // Giữ String nếu User.id là String
}