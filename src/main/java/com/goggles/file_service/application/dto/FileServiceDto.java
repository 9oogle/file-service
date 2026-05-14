package com.goggles.file_service.application.dto;

import java.io.InputStream;

import com.goggles.file_service.domain.FileInfo;
import com.goggles.file_service.domain.service.FileDownloadContent;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileServiceDto {

	@Builder
	public record FileUpload(
		String groupId,
		String tag,
		InputStream inputStream,
		String originalFileName,
		String contentType,
		long contentLength
	) {
		public FileInfo.FileSource toSource() {
			return FileInfo.FileSource.builder()
				.inputStream(this.inputStream)
				.originalFileName(this.originalFileName)
				.contentType(this.contentType)
				.contentLength(this.contentLength)
				.build();
		}
	}

	@Builder
	public record FileDownload(
		InputStream inputStream,
		String fileName,
		String contentType,
		long contentLength
	) {
		public static FileDownload from(FileDownloadContent content) {
			return FileDownload.builder()
				.inputStream(content.inputStream())
				.fileName(content.fileName())
				.contentType(content.contentType())
				.contentLength(content.contentLength())
				.build();
		}
	}
}