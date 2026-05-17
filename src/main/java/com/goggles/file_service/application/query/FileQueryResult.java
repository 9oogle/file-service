package com.goggles.file_service.application.query;

import java.time.LocalDateTime;
import java.util.UUID;

import com.goggles.file_service.domain.FileGroup;
import com.goggles.file_service.domain.FileInfo;
import com.goggles.file_service.domain.FileMeta;

import lombok.Builder;

@Builder
public record FileQueryResult(
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
	public static FileQueryResult from(FileInfo fileInfo, String storageEndpoint) {
		FileGroup group = fileInfo.getGroup();
		FileMeta meta = fileInfo.getMetadata();

		return FileQueryResult.builder()
			.fileId(fileInfo.getId())
			.groupId(group.getGroupId())
			.tag(group.getTag().name())
			.tagDescription(group.getTag().getDescription())
			.fileName(meta.getFileName())
			.extension(meta.getExtension())
			.contentType(meta.getContentType())
			.isImage(meta.isImage())
			.isVideo(meta.isVideo())
			.createdAt(fileInfo.getCreatedAt())
			.fileUrl(getUrl(storageEndpoint, fileInfo.getFilePath()))
			.build();
	}

	private static String getUrl(String endpoint, String filePath) {

		if (!endpoint.endsWith("/"))
			endpoint += "/";
		return endpoint + filePath;
	}

}
