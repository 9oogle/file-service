package com.goggles.file_service.infrastructure.storage.config;

import java.io.File;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration
@ConditionalOnProperty(
	name = "file.storage.type",
	havingValue = "local",
	matchIfMissing = true
)
@RequiredArgsConstructor
@EnableConfigurationProperties(LocalStorageProperties.class)
public class LocalStorageConfig implements WebMvcConfigurer {

	private final LocalStorageProperties properties;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		String absolutePath = new File(properties.path()).getAbsolutePath();

		registry.addResourceHandler(properties.normalizedUrl() + "**")
			.addResourceLocations("file:" + absolutePath);
	}
}