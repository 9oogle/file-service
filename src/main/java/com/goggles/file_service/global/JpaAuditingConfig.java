package com.goggles.file_service.global;

import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class JpaAuditingConfig {

	private static final String USER_ID_HEADER = "X-User-Id";

	public static final UUID SYSTEM_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

	@Bean
	public AuditorAware<UUID> auditorAware() {
		return () -> {
			ServletRequestAttributes attributes =
				(ServletRequestAttributes)RequestContextHolder.getRequestAttributes();

			// HTTP 요청이 없는 경우: 스케줄러, Kafka Consumer, 테스트 등
			if (attributes == null) {
				return Optional.of(SYSTEM_USER_ID);
			}

			HttpServletRequest request = attributes.getRequest();
			String userId = request.getHeader(USER_ID_HEADER);

			// 헤더가 없거나 파싱 실패 → SYSTEM으로 fallback
			if (userId == null || userId.isBlank()) {
				return Optional.of(SYSTEM_USER_ID);
			}

			try {
				return Optional.of(UUID.fromString(userId));
			} catch (IllegalArgumentException e) {
				return Optional.of(SYSTEM_USER_ID);
			}
		};
	}
}
