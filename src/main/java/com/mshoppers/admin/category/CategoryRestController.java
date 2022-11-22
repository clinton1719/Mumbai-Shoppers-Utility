/**
 * 
 */
package com.mshoppers.admin.category;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

/**
 * @author Clinton
 *
 */
@RestController
public class CategoryRestController {

	private static final Logger LOGGER = LoggerFactory.getLogger(CategoryController.class);

	@Autowired
	private CategoryService categoryService;

	@PostMapping("/categories/check_category")
	public String checkUniqueCategory(@Param("id") Integer id, @Param("name") String name,
			@Param("alias") String alias) {
		String jsonResult;
		try {
			Map<String, String> result = categoryService.checkCategoryUnqiue(id, name, alias);
			Gson gson = new Gson();
			jsonResult = gson.toJson(result);
		} catch (Exception e) {
			LOGGER.error("ERROR occurred ::: METHOD name is checkUniqueCategory ::: " + e);
			e.printStackTrace();
			return "error/500";
		}
		return jsonResult;
	}
}
