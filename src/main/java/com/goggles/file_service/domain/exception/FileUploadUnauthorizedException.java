package com.goggles.file_service.domain.exception;

import com.goggles.common.exception.UnAuthorizedException;

public class FileUploadUnauthorizedException extends UnAuthorizedException {
	public FileUploadUnauthorizedException() {
		super(FileErrorCode.FILE_UPLOAD_LOGIN_REQUIRED.getMessage());
	}
}