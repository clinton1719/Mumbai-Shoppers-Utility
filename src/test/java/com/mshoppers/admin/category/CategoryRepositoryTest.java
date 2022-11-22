/**
 * 
 */
package com.mshoppers.admin.category;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.mshoppers.common.entity.Category;

/**
 * @author Clinton
 *
 */

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CategoryRepositoryTest {
	/*
	 * 
	 * @Autowired private CategoryRepository categoryRepo;
	 * 
	 * @Test public void testCreateRootCategory() { Category category = new
	 * Category("Computers");
	 * 
	 * Category categoryFromDb = categoryRepo.save(category);
	 * 
	 * assertThat(categoryFromDb.getId()).isGreaterThan(0); }
	 * 
	 * @Test public void testCreateSubCategories() { Category root = new
	 * Category(3);
	 * 
	 * Category sub = new Category("Lenovo", root);
	 * 
	 * Category db = categoryRepo.save(sub);
	 * 
	 * assertThat(db.getId()).isGreaterThan(0); }
	 * 
	 * @Test public void testGetCategory(int id) { Category root =
	 * categoryRepo.findById(id).get(); System.out.println("Parent is: " +
	 * root.getName()); Set<Category> children = root.getChildren(); for(Category
	 * child : children) { System.out.println("Child is: " + child.getName());
	 * testGetCategory(child.getId()); } //
	 * assertThat(children).size().isGreaterThan(0); }
	 * 
	 * @Test public void printAll() { testGetCategory(1); }
	 */}
