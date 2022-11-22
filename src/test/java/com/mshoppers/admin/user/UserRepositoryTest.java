package com.mshoppers.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import com.mshoppers.common.entity.Role;
import com.mshoppers.common.entity.User;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTest {
	/*
	 * 
	 * @Autowired private UserRepository repo;
	 * 
	 * @Autowired private TestEntityManager entityManager;
	 * 
	 * @Test public void testCreateUserWithOneRole() { Role role =
	 * entityManager.find(Role.class, 1);
	 * 
	 * User user = new User("Ravi@gmail.com", "secret", "Ravi", "Birla");
	 * user.addRole(role);
	 * 
	 * repo.save(user);
	 * 
	 * assertThat(user.getId()).isGreaterThan(0); }
	 * 
	 * @Test public void testCreateUserWithTwoRules() { User user = new
	 * User("Test@gmail.com", "test", "Testing", "First"); Role roleEditor = new
	 * Role(3); Role roleAssistant = new Role(5);
	 * 
	 * user.addRole(roleAssistant); user.addRole(roleEditor);
	 * 
	 * User savedUser = repo.save(user);
	 * 
	 * assertThat(savedUser.getId()).isGreaterThan(0); }
	 * 
	 * 
	 * @Test public void testFindAll() { Iterable<User> userList = repo.findAll();
	 * 
	 * userList.forEach(user -> System.out.println(user)); }
	 * 
	 * @Test public void testFindById() { User user = repo.findById(1).get();
	 * 
	 * System.out.println(user);
	 * 
	 * assertThat(user).isNotNull(); }
	 * 
	 * @Test public void testUpdate() { User user = repo.findById(1).get();
	 * 
	 * user.setEnabled(true); user.setEmail("updatedmail@gmail.com");
	 * 
	 * repo.save(user); }
	 * 
	 * @Test public void testChangeRole() { User user = repo.findById(2).get(); Role
	 * roleEditor = new Role(3); Role roleSalesPerson = new Role(4);
	 * 
	 * user.getRoles().remove(roleEditor); user.getRoles().add(roleSalesPerson);
	 * 
	 * repo.save(user); }
	 * 
	 * @Test public void testDelete() { repo.deleteById(2); }
	 * 
	 * @Test public void testGetUserByEmail() { String email =
	 * "aaaaaaaaaa@aaaaaaa.com"; User user = repo.getUserByEmail(email);
	 * 
	 * assertThat(user).isNotNull(); }
	 * 
	 * //get list of users by page
	 * 
	 * @Test public void testPagination() {
	 * 
	 * int pageNo = 0; int pageSize = 4;
	 * 
	 * Pageable pageable = PageRequest.of(pageNo, pageSize);
	 * 
	 * Page<User> page = repo.findAll(pageable);
	 * 
	 * List<User> listUsers = page.getContent();
	 * 
	 * listUsers.forEach(user -> System.out.println(user));
	 * 
	 * assertThat(listUsers).size().isEqualTo(pageSize); }
	 * 
	 * @Test public void testFiltering() { int pageNo = 0; int pageSize= 4;
	 * 
	 * Pageable pageable = PageRequest.of(pageNo, pageSize);
	 * 
	 * Page<User> page = repo.findAll("hotmail", pageable);
	 * 
	 * List<User> listUsers = page.getContent();
	 * 
	 * listUsers.forEach(user -> System.out.println(user));
	 * 
	 * assertThat(listUsers).size().isGreaterThan(0);
	 * 
	 * 
	 * }
	 */}
