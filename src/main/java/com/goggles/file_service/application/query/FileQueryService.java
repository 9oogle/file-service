package com.goggles.file_service.application.query;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.goggles.file_service.domain.FileTag;
import com.goggles.file_service.domain.exception.FileNotFoundException;
import com.goggles.file_service.domain.query.FileQueryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileQueryService {

	private final FileQueryRepository fileQueryRepository;

	@Value("${file.storage.endpoint}")
	private String storageEndpoint;

	public FileQueryResult findById(UUID fileId) {
		return fileQueryRepository.findById(fileId)
			.map(fileInfo -> FileQueryResult.from(fileInfo,
				storageEndpoint))
			.orElseThrow(() -> new FileNotFoundException(fileId));
	}

	public List<FileQueryResult> findAll(String groupId, String tag) {

		return fileQueryRepository.findAll(groupId, StringUtils.hasText(tag) ? FileTag.from(tag) : null)
			.stream()
			.map(fileInfo -> FileQueryResult.from(fileInfo, storageEndpoint))
			.toList();
	}

}
