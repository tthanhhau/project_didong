package StoreApp.StoreApp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;
import java.util.Locale.Category;
@Data
public class ProductDto {
	private int id;
	private String product_name;
	private String description;
	private int sold;
	private int is_active;
	private int is_selling;
	private Date created_at;
	private int price;
	private int quantity;
	private List<ProductImageDto> productImage;
	private List<CartDto> cartDto;
	private int category_id;
}
