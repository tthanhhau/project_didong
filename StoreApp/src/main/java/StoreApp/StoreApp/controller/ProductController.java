package StoreApp.StoreApp.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import StoreApp.StoreApp.entity.Cart;
import StoreApp.StoreApp.entity.Category;
import StoreApp.StoreApp.entity.Product;
import StoreApp.StoreApp.entity.ProductImage;
import StoreApp.StoreApp.model.ProductDto;
import StoreApp.StoreApp.model.ProductImageDto;
import StoreApp.StoreApp.service.CartService;
import StoreApp.StoreApp.service.CategoryService;
import StoreApp.StoreApp.service.ProductImageService;
import StoreApp.StoreApp.service.ProductService;

@RestController
public class ProductController {
	@Autowired
	ProductService productService;
	@Autowired
	CartService cartService;
	@Autowired
	ProductImageService productImageService;
	@Autowired
	CategoryService categoryService;
	@Autowired
	private ModelMapper modelMapper;
	@GetMapping(path = "/newproduct")
	public ResponseEntity<List<Product>> newProduct(){
		List<Product> newProducts = productService.findTop12ProductNewArrivals();
		return new ResponseEntity<>(newProducts, HttpStatus.OK);
	}
	
	@GetMapping(path = "/bestsellers")
	public ResponseEntity<List<Product>> bestSellers(){
		List<Product> bestSellers = productService.findTop12ProductBestSellers();
		return new ResponseEntity<>(bestSellers, HttpStatus.OK);
	}
	
	@GetMapping(path = "/search")
	public ResponseEntity<List<Product>> Search(String searchContent){
		List<Product> products = productService.findByProduct_NameContaining(searchContent);
		return new ResponseEntity<>(products, HttpStatus.OK);
	}
	@GetMapping(path = "/getAll")
	public ResponseEntity<List<Product>> GetProducts(){
		List<Product> products = productService.getAllProduct();
		return new ResponseEntity<>(products, HttpStatus.OK);
	}
	@PostMapping("/add")
    public ResponseEntity<?> addProduct(@RequestBody ProductDto productDto) {
        try {
            // Validate input
            if (productDto == null || productDto.getCategory_id() <= 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Invalid product or category data"));
            }

            // Fetch category entity by ID
            Category category = categoryService.getCategoryById(productDto.getCategory_id());
            if (category == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Category not found"));
            }
            
            // Map ProductDto to Product entity
            Product product = modelMapper.map(productDto, Product.class);
            product.setCategory(category);

            // Save product
            Product savedProduct = productService.saveProduct(product);

            // Save product images if present
            List<ProductImageDto> productImageDtos = productDto.getProductImage();
            if (productImageDtos != null && !productImageDtos.isEmpty()) {
                List<ProductImage> productImages = productImageDtos.stream()
                        .map(dto -> {
                            ProductImage image = modelMapper.map(dto, ProductImage.class);
                            image.setProduct(savedProduct);
                            return image;
                        })
                        .collect(Collectors.toList());
                productImages.forEach(productImageService::save);
            }

            return ResponseEntity.ok(Map.of("success", true, "message", "Product added successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Failed to add product: " + e.getMessage()));
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : e.toString();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to add product: " + errorMessage));
        }
    }
	@PostMapping("/remove")
	public ResponseEntity<?> removeProduct(int id) {
	    try {
	        productService.deleteProductById(id);
	    
	        return ResponseEntity.ok(Map.of("success", true, "message", "Product added successfully"));
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Map.of("success", false, "message", "Failed to add product"));
	    }
	}
}
