# ERD (펩시미만잡)

> 초안. 스켈레톤 병합하면서 실제 컬럼명·타입은 조정될 수 있음. 수정은 아래 mermaid 코드를 바로 고치면 됨(https://mermaid.live 에서 미리보기 가능, GitHub에서도 자동 렌더링).

```mermaid
erDiagram
    USER ||--o{ BOOK : "등록"
    USER ||--o{ EXCHANGE_REQUEST : "요청"
    BOOK ||--o{ EXCHANGE_REQUEST : "받음"
    BOOK ||--o{ EXCHANGE_REQUEST : "제시(책 교환)"

    USER {
        bigint id PK
        varchar email
        varchar password
        varchar nickname
        datetime created_at
    }

    BOOK {
        bigint id PK
        bigint owner_id FK
        varchar title
        varchar author
        varchar cover_image_url
        varchar status "거래중, 거래완료"
        datetime created_at
    }

    EXCHANGE_REQUEST {
        bigint id PK
        bigint book_id FK "요청 대상 책"
        bigint requester_id FK "요청자"
        varchar type "가격 제시, 책 교환 제시"
        int offered_price "가격 제시일 때만"
        bigint offered_book_id FK "책 교환 제시일 때만, nullable"
        varchar status "요청, 거절, 수락"
        datetime created_at
    }
```

## 참고

- `book.status`(책 자체 거래 상태)와 `exchange_request.status`(개별 요청 상태)는 서로 다른 축 — 헷갈리지 않도록 분리.
- `exchange_request.type`에 따라 `offered_price` 또는 `offered_book_id` 중 하나만 채워짐(상호 배타적).
- 인기 도서 랭킹·동시 수락 방지 락은 DB 테이블이 아니라 Redis에만 존재([04_Redis키설계.md](04_Redis키설계.md) 참고).
