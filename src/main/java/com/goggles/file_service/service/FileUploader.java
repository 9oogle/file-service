package com.goggles.file_service.service;

import com.goggles.file_service.domain.FileInfo;
import com.goggles.file_service.domain.FileTag;

/**
 파일 업로드- local, s3, gcs
 업로드가 완료되면 업로된 서버 경로 반환
 * */
public interface FileUploader {
	String upload(FileTag tag, FileInfo.FileSource source);
}
