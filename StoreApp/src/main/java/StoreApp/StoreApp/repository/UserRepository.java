package StoreApp.StoreApp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import StoreApp.StoreApp.entity.User;
@Repository
public interface UserRepository extends JpaRepository<User, String>{
	User findByEmail(String email);
	Optional findById(String id);
	
//	@Query(value="select * from user u where u.id = ?1 and u.role = ?2",nativeQuery = true)
	User findByIdAndRole(String id, String role);
	
	void deleteById(String id);
}
