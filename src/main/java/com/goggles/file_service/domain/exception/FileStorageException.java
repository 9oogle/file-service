package com.goggles.file_service.domain.exception;

import com.goggles.common.exception.BadRequestException;

public class FileStorageException extends BadRequestException {
	public FileStorageException(String message) {
		super(message);
	}
}
