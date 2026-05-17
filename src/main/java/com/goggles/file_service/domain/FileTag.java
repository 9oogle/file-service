package com.goggles.file_service.domain;

import com.goggles.file_service.domain.exception.InvalidFileTagException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum FileTag {
	PROFILE("profile", "회원 프로필 이미지", true, false),

	LECTURE_LIST("lecture", "강의 목록 이미지", true, false),
	LECTURE_MAIN("lecture", "강의 메인 이미지", true, false),
	LECTURE_DESCRIPTION("lecture", "강의 상세설명 이미지", true, false),

	LECTURE_DOCUMENT("lecture", "강의 자료", true, true),

	LECTURE_VIDEO("lecture", "강의 동영상", false, false),

	MENTORING_LIST("mentoring", "멘토링 목록 이미지", true, false),
	MENTORING_MAIN("mentoring", "멘토링 메인 이미지", true, false),
	MENTORING_DESCRIPTION("mentoring", "멘토링 상세설명 이미지", true, false);

	private final String directory;
	private final String description;
	private final boolean downloadable;
	private final boolean enrollmentRequired;

	public static FileTag from(String tagName) {
		try {
			return FileTag.valueOf(tagName);
		} catch (IllegalArgumentException e) {
			log.warn("지원하지 않는 파일 태그 - 태그: {}", tagName);
			throw new InvalidFileTagException(tagName);
		}
	}
}