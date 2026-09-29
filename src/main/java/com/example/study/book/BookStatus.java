package com.example.study.book;

/**
 * 책 거래 상태.
 *
 * <p>거래완료로 간 뒤에는 되돌리는 전이가 없음. 교환·판매·나눔 구분과 무관하게 하나의 거래만 확정됨. 화면 표기는 TRADING="거래중",
 * COMPLETED="거래완료".
 */
public enum BookStatus {
  TRADING,
  COMPLETED
}
