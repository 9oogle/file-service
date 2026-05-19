package com.goggles.file_service.infrastructure.storage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import com.goggles.file_service.domain.FileInfo;
import com.goggles.file_service.domain.FileTag;
import com.goggles.file_service.domain.exception.FileErrorCode;
import com.goggles.file_service.domain.exception.FileStorageException;
import com.goggles.file_service.domain.service.FileUploader;
import com.goggles.file_service.infrastructure.storage.config.GcsStorageProperties;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ConditionalOnProperty(
	name = "file.storage.type",
	havingValue = "gcs"
)
@RequiredArgsConstructor
@EnableConfigurationProperties(GcsStorageProperties.class)
public class GcsFileUploader implements FileUploader {

	private final Storage storage;
	private final GcsStorageProperties properties;
	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

	@Override
	public String upload(FileTag tag, FileInfo.FileSource source) {
		String today = formatter.format(LocalDate.now());
		String storageFileName = StorageHelper.getStorageFileName(source.originalFileName());
		String relativePath = "%s/%s/%s".formatted(tag.getDirectory(), today, storageFileName);

		try {
			BlobInfo blobInfo = BlobInfo.newBuilder(properties.bucket(), relativePath)
				.setContentType(source.contentType())
				.build();

			storage.createFrom(blobInfo, source.inputStream());

			return relativePath;
		} catch (Exception e) {
			log.error("Google Cloud Storage 파일 업로드 실패 - 사유: {}", e.getMessage(), e);
			throw new FileStorageException(FileErrorCode.FILE_STORAGE_UPLOAD_ERROR);
		}

	}
}