package com.example.study.book;

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
 * 등록된 책.
 *
 * <p>{@code accepts*} 는 등록자가 "받고 싶은 조건"을 참고용으로 표시하는 값 — 강제 제한이 아니라서 요청자는 이 값과 무관하게 아무 조합이나 제안할 수
 * 있음(자세한 내용은 {@code docs/02_ERD.md} 참고).
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 200)
  private String title;

  @Column(nullable = false, length = 100)
  private String author;

  @Column(length = 300)
  private String coverImageUrl;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private BookStatus status;

  // 등록자. 다대일의 기본 조회 시점은 함께 조회이므로 지연을 명시함.
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_id", nullable = false)
  private Member owner;

  @Column(nullable = false)
  private boolean acceptsPrice;

  @Column(nullable = false)
  private boolean acceptsSwap;

  @Column(nullable = false)
  private boolean acceptsGiveaway;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  // 상세 조회수. 인기 랭킹 가중치 계산에만 씀(04_Redis키설계.md "향후 확장" 참고) — 화면에 직접 노출하진 않음.
  @Column(nullable = false)
  private long viewCount;

  public Book(
      String title,
      String author,
      String coverImageUrl,
      String description,
      Member owner,
      boolean acceptsPrice,
      boolean acceptsSwap,
      boolean acceptsGiveaway) {
    this.title = title;
    this.author = author;
    this.coverImageUrl = coverImageUrl;
    this.description = description;
    this.owner = owner;
    this.acceptsPrice = acceptsPrice;
    this.acceptsSwap = acceptsSwap;
    this.acceptsGiveaway = acceptsGiveaway;
    this.status = BookStatus.TRADING;
    this.createdAt = LocalDateTime.now();
  }

  /** 거래 확정. 요청 하나가 수락되면 호출되어 나머지 요청은 더 이상 받지 않음을 표시함. */
  public void complete() {
    this.status = BookStatus.COMPLETED;
    this.updatedAt = LocalDateTime.now();
  }

  public boolean isOwnedBy(Long memberId) {
    return this.owner.getId().equals(memberId);
  }

  public boolean isTrading() {
    return this.status == BookStatus.TRADING;
  }

  /** 상세 조회 1회당 호출. */
  public void increaseViewCount() {
    this.viewCount++;
  }
}
