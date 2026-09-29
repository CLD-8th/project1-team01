package com.example.study.book.dto;

import com.example.study.book.Book;
import com.example.study.book.BookStatus;

/**
 * 인기 도서 랭킹 항목(11번 API).
 *
 * <p>{@code rank}·{@code requestCount}는 도서 상세 화면의 "인기 도서 랭킹 N위 · 거래 요청 M회" 표시에 씀(화면 설계서 2번 화면 참고).
 */
public record BookRankingResponse(
    Long id,
    String title,
    String author,
    String coverImageUrl,
    BookStatus status,
    int rank,
    long requestCount) {

  public static BookRankingResponse from(Book book, int rank, long requestCount) {
    return new BookRankingResponse(
        book.getId(),
        book.getTitle(),
        book.getAuthor(),
        book.getCoverImageUrl(),
        book.getStatus(),
        rank,
        requestCount);
  }
}
