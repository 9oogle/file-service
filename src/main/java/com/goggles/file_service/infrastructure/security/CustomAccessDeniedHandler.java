package com.goggles.file_service.infrastructure.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
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
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	private final ObjectMapper objectMapper;

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
		AccessDeniedException accessDeniedException) throws IOException {

		String traceId = MDC.get("traceId");
		log.warn("[TraceID: {}] Access Denied: Method: {}, URI: {}, Message: {}",
			traceId != null ? traceId : "N/A",
			request.getMethod(),
			request.getRequestURI(),
			accessDeniedException.getMessage());

		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.setStatus(HttpStatus.FORBIDDEN.value());

		ErrorResponse errorResponse = ErrorResponse.of(
			HttpStatus.FORBIDDEN,
			"접근 권한이 없습니다."
		);

		response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
	}
}