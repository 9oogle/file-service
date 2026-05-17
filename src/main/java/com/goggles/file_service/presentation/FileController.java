package com.goggles.file_service.presentation;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.goggles.file_service.application.FileService;
import com.goggles.file_service.application.dto.FileServiceDto;
import com.goggles.file_service.application.query.FileQueryService;
import com.goggles.file_service.presentation.dto.FileRequest;
import com.goggles.file_service.presentation.dto.FileResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FileController {
	private final FileService fileService;
	private final FileQueryService fileQueryService;

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(path = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public List<FileResponse.Upload> upload(@Valid FileRequest.Upload request,
		@RequestPart("file") MultipartFile[] files) throws IOException {
		List<FileResponse.Upload> uploads = new ArrayList<>();
		for (MultipartFile file : files) {
			if (file.isEmpty())
				continue;

			UUID fileId = fileService.upload(request.toServiceDto(file));
			uploads.add(new FileResponse.Upload(fileId));
		}

		return uploads;
	}

	@GetMapping("/{fileId}/download")
	public ResponseEntity<Resource> download(@PathVariable("fileId") UUID fileId) {
		FileServiceDto.FileDownload download = fileService.download(fileId);

		String contentDisposition = ContentDisposition.attachment()
			.filename(download.fileName(), StandardCharsets.UTF_8)
			.build()
			.toString();

		return ResponseEntity.ok()
			.contentType(MediaType.parseMediaType(download.contentType()))
			.header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
			.cacheControl(CacheControl.noCache())
			.contentLength(download.contentLength())
			.body(new InputStreamResource(download.inputStream()));

	}

	@DeleteMapping("/{fileId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteFile(@PathVariable("fileId") UUID fileId) {
		fileService.delete(fileId);
	}

	@GetMapping("/{fileId}/details")
	public FileResponse.FileInfo getFileInfo(@PathVariable("fileId") UUID fileId) {
		return FileResponse.FileInfo.from(fileQueryService.findById(fileId));

	}

	@GetMapping("/serach")
	public List<FileResponse.FileInfo> getFile(@Valid FileRequest.FileSearch search) {
		String groupId = search.groupId();
		String tag = search.tag();
		return fileQueryService.findAll(groupId, tag)
			.stream()
			.map(FileResponse.FileInfo::from)
			.toList();
	}

}