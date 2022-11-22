/**
 * 
 */
package com.mshoppers.admin.category;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.mshoppers.common.entity.Category;

/**
 * @author Clinton
 *
 */
public interface CategoryRepository extends PagingAndSortingRepository<Category, Integer> {

	@Query("SELECT c FROM Category c WHERE c.parent.id is NULL")
	public List<Category> listCategoriesInHierarchy(Sort sort);
	
	@Query("SELECT c FROM Category c WHERE c.parent.id is NULL")
	public List<Category> listAll();

	@Query("SELECT c FROM Category c WHERE c.name=:name")
	public List<Category> findByName(@Param("name") String name);

	@Query("SELECT c FROM Category c WHERE c.alias=:alias")
	public List<Category> findByAlias(@Param("alias") String alias);
}
