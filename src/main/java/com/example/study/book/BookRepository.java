package com.example.study.book;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookRepository extends JpaRepository<Book, Long> {

  /**
   * 목록 조회(1번 API).
   *
   * <p>검색어와 상태를 함께 받으며 값이 없으면 조건에서 제외됨. 등록자를 함께 가져와 목록 건수만큼 조회가 늘어나지 않게 함.
   */
  @EntityGraph(attributePaths = {"owner"})
  @Query(
      """
            select b from Book b
            where (:keyword is null or b.title like concat('%', :keyword, '%'))
              and (:status is null or b.status = :status)
            """)
  Page<Book> search(String keyword, BookStatus status, Pageable pageable);

  /** 등록자를 함께 가져옴(2번 API, 상세). 지정하지 않으면 등록자 조회가 따로 나감. */
  @EntityGraph(attributePaths = {"owner"})
  Optional<Book> findWithOwnerById(Long id);

  /** 내가 등록한 책 목록(5번 API), 최신순. */
  List<Book> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);
}
