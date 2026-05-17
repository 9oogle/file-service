package com.goggles.file_service.infrastructure.storage.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Slf4j
@Configuration
@ConditionalOnProperty(
	name = "file.storage.type",
	havingValue = "s3"
)
@RequiredArgsConstructor
@EnableConfigurationProperties(S3StorageProperties.class)
public class S3StorageConfig implements WebMvcConfigurer {
	private final S3StorageProperties s3StorageProperties;

	@Bean
	public S3Client s3Client() {
		AwsBasicCredentials credentials = AwsBasicCredentials.create(
			s3StorageProperties.accessKey(),
			s3StorageProperties.secretKey()
		);

		return S3Client.builder()
			.region(Region.of(s3StorageProperties.region()))
			.credentialsProvider(StaticCredentialsProvider.create(credentials))
			.build();
	}

}