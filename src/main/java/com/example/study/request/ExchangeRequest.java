package com.example.study.request;

import com.example.study.book.Book;
import com.example.study.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 거래 요청.
 *
 * <p>{@code offeredPrice}(기본 0)와 {@code offeredPhotoUrl}(nullable)의 조합으로 요청 종류가 계산됨 — 별도 type 컬럼을
 * 두지 않음. {@code offeredPhotoUrl}은 기존 등록된 책을 참조(FK)하는 게 아니라 요청 시 그냥 업로드하는 이미지임.
 *
 * <table>
 * <caption>가격 · 사진 조합</caption>
 * <tr><td>0원 + 사진 있음</td><td>교환 요청</td></tr>
 * <tr><td>1원 이상 + 사진 있음</td><td>웃돈 주고 교환 제안</td></tr>
 * <tr><td>0원 + 사진 없음</td><td>나눔 요청</td></tr>
 * <tr><td>1원 이상 + 사진 없음</td><td>구매 제안</td></tr>
 * </table>
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExchangeRequest {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "book_id", nullable = false)
  private Book book;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "requester_id", nullable = false)
  private Member requester;

  @Column(nullable = false)
  private int offeredPrice;

  @Column(length = 300)
  private String offeredPhotoUrl;

  @Column(length = 300)
  private String message;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private ExchangeRequestStatus status;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  private LocalDateTime processedAt;

  public ExchangeRequest(
      Book book, Member requester, int offeredPrice, String offeredPhotoUrl, String message) {
    this.book = book;
    this.requester = requester;
    this.offeredPrice = offeredPrice;
    this.offeredPhotoUrl = offeredPhotoUrl;
    this.message = message;
    this.status = ExchangeRequestStatus.PENDING;
    this.createdAt = LocalDateTime.now();
  }

  public void accept() {
    this.status = ExchangeRequestStatus.ACCEPTED;
    this.processedAt = LocalDateTime.now();
  }

  public void reject() {
    this.status = ExchangeRequestStatus.REJECTED;
    this.processedAt = LocalDateTime.now();
  }

  public boolean isPending() {
    return this.status == ExchangeRequestStatus.PENDING;
  }

  public boolean isRequestedBy(Long memberId) {
    return this.requester.getId().equals(memberId);
  }
}
