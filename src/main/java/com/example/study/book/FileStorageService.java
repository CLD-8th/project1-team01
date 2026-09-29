package com.example.study.book;

import com.example.study.common.BusinessException;
import com.example.study.common.ErrorCode;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

// 파일 저장 후 URL 변환
@Service
public class FileStorageService {

  // 허용할 확장자
  private static final List<String> ALLOWED_EXTENSIONS = List.of(".jpg", ".jpeg", ".png", ".webp");

  @Value("${app.upload-dir:./uploads}")
  private String uploadDir;

  public String store(MultipartFile file) {

    String extension = extractExtension(file.getOriginalFilename());
    validateExtension(extension);
    try {
      Path dirPath = Path.of(uploadDir);
      Files.createDirectories(dirPath);

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
    return originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
  }

  private void validateExtension(String extension) {
    if (!ALLOWED_EXTENSIONS.contains(extension)) {
      throw new BusinessException(
          ErrorCode.INVALID_INPUT, "이미지 파일(jpg, jpeg, png, webp)만 업로드 가능합니다");
    }
  }
}
