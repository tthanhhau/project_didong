package StoreApp.StoreApp.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import StoreApp.StoreApp.entity.ProductImage;
import StoreApp.StoreApp.repository.ProductImageRepository;
import StoreApp.StoreApp.service.ProductImageService;

@Service
public class ProductImageServiceImpl implements ProductImageService{
	@Autowired
	ProductImageRepository productImageRepository;

	@Override
	public void save(ProductImage productImage) {
		productImageRepository.save(productImage);
	}

	@Override
	public void deleteById(int id) {
		productImageRepository.deleteById(id);
	}
	@Override
	public List<ProductImage> findByProductId(int id) {
		return productImageRepository.findByProductId(id);
	}
	
}
