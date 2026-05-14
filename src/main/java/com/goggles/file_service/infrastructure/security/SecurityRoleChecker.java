package com.goggles.file_service.infrastructure.security;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.goggles.file_service.domain.FileInfo;
import com.goggles.file_service.domain.service.RoleChecker;

@Component
public class SecurityRoleChecker implements RoleChecker {
	@Override
	public boolean isMaster() {
		return true;
	}

	@Override
	public boolean isMine(FileInfo fileInfo) {
		return true;
	}

	@Override
	public boolean isLoggedIn() {
		return true;
	}

	@Override
	public UUID getLoggedUserId() {
		//todo: 추후 바꿔야함
		return UUID.randomUUID();
	}
}
