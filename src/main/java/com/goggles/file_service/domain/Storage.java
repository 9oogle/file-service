package com.goggles.file_service.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Storage {
	LOCAL("서버 로컬 저장소"),
	S3("AWS S3"),
	GCS("Google Cloud Storage");

	private final String description;
}
