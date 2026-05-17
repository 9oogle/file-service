package com.goggles.file_service.infrastructure.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goggles.common.response.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final ObjectMapper objectMapper;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException) throws IOException {

		String traceId = MDC.get("traceId");
		log.warn("[TraceID: {}] Unauthorized access attempt to {}: {}",
			traceId != null ? traceId : "N/A",
			request.getRequestURI(),
			authException.getMessage());

		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.setStatus(HttpStatus.UNAUTHORIZED.value());

		ErrorResponse errorResponse = ErrorResponse.of(
			HttpStatus.UNAUTHORIZED,
			"인증에 실패했습니다. 유효한 인증 정보를 제공해주세요."
		);

		response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
	}
}