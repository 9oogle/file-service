package com.goggles.file_service.application;

import static org.mockito.BDDMockito.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.goggles.file_service.application.dto.FileServiceDto;
import com.goggles.file_service.domain.FileInfo;
import com.goggles.file_service.domain.service.RoleChecker;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class FileServiceTest {

	@Autowired
	FileService fileService;

	@MockitoBean
	RoleChecker roleChecker;

	MockMultipartFile file;

	@BeforeEach
	void setup() {
		file = new MockMultipartFile("file", "test.png", MediaType.IMAGE_PNG_VALUE,
			new byte[] {1, 2, 3, 4, 5, 6, 7, 8});

		// 로그인 사용자로 stub
		given(roleChecker.isLoggedIn()).willReturn(true);
		given(roleChecker.isMaster()).willReturn(true);
		given(roleChecker.getLoggedUserId())
			.willReturn(UUID.fromString("0716d678-2a92-4bc5-991a-435d318574d8"));
		given(roleChecker.isMine(any(FileInfo.class))).willReturn(false);
		given(roleChecker.canDownload(any(FileInfo.class))).willReturn(true);
	}

	@Test
	@DisplayName("파일 업로드 테스트")
	void fileUploadTest() throws Exception {
		FileServiceDto.FileUpload dto = FileServiceDto.FileUpload.builder()
			.groupId("test-group")
			.tag("PROFILE")
			.originalFileName(file.getOriginalFilename())
			.contentType(file.getContentType())
			.contentLength(file.getSize())
			.inputStream(file.getInputStream())
			.build();

		UUID fileId = fileService.upload(dto);
		log.info("fileId: {}", fileId);
	}
}