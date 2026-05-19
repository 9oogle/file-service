package com.goggles.file_service.domain;

import org.springframework.util.StringUtils;

import com.goggles.common.exception.BadRequestException;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileGroup {
	@Column(length = 45, nullable = false)
	private String groupId;

	@Enumerated(EnumType.STRING)
	@Column(name = "tag_name", length = 35, nullable = false)
	private FileTag tag;

	//GroupId, Tags는 필수
	protected FileGroup(String groupId, FileTag tag) {
		if (!StringUtils.hasText(groupId)) {
			throw new BadRequestException("groupId는 필수 입력 값입니다.");
		}

		if (tag == null) {
			throw new BadRequestException("tags는 필수 입력 값 입니다.");
		}

		this.groupId = groupId;
		this.tag = tag;
	}
}
