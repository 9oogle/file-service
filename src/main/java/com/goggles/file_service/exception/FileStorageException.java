package com.goggles.file_service.exception;

import com.goggles.common.exception.BadRequestException;

public class FileStorageException extends BadRequestException {
	public FileStorageException(String message) {
		super(message);
	}
}
