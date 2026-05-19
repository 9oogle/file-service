package com.goggles.file_service.infrastructure.storage;

import java.util.Set;
import java.util.UUID;

import org.springframework.util.StringUtils;

public class StorageHelper {

	// 허용 확장자 화이트리스트 (필요에 따라 추가)
	private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
		// 문서
		"pdf", "doc", "docx", "ppt", "pptx", "xls", "xlsx", "hwp", "txt",
		// 이미지
		"jpg", "jpeg", "png", "gif", "webp", "bmp",
		// 영상
		"mp4", "mov", "avi", "mkv", "webm",
		// 압축
		"zip"
	);

	public static String getStorageFileName(String fileName) {
		String ext = extractSafeExtension(fileName);
		return UUID.randomUUID() + (ext.isEmpty() ? "" : "." + ext);
	}

	private static String extractSafeExtension(String fileName) {
		if (!StringUtils.hasText(fileName)) {
			return "";
		}

		int dotIndex = fileName.lastIndexOf(".");
		if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
			return "";
		}

		String ext = fileName.substring(dotIndex + 1).toLowerCase();

		// 경로 탈출 문자 차단 (/, \, .., 공백 등)
		if (ext.contains("/") || ext.contains("\\") || ext.contains("..") || ext.contains(" ")) {
			return "";
		}

		// 화이트리스트 검증
		if (!ALLOWED_EXTENSIONS.contains(ext)) {
			return "";
		}

		return ext;
	}
}