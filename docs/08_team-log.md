# Team Log (펩시미만잡)

일자별 결정·변경 사항 기록.

## 2026-09-28

- 프로젝트 1 킥오프. 팀 구성: 강승곤, 김시웅, 문병현, 전동환, 홍정민.
- 로컬 작업 폴더(`03.project/`) 세팅. 도메인·스켈레톤 저장소는 아직 미정.
- 도메인 확정: 중고 도서 교환. 서비스명: 헌책방.
- 팀명 확정: 펩시미만잡.
- 강승곤 취업으로 탈퇴. 팀원: 김시웅, 문병현, 전동환, 홍정민.
- Figma에 와이어프레임 7개(게시판/상세/등록/마이페이지 3종/거래 제안) 작성 — 웹 기준.
- 거래 제안 로직 확정: 가격(기본 0)·책 사진(업로드, 선택) 조합으로 교환요청/웃돈제안/나눔요청/구매제안 4가지 자동 결정. Figma·ERD 반영.
- 도서 등록 화면의 "거래 방식" 선택을 "받고 싶은 조건"(참고용, 강제 아님)으로 변경 — `book.accepts_price/accepts_swap/accepts_giveaway`.
- 도메인·기능 정의서, ERD, URL/API 목록(11개), Redis 키 설계(랭킹·동시 수락 방지 락) 작성해 repo에 push(PR #1~#10). 팀 Notion 내용과 교차 확인해 일치시킴.
- repo 정리: 테스트 파일 삭제, README를 이 repo 기준으로 재작성, main 브랜치 보호(PR 필수·CI 통과 필수, approve는 불필요·본인 merge 가능) 설정, 머지된 브랜치 정리.
- `study-app-skeleton` 병합(PR #14) — 인증·회원·공통설정·Dockerfile 재사용 예정, `study`/`application`/`review` 도메인 코드는 `book`/`exchange_request`로 교체 필요. `build.gradle`에 Redis 의존성 없어서 구현 시작할 때 추가해야 함.

## 2026-09-29

- 스켈레톤 재활용 가이드 작성(`13_스켈레톤_재활용가이드.md`) — Service·Controller는 전부 `TODO` 빈 껍데기라 참고만 하고 새로 작성, `StudyPost`→`Book`·`Application`→`ExchangeRequest`는 구조·필드명 거의 그대로 재사용 가능, `review` 패키지·스터디 화면(`static/study.html` 등)·`db/schema.sql`은 삭제 대상으로 정리.
- 화면 구현 기준 확정: Figma 와이어프레임대로 만들면 제일 좋지만, 시간 부족하면 최대한 비슷하게만 만들어도 됨 — 평가 기준은 화면 완성도가 아니라 기능 동작 여부.
- 강사님 지시: ERD대로 엔티티 공통 push 후 각자 개발, 화면 제거하고 backend API만 진행. 오늘 17:00까지 개발·merge·이미지 생성, 이후 추가사항은 개별 PR로. 내일은 이미지로 컨테이너화 동작 확인 후 바로 AWS 설계 착수.
- `Book`(←StudyPost)·`ExchangeRequest`(←Application) 엔티티·리포지토리 구현해 공통 push(PR #25) — `review` 패키지·스터디 전용 화면 10개·`db/schema.sql`·`sample.sql` 삭제, `SecurityConfig`·`ErrorCode` 도메인에 맞게 수정. 로컬에서 `docker compose up --build` 후 `book`/`exchange_request`/`member` 테이블만 깨끗하게 생성되는 것까지 확인.
- Gradle wrapper 버전 문제(8.10→8.14.2, Spring Boot 4.1 플러그인 최소요구), `application.yml` 자격증명 불일치, `compose.yaml` 호스트 포트 매핑 누락(db/cache를 IDE에서 직접 못 붙던 문제) 등 인프라 버그 다수 발견·수정(PR #17, #21, #23) — 상세는 `09_트러블슈팅.md`.
- 기능 구현 가이드(`14_기능구현가이드.md`) 작성 — 브랜치별 담당 컨트롤러·DB 메서드·구현 순서 상세 기술.
- API 11번(인기 도서 랭킹 조회) 구현·merge(PR #30) — Redis 랭킹 점수 증가·동시 수락 방지 락은 문병현이 4·8번 API 안에서 직접 구현하는 걸로 역할분담표 수정(`06_역할분담표.md`).
- 홍정민 1·2번(도서 목록·상세) 구현·merge(PR #31).
- 진행 체크리스트(`15_진행체크리스트.md`) 신설 — API별 진행 상태·오늘/내일 일정 실시간 추적.
- 팀원 개발환경 트러블슈팅 지원(Windows Docker/gh 설치 PATH 문제, `gh auth login` 권한 문제 등) — `09_트러블슈팅.md`에 기록.
