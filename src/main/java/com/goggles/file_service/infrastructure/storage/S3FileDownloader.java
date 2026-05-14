package com.goggles.file_service.infrastructure.storage;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.goggles.file_service.domain.FileInfo;
import com.goggles.file_service.domain.service.FileDownloadContent;
import com.goggles.file_service.domain.service.FileDownloader;

@Component
@ConditionalOnProperty(
	name = "file.storage.type",
	havingValue = "s3"
)
public class S3FileDownloader implements FileDownloader {

	@Override
	public FileDownloadContent download(FileInfo fileInfo) {
		return null;
	}
}
