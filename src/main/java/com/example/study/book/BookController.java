package com.example.study.book;

import com.example.study.book.dto.BookCreateRequest;
import com.example.study.book.dto.BookDetailResponse;
import com.example.study.book.dto.BookListResponse;
import com.example.study.common.PageResponse;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

  private final BookService bookService;

  // 도서 목록
  @GetMapping
  public ResponseEntity<PageResponse<BookListResponse>> getBooks(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) String status) {
    Page<BookListResponse> result =
        bookService.getBooks(
            keyword, status, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
    return ResponseEntity.ok(PageResponse.of(result, response -> response));
  }

  // 도서 상세
  @GetMapping("/{bookId}")
  public ResponseEntity<BookDetailResponse> getBook(@PathVariable Long bookId) {
    return ResponseEntity.ok(bookService.getBook(bookId));
  }

  // 도서 등록
  @PostMapping
  public ResponseEntity<BookDetailResponse> createBook(
      @Valid @RequestBody BookCreateRequest request, @AuthenticationPrincipal Long memberId) {
    BookDetailResponse response = bookService.createBook(request, memberId);
    return ResponseEntity.created(URI.create("/api/books/" + response.id())).body(response);
  }
}
