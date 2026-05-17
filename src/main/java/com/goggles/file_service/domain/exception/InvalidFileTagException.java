package com.goggles.file_service.domain.exception;

import com.goggles.common.exception.BadRequestException;

public class InvalidFileTagException extends BadRequestException {
	public InvalidFileTagException(String tagName) {
		super(FileErrorCode.FILE_TAG_INVALID.getMessage() + " tag=" + tagName);
	}
}