package com.example.study.request;

/**
 * 요청 상태.
 *
 * <p>수락과 거절은 되돌릴 수 없음.
 */
public enum ExchangeRequestStatus {
  PENDING,
  ACCEPTED,
  REJECTED
}
