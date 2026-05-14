package com.goggles.file_service.infrastructure.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import com.goggles.file_service.domain.FileInfo;
import com.goggles.file_service.domain.FileTag;
import com.goggles.file_service.domain.exception.FileStorageException;
import com.goggles.file_service.domain.service.FileUploader;
import com.goggles.file_service.infrastructure.storage.config.LocalStorageProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ConditionalOnProperty(
	name = "file.storage.type",
	havingValue = "local",
	matchIfMissing = true //값이 설정되어 있지 않으면 기본 local 값으로 설정
)
@RequiredArgsConstructor
@EnableConfigurationProperties(LocalStorageProperties.class)
public class LocalFileUploader implements FileUploader {

	private final LocalStorageProperties properties;
	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

	//태그 디렉토리/년월일/uuid.확장자
	//다운로드시 원본 파일
	@Override
	public String upload(FileTag tag, FileInfo.FileSource source) {
		//서버 업로드 경로
		String today = formatter.format(LocalDateTime.now());
		String relativePath = "%s/%s".formatted(tag.getDirectory(), today);
		Path parentPath = Path.of(properties.path()).toAbsolutePath().normalize();
		Path targetDirectory = parentPath.resolve(relativePath);

		//업로드할 디렉토리가 없다면 생성
		try {
			if (!Files.exists(targetDirectory)) {
				Files.createDirectories(targetDirectory);
			}

			//파일 업로드
			String storageFileName = StorageHelper.getStorageFileName(source.originalFileName());
			//파일 업로드 경로 생성
			Path targetFile = targetDirectory.resolve(storageFileName);
			//파일 업로드
			Files.copy(source.inputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);

			log.info("로컬 파일 업로드 성공 - 업로드 경로 {}", targetFile);
			return relativePath + "/" + storageFileName;

		} catch (IOException e) {
			log.error("로컬 파일 업로드 실패 - 업로드 경로 {}, 사유{}", targetDirectory, e.getMessage(), e);
			throw new FileStorageException("퍄일 저장 중 시스템 오류가 발생했습니다.");
		}
	}

}
