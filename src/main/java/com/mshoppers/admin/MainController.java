package com.mshoppers.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

	@GetMapping("/")
	public String viewHomePage() {
		return "index";
	}

	@GetMapping("/login")
	public String viewLoginPage() {
		return "login";
	}

	@GetMapping("/logout")
	public String viewLogOutPage() {
		return "login";
	}

	@GetMapping("/error")
	public String viewErrorPage() {
		return "error";
	}
}
