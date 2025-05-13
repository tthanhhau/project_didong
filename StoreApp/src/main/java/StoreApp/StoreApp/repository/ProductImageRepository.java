package StoreApp.StoreApp.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import StoreApp.StoreApp.entity.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {

    void deleteById(int id);
    
    ProductImage getProductImageById(Integer id);
    
    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.id = :productId")
    List<ProductImage> findByProductId(@Param("productId") Integer productId);
}