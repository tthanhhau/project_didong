package StoreApp.StoreApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import StoreApp.StoreApp.entity.Category;

public interface CategoryRepository extends JpaRepository<Category,Integer>{
	

}
