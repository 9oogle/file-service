package com.goggles.file_service.infrastructure.persistence;

import org.springframework.stereotype.Repository;

import com.goggles.file_service.domain.FileInfo;
import com.goggles.file_service.domain.FileRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FileRepositoryImpl implements FileRepository {

	private final JpaFileRepository fileJpaRepository;

	@Override
	public FileInfo save(FileInfo fileInfo) {
		return fileJpaRepository.save(fileInfo);
	}
}