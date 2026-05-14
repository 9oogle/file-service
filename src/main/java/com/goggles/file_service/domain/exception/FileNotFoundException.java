package com.goggles.file_service.domain.exception;

import java.util.UUID;

public class FileNotFoundException extends RuntimeException {
	public FileNotFoundException(UUID fileId) {
		super("File Not Found" + fileId);
	}
}
