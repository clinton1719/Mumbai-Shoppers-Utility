package com.mshoppers.admin.user;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mshoppers.admin.FileUploadUtil;
import com.mshoppers.common.entity.Role;
import com.mshoppers.common.entity.User;

@Service
@Transactional
public class UserService {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

	@Value("${USERS_PER_PAGE}")
	private int USERS_PER_PAGE;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private RoleRepository roleRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public List<User> listAllUsers() {
		return (List<User>) userRepo.findAll(Sort.by("id").ascending());
	}

	public User getByEmail(String email) {
		return userRepo.getUserByEmail(email);
	}

	public List<Role> listAllRoles() {
		return (List<Role>) roleRepo.findAll();
	}

	/* For Saving or Updating a user */
	public User saveNewUser(User theUser) {
		boolean isUpdatingUser = (theUser.getId() != null);

		if (isUpdatingUser) {
			User existingUser = userRepo.findById(theUser.getId()).get();

			if (theUser.getPassword().isEmpty()) {
				theUser.setPassword(existingUser.getPassword());
			} else {
				encodePassword(theUser);
			}
		} else {
			encodePassword(theUser);
		}

		return userRepo.save(theUser);
	}

	public User updateAccount(User userInForm) {
		User userInDb = userRepo.findById(userInForm.getId()).get();

		/* Check if user updated password */
		if (userInForm.getPassword().length() >= 8) {
			userInDb.setPassword(userInForm.getPassword());
			encodePassword(userInDb);
		}

		/* Check if user updated photos */
		if (userInForm.getPhotos() != null) {
			userInDb.setPhotos(userInForm.getPhotos());
			;
		}

		userInDb.setFirstName(userInForm.getFirstName());
		userInDb.setLastName(userInForm.getLastName());

		return userRepo.save(userInDb);

	}

	private void encodePassword(User user) {
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
	}

	// To check if duplicate email is there
	public boolean isEmailUnqiue(Integer id, String email) {
		User user = userRepo.getUserByEmail(email);

		boolean isCreatingNew = (id == null);

		if (isCreatingNew) {
			if (user != null)
				return false;
		} else {
			if (user.getId() != id) {
				return false;
			}
		}

		return true;
	}

	public User getExistingUser(Integer id) throws UserNotFoundException {
		try {
			User user = userRepo.findById(id).get();

			return user;
		} catch (NoSuchElementException e) {
			LOGGER.error("ERROR occurred ::: " + e);
			throw new UserNotFoundException("No user found with user ID " + id);
		}
	}

	public void deleteUser(Integer id) throws UserNotFoundException {
		Long count = userRepo.countById(id);

		if (count == null || count == 0) {
			LOGGER.error("ERROR occurred ::: " + id);
			throw new UserNotFoundException("No user found with user ID " + id);
		}
		FileUploadUtil.deleteFile("user-photos/" + id);
		userRepo.deleteById(id);

	}

	public void updateUserStatus(Integer id, boolean status) {
		userRepo.updateStatus(id, status);
	}

	public Page<User> listUsersByPage(int pageNumber, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);

		sort = sortDir.equalsIgnoreCase("asc") ? sort.ascending() : sort.descending();

		Pageable pageable = PageRequest.of(pageNumber - 1, USERS_PER_PAGE, sort);

		if (keyword == null) {
			return userRepo.findAll(pageable);
		}

		return userRepo.findAll(keyword, pageable);
	}

}
