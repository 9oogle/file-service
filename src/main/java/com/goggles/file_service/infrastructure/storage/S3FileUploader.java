package com.goggles.file_service.infrastructure.storage;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.goggles.file_service.domain.FileInfo;
import com.goggles.file_service.domain.FileTag;
import com.goggles.file_service.domain.service.FileUploader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ConditionalOnProperty(
	name = "file.storage.type",
	havingValue = "s3"
)
@RequiredArgsConstructor
public class S3FileUploader implements FileUploader {
	@Override
	public String upload(FileTag tag, FileInfo.FileSource source) {
		return "";
	}
}
