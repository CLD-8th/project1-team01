# 헌책방 (Project 1 — 펩시미만잡)

읽은 책을 등록하고 다른 사람의 책과 교환하거나 판매하는 서비스입니다.

## 팀 정보

- 팀명: 펩시미만잡
- 팀원: 김시웅, 문병현, 전동환, 홍정민
- 제출 마감: 2026-10-01(목) 12:00

## 빠른 실행

```bash
git clone https://github.com/CLD-8th/project1-team01.git
cd project1-team01
cp .env.example .env
docker compose up -d --build
```

기동 후 `http://localhost:8090/index.html` 접속, `curl http://localhost:8090/actuator/health`로 상태 확인(`{"status":"UP"}`). 자세한 확인 절차·트러블슈팅은 [컨테이너화 재현 확인 가이드](docs/16_컨테이너확인가이드.md) 참고.

## 주요 기능

- 도서 등록·목록·상세 조회(검색·상태 필터)
- 거래 요청 — 가격·사진 조합으로 교환요청/웃돈제안/나눔요청/구매제안 자동 판정
- 요청 수락(Redis 락으로 동시 수락 방지)·거절
- 마이페이지(등록한 책·받은 요청·보낸 요청)
- 인기 도서 랭킹(거래 요청 수 + 조회수 가중치, Redis Sorted Set)
- 도서 사진 업로드

## 진행 상태

| 단계 | 상태 |
|---|---|
| 도메인·기능 정의 | 완료 |
| 스켈레톤 병합 | 완료 |
| 엔티티(Book/ExchangeRequest) | 완료 |
| 최소 동작본(API 11개) + 프론트 전체 화면 | 완료 — [진행 체크리스트](docs/15_진행체크리스트.md) 참고 |
| 컨테이너 이미지 빌드·기동 확인 | 완료 (각자 VM 재현 확인은 내일) |
| AWS 아키텍처 설계 | 시작 전 |

## 문서

**정의**
- [주제 제출](docs/00_주제제출.md)
- [도메인·기능 정의서](docs/01_도메인기능정의서.md)

**설계**
- [ERD](docs/02_ERD.md)
- [URL/API 목록](docs/03_API목록.md)
- [API 요청 예시(curl)](docs/17_API요청예시.md)
- [Redis 키 설계](docs/04_Redis키설계.md)

**AWS 아키텍처 설계**
- [전체 폴더](docs/05_AWS설계/)
- [역할 기반 매핑](docs/05_AWS설계/01_역할매핑.md) · [단계별 진화](docs/05_AWS설계/02_단계별진화.md) · [VPC 설계](docs/05_AWS설계/03_VPC설계.md) · [보안 그룹](docs/05_AWS설계/04_보안그룹.md) · [용량 산정](docs/05_AWS설계/05_용량산정.md) · [비용 산정](docs/05_AWS설계/06_비용산정.md) · [ADR](docs/05_AWS설계/07_ADR.md)

**팀 운영**
- [역할 분담표](docs/06_역할분담표.md)
- [Git 규칙](docs/07_git규칙.md)
- [Team Log](docs/08_team-log.md)
- [트러블슈팅](docs/09_트러블슈팅.md)
- [회고](docs/10_회고.md)

**제출 자료**
- [발표 자료](docs/11_발표자료/)
- [동작 증빙](docs/12_동작증빙/)

**참고**
- [스켈레톤 안내](SKELETON.md), [스켈레톤 API 문서](docs/skeleton/api.md), [스켈레톤 기능 문서](docs/skeleton/functions.md)
- [스켈레톤 재활용 가이드](docs/13_스켈레톤_재활용가이드.md) — 뭘 그대로 쓰고, 뭘 이름만 바꾸고, 뭘 새로 짜야 하는지
- [기능 구현 가이드](docs/14_기능구현가이드.md) — 브랜치별 컨트롤러·DB·구현 순서 상세
- [진행 체크리스트](docs/15_진행체크리스트.md) — 실시간 진행 상황
- [컨테이너화 재현 확인 가이드](docs/16_컨테이너확인가이드.md) — 각자 컴퓨터/VM에서 `docker compose up -d --build`로 전체 재현하는 법
