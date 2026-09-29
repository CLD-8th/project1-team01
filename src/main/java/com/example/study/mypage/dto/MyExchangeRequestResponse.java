package com.example.study.mypage.dto;

import com.example.study.request.ExchangeRequest;
import com.example.study.request.ExchangeRequestStatus;
import java.time.LocalDateTime;

/** 받은 요청과 보낸 요청에서 함께 사용하는 응답. 요청자는 식별자와 닉네임만 공개함. */
public record MyExchangeRequestResponse(
    Long id,
    Long bookId,
    String bookTitle,
    String bookCoverImageUrl,
    Long requesterId,
    String requesterNickname,
    int offeredPrice,
    String offeredPhotoUrl,
    String message,
    ExchangeRequestStatus status,
    LocalDateTime createdAt,
    LocalDateTime processedAt) {

  public static MyExchangeRequestResponse from(ExchangeRequest request) {
    return new MyExchangeRequestResponse(
        request.getId(),
        request.getBook().getId(),
        request.getBook().getTitle(),
        request.getBook().getCoverImageUrl(),
        request.getRequester().getId(),
        request.getRequester().getNickname(),
        request.getOfferedPrice(),
        request.getOfferedPhotoUrl(),
        request.getMessage(),
        request.getStatus(),
        request.getCreatedAt(),
        request.getProcessedAt());
  }
}
