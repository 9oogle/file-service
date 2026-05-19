package com.goggles.file_service.infrastructure.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import com.goggles.file_service.domain.FileInfo;
import com.goggles.file_service.domain.exception.FileErrorCode;
import com.goggles.file_service.domain.exception.FileNotFoundException;
import com.goggles.file_service.domain.exception.FileStorageException;
import com.goggles.file_service.domain.service.FileDownloadContent;
import com.goggles.file_service.domain.service.FileDownloader;
import com.goggles.file_service.infrastructure.storage.config.LocalStorageProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ConditionalOnProperty(
	name = "file.storage.type",
	havingValue = "local",
	matchIfMissing = true
)
@RequiredArgsConstructor
@EnableConfigurationProperties(LocalStorageProperties.class)
public class LocalFileDownloader implements FileDownloader {

	private final LocalStorageProperties properties;

	@Override
	public FileDownloadContent download(FileInfo fileInfo) {
		Path parentPath = Path.of(properties.path()).toAbsolutePath().normalize();
		Path filePath = parentPath.resolve(fileInfo.getFilePath()).normalize();

		// path traversal 방어 — 부모 경로를 벗어나는 접근 차단
		if (!filePath.startsWith(parentPath)) {
			log.warn("유효하지 않은 파일 경로 접근 시도 - fileId: {}, path: {}",
				fileInfo.getId(), filePath);
			throw new FileStorageException(FileErrorCode.FILE_STORAGE_INVALID_PATH);
		}

		// 파일이 없거나 디렉토리인 경우 → NotFound가 의미상 더 정확
		if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
			throw new FileNotFoundException(fileInfo.getId());
		}

		try {
			return FileDownloadContent.builder()
				.inputStream(Files.newInputStream(filePath))
				.contentType(Files.probeContentType(filePath))
				.fileName(fileInfo.getMetadata().getFileName())
				.contentLength(Files.size(filePath))
				.build();
		} catch (IOException e) {
			log.error("파일 읽기 오류 발생 - 사유: {}", e.getMessage(), e);
			throw new FileStorageException(FileErrorCode.FILE_STORAGE_READ_ERROR);
		}
	}
}