package com.example.study.book;

import com.example.study.book.dto.BookRankingResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인기 도서 랭킹 표현 계층(11번 API).
 *
 * <p>랭킹 점수 자체는 여기서 올리지 않음 — 거래 요청이 발생할 때(4번 API 담당) {@code book:ranking}에 점수가 쌓임. 이 계층은 조회만 함(읽기
 * 전용). 키 설계는 {@code docs/04_Redis키설계.md} 참고.
 */
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class RankingController {

  private static final String RANKING_KEY = "book:ranking";
  private static final int TOP_N = 10;

  private final StringRedisTemplate redisTemplate;
  private final BookRepository bookRepository;

  @GetMapping("/ranking")
  public List<BookRankingResponse> ranking() {
    Set<String> topIds = redisTemplate.opsForZSet().reverseRange(RANKING_KEY, 0, TOP_N - 1);
    if (topIds == null || topIds.isEmpty()) {
      return List.of();
    }

    List<Long> bookIds = topIds.stream().map(Long::valueOf).toList();

    // Redis가 준 순위 순서를 유지해야 함 — JpaRepository.findAllById()는 순서를 보장하지 않으므로
    // 식별자로 찾아 다시 순서대로 나열함.
    Map<Long, Book> booksById =
        bookRepository.findAllById(bookIds).stream()
            .collect(Collectors.toMap(Book::getId, Function.identity()));

    return bookIds.stream()
        .map(booksById::get)
        .filter(Objects::nonNull)
        .map(BookRankingResponse::from)
        .toList();
  }
}
