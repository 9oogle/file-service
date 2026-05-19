package com.goggles.file_service.domain.service;

import com.goggles.file_service.domain.FileInfo;

public interface FileDownloader {
	FileDownloadContent download(FileInfo fileInfo);
}
