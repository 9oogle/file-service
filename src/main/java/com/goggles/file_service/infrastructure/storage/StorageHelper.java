package com.goggles.file_service.infrastructure.storage;

import java.util.UUID;

import org.springframework.util.StringUtils;

public class StorageHelper {

	public static String getStorageFileName(String fileName) {
		String ext = fileName.lastIndexOf(".") !=
			-1 ? fileName.substring(fileName.lastIndexOf(".") + 1) : "";

		return UUID.randomUUID().toString() + (StringUtils.hasText(ext) ? "." + ext : "");
	}
}