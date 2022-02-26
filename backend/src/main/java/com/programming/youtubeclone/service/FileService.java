package com.programming.youtubeclone.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    String uploadFile(MultipartFile file);
}
