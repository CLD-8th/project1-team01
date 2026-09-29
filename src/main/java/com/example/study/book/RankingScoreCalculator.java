package com.example.study.book;

import org.springframework.stereotype.Component;

/**
 * 인기 랭킹 점수 계산(04_Redis키설계.md "향후 확장" 반영).
 *
 * <p>거래 요청 수가 실제 거래 의사를 나타내는 핵심 신호라 가중치를 크게 두고, 조회수는 새로고침으로 쉽게 부풀릴 수 있어 로그로 눌러 보조 신호로만 반영함. 가중치 조절이
 * 필요하면 이 클래스 상수만 고치면 됨.
 */
@Component
public class RankingScoreCalculator {

  private static final double REQUEST_WEIGHT = 5.0;
  private static final double VIEW_WEIGHT = 1.0;

  public double calculate(long requestCount, long viewCount) {
    return requestCount * REQUEST_WEIGHT + Math.log(viewCount + 1) * VIEW_WEIGHT;
  }
}
