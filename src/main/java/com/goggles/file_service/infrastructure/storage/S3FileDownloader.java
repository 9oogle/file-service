package com.goggles.file_service.infrastructure.storage;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import com.goggles.file_service.domain.FileInfo;
import com.goggles.file_service.domain.exception.FileErrorCode;
import com.goggles.file_service.domain.exception.FileNotFoundException;
import com.goggles.file_service.domain.exception.FileStorageException;
import com.goggles.file_service.domain.service.FileDownloadContent;
import com.goggles.file_service.domain.service.FileDownloader;
import com.goggles.file_service.infrastructure.storage.config.S3StorageProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

@Slf4j
@Component
@ConditionalOnProperty(
	name = "file.storage.type",
	havingValue = "s3"
)
@RequiredArgsConstructor
@EnableConfigurationProperties({S3StorageProperties.class})
public class S3FileDownloader implements FileDownloader {

	private final S3StorageProperties s3StorageProperties;
	private final S3Client s3Client;

	@Override
	public FileDownloadContent download(FileInfo fileInfo) {
		try {
			GetObjectRequest request = GetObjectRequest.builder()
				.bucket(s3StorageProperties.bucket())
				.key(fileInfo.getFilePath())
				.build();

			ResponseInputStream<GetObjectResponse> inputStream
				= s3Client.getObject(request);
			GetObjectResponse response = inputStream.response();
			return FileDownloadContent.builder()
				.inputStream(inputStream)
				.fileName(fileInfo.getMetadata().getFileName())
				.contentType(response.contentType())
				.contentLength(response.contentLength())
				.build();
		} catch (NoSuchKeyException e) {
			log.error("AWS S3에 파일을 찾을 수 없음 - bucket: {}, key: {}, 사유: {}", s3StorageProperties.bucket(),
				fileInfo.getFilePath(),
				e.getMessage(), e);
			throw new FileNotFoundException(fileInfo.getId());
		} catch (Exception e) {
			log.error("AWS S3 다운로드 중 오류 발생 - 사유: {}", e.getMessage(), e);
			throw new FileStorageException(FileErrorCode.FILE_STORAGE_DOWNLOAD_ERROR);
		}
	}
}