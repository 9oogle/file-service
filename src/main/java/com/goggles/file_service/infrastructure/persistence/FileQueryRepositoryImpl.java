package com.goggles.file_service.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.goggles.file_service.domain.FileInfo;
import com.goggles.file_service.domain.QFileInfo;
import com.goggles.file_service.domain.query.FileQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FileQueryRepositoryImpl implements FileQueryRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<FileInfo> findById(UUID fileId) {

		QFileInfo fileInfo = QFileInfo.fileInfo;

		return Optional.ofNullable(
			queryFactory.selectFrom(fileInfo)
				.where(
					fileInfo.id.eq(fileId),
					fileInfo.deletedAt.isNull()
				)
				.fetchOne()
		);
	}
}