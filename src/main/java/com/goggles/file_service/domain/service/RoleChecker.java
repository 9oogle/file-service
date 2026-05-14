package com.goggles.file_service.domain.service;

import java.util.UUID;

import com.goggles.file_service.domain.FileInfo;

public interface RoleChecker {
	boolean isMaster();

	boolean isMine(FileInfo fileInfo);

	boolean isLoggedIn();

	UUID getLoggedUserId();
}
