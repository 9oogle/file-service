package com.goggles.file_service.infrastructure.storage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "file.local.base")
public record LocalStorageProperties(
	@DefaultValue("./stroage/files")
	String path,

	@DefaultValue("/upload/")
	String url
) {
	public String normalizedUrl() {
		return url.endsWith("/") ? url : url + "/";
	}
}