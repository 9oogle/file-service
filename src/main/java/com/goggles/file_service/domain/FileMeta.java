package com.goggles.file_service.domain;

import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import com.goggles.common.exception.BadRequestException;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileMeta {
	@Enumerated(EnumType.STRING)
	@Column(length = 15, nullable = false)
	private Storage storage;

	@Column(length = 120, nullable = false)
	private String fileName; //업로드시 원본 파일명

	@Column(length = 35)
	private String extension; // 파일 확장자

	@Column(length = 65, nullable = false)
	private String contentType; //파일 형식(이미지/영상/pdf)

	private long contentLength; // 파일 길이

	protected FileMeta(Storage storage, String fileName, String contentType, long contentLength) {
		if (storage == null) {
			throw new BadRequestException("파일 저장소는 필수 입력값 입니다.");
		}
		if (!StringUtils.hasText(fileName)) {
			throw new IllegalArgumentException("fileName은 필수값");
		}

		this.storage = storage;
		this.fileName = fileName;
		this.contentType = StringUtils.hasText(contentType) ? contentType : MediaType.APPLICATION_OCTET_STREAM_VALUE;
		this.contentLength = contentLength;
		this.extension = extractExtension(fileName);
	}

	private String extractExtension(String fileName) {
		int dotIndex = fileName.lastIndexOf(".");
		if (dotIndex == -1) {
			return "";
		}
		return fileName.substring(dotIndex + 1).toLowerCase();
	}

	public boolean isImage() {
		return StringUtils.hasText(contentType) && contentType.startsWith("image/");
	}

	public boolean isVideo() {
		return StringUtils.hasText(contentType) && contentType.startsWith("video/");
	}
}
