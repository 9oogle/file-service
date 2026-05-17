package com.goggles.file_service.presentation.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.goggles.file_service.application.query.FileQueryResult;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileResponse {
	public record Upload(
		UUID fileId
	) {
	}

	@Builder
	public record FileInfo(
		UUID fileId,
		String groupId,
		String tag,
		String tagDescription,
		String fileName,
		String extension,
		String contentType,
		boolean isImage,
		boolean isVideo,
		String fileUrl,
		LocalDateTime createdAt
	) {
		public static FileInfo from(FileQueryResult result) {
			return FileInfo.builder()
				.fileId(result.fileId())
				.groupId(result.groupId())
				.tag(result.tag())
				.tagDescription(result.tagDescription())
				.fileName(result.fileName())
				.extension(result.extension())
				.contentType(result.contentType())
				.isImage(result.isImage())
				.isVideo(result.isVideo())
				.fileUrl(result.fileUrl())
				.createdAt(result.createdAt())
				.build();
		}
	}
}
