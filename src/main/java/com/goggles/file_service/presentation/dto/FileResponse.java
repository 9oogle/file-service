package com.goggles.file_service.presentation.dto;

import java.util.UUID;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileResponse {
	public record Upload(
		UUID fileId
	) {
	}
}
