package com.mshoppers.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan({"com.mshoppers.common.entity"})
public class MshoppersBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MshoppersBackendApplication.class, args);
	}

}
