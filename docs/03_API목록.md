# URL/API 목록 (펩시미만잡)

> 팀 Notion(URL/API 표) 기준.

| # | 기능 | Method | URL | 인증 | 비고 |
|---|---|---|---|---|---|
| 1 | 도서 목록 | GET | `/api/books` | X | 검색·상태 필터 |
| 2 | 도서 상세 | GET | `/api/books/{bookId}` | X | |
| 3 | 도서 등록 | POST | `/api/books` | O | 사진 포함 |
| 4 | 거래 요청 | POST | `/api/books/{bookId}/requests` | O | 가격(기본 0)·책 사진(선택) 조합으로 결정(교환요청/웃돈제안/나눔요청/구매제안) |
| 5 | 마이페이지(등록한 책) | GET | `/api/mypage/books` | O | |
| 6 | 마이페이지(받은 요청) | GET | `/api/mypage/requests/received` | O | |
| 7 | 마이페이지(보낸 요청) | GET | `/api/mypage/requests/sent` | O | |
| 8 | 요청 수락 | POST | `/api/requests/{requestId}/accept` | O | Redis 락(중복 수락 방지) |
| 9 | 요청 거절 | POST | `/api/requests/{requestId}/reject` | O | |
| 10 | 사진 업로드 | POST | `/api/books/images` | O | 로컬 디스크(`uploads/`) 저장, jpg·jpeg·png·webp만 허용, 5MB 이하 — URL만 반환받아 등록·요청 본문에 사용. AWS 단계에서 S3 전환 검토 |
| 11 | 인기 도서 랭킹 TOP 10 | GET | `/api/books/ranking` | X | Redis Sorted Set |
