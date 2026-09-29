package com.example.study.book.dto;

import com.example.study.book.Book;

// 목록 조회 응답. 카드 UI에 필요한 최소 정보만 담음. accepts*는 목록 카드의 "받고 싶은 조건" 한 줄 요약에 씀(화면 설계서 1번 화면 참고).
public record BookListResponse(
    Long id,
    String title,
    String author,
    String coverImageUrl,
    String status,
    boolean acceptsPrice,
    boolean acceptsSwap,
    boolean acceptsGiveaway) {

  public static BookListResponse from(Book book) {
    return new BookListResponse(
        book.getId(),
        book.getTitle(),
        book.getAuthor(),
        book.getCoverImageUrl(),
        book.getStatus().name(),
        book.isAcceptsPrice(),
        book.isAcceptsSwap(),
        book.isAcceptsGiveaway());
  }
}
