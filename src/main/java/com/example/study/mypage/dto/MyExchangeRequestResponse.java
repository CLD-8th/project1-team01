package com.example.study.mypage.dto;

import com.example.study.request.ExchangeRequest;
import com.example.study.request.ExchangeRequestStatus;
import java.time.LocalDateTime;

/**
 * 받은 요청과 보낸 요청에서 함께 사용하는 응답. 요청자·등록자 모두 식별자와 닉네임만 공개함.
 *
 * <p>{@code ownerNickname}은 보낸 요청 화면에서 "판매자: OOO"로 표시하는 값(화면 설계서 7번 화면 참고) — 받은 요청 화면에서는 본인 닉네임이
 * 그대로 들어가므로 화면에서 굳이 쓰지 않음.
 */
public record MyExchangeRequestResponse(
    Long id,
    Long bookId,
    String bookTitle,
    String bookCoverImageUrl,
    Long ownerId,
    String ownerNickname,
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
        request.getBook().getOwner().getId(),
        request.getBook().getOwner().getNickname(),
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
