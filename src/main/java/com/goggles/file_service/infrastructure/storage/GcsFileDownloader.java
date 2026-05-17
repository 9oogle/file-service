package com.goggles.file_service.infrastructure.storage;

import java.io.InputStream;
import java.nio.channels.Channels;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import com.goggles.file_service.domain.FileInfo;
import com.goggles.file_service.domain.exception.FileNotFoundException;
import com.goggles.file_service.domain.service.FileDownloadContent;
import com.goggles.file_service.domain.service.FileDownloader;
import com.goggles.file_service.infrastructure.storage.config.GcsStorageProperties;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;

import lombok.RequiredArgsConstructor;

@Component
@ConditionalOnProperty(
	name = "file.storage.type",
	havingValue = "gcs"
)
@RequiredArgsConstructor
@EnableConfigurationProperties(GcsStorageProperties.class)
public class GcsFileDownloader implements FileDownloader {

	private Storage storage;
	private GcsStorageProperties properties;

	@Override
	public FileDownloadContent download(FileInfo fileInfo) {

		//BlobId
		BlobId blobId = BlobId.of(properties.bucket(), fileInfo.getFilePath());
		//파일가져오기
		Blob blob = storage.get(blobId);

		if (blob == null || !blob.exists()) {//파일 존재여부 페크
			throw new FileNotFoundException(fileInfo.getId());
		}

		InputStream inputStream = Channels.newInputStream(blob.reader());

		return FileDownloadContent.builder()
			.inputStream(inputStream)
			.fileName(fileInfo.getMetadata().getFileName())
			.contentType(blob.getContentType())
			.contentLength(blob.getSize())
			.build();
	}
}
