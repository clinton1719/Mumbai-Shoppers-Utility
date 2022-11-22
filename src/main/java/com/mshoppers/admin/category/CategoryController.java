/**
 * 
 */
package com.mshoppers.admin.category;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mshoppers.admin.FileUploadUtil;
import com.mshoppers.common.entity.Category;

/**
 * @author Clinton
 *
 */
@Controller
public class CategoryController {

	private static final Logger LOGGER = LoggerFactory.getLogger(CategoryController.class);

	@Value("${CATEGORIES_PER_PAGE}")
	private int CATEGORIES_PER_PAGE;

	@Value("${CATEGORY_PHOTOS}")
	private String imageUploadPath; // For Photo Save in Categories

	@Autowired
	private CategoryService categoryService;

	/* Retrieving all categories to display in table BY DEFAULT */
	@GetMapping("/categories")
	public String listFirstPageByDefault(@Param("sortDir") String sortDir, @Param("sortField") String sortField,
			Model model) {
		try {

		} catch (Exception e) {
			LOGGER.error("ERROR occurred ::: METHOD name is listAllCategories ::: " + e);
			e.printStackTrace();
			return "error/500";
		}

		return listCategoriesByPage(1, sortDir, sortField, null, model);
	}

	/* Retrieving all categories to display in table according to PAGE NUMBER */
	@GetMapping("/categories/page/{pageNum}")
	public String listCategoriesByPage(@PathVariable(name = "pageNum") int pageNum, @Param("sortDir") String sortDir,
			@Param("sortField") String sortField, @Param("keyword") String keyword, Model model) {
		try {

			sortDir = (sortDir == null || sortDir.length() == 0 || sortDir.equals("desc")) ? "asc" : "desc";
			sortField = (sortField == null || sortField.length() == 0) ? "name" : sortField;

			// Pagination
			int startCount = ((pageNum - 1) * CATEGORIES_PER_PAGE) + 1;
			int endCount = startCount + CATEGORIES_PER_PAGE - 1;

			/* For search Only */
			if (keyword == null || keyword.length() == 0) {
				List<Category> categories = categoryService.listAll(pageNum, sortDir, sortField, null);

				if (endCount > categories.size()) {
					endCount = categories.size();
				}

				int totalPages = 0;
				if (categories.size() % 10 > 0) {
					totalPages = (categories.size() / 10) + 1;
				} else {
					totalPages = (categories.size() / 10);
				}

				model.addAttribute("categories",
						categories.subList(startCount - 1, endCount));
				model.addAttribute("totalPages", totalPages);
				model.addAttribute("currentPage", pageNum);
				model.addAttribute("startCount", startCount);
				model.addAttribute("endCount", endCount);
				model.addAttribute("totalCount", categories.size());

			} else {
				List<Category> searchResult = categoryService.listAll(pageNum, sortDir, sortField, keyword);

				if (endCount > searchResult.size()) {
					endCount = searchResult.size();
				}

				int totalPages = 0;
				if (searchResult.size() % 10 > 0) {
					totalPages = (searchResult.size() / 10) + 1;
				} else {
					totalPages = (searchResult.size() / 10);
				}

				model.addAttribute("categories",
						searchResult.subList(pageNum == 1 ? startCount - 1 : startCount, endCount));
				model.addAttribute("totalPages", totalPages);
				model.addAttribute("currentPage", pageNum);
				model.addAttribute("startCount", startCount);
				model.addAttribute("endCount", endCount);
				model.addAttribute("totalCount", searchResult.size());

			}

			model.addAttribute("pageTitle", "Manage Categories");
			model.addAttribute("sortDir", sortDir);
			model.addAttribute("sortField", sortField);
			model.addAttribute("keyword", keyword);

		} catch (Exception e) {
			LOGGER.error("ERROR occurred ::: METHOD name is listAllCategories ::: " + e);
			e.printStackTrace();
			return "error/500";
		}

		return "category_pages/categories";
	}

	/* Creates new Category from Category Form */
	@GetMapping("/categories/new")
	public String createNewCategory(Model model) {
		try {
			List<Category> categories = categoryService.listCategoriesInForm();
			model.addAttribute("category", new Category());
			model.addAttribute("categories", categories);
			model.addAttribute("pageTitle", "Create new Category");
		} catch (Exception e) {
			LOGGER.error("ERROR occurred ::: METHOD name is createNewCategory ::: " + e);
			e.printStackTrace();
			return "error/500";
		}
		return "category_pages/category_form";
	}

	/* Saves new Category in DB */
	@PostMapping("/categories/save")
	public String saveNewCategory(@RequestParam("imageFile") MultipartFile imgFile, Category category,
			RedirectAttributes redirectAttributes) {
		try {
			String fileName = StringUtils.cleanPath(imgFile.getOriginalFilename());
			boolean isChangingPhoto = false;
			if (category.getId() == null) {
				category.setImage(fileName);
			} else {
				if (fileName.trim().length() != 0 && fileName != null) {
					category.setImage(fileName);
				} else {
					isChangingPhoto = true;
				}
			}
			Category categoryInDb = categoryService.saveNewCategory(category);
			if (categoryInDb.getId() > 0) {
				if (!isChangingPhoto) {
					String photoFolderPath = imageUploadPath;
					photoFolderPath += "/" + category.getId();
					FileUploadUtil.cleanDirectory(photoFolderPath);
					FileUploadUtil.saveFile(photoFolderPath, fileName, imgFile);
				}

				redirectAttributes.addFlashAttribute("message", "The Category has been saved successfully!");
				redirectAttributes.addFlashAttribute("modalTitle", "Success!");
			} else {
				redirectAttributes.addFlashAttribute("message",
						"Oops! Unable to save new Category, please try after some time");
				redirectAttributes.addFlashAttribute("modalTitle", "Failed!");
			}

		} catch (Exception e) {
			LOGGER.error("ERROR occurred ::: METHOD name is saveNewCategory ::: " + e);
			e.printStackTrace();
			return "error/500";
		}
		return "redirect:/categories";
	}

	/*
	 * Fetches the category for updating it's details
	 */
	@GetMapping("/category/edit/{id}")
	public String getCategoryForUpdating(@PathVariable(name = "id") Integer id, Model model) {
		try {
			Category category = categoryService.findById(id);
			List<Category> categories = categoryService.listCategoriesInForm().stream()
					.filter(categoryInStream -> !categoryInStream.getNameInDropDown()
							.contains(category.getName().toUpperCase()))
					.collect(Collectors.toList());
			model.addAttribute("category", category);
			model.addAttribute("categories", categories);
			model.addAttribute("pageTitle", "Edit " + category.getName() + " category");
		} catch (Exception e) {
			LOGGER.error("ERROR occurred ::: METHOD name is getCategoryForUpdating ::: " + e);
			e.printStackTrace();
			return "error/500";
		}
		return "category_pages/category_form";
	}

	/* Enable or Disable a category */
	@GetMapping("/category/changestatus/{action}/{id}")
	public String changeEnabledStatus(@PathVariable(name = "action") String action,
			@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {

		try {
			Category category = categoryService.findById(id);
			if (action.equals("false")) {
				category.setIsEnabled(false);
			} else if (action.equals("true")) {
				category.setIsEnabled(true);
			}
			categoryService.saveNewCategory(category);
			redirectAttributes.addFlashAttribute("message", "Category " + category.getName() + " has been "
					+ (action.equals("false") ? "disabled" : "enabled") + " successfully!");
			redirectAttributes.addFlashAttribute("modalTitle", "Success!");
		} catch (Exception e) {
			LOGGER.error("ERROR occurred ::: METHOD name is changeEnabledStatus ::: " + e);
			e.printStackTrace();
			return "error/500";
		}
		return "redirect:/categories";
	}

	@GetMapping("/categories/delete/{id}")
	public String deleteCategory(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
		try {
			categoryService.deleteCategory(id);
			redirectAttributes.addFlashAttribute("modalTitle", "Success!");
			redirectAttributes.addFlashAttribute("message", "The Category has been deleted successfully!");
		} catch (Exception e) {
			LOGGER.error("ERROR occurred ::: METHOD name is deleteCategory ::: " + e);
			e.printStackTrace();
			return "error/500";
		}
		return "redirect:/categories";
	}
	
	@GetMapping("/categories/export/csv")
	public void exportToCSV(HttpServletResponse response) throws IOException {
		List<Category> categories = categoryService.listCategoriesInForm();
		CategoryCSVExporter exporter = new CategoryCSVExporter();
		exporter.export(categories, response);
	}
}
