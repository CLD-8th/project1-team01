package com.example.study.book;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

// 파일 저장 후 URL 변환
@Service
public class FileStorageService {

  @Value("${app.upload-dir}")
  private String uploadDir;

  public String store(MultipartFile file) {
    try {
      Path dirPath = Path.of(uploadDir);
      Files.createDirectories(dirPath);

      String extension = extractExtension(file.getOriginalFilename());
      String storedName = UUID.randomUUID() + extension;

      Path targetPath = dirPath.resolve(storedName);
      Files.copy(file.getInputStream(), targetPath);

      return "/uploads/" + storedName;
    } catch (IOException e) {
      throw new IllegalStateException("파일 저장에 실패했습니다.", e);
    }
  }

  private String extractExtension(String originalFilename) {
    if (originalFilename == null || !originalFilename.contains(".")) {
      return "";
    }
    return originalFilename.substring(originalFilename.lastIndexOf("."));
  }
}
