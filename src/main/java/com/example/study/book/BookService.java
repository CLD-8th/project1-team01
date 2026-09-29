package com.example.study.book;

import com.example.study.book.dto.BookCreateRequest;
import com.example.study.book.dto.BookDetailResponse;
import com.example.study.book.dto.BookListResponse;
import com.example.study.book.dto.ImageUploadResponse;
import com.example.study.common.BusinessException;
import com.example.study.common.ErrorCode;
import com.example.study.member.Member;
import com.example.study.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

  private final BookRepository bookRepository;
  private final MemberService memberService;
  private final FileStorageService fileStorageService;

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
      throw new BusinessException(ErrorCode.INVALID_INPUT, "잘못된 status 값입니다");
    }
  }

  // 상세 조회
  public BookDetailResponse getBook(Long bookId) {
    Book book =
        bookRepository
            .findWithOwnerById(bookId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "책 부재"));
    return BookDetailResponse.from(book);
  }

  // 도서 등록
  @Transactional
  public BookDetailResponse createBook(BookCreateRequest request, Long memberId) {
    Member owner = memberService.getMember(memberId);
    String author =
        (request.author() == null || request.author().isBlank()) ? "익명" : request.author();

    Book book =
        new Book(
            request.title(),
            author,
            request.coverImageUrl(),
            request.description(),
            owner,
            request.acceptsPrice(),
            request.acceptsSwap(),
            request.acceptsGiveaway());

    return BookDetailResponse.from(bookRepository.save(book));
  }

  // 사진 업로드
  public ImageUploadResponse uploadImage(MultipartFile file) {
    String url = fileStorageService.store(file);
    return new ImageUploadResponse(url);
  }
}
