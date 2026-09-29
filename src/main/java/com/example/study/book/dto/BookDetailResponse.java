package com.example.study.book.dto;

import com.example.study.book.Book;

/** 상세 조회(2번 API) 응답. 등록자 정보와 수락 조건까지 포함. */
public record BookDetailResponse(
    Long id,
    String title,
    String author,
    String coverImageUrl,
    String description,
    String status,
    Long ownerId,
    String ownerNickname,
    boolean acceptsPrice,
    boolean acceptsSwap,
    boolean acceptsGiveaway) {

  public static BookDetailResponse from(Book book) {
    return new BookDetailResponse(
        book.getId(),
        book.getTitle(),
        book.getAuthor(),
        book.getCoverImageUrl(),
        book.getDescription(),
        book.getStatus().name(),
        book.getOwner().getId(),
        book.getOwner().getNickname(),
        book.isAcceptsPrice(),
        book.isAcceptsSwap(),
        book.isAcceptsGiveaway());
  }
}
