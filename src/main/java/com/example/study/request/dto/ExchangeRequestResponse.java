package com.example.study.request.dto;

import com.example.study.request.ExchangeRequest;

public record ExchangeRequestResponse(
    Long id,
    Long bookId,
    Long requesterId,
    int offeredPrice,
    String offeredPhotoUrl,
    String message,
    String status) {

  public static ExchangeRequestResponse from(ExchangeRequest request) {
    return new ExchangeRequestResponse(
        request.getId(),
        request.getBook().getId(),
        request.getRequester().getId(),
        request.getOfferedPrice(),
        request.getOfferedPhotoUrl(),
        request.getMessage(),
        request.getStatus().name());
  }
}
