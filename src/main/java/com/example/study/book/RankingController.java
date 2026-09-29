package com.example.study.book;

import com.example.study.book.dto.BookRankingResponse;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인기 도서 랭킹 표현 계층(11번 API).
 *
 * <p>랭킹 점수 자체는 여기서 올리지 않음 — 거래 요청이 발생할 때(4번 API 담당) {@code book:ranking}에 점수가 쌓임. 이 계층은 조회만 함(읽기
 * 전용). 키 설계는 {@code docs/04_Redis키설계.md} 참고.
 *
 * <p>거래 요청이 한 번도 없던 책은 대상에서 제외(요청수가 기본 기준). 그 안에서만 조회수를 보조 신호로 섞어 순서를 재조정함 — 계산은 {@link
 * RankingScoreCalculator} 참고.
 */
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class RankingController {

  private static final String RANKING_KEY = "book:ranking";
  private static final int TOP_N = 10;

  private final StringRedisTemplate redisTemplate;
  private final BookRepository bookRepository;
  private final RankingScoreCalculator scoreCalculator;

  private record Candidate(Book book, long requestCount, double score) {}

  @GetMapping("/ranking")
  public List<BookRankingResponse> ranking() {
    // 요청수 기준 후보 전체를 가져와서(수량이 적으니 전체 스캔해도 무방) 조회수까지 섞은 점수로 다시 정렬함.
    Set<TypedTuple<String>> entries =
        redisTemplate.opsForZSet().reverseRangeWithScores(RANKING_KEY, 0, -1);
    if (entries == null || entries.isEmpty()) {
      return List.of();
    }

    Map<Long, Book> booksById =
        bookRepository
            .findAllById(entries.stream().map(entry -> Long.valueOf(entry.getValue())).toList())
            .stream()
            .collect(Collectors.toMap(Book::getId, Function.identity()));

    List<Candidate> ranked =
        entries.stream()
            .map(entry -> toCandidate(entry, booksById))
            .filter(Objects::nonNull)
            .sorted(Comparator.comparingDouble(Candidate::score).reversed())
            .limit(TOP_N)
            .toList();

    List<BookRankingResponse> result = new ArrayList<>();
    for (int i = 0; i < ranked.size(); i++) {
      Candidate candidate = ranked.get(i);
      result.add(BookRankingResponse.from(candidate.book(), i + 1, candidate.requestCount()));
    }
    return result;
  }

  private Candidate toCandidate(TypedTuple<String> entry, Map<Long, Book> booksById) {
    Book book = booksById.get(Long.valueOf(entry.getValue()));
    if (book == null) return null;
    long requestCount = entry.getScore() == null ? 0 : entry.getScore().longValue();
    double score = scoreCalculator.calculate(requestCount, book.getViewCount());
    return new Candidate(book, requestCount, score);
  }
}
