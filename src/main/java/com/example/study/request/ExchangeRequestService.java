package com.example.study.request;

import com.example.study.book.Book;
import com.example.study.book.BookRepository;
import com.example.study.common.BusinessException;
import com.example.study.common.ErrorCode;
import com.example.study.member.Member;
import com.example.study.member.MemberService;
import com.example.study.request.dto.ExchangeRequestCreateRequest;
import com.example.study.request.dto.ExchangeRequestResponse;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 거래 요청 업무 계층.
 *
 * <p>판단 순서: 대상 확인 → 자기 책 → 거래중인지, 순서임(14_기능구현가이드.md 참고). 저장 성공 시 인기 도서 랭킹 점수를 올림(11번 담당 전동환이 조회만 함
 * — 여기서 점수를 올려야 랭킹에 반영됨).
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExchangeRequestService {

  private static final String RANKING_KEY = "book:ranking";

  private final ExchangeRequestRepository exchangeRequestRepository;
  private final BookRepository bookRepository;
  private final MemberService memberService;
  private final StringRedisTemplate redisTemplate;

  /** 거래 요청 생성(4번 API). */
  @Transactional
  public ExchangeRequestResponse create(
      Long bookId, ExchangeRequestCreateRequest request, Long memberId) {
    Book book =
        bookRepository
            .findWithOwnerById(bookId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "책 부재"));

    if (book.isOwnedBy(memberId)) {
      throw new BusinessException(ErrorCode.SELF_REQUEST);
    }
    if (!book.isTrading()) {
      throw new BusinessException(ErrorCode.BOOK_NOT_TRADING);
    }

    Member requester = memberService.getMember(memberId);
    ExchangeRequest saved =
        exchangeRequestRepository.save(
            new ExchangeRequest(
                book,
                requester,
                request.offeredPrice(),
                request.offeredPhotoUrl(),
                request.message()));

    redisTemplate.opsForZSet().incrementScore(RANKING_KEY, String.valueOf(bookId), 1);

    return ExchangeRequestResponse.from(saved);
  }

  // TODO(문병현) 8번 · 요청 수락 — book:{bookId}:lock 락 잡고 accept() → book.complete() →
  // findByBookIdAndStatus(bookId, PENDING) 나머지 자동 거절. 14_기능구현가이드.md 참고.
  @Transactional
  public void accept(Long requestId, Long memberId) {
    ExchangeRequest exchangeRequest =
        exchangeRequestRepository
            .findWithBookById(requestId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "요청 부재"));

    Book book = exchangeRequest.getBook();

    if (!book.isOwnedBy(memberId)) {
      throw new BusinessException(ErrorCode.FORBIDDEN);
    }
    if (!exchangeRequest.isPending()) {
      throw new BusinessException(ErrorCode.ALREADY_PROCESSED);
    }

    String lockKey = "book:" + book.getId() + ":lock";
    Boolean acquired =
        redisTemplate
            .opsForValue()
            .setIfAbsent(lockKey, String.valueOf(requestId), Duration.ofSeconds(5));

    if (!Boolean.TRUE.equals(acquired)) {
      throw new BusinessException(ErrorCode.LOCK_CONFLICT);
    }

    exchangeRequest.accept();
    book.complete();

    List<ExchangeRequest> pendingRequests =
        exchangeRequestRepository.findByBookIdAndStatus(
            book.getId(), ExchangeRequestStatus.PENDING);

    for (ExchangeRequest other : pendingRequests) {
      if (!other.getId().equals(requestId)) {
        other.reject();
      }
    }
  }

  // TODO(문병현) 9번 · 요청 거절 — 락 필요 없음. isPending() 확인 후 reject()만.
  @Transactional
  public void reject(Long requestId, Long memberId) {
    ExchangeRequest exchangeRequest =
        exchangeRequestRepository
            .findWithBookById(requestId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "요청 부재"));

    if (!exchangeRequest.getBook().isOwnedBy(memberId)) {
      throw new BusinessException(ErrorCode.FORBIDDEN);
    }
    if (!exchangeRequest.isPending()) {
      throw new BusinessException(ErrorCode.ALREADY_PROCESSED);
    }

    exchangeRequest.reject();
  }
}
