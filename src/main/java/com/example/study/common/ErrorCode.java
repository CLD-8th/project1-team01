package com.example.study.common;

import lombok.Getter;

/**
 * 실패 사유.
 *
 * <p>응답 코드가 같아도 사유가 다르면 화면이 구분해야 하므로 값을 나눔. 화면은 code 만 보고 분기함. 필요한 사유가 더 있으면 여기에 추가해서 씀.
 */
@Getter
public enum ErrorCode {
  INVALID_INPUT(400, "입력값 확인 필요"),
  BOOK_NOT_TRADING(400, "거래중이 아닌 책"),
  SELF_REQUEST(400, "자기 책에는 요청 불가"),
  ALREADY_PROCESSED(400, "이미 처리된 요청"),
  UNAUTHORIZED(401, "인증 실패"),
  FORBIDDEN(403, "권한 부재"),
  NOT_FOUND(404, "대상 부재"),
  LOCK_CONFLICT(409, "다른 요청이 먼저 처리됨"),
  INTERNAL_ERROR(500, "잠시 후 다시 시도");

  private final int status;
  private final String message;

  ErrorCode(int status, String message) {
    this.status = status;
    this.message = message;
  }
}
