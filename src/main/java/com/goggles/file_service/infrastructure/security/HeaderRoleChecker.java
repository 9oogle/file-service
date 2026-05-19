package com.goggles.file_service.infrastructure.security;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.goggles.file_service.domain.FileInfo;
import com.goggles.file_service.domain.FileTag;
import com.goggles.file_service.domain.service.RoleChecker;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 게이트웨이가 주입한 X-User-* 헤더를 기반으로 권한을 판단하는 RoleChecker 구현체.
 *
 * <p>Spring Security를 사용하지 않으며, 인증은 게이트웨이가 이미 처리했다고 가정합니다.
 *
 * <p>참고 헤더:
 *
 * <ul>
 *   <li>X-User-Id: 사용자 UUID
 *   <li>X-User-Role: 사용자 역할 (MASTER, INSTRUCTOR, STUDENT 등)
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HeaderRoleChecker implements RoleChecker {

	private static final String HEADER_USER_ID = "X-User-Id";
	private static final String HEADER_USER_ROLE = "X-User-Role";
	private static final String ROLE_MASTER = "MASTER";

	/** 수강자 확인이 필요한 파일 태그. 그 외 파일(이미지 등)은 로그인 사용자 누구나 접근 가능. */
	private static final Set<FileTag> ENROLLMENT_REQUIRED_TAGS =
		Set.of(FileTag.LECTURE_DOCUMENT, FileTag.LECTURE_VIDEO);

	@Override
	public boolean isMaster() {
		return getHeader(HEADER_USER_ROLE)
			.map(role -> role.contains(ROLE_MASTER))
			.orElse(false);
	}

	@Override
	public boolean isMine(FileInfo fileInfo) {
		UUID userId = getLoggedUserId();
		return userId != null
			&& fileInfo.getCreatedBy() != null
			&& fileInfo.getCreatedBy().equals(userId);
	}

	@Override
	public boolean isLoggedIn() {
		return getLoggedUserId() != null;
	}

	@Override
	public UUID getLoggedUserId() {
		return getHeader(HEADER_USER_ID)
			.flatMap(HeaderRoleChecker::parseUuid)
			.orElse(null);
	}

	@Override
	public boolean canDownload(FileInfo fileInfo) {
		// 1) 비로그인 사용자는 다운로드 불가
		if (!isLoggedIn()) {
			return false;
		}

		// 2) MASTER는 모든 파일 다운로드 가능
		if (isMaster()) {
			return true;
		}

		// 3) 파일 소유자는 본인 파일 다운로드 가능
		if (isMine(fileInfo)) {
			return true;
		}

		FileTag tag = fileInfo.getGroup().getTag();

		// 4) 강의 자료/동영상 수강 여부 체크
		//    TODO: lecture-service Internal Enrollment Check API(PR1) 머지 후
		//          EnrollmentChecker.isEnrolled(studentId, lectureId) 호출로 교체.
		//          현재는 로그인 사용자라면 허용하는 임시 정책.
		if (ENROLLMENT_REQUIRED_TAGS.contains(tag)) {
			log.warn(
				"[HeaderRoleChecker] 수강 여부 체크 미구현 - 임시 허용. fileId={}, tag={}",
				fileInfo.getId(),
				tag);
			return true;
		}

		// 5) 그 외 공개 성격의 파일(이미지 등)은 로그인 사용자 모두 허용
		return true;
	}

	private static Optional<String> getHeader(String name) {
		ServletRequestAttributes attrs =
			(ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
		if (attrs == null) {
			return Optional.empty();
		}
		HttpServletRequest request = attrs.getRequest();
		String value = request.getHeader(name);
		return StringUtils.hasText(value) ? Optional.of(value) : Optional.empty();
	}

	private static Optional<UUID> parseUuid(String value) {
		if (!StringUtils.hasText(value)) {
			return Optional.empty();
		}
		try {
			return Optional.of(UUID.fromString(value));
		} catch (IllegalArgumentException e) {
			return Optional.empty();
		}
	}
}