package com.example.study.request.dto;

import jakarta.validation.constraints.Min;

/**
 * 거래 요청 생성(4번 API) 요청 본문.
 *
 * <p>{@code offeredPrice}(기본 0)와 {@code offeredPhotoUrl}(선택)의 조합으로 교환요청/웃돈제안/나눔요청/구매제안 중 의미가 정해짐 —
 * 별도 type 필드 없음.
 */
public record ExchangeRequestCreateRequest(
  @Min(0) int offeredPrice, String offeredPhotoUrl, String message) {}
