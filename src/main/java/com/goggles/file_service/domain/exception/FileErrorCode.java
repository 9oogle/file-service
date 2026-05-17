package com.goggles.file_service.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileErrorCode {

	// 파일 조회/접근
	FILE_NOT_FOUND("해당 파일을 찾을 수 없습니다."),

	// 파일 업로드
	FILE_UPLOAD_LOGIN_REQUIRED("파일 업로드는 로그인이 필요합니다."),

	// 파일 다운로드
	FILE_DOWNLOAD_FORBIDDEN("해당 파일을 다운로드할 권한이 없습니다."),

	// 파일 삭제
	FILE_DELETE_FORBIDDEN("파일 삭제 권한이 없습니다."),

	// 파일 태그
	FILE_TAG_INVALID("지원하지 않는 파일 태그입니다."),

	// 파일 저장소 (시스템 오류)
	FILE_STORAGE_UPLOAD_FAILED("파일 저장소 업로드에 실패하였습니다."),
	FILE_STORAGE_IO_ERROR("파일 저장 중 시스템 오류가 발생했습니다."),
	FILE_STORAGE_TYPE_UNSUPPORTED("지원하지 않는 저장소 타입입니다."),
	FILE_STORAGE_UPLOAD_ERROR("파일 업로드 중 오류가 발생했습니다."),
	FILE_STORAGE_DOWNLOAD_ERROR("파일 다운로드 중 오류가 발생했습니다."),
	FILE_STORAGE_READ_ERROR("파일을 읽는 중 오류가 발생했습니다."),
	FILE_STORAGE_INVALID_PATH("유효하지 않은 파일 접근입니다.");

	private final String message;
}