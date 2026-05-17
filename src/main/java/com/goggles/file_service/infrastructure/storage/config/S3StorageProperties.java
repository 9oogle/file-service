package com.goggles.file_service.infrastructure.storage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file.s3")
public record S3StorageProperties(
	String bucket,
	String region,
	String accessKey,
	String secretKey

) {
}
