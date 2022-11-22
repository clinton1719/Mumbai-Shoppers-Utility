package com.mshoppers.admin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

public class FileUploadUtil {
	
	ClassLoader classLoader = getClass().getClassLoader();

	private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadUtil.class);

	public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) {

		Path uploadPath = Paths.get(uploadDir);

		if (!Files.exists(uploadPath)) {
			try {
				Files.createDirectories(uploadPath);
			} catch (IOException e) {
				LOGGER.error("ERROR Occurred ::: " + "Could not create directory: " + uploadPath + ", File name is: "
						+ fileName + "  " + e);
				e.printStackTrace();
			}
		}

		try (InputStream inputStream = multipartFile.getInputStream()) {
			Path filePath = uploadPath.resolve(fileName);
			Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			LOGGER.error("ERROR Occurred ::: " + "Could not create file in: " + uploadPath + ", File name is: "
					+ fileName + "  " + e);
			e.printStackTrace();
		}
	}

	public static void cleanDirectory(String directory) {
		try {
			Path dirPath = Paths.get(directory);
			if (Files.exists(dirPath)) {
				System.out.println(dirPath.toFile().getAbsolutePath());
				FileUtils.cleanDirectory(dirPath.toFile());
			}
		} catch (IOException e) {
			LOGGER.error("ERROR Occurred ::: " + "Could not clean directory in: " + directory + "  " + e);
			e.printStackTrace();
		}
	}

	public static void deleteFile(String directory) {
		try {
			Path dirPath = Paths.get(directory);
			cleanDirectory(directory);
			Files.deleteIfExists(dirPath);
		} catch (Exception e) {
			LOGGER.error("ERROR Occurred ::: " + "Could not delete directory in: " + directory + "  " + e);
			e.printStackTrace();
		}
	}
}
