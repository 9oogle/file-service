// com.goggles.file_service.infrastructure.persistence.FileJpaRepository
package com.goggles.file_service.infrastructure.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.goggles.file_service.domain.FileInfo;

public interface JpaFileRepository extends JpaRepository
	<FileInfo, UUID> {
}