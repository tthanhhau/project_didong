package StoreApp.StoreApp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import StoreApp.StoreApp.entity.Order_Item;

public interface Order_ItemRepository extends JpaRepository<Order_Item,Integer>{

	List<Order_Item> findAllByOrder_id(int id);

	void deleteById(int id);
	
	@Query("SELECT CASE WHEN COUNT(oi) > 0 THEN true ELSE false END " +
	           "FROM Order_Item oi " +
	           "WHERE oi.product.id = :productId AND oi.order.user.id = :userId")
	    boolean existsByProductIdAndUserId(@Param("productId") Integer productId, @Param("userId") String userId);
	
	}


