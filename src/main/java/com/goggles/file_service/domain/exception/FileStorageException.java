package com.goggles.file_service.domain.exception;

import com.goggles.common.exception.InternalServerException;

public class FileStorageException extends InternalServerException {

	public FileStorageException(FileErrorCode errorCode) {
		super(errorCode.getMessage());
	}
}