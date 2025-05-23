package StoreApp.StoreApp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import StoreApp.StoreApp.entity.Cart;
import StoreApp.StoreApp.entity.Product;

public interface CartRepository extends JpaRepository<Cart,Integer>{

//	@Query(value="DELETE FROM `cart` e WHERE e.id= ?1",nativeQuery = true)
//	void deleteById(int id);

	
	@Query(value="select product_id from fashionstore.cart where id = ?",nativeQuery = true)
	int findProductByCart_id(int cart_id);
	
    List<Cart> findAllByUser_id( String userId);
}
