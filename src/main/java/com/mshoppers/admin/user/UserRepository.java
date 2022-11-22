package com.mshoppers.admin.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mshoppers.common.entity.User;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Integer> {

	// To check if duplicate email is there
	@Query("SELECT u from User u WHERE u.email=:email")
	public User getUserByEmail(@Param("email") String email);

	// To check if user is present
	public Long countById(Integer id);

	// To change status enabled
	@Query("UPDATE User u SET u.enabled =?2 WHERE u.id=?1")
	@Modifying
	public void updateStatus(Integer id, boolean status);

	// to filter out using keywords
	@Query("SELECT u from User u WHERE CONCAT(u.id, ' ', u.firstName, ' ', u.lastName , ' ', u.email) like %?1%")
	public Page<User> findAll(String keyword, Pageable pageable);
}
