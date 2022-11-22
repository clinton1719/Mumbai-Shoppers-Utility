/**
 * 
 */
package com.mshoppers.admin.user.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mshoppers.admin.FileUploadUtil;
import com.mshoppers.admin.security.MshoppersUserDetails;
import com.mshoppers.admin.user.UserService;
import com.mshoppers.common.entity.User;

/**
 * @author Clinton
 *
 */
@Controller
public class AccountController {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	@Value("${USER_PHOTOS}")
	private String imageUploadPath; // For Photo Save

	@Autowired
	private UserService userService;

	@GetMapping("/account")
	public String viewAccountDetails(@AuthenticationPrincipal MshoppersUserDetails loggedUser, Model model) {
		try {
			String email = loggedUser.getUsername();

			User user = userService.getByEmail(email);

			model.addAttribute("user", user);
		} catch (Exception e) {
			LOGGER.error("ERROR occurred ::: METHOD name is viewAccountDetails ::: " + e);
			e.printStackTrace();
		}

		return "user_pages/account_form";
	}

	@PostMapping("/account/update")
	public String updateUser(User user, RedirectAttributes theRedirectAttributes,
			@AuthenticationPrincipal MshoppersUserDetails loggedInUser,
			@RequestParam("image") MultipartFile multipartFile) throws IOException {
		try {
			// Upload without photo
			if (multipartFile.isEmpty()) {
				if (user.getPhotos() == null)
					user.setPhotos(null);
				userService.updateAccount(user);
			} else {
				// Uploads photo as well
				String fileName = org.springframework.util.StringUtils.cleanPath(multipartFile.getOriginalFilename());
				user.setPhotos(fileName);
				User userForImageUpload = userService.updateAccount(user);
				String photoFolderPath = imageUploadPath;
				photoFolderPath += "/" + userForImageUpload.getId();
				FileUploadUtil.cleanDirectory(photoFolderPath);
				FileUploadUtil.saveFile(photoFolderPath, fileName, multipartFile);

			}

			loggedInUser.setFirstName(user.getFirstName());
			loggedInUser.setLastName(user.getLastName());

			theRedirectAttributes.addFlashAttribute("message", "Your account details have been updated!");
			theRedirectAttributes.addFlashAttribute("modalTitle", "Success!");
		} catch (Exception e) {
			LOGGER.error("ERROR occurred ::: METHOD name is updateUser ::: " + e);
			e.printStackTrace();
		}

		return "redirect:/account";
	}

}
