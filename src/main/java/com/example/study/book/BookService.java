package com.example.study.book;

import com.example.study.book.dto.BookDetailResponse;
import com.example.study.book.dto.BookListResponse;
import com.example.study.common.BusinessException;
import com.example.study.common.ErrorCode;
import com.example.study.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

  private final BookRepository bookRepository;
  private final MemberService memberService;

  // 목록 조회
  public Page<BookListResponse> getBooks(String keyword, String status, Pageable pageable) {
    BookStatus bookStatus = parseStatus(status);
    return bookRepository.search(keyword, bookStatus, pageable).map(BookListResponse::from);
  }

  private BookStatus parseStatus(String status) {
    if (status == null || status.isBlank()) {
      return null;
    }
    try {
      return BookStatus.valueOf(status.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new BusinessException(ErrorCode.INVALID_INPUT);
    }
  }

  // 상세 조회
  public BookDetailResponse getBook(Long bookId) {
    Book book =
        bookRepository
            .findWithOwnerById(bookId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
    return BookDetailResponse.from(book);
  }
}
