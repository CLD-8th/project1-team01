package com.example.study.request;

import com.example.study.request.dto.ExchangeRequestCreateRequest;
import com.example.study.request.dto.ExchangeRequestResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 거래 요청 표현 계층. */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ExchangeRequestController {

  private final ExchangeRequestService exchangeRequestService;

  /** 거래 요청 생성(4번 API). */
  @PostMapping("/books/{bookId}/requests")
  public ResponseEntity<ExchangeRequestResponse> create(
      @PathVariable Long bookId,
      @Valid @RequestBody ExchangeRequestCreateRequest request,
      @AuthenticationPrincipal Long memberId) {
    ExchangeRequestResponse created = exchangeRequestService.create(bookId, request, memberId);
    return ResponseEntity.status(201).body(created);
  }

  // TODO(문병현) 8번 · POST /api/requests/{requestId}/accept
  @PostMapping("/requests/{requestId}/accept")
  public void accept(
    @PathVariable Long requestId,
    @AuthenticationPrincipal Long memberId) {
    exchangeRequestService.accept(requestId, memberId);
  }


  // TODO(문병현) 9번 · POST /api/requests/{requestId}/reject
  @PostMapping("/requests/{requestId}/reject")
  public void reject(
    @PathVariable Long requestId,
    @AuthenticationPrincipal Long memberId) {
    exchangeRequestService.reject(requestId, memberId);
  }
}
