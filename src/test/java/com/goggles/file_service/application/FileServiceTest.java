package com.goggles.file_service.application;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.goggles.file_service.application.dto.FileServiceDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public class FileServiceTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	FileService fileService;

	MockMultipartFile file;

	@BeforeEach
	void setup() {
		file = new MockMultipartFile("file", "test.png", MediaType.IMAGE_PNG_VALUE,
			new byte[] {1, 2, 3, 4, 5, 6, 7, 8});
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