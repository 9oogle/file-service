package com.goggles.file_service.infrastructure.storage.config;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@ConditionalOnProperty(
	name = "file.storage.type",
	havingValue = "gcs"
)
@RequiredArgsConstructor
@EnableConfigurationProperties(GcsStorageProperties.class)
public class GcsStorageConfig {

	private final GcsStorageProperties properties;
	private final ResourceLoader resourceLoader;

	@Bean
	public Storage googleCloundStorage() throws IOException {
		if (StringUtils.hasText(properties.keyPath())) {
			String cleanPath = properties.keyPath().trim();
			if (cleanPath.startsWith("/") && !cleanPath.startsWith("file:")) {
				cleanPath = "file:" + cleanPath;
			}

			Resource resource = resourceLoader.getResource(cleanPath);
			try (InputStream inputStream = resource.getInputStream()) {
				return StorageOptions.newBuilder()
					.setProjectId(properties.projectId())
					.setCredentials(GoogleCredentials.fromStream(inputStream))
					.build()
					.getService();
			}
		}

		return StorageOptions.newBuilder()
			.setProjectId(properties.projectId())
			.setCredentials(GoogleCredentials.getApplicationDefault())
			.build()
			.getService();
	}
}