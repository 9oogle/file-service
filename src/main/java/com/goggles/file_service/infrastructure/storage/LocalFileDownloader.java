package com.goggles.file_service.infrastructure.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import com.goggles.file_service.domain.FileInfo;
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

		if (!filePath.startsWith(parentPath)) {
			throw new FileStorageException("유효하지 않은 파일 접근입니다.");
		}

		// 파일 존재 유무와 권한 체크
		if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
			throw new FileStorageException("파일이 존재하지 않거나 읽을 수 없습니다.");
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
			throw new FileStorageException("파일을 읽는 중 오류가 발생했습니다.");
		}
	}
}