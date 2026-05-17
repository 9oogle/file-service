package com.goggles.file_service.domain.query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.goggles.file_service.domain.FileInfo;
import com.goggles.file_service.domain.FileTag;

public interface FileQueryRepository {
	Optional<FileInfo> findById(UUID fileId);

	List<FileInfo> findAll(String groupId, FileTag tag);

	default List<FileInfo> findAll(String groupId) {
		return findAll(groupId, null);
	}
}