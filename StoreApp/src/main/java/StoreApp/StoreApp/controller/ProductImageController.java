package StoreApp.StoreApp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import StoreApp.StoreApp.entity.Product;
import StoreApp.StoreApp.entity.ProductImage;
import StoreApp.StoreApp.service.ProductImageService;

@RestController
public class ProductImageController {
	@Autowired
	ProductImageService productImageService;
	@GetMapping(path = "/getImageByProductID")
	public ResponseEntity<List<ProductImage>> getImageByProductID(int id){
		List<ProductImage> newProducts = productImageService.findByProductId(id);
		return new ResponseEntity<>(newProducts, HttpStatus.OK);
	}
}
