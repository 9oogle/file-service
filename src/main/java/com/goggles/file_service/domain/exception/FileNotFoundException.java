package com.goggles.file_service.domain.exception;

import java.util.UUID;

import com.goggles.common.exception.NotFoundException;

public class FileNotFoundException extends NotFoundException {
	public FileNotFoundException(UUID fileId) {
		super(FileErrorCode.FILE_NOT_FOUND.getMessage() + " fileId=" + fileId);
	}
}