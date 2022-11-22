package com.mshoppers.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderTest {

	@Test
	public void testPassword() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String pass = "secret";
		String encodedPassword = encoder.encode(pass);
		
		System.out.println(encodedPassword);
		
		boolean result = encoder.matches(pass, encodedPassword);
		
		assertThat(result).isTrue();
	}
}
