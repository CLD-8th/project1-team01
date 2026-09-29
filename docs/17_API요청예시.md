# API 요청 예시 (펩시미만잡)

> 코드 안 봐도 바로 테스트해볼 수 있도록 curl 예시 정리. 로컬 실행 후 http://localhost:8090 기준.
> 순서대로 따라하면 회원가입→로그인→도서 등록→거래→수락까지 전체 흐름을 그대로 재현할 수 있음.

## 1. 회원가입

```bash
curl -X POST http://localhost:8090/api/members \
  -H "Content-Type: application/json" \
  -d '{"email":"owner@example.com","password":"pass1234","nickname":"판매자"}'
```

## 2. 로그인

```bash
curl -X POST http://localhost:8090/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"owner@example.com","password":"pass1234"}'
```

응답의 `accessToken`을 이후 요청의 `Authorization: Bearer <값>`에 사용.

## 3. 사진 업로드 (10번)

```bash
curl -X POST http://localhost:8090/api/books/images \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@cover.jpg;type=image/jpeg"
```

→ `{"url":"/uploads/xxxx.jpg"}` — 이 url을 도서 등록에 사용.

## 4. 도서 등록 (3번)

```bash
curl -X POST http://localhost:8090/api/books \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "어린 왕자",
    "author": "생텍쥐페리",
    "coverImageUrl": "/uploads/xxxx.jpg",
    "description": "표지 약간 헤짐, 내용은 깨끗해요.",
    "acceptsPrice": true,
    "acceptsSwap": true,
    "acceptsGiveaway": false
  }'
```

## 5. 도서 목록 / 상세 (1·2번)

```bash
curl "http://localhost:8090/api/books?keyword=어린왕자&status=TRADING"
curl http://localhost:8090/api/books/1
```

## 6. 거래 요청 (4번) — 다른 사용자로 로그인해서 호출

가격·사진 조합으로 종류가 자동 결정됨(0원+사진=교환요청, 1원↑+사진=웃돈제안, 0원+사진없음=나눔요청, 1원↑+사진없음=구매제안).

```bash
curl -X POST http://localhost:8090/api/books/1/requests \
  -H "Authorization: Bearer $BUYER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"offeredPrice": 3000, "offeredPhotoUrl": null, "message": "구매하고 싶어요"}'
```

## 7. 마이페이지 (5·6·7번)

```bash
curl -H "Authorization: Bearer $TOKEN" http://localhost:8090/api/mypage/books
curl -H "Authorization: Bearer $TOKEN" http://localhost:8090/api/mypage/requests/received
curl -H "Authorization: Bearer $TOKEN" http://localhost:8090/api/mypage/requests/sent
```

## 8. 요청 수락 / 거절 (8·9번) — 책 등록자(판매자) 토큰으로 호출

```bash
curl -X POST http://localhost:8090/api/requests/1/accept -H "Authorization: Bearer $TOKEN"
curl -X POST http://localhost:8090/api/requests/1/reject -H "Authorization: Bearer $TOKEN"
```

주요 에러 응답:

| 상황 | 응답 |
|---|---|
| 본인 책 아닌데 수락/거절 시도 | 403 FORBIDDEN |
| 이미 처리된 요청 재처리 시도 | 400 ALREADY_PROCESSED |
| 존재하지 않는 요청 | 404 NOT_FOUND |
| 같은 책에 동시 수락 요청 몰릴 때 | 409 LOCK_CONFLICT |

## 9. 인기 도서 랭킹 (11번)

```bash
curl http://localhost:8090/api/books/ranking
```

거래 요청이 한 번도 없던 책은 대상에서 제외되고, 그 안에서 `요청수 × 5 + log(조회수+1)`로 순위가 매겨짐.
