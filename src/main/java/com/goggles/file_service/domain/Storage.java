package com.goggles.file_service.domain;

import com.goggles.file_service.domain.exception.FileErrorCode;
import com.goggles.file_service.domain.exception.FileStorageException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum Storage {
	LOCAL("서버 로컬 저장소"),
	S3("AWS S3"),
	GCS("Google Cloud Storage");

	private final String description;

	public static Storage from(String storageType) {
		try {
			return Storage.valueOf(storageType.toUpperCase());
		} catch (IllegalArgumentException | NullPointerException e) {
			log.warn("지원하지 않는 저장소- 타입: {} , 사유: {}", storageType, e.getMessage(), e);
			throw new FileStorageException(FileErrorCode.FILE_STORAGE_TYPE_UNSUPPORTED);
		}
	}
}
