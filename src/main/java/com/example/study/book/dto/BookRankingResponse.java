package com.example.study.book.dto;

import com.example.study.book.Book;
import com.example.study.book.BookStatus;

/** 인기 도서 랭킹 항목(11번 API). */
public record BookRankingResponse(
    Long id, String title, String author, String coverImageUrl, BookStatus status) {

  public static BookRankingResponse from(Book book) {
    return new BookRankingResponse(
        book.getId(), book.getTitle(), book.getAuthor(), book.getCoverImageUrl(), book.getStatus());
  }
}
