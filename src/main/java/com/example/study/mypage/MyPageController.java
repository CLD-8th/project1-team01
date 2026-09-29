package com.example.study.mypage;

import com.example.study.book.BookRepository;
import com.example.study.mypage.dto.MyBookResponse;
import com.example.study.mypage.dto.MyExchangeRequestResponse;
import com.example.study.request.ExchangeRequestRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 로그인한 회원의 책과 거래 요청 조회. 회원 식별자는 요청값 대신 토큰에서 가져옴. */
@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageController {

  private final BookRepository bookRepository;
  private final ExchangeRequestRepository exchangeRequestRepository;

  @GetMapping("/books")
  public List<MyBookResponse> books(@AuthenticationPrincipal Long memberId) {
    return bookRepository.findByOwnerIdOrderByCreatedAtDesc(memberId).stream()
        .map(MyBookResponse::from)
        .toList();
  }

  @GetMapping("/requests/received")
  public List<MyExchangeRequestResponse> received(@AuthenticationPrincipal Long memberId) {
    return exchangeRequestRepository.findByBookOwnerIdOrderByCreatedAtDesc(memberId).stream()
        .map(MyExchangeRequestResponse::from)
        .toList();
  }

  @GetMapping("/requests/sent")
  public List<MyExchangeRequestResponse> sent(@AuthenticationPrincipal Long memberId) {
    return exchangeRequestRepository.findByRequesterIdOrderByCreatedAtDesc(memberId).stream()
        .map(MyExchangeRequestResponse::from)
        .toList();
  }
}
