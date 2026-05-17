package com.goggles.file_service.application;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.goggles.file_service.application.dto.FileServiceDto;
import com.goggles.file_service.domain.FileInfo;
import com.goggles.file_service.domain.FileRepository;
import com.goggles.file_service.domain.FileTag;
import com.goggles.file_service.domain.Storage;
import com.goggles.file_service.domain.exception.FileNotFoundException;
import com.goggles.file_service.domain.query.FileQueryRepository;
import com.goggles.file_service.domain.service.FileDownloader;
import com.goggles.file_service.domain.service.FileUploader;
import com.goggles.file_service.domain.service.RoleChecker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

	private final FileQueryRepository fileQueryRepository;

	private final FileRepository fileRepository;
	private final FileUploader fileUploader;
	private final FileDownloader fileDownloader;
	private final RoleChecker roleChecker;

	@Value("${file.storage.type:local}")
	private String storage;

	@Transactional
	public UUID upload(FileServiceDto.FileUpload dto) {
		FileInfo fileInfo = FileInfo.upload(
			Storage.from(storage),
			dto.groupId(),
			FileTag.from(dto.tag()),
			dto.toSource(),
			fileUploader,
			roleChecker
		);
		fileRepository.save(fileInfo);
		return fileInfo.getId();
	}

	@Transactional(readOnly = true)
	public FileServiceDto.FileDownload download(UUID fileId) {
		FileInfo fileInfo = getFileInfo(fileId);

		// 다운로드 권한 검증 (강의 자료/동영상은 수강자만 가능)
		fileInfo.verifyDownloadable(roleChecker);

		return FileServiceDto.FileDownload.from(
			fileDownloader.download(fileInfo)
		);
	}

	@Transactional
	public void delete(UUID fileId) {
		FileInfo fileInfo = getFileInfo(fileId);
		fileInfo.delete(roleChecker);
	}

	private FileInfo getFileInfo(UUID fileId) {
		return fileQueryRepository.findById(fileId)
			.orElseThrow(() -> new FileNotFoundException(fileId));
	}
}
