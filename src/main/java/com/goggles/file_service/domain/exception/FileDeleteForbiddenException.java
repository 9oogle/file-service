package com.goggles.file_service.domain.exception;

import com.goggles.common.exception.ForbiddenException;

public class FileDeleteForbiddenException extends ForbiddenException {
	public FileDeleteForbiddenException() {
		super(FileErrorCode.FILE_DELETE_FORBIDDEN.getMessage());
	}
}