package com.goggles.file_service.domain;

import java.io.InputStream;

import org.hibernate.annotations.SQLRestriction;
import org.springframework.util.StringUtils;

import com.goggles.common.domain.BaseAudit;
import com.goggles.file_service.exception.FileStorageException;
import com.goggles.file_service.service.FileUploader;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Entity
@ToString
@Table(name = "p_file_info")
@SQLRestriction("deleted_at IS NULL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileInfo extends BaseAudit {

	@Id
	@Column(name = "file_id", length = 45)
	@GeneratedValue(strategy = GenerationType.UUID)
	private Long id;

	@Embedded
	private FileGroup group;

	@Embedded
	private FileMeta metadata;

	private String filePath; //서버 업로드 경로

	@Builder
	protected FileInfo(Storage storage, String groupId, FileTag tag, String fileName, String contentType,
		long contentLength, String filePath) {
		this.group = new FileGroup(groupId, tag);
		this.metadata = new FileMeta(storage, fileName, contentType, contentLength);
		this.filePath = filePath;
	}

	public static FileInfo upload(Storage storage, String groupId, FileTag tag,
		FileSource source, FileUploader uploader) {
		//파일 업로드 진행
		String filePath = uploader.upload(tag, source);
		if (!StringUtils.hasText(filePath)) {
			throw new FileStorageException("업로드 실패");
		}
		//파일 정보 엔티티 완성
		return FileInfo.builder()
			.storage(storage)
			.groupId(groupId)
			.tag(tag)
			.fileName(source.originalFileName())
			.contentType(source.contentType())
			.contentLength(source.contentLength())
			.filePath(filePath)
			.build();
	}

	public record FileSource(
		InputStream inputStream,
		String originalFileName,
		String contentType,
		long contentLength
	) {
	}

	//파일 삭제는 master 또는 파일소유자만 삭제 가능
	public void delete() {
		
	}

}

