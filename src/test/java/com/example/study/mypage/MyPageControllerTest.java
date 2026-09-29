package com.example.study.mypage;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.study.auth.JwtProperties;
import com.example.study.auth.TokenProvider;
import com.example.study.book.Book;
import com.example.study.member.Member;
import com.example.study.request.ExchangeRequest;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

/** 실제 JWT 필터와 H2 조회를 함께 검증. 테스트 자료는 각 테스트가 끝나면 롤백됨. */
@SpringBootTest(properties = "spring.jpa.open-in-view=false")
@AutoConfigureMockMvc
@Transactional
class MyPageControllerTest {

  @Autowired private MockMvc mvc;
  @Autowired private EntityManager entityManager;
  @Autowired private TokenProvider tokenProvider;
  @Autowired private JwtProperties jwtProperties;

  private Member me;
  private Member other;
  private Member emptyMember;
  private Book oldBook;
  private Book newBook;
  private Book otherBook;
  private ExchangeRequest oldReceived;
  private ExchangeRequest newReceived;
  private ExchangeRequest sent;

  @BeforeEach
  void setUp() {
    me = persist(new Member("me@example.com", "private-password", "나"));
    other = persist(new Member("other@example.com", "other-password", "상대방"));
    emptyMember = persist(new Member("empty@example.com", "empty-password", "신규회원"));
    oldBook = book("예전 책", me, 1);
    oldBook.complete();
    newBook = book("새 책", me, 2);
    otherBook = book("다른 사람의 책", other, 3);
    oldReceived = request(oldBook, other, 0, null, 1);
    oldReceived.accept();
    newReceived = request(newBook, other, 1000, "/uploads/offer.jpg", 2);
    sent = request(otherBook, me, 2000, null, 3);
    sent.reject();
    entityManager.flush();
    entityManager.clear();
  }

  @Test
  void booksReturnsOnlyMyBooksNewestFirstIncludingCompleted() throws Exception {
    authenticatedGet("/books", me)
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].id").value(newBook.getId()))
        .andExpect(jsonPath("$[0].title").value("새 책"))
        .andExpect(jsonPath("$[0].author").value("저자"))
        .andExpect(jsonPath("$[0].coverImageUrl").value("/uploads/cover.jpg"))
        .andExpect(jsonPath("$[0].status").value("TRADING"))
        .andExpect(jsonPath("$[0].createdAt").value("2026-09-02T12:00:00"))
        .andExpect(jsonPath("$[1].id").value(oldBook.getId()))
        .andExpect(jsonPath("$[1].status").value("COMPLETED"))
        .andExpect(jsonPath("$..owner").doesNotExist());
  }

  @Test
  void receivedReturnsRequestsForMyBooksNewestFirstIncludingAccepted() throws Exception {
    authenticatedGet("/requests/received", me)
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].id").value(newReceived.getId()))
        .andExpect(jsonPath("$[0].bookId").value(newBook.getId()))
        .andExpect(jsonPath("$[0].bookTitle").value("새 책"))
        .andExpect(jsonPath("$[0].bookCoverImageUrl").value("/uploads/cover.jpg"))
        .andExpect(jsonPath("$[0].requesterId").value(other.getId()))
        .andExpect(jsonPath("$[0].requesterNickname").value("상대방"))
        .andExpect(jsonPath("$[0].offeredPrice").value(1000))
        .andExpect(jsonPath("$[0].offeredPhotoUrl").value("/uploads/offer.jpg"))
        .andExpect(jsonPath("$[0].message").value("거래 제안"))
        .andExpect(jsonPath("$[0].status").value("PENDING"))
        .andExpect(jsonPath("$[0].processedAt").isEmpty())
        .andExpect(jsonPath("$[1].id").value(oldReceived.getId()))
        .andExpect(jsonPath("$[1].offeredPrice").value(0))
        .andExpect(jsonPath("$[1].offeredPhotoUrl").isEmpty())
        .andExpect(jsonPath("$[1].status").value("ACCEPTED"))
        .andExpect(jsonPath("$[1].processedAt").isNotEmpty());
  }

  @Test
  void sentReturnsOnlyMyRequestsIncludingRejected() throws Exception {
    authenticatedGet("/requests/sent", me)
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].id").value(sent.getId()))
        .andExpect(jsonPath("$[0].bookId").value(otherBook.getId()))
        .andExpect(jsonPath("$[0].requesterId").value(me.getId()))
        .andExpect(jsonPath("$[0].status").value("REJECTED"))
        .andExpect(jsonPath("$[0].processedAt").isNotEmpty());
  }

  @ParameterizedTest
  @ValueSource(strings = {"/books", "/requests/received", "/requests/sent"})
  void noRecordsReturnsEmptyArray(String path) throws Exception {
    authenticatedGet(path, emptyMember).andExpect(content().json("[]"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"/books", "/requests/received", "/requests/sent"})
  void missingTokenReturnsUnauthorized(String path) throws Exception {
    mvc.perform(get("/api/mypage" + path))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"/books", "/requests/received", "/requests/sent"})
  void invalidTokenReturnsUnauthorized(String path) throws Exception {
    unauthorizedGet(path, "invalid-token");
  }

  @ParameterizedTest
  @ValueSource(strings = {"/books", "/requests/received", "/requests/sent"})
  void refreshTokenCannotBeUsedAsAccessToken(String path) throws Exception {
    unauthorizedGet(path, tokenProvider.issueRefreshToken(me.getId()));
  }

  @ParameterizedTest
  @ValueSource(strings = {"/books", "/requests/received", "/requests/sent"})
  void expiredTokenReturnsUnauthorized(String path) throws Exception {
    TokenProvider expiredProvider =
        new TokenProvider(new JwtProperties(jwtProperties.secret(), -1, 7));
    unauthorizedGet(path, expiredProvider.issueAccessToken(me.getId(), "USER"));
  }

  private ResultActions authenticatedGet(String path, Member member) throws Exception {
    return mvc.perform(
            get("/api/mypage" + path)
                // 다른 회원 ID를 보내도 토큰의 회원 ID만 조회 기준으로 사용해야 함.
                .param("memberId", other.getId().toString())
                .header(
                    "Authorization",
                    "Bearer " + tokenProvider.issueAccessToken(member.getId(), "USER")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$..password").doesNotExist())
        .andExpect(jsonPath("$..email").doesNotExist())
        .andExpect(jsonPath("$..requester").doesNotExist())
        .andExpect(jsonPath("$..book").doesNotExist());
  }

  private void unauthorizedGet(String path, String token) throws Exception {
    mvc.perform(get("/api/mypage" + path).header("Authorization", "Bearer " + token))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }

  private Book book(String title, Member owner, int day) {
    Book book = new Book(title, "저자", "/uploads/cover.jpg", "설명", owner, true, true, true);
    ReflectionTestUtils.setField(book, "createdAt", LocalDateTime.of(2026, 9, day, 12, 0));
    return persist(book);
  }

  private ExchangeRequest request(Book book, Member requester, int price, String photo, int day) {
    ExchangeRequest request = new ExchangeRequest(book, requester, price, photo, "거래 제안");
    ReflectionTestUtils.setField(request, "createdAt", LocalDateTime.of(2026, 9, day, 13, 0));
    return persist(request);
  }

  private <T> T persist(T entity) {
    entityManager.persist(entity);
    return entity;
  }
}
