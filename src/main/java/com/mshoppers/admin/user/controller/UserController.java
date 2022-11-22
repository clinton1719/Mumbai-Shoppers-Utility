package com.mshoppers.admin.user.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mshoppers.admin.FileUploadUtil;
import com.mshoppers.admin.user.UserNotFoundException;
import com.mshoppers.admin.user.UserService;
import com.mshoppers.admin.user.export.UserCsvExporter;
import com.mshoppers.admin.user.export.UserExcelExporter;
import com.mshoppers.admin.user.export.UserPDFExporter;
import com.mshoppers.common.entity.Role;
import com.mshoppers.common.entity.User;

@Controller
public class UserController {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	@Value("${USERS_PER_PAGE}")
	private int USERS_PER_PAGE;
	
	@Value("${USER_PHOTOS}")
	private String imageUploadPath; // For Photo Save in Users

	@Value("${HEADER_VALUES}")
	String headerValues; // For Excel Sheet

	@Autowired
	private UserService userService;

	@GetMapping("/users")
	public String listFirstPage(Model model) {
		return listAllUsersByPage(1, "firstName", "asc", null, model);
	}

	@GetMapping("/users/page/{pageNumber}")
	public String listAllUsersByPage(@PathVariable(name = "pageNumber") Integer pageNumber,
			@Param("sortField") String sortField, @Param("sortDir") String sortDir, @Param("keyword") String keyword,
			Model model) {
		try {
			Page<User> users = userService.listUsersByPage(pageNumber, sortField, sortDir, keyword);

			long startCount = ((pageNumber - 1) * USERS_PER_PAGE) + 1;
			long endCount = startCount + USERS_PER_PAGE - 1;
			if (endCount > users.getTotalElements()) {
				endCount = users.getTotalElements();
			}

			String reverseSortDir = sortDir.equalsIgnoreCase("asc") ? "desc" : "asc";

			// Sorting model info
			model.addAttribute("sortField", sortField);
			model.addAttribute("sortDir", sortDir);
			model.addAttribute("reverseSortDir", reverseSortDir);
			model.addAttribute("keyword", keyword);

			model.addAttribute("currentPage", pageNumber);
			model.addAttribute("totalPages", users.getTotalPages());
			model.addAttribute("startCount", startCount);
			model.addAttribute("endCount", endCount);
			model.addAttribute("totalCount", users.getTotalElements());

			List<User> userList = users.getContent();
			model.addAttribute("userList", userList);
		} catch (Exception e) {
			LOGGER.error("ERROR occurred ::: METHOD name is listAllUsersByPage ::: " + e);
			e.printStackTrace();
		}

		return "user_pages/users";
	}

	@GetMapping("/users/new")
	public String createNewUser(Model model) {
		try {
			User theUser = new User();

			List<Role> theRoles = userService.listAllRoles();

			model.addAttribute("user", theUser);
			model.addAttribute("roles", theRoles);
			model.addAttribute("pageTitle", "Create New User");
		} catch (Exception e) {
			LOGGER.error("ERROR occurred ::: METHOD name is createNewUser ::: " + e);
			e.printStackTrace();
		}

		return "user_pages/user_form";
	}

	@PostMapping("/users/save")
	public String saveNewUser(User user, RedirectAttributes theRedirectAttributes,
			@RequestParam("image") MultipartFile multipartFile) throws IOException {
		try {
			// Upload without photo
			if (multipartFile.isEmpty()) {
				if (user.getPhotos() == null)
					user.setPhotos(null);
				userService.saveNewUser(user);
			} else {
				// Uploads photo as well
				String fileName = org.springframework.util.StringUtils.cleanPath(multipartFile.getOriginalFilename());
				user.setPhotos(fileName);
				User userForImageUpload = userService.saveNewUser(user);
				String photoFolderPath = imageUploadPath;
				photoFolderPath += "/" + userForImageUpload.getId();
				FileUploadUtil.cleanDirectory(photoFolderPath);
				FileUploadUtil.saveFile(photoFolderPath, fileName, multipartFile);

			}

			theRedirectAttributes.addFlashAttribute("message", "The user has been saved successfully!");
			theRedirectAttributes.addFlashAttribute("modalTitle", "Success!");
		} catch (Exception e) {
			LOGGER.error("ERROR occurred ::: METHOD name is saveNewUser ::: " + e);
			e.printStackTrace();
		}

		return getRedirectURLforUser(user);
	}

	private String getRedirectURLforUser(User user) {
		return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + user.getEmail();
	}

	/* This is for editing an user details */
	@GetMapping("/users/edit/{id}")
	public String getExistingUser(@PathVariable(name = "id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			User user = userService.getExistingUser(id);
			List<Role> theRoles = userService.listAllRoles();

			model.addAttribute("user", user);
			model.addAttribute("roles", theRoles);
			model.addAttribute("pageTitle", "Edit user (User ID: " + user.getId() + ")");
			// if user found, redirect to edit page
			return "user_pages/user_form";
		} catch (UserNotFoundException e) {
			LOGGER.error("ERROR occurred ::: METHOD name is getExistingUser ::: " + e);
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			redirectAttributes.addFlashAttribute("modalTitle", "Warning!");
			// if user not found, redirect to users page
			return "redirect:/users";
		} catch (Exception e) {
			LOGGER.error("ERROR occurred ::: METHOD name is getExistingUser ::: " + e);
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			redirectAttributes.addFlashAttribute("modalTitle", "Warning!");
			// if user not found, redirect to users page
			return "redirect:/users";
		}

	}

	@GetMapping("/users/delete/{id}")
	public String deleteUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
		try {
			userService.deleteUser(id);
			redirectAttributes.addFlashAttribute("message", "The user has been deleted successfully!");
			redirectAttributes.addFlashAttribute("modalTitle", "Success!");
			return "redirect:/users";
		} catch (UserNotFoundException e) {
			LOGGER.error("ERROR occurred ::: METHOD name is deleteUser ::: " + e);
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			redirectAttributes.addFlashAttribute("modalTitle", "Warning!");
			// if user not found, redirect to users page
			return "redirect:/users";
		} catch (Exception e) {
			LOGGER.error("ERROR occurred ::: METHOD name is deleteUser ::: " + e);
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			redirectAttributes.addFlashAttribute("modalTitle", "Warning!");
			// if user not found, redirect to users page
			return "redirect:/users";
		}
	}

	@GetMapping("/users/changestatus/{status}/{id}")
	public String updateStatus(@PathVariable(name = "status") boolean status, @PathVariable(name = "id") Integer id,
			RedirectAttributes redirectAttributes) throws UserNotFoundException {
		try {
			userService.updateUserStatus(id, status);

			redirectAttributes.addFlashAttribute("message",
					"You have successfully " + (status == true ? "enabled " : "disbaled ") + "user "
							+ userService.getExistingUser(id).getFirstName());
			redirectAttributes.addFlashAttribute("modalTitle", "Success!");
		} catch (UserNotFoundException e) {
			LOGGER.error("ERROR occurred ::: METHOD name is updateStatus ::: " + e);
			e.printStackTrace();
		} catch (Exception e) {
			LOGGER.error("ERROR occurred ::: METHOD name is updateStatus ::: " + e);
			e.printStackTrace();
		}
		return "redirect:/users";
	}

	@GetMapping("/users/export/csv")
	public void exportToCSV(HttpServletResponse response) throws IOException {
		try {
			List<User> users = userService.listAllUsers();
			UserCsvExporter exporter = new UserCsvExporter();
			exporter.export(users, response);
		} catch (Exception e) {
			LOGGER.error("ERROR occurred ::: METHOD name is exportToCSV ::: " + e);
			e.printStackTrace();
		}
	}

	@GetMapping("/users/export/excel")
	public void exportToExcel(HttpServletResponse response) throws IOException {
		try {
			List<User> users = userService.listAllUsers();
			UserExcelExporter exporter = new UserExcelExporter(headerValues);
			exporter.export(users, response);
		} catch (Exception e) {
			LOGGER.error("ERROR occurred ::: METHOD name is exportToExcel ::: " + e);
			e.printStackTrace();
		}
	}

	@GetMapping("/users/export/pdf")
	public void exportToPDF(HttpServletResponse response) throws IOException {
		try {
			List<User> users = userService.listAllUsers();
			UserPDFExporter exporter = new UserPDFExporter(headerValues);
			exporter.export(users, response);
		} catch (Exception e) {
			LOGGER.error("ERROR occurred ::: METHOD name is exportToPDF ::: " + e);
			e.printStackTrace();
		}
	}
}
