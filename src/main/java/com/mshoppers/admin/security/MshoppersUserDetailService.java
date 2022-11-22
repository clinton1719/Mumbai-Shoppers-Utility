package com.mshoppers.admin.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.mshoppers.admin.user.UserRepository;
import com.mshoppers.common.entity.User;

public class MshoppersUserDetailService implements UserDetailsService {

	@Autowired
	private UserRepository userRepo;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepo.getUserByEmail(email);
		if(user != null) {
			return new MshoppersUserDetails(user);
		}
		
		throw new UsernameNotFoundException("Could not find user with E-mail: "+ email);
	}

}
