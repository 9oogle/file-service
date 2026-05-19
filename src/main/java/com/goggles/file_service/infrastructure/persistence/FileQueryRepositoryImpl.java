package com.goggles.file_service.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.goggles.file_service.domain.FileInfo;
import com.goggles.file_service.domain.FileTag;
import com.goggles.file_service.domain.QFileInfo;
import com.goggles.file_service.domain.query.FileQueryRepository;
import com.querydsl.core.BooleanBuilder;
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

	@Override
	public List<FileInfo> findAll(String groupId, FileTag tag) {
		QFileInfo fileInfo = QFileInfo.fileInfo;
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(fileInfo.group.groupId.eq(groupId))
			.and(fileInfo.deletedAt.isNull());

		if (tag != null) {
			builder.and(fileInfo.group.tag.eq(tag));
		}

		return queryFactory
			.selectFrom(fileInfo)
			.where(builder)
			.orderBy(fileInfo.createdAt.asc())
			.fetch();
	}
}