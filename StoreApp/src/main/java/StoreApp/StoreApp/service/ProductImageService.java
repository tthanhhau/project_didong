package StoreApp.StoreApp.service;

import java.util.List;

import StoreApp.StoreApp.entity.ProductImage;

public interface ProductImageService {

	void save(ProductImage productImage);

	void deleteById(int id);
	
	List<ProductImage> findByProductId(int id);
}
