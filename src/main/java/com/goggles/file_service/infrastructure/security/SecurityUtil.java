package com.goggles.file_service.infrastructure.security;

import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.goggles.common.exception.UnAuthorizedException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityUtil {

	public static Optional<UserDetailsImpl> getCurrentUser() {
		return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
			.map(Authentication::getPrincipal)
			.filter(principal -> principal instanceof UserDetailsImpl)
			.map(UserDetailsImpl.class::cast);
	}

	public static Optional<UUID> getCurrentUserId() {
		return getCurrentUser().map(UserDetailsImpl::getUuid);
	}

	public static UUID getCurrentUserIdOrThrow() {
		return getCurrentUserId().orElseThrow(UnAuthorizedException::new);
	}

	public static Optional<String> getCurrentUsername() {
		return getCurrentUser().map(UserDetailsImpl::getUsername);
	}
}