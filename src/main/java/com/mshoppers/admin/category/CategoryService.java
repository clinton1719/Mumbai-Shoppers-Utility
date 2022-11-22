/**
 * 
 */
package com.mshoppers.admin.category;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.mshoppers.admin.FileUploadUtil;
import com.mshoppers.common.entity.Category;

/**
 * @author Clinton
 *
 */
@Service
public class CategoryService {

	private static final Logger LOGGER = LoggerFactory.getLogger(CategoryService.class);

	@Autowired
	private CategoryRepository categoryRepo;

	@Value("${CATEGORIES_PER_PAGE}")
	private int CATEGORIES_PER_PAGE;

	/* This is for getting all categories in no order -- for search function */
	public List<Category> listAllCategories() {
		return (List<Category>) categoryRepo.findAll();
	}

	/*
	 * This is for listing the categories in hierarchical form in the Categories
	 * page
	 */
	public List<Category> listAll(int pageNum, String sortDir, String sortField, String keyword) {
		List<Category> finalList = new ArrayList<>();
		try {
			Sort sort = Sort.by("name");

			if (sortDir.equals("asc")) {
				sort = sort.ascending();
			} else {
				sort = sort.descending();
			}

			List<Category> rootCategories = null;

			/* Adding search condition */
			if (keyword == null || keyword.length() == 0) {
				rootCategories= categoryRepo.listCategoriesInHierarchy(sort);
			} else {
				rootCategories = (List<Category>) categoryRepo.listAll();
			}

			for (Category category : rootCategories) {
				category.setNameInDropDown(category.getName().toUpperCase());
				finalList.add(category);
				if (category.getChildren() != null && category.getChildren().size() > 0) {
					addChildren(category.getName().toLowerCase(), category.getChildren(), finalList, sortDir,
							sortField);
				}
			}
			if (keyword != null && keyword.length()>0) {
				finalList = finalList.stream().filter(c -> searchRegex(c, keyword)).collect(Collectors.toList());
			}

			if (sortField.equals("alias")) {
				if (sortDir.equals("asc")) {
					finalList = finalList.stream().sorted(Comparator.comparing(Category::getAlias))
							.collect(Collectors.toList());
				} else if (sortDir.equals("desc")) {
					finalList = finalList.stream().sorted(Comparator.comparing(Category::getAlias).reversed())
							.collect(Collectors.toList());
				}
			}

		} catch (Exception e) {
			LOGGER.error("ERROR occurred ::: METHOD name is listAll ::: " + e);
		}
		return finalList;
	}

	/* Helper For search bar */
	public boolean searchRegex(Category category, String keyword) {
		try {
			if (category.getId() == Integer.valueOf(keyword)) {
				return true;
			}
		} catch (NumberFormatException e) {
			try {
				String textToSearch = category.getNameInDropDown() + " " + category.getAlias();
				Pattern pattern = Pattern.compile(keyword, Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(textToSearch);
				if (matcher.find()) {
					return true;
				}
			} catch (Exception e1) {
				LOGGER.error("ERROR occurred ::: METHOD name is searchRegex ::: " + e);
				e.printStackTrace();
			}
		}
		return false;
	}

	/*
	 * This is for listing the categories in hierarchical form in the create
	 * Category page
	 */
	public List<Category> listCategoriesInForm() {
		List<Category> finalList = new ArrayList<>();
		try {
			List<Category> categories = (List<Category>) categoryRepo
					.listCategoriesInHierarchy(Sort.by("name").ascending());

			for (Category category : categories) {
				category.setNameInDropDown(category.getName().toUpperCase());
				finalList.add(category);
				if (category.getChildren() != null && category.getChildren().size() > 0) {
					addChildren(category.getName().toLowerCase(), category.getChildren(), finalList, "asc", "name");
				}
			}
		} catch (Exception e) {
			LOGGER.error("ERROR occurred ::: METHOD name is listCategoriesInForm ::: " + e);
			e.printStackTrace();
		}
		return finalList;
	}

	public List<Category> addChildren(String parent, Set<Category> childrenSet, List<Category> finalList,
			String sortDir, String sortField) {
		try {
			List<Category> children = new ArrayList<>();
			if (sortField.equals("name")) {
				if (sortDir.equals("asc")) {
					children = childrenSet.stream().sorted(Comparator.comparing(Category::getName))
							.collect(Collectors.toList());
				} else {
					children = childrenSet.stream().sorted(Comparator.comparing(Category::getName).reversed())
							.collect(Collectors.toList());
				}
			} else if (sortField.equals("alias")) {
				children = childrenSet.stream().collect(Collectors.toList());
			}

			for (Category child : children) {
				if (parent.contains("]")) {
					if (!parent.contains("[" + child.getParent().getName().toUpperCase() + "]")) {
						parent = parent.replace(child.getParent().getName().toUpperCase(),
								"[" + child.getParent().getName().toLowerCase() + "]");
					}
					child.setNameInDropDown(parent + " -> " + child.getName().toUpperCase());

				} else {
					child.setNameInDropDown("[" + parent + "]" + " -> " + child.getName().toUpperCase());
				}
				finalList.add(child);
				if (child.getChildren().size() > 0) {
					addChildren(child.getNameInDropDown(), child.getChildren(), finalList, sortDir, sortField);
				}
			}
		} catch (Exception e) {
			LOGGER.error("ERROR occurred ::: METHOD name is addChildren ::: " + e);
			e.printStackTrace();
		}

		return finalList;
	}

	public Category saveNewCategory(Category category) {
		Category categoryInDb = categoryRepo.save(category);
		return categoryInDb;
	}

	public Category findById(Integer id) {
		Category category = categoryRepo.findById(id).get();
		return category;
	}

	public Map<String, String> checkCategoryUnqiue(Integer id, String name, String alias) {
		Map<String, String> result = new HashMap<>();
		if (id != null) {
			result.put("status", "true");
			return result;
		}
		boolean isName = false;
		boolean isAlias = false;

		if (categoryRepo.findByName(name) != null && categoryRepo.findByName(name).size() > 0) {
			isName = true;
		}

		if (categoryRepo.findByAlias(alias) != null && categoryRepo.findByAlias(alias).size() > 0) {
			isAlias = true;
		}

		if (isName && isAlias) {
			result.put("status", "false");
			result.put("message", "There is another Category with the same name and alias");
		} else if (isName) {
			result.put("status", "false");
			result.put("message", "There is another Category with the same name");
		} else if (isAlias) {
			result.put("status", "false");
			result.put("message", "There is another Category with the same alias");
		} else {
			result.put("status", "true");
		}

		return result;
	}

	public void deleteCategory(Integer id) {
		FileUploadUtil.deleteFile("../category-photos/" + id);
		categoryRepo.deleteById(id);
	}
}
