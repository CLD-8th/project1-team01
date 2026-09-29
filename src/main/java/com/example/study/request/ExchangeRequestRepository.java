package com.example.study.request;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExchangeRequestRepository extends JpaRepository<ExchangeRequest, Long> {

  /** 대상 책과 요청자를 함께 가져옴(8·9번 API, 수락·거절 처리에서 씀). */
  @EntityGraph(attributePaths = {"book", "requester"})
  Optional<ExchangeRequest> findWithBookById(Long id);

  /** 특정 책에 대한 대기 중인 다른 요청들(수락 시 자동 거절용). */
  List<ExchangeRequest> findByBookIdAndStatus(Long bookId, ExchangeRequestStatus status);

  /** 받은 요청 목록(6번 API), 등록자 기준, 최신순. */
  @EntityGraph(attributePaths = {"book", "requester"})
  List<ExchangeRequest> findByBookOwnerIdOrderByCreatedAtDesc(Long ownerId);

  /** 보낸 요청 목록(7번 API), 요청자 기준, 최신순. */
  @EntityGraph(attributePaths = {"book", "requester"})
  List<ExchangeRequest> findByRequesterIdOrderByCreatedAtDesc(Long requesterId);
}
