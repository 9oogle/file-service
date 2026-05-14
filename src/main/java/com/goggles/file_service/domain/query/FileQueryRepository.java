package com.goggles.file_service.domain.query;

import java.util.Optional;
import java.util.UUID;

import com.goggles.file_service.domain.FileInfo;

public interface FileQueryRepository {
	Optional<FileInfo> findById(UUID fileId);
}