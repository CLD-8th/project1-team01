package com.example.study.book.dto;

import com.example.study.book.Book;

// 목록 조회 응답. 카드 UI에 필요한 최소 정보만 담음
public record BookListResponse(
    Long id, String title, String author, String coverImageUrl, String status) {

  public static BookListResponse from(Book book) {
    return new BookListResponse(
        book.getId(),
        book.getTitle(),
        book.getAuthor(),
        book.getCoverImageUrl(),
        book.getStatus().name());
  }
}
