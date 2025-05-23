package StoreApp.StoreApp.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import StoreApp.StoreApp.entity.Product;
import StoreApp.StoreApp.entity.ProductImage;
/**
 * @author HOAN HAO
 *
 */
@Repository
public interface ProductRepository extends JpaRepository<Product,Integer>{
	
	@Query(value="select * from product p where p.product_name like CONCAT('%', ?1, '%')",nativeQuery = true)
	List<Product> findByProduct_NameContaining(String name);
	
	@Query(value="Select * From product p ORDER BY p.quantity DESC LIMIT 12;",nativeQuery = true)
	List<Product> findTop12ProductBestSellers();
	
	@Query(value="Select * From product p ORDER BY p.created_at DESC LIMIT 12;",nativeQuery = true)
	List<Product> findTop12ProductNewArrivals();
	
	Page<Product> findAllByCategory_id(int id, Pageable pageable);
	
	Product findById(int id);
	
	@Query(value = "SELECT * FROM fashionstore.product WHERE product_name LIKE CONCAT('%', ?1, '%') AND category_id = ?2", nativeQuery = true)
	Page<Product> findByProduct_NameAndCategory_idContaining(String name, int category_id, Pageable pageable);
	
	@Query(value="select * from `fashionstore`.product where `fashionstore`.product.product_name like CONCAT('%', ?1, '%')",nativeQuery = true)
	Page<Product> findByProduct_NameContaining(String name, Pageable pageable);
	
	@Query(value="select * from product p where p.category_id = ?1 ORDER BY p.sold DESC LIMIT 4;",nativeQuery = true)
	List<Product> findTop4ProductByCategory_id(int id);
	
	List<Product> findAll();
	

}
