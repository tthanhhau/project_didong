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

    boolean existsByProduct_IdAndUser_Id(Integer productId, String userId);
}

