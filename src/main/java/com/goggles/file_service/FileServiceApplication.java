package com.goggles.file_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(scanBasePackages = "com.goggles")
@ConfigurationPropertiesScan
@EnableJpaAuditing
public class FileServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileServiceApplication.class, args);
	}

}
