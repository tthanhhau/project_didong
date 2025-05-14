package StoreApp.StoreApp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import StoreApp.StoreApp.entity.Cart;
import StoreApp.StoreApp.entity.Product;
import StoreApp.StoreApp.entity.Rating;
@Repository
public interface RatingRepository extends JpaRepository<Rating, Integer> {

    @Query(value = "SELECT * FROM rating WHERE product_id = :productId", nativeQuery = true)
    List<Rating> getRatingsByProductIdNative(@Param("productId") Integer productId);

    @Query(value = "SELECT " +
                   "EXISTS (" +
                   "  SELECT 1 " +
                   "  FROM fashionstore.`order` o " +
                   "  JOIN fashionstore.order_item od ON o.id = od.order_id " +
                   "  WHERE o.user_id = :userId " +
                   "  AND od.product_id = :productId) AS has_purchased" , 
           nativeQuery = true)
    List<Object[]> checkPurchaseAndRatingNative(@Param("userId") String userId, @Param("productId") Integer productId);
    

}

