package com.goggles.file_service.infrastructure.security;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.goggles.file_service.domain.FileInfo;
import com.goggles.file_service.domain.service.RoleChecker;

@Component
public class SecurityRoleChecker implements RoleChecker {

	@Override
	public boolean isMaster() {
		return isLoggedIn() && getLoggedUser() != null
			&& getLoggedUser().getUserRole().contains("MASTER");
	}

	@Override
	public boolean isMine(FileInfo fileInfo) {
		return getLoggedUserId() != null
			&& fileInfo.getCreatedBy() != null
			&& fileInfo.getCreatedBy().equals(getLoggedUserId());
	}

	@Override
	public boolean isLoggedIn() {
		return getLoggedUser() != null;
	}

	@Override
	public UUID getLoggedUserId() {
		//todo: 추후 바꿔야함
		return SecurityUtil.getCurrentUserId().orElse(null);
	}

	private UserDetailsImpl getLoggedUser() {
		return SecurityUtil.getCurrentUser().orElse(null);
	}
}