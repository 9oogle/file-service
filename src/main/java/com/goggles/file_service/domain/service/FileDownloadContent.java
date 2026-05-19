package com.goggles.file_service.domain.service;

import java.io.InputStream;

import lombok.Builder;

@Builder
public record FileDownloadContent(
	InputStream inputStream,
	String fileName,
	String contentType,
	long contentLength
) {
}
