package com.mshoppers.admin;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

	@Value("${USER_PHOTOS}")
	private String userPhoto;

	@Value("${CATEGORY_PHOTOS}")
	private String categoryPhoto;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		/* For User Photos */
		Path userPhotoDir = Paths.get(userPhoto);

		String userPhotoPath = userPhotoDir.toFile().getAbsolutePath();

		registry.addResourceHandler("/" + userPhotoDir + "/**").addResourceLocations("file:/" + userPhotoPath + "/");

		/* For Category Photos */
		Path categoryPhotoDir = Paths.get(categoryPhoto);

		String categoryPhotoPath = categoryPhotoDir.toFile().getAbsolutePath();

		registry.addResourceHandler("/" + categoryPhotoDir + "/**").addResourceLocations("file:/" + categoryPhotoPath + "/");
	}

}
