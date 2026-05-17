package com.goggles.file_service.domain.exception;

import com.goggles.common.exception.ForbiddenException;

public class FileForbiddenException extends ForbiddenException {

	public FileForbiddenException(FileErrorCode errorCode) {
		super(errorCode.getMessage());
	}
}