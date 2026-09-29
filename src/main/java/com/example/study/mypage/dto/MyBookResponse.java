package com.example.study.mypage.dto;

import com.example.study.book.Book;
import com.example.study.book.BookStatus;
import java.time.LocalDateTime;

/** 내 책 목록 응답. 등록자 엔티티와 회원 정보는 응답에 포함하지 않음. */
public record MyBookResponse(
    Long id,
    String title,
    String author,
    String coverImageUrl,
    BookStatus status,
    LocalDateTime createdAt) {

  public static MyBookResponse from(Book book) {
    return new MyBookResponse(
        book.getId(),
        book.getTitle(),
        book.getAuthor(),
        book.getCoverImageUrl(),
        book.getStatus(),
        book.getCreatedAt());
  }
}
