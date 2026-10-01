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
- 병현님 4번 초안 작성·테스트·merge(PR #34) — 로컬 환경 세팅이 늦어져 8·9번(수락·거절) TODO 스텁만 남겨두고 head-start로 대신 구현.
- 정민님 3번(도서 등록, PR #36), 시웅님 5·6·7번(마이페이지 3종, PR #35) 코드 리뷰·merge. #36은 `description` 필수 검증 누락 코멘트 남겨서 반영 후 merge.
- Figma 와이어프레임 링크 받아서 실제 화면 설계와 대조 — 목록 필터(인기순위 포함)·상세-거래제안 화면 분리·마이페이지 프로필 요약 등 구조적으로 다른 부분 다수 확인, 프론트 전체 화면 새로 구현해 merge(PR #37). 사진 등록란은 파일/링크 선택 + 드래그앤드롭 UI로 만들되, 실제 업로드 API(10번) 전까지는 미리보기만 지원.
- 병현님 8·9번(요청 수락·거절, PR #38) 코드 리뷰 — Redis 락 기반 동시 수락 방지 로직 정상 동작 실제 curl로 검증(FORBIDDEN/ALREADY_PROCESSED/NOT_FOUND 케이스 포함) 후 merge.
- 정민님 10번(사진 업로드, PR #39) 리뷰 중 CI 실패 발견 — `@Value("${app.upload-dir}")`가 테스트 전용 설정(`src/test/resources/application.yml`)엔 없어서 테스트 전체가 컨텍스트 로딩 단계에서 실패하는 문제. 원인·수정법 PR 코멘트로 전달, `09_트러블슈팅.md`에도 기록. 테스트 중 생성된 이미지 파일이 실수로 커밋된 것도 같이 발견해 `.gitignore` 추가 요청. 정민님이 기본값 추가·gitignore 수정에 확장자 화이트리스트(jpg/jpeg/png/webp)까지 더해서 재푸시, CI 통과 후 merge — **API 11개 전부 merge 완료(17:00 마감 전).**
- 사진 업로드 API-프론트 연결(PR #41) — `photo-picker.js` 파일 탭에서 고른 파일을 선택 즉시 `POST /api/books/images`로 올리고 반환 URL을 저장하도록 변경. 도서 등록 시 사진 미선택이면 서버 왕복 없이 바로 안내하도록 클라이언트 검증 추가.
- 보류해뒀던 인기 랭킹 조회수 가중치 반영(PR #42) — `Book.viewCount` 추가(도서 상세 조회 시 +1), `RankingScoreCalculator`로 `요청수 × 5 + log(조회수+1) × 1` 계산. 거래 요청이 한 번도 없던 책은 여전히 랭킹 대상에서 제외(요청수가 기본 기준), 그 안에서만 조회수로 순서 재조정. 기존 데이터 있는 개발 DB에 `ddl-auto: update`로 컬럼 추가되는 것도 직접 재시작해서 문제없음을 확인. 관련 문서(00·02·04번) 최신화.
- 다른 팀(02~06) 진행 상황도 확인 — 대부분 아직 백엔드 버그 수정 중이거나 프론트를 최소한으로만 구현 중, 우리 팀이 API·프론트·부가 기능까지 가장 앞서 있음을 확인.

## 2026-09-30

- 전원 각자 Ubuntu VM에서 `docker compose up -d --build` 재현 성공(개인별 평가 기준) — 시웅·병현·동환 3명 확인, 과정에서 Docker 미설치·SSH 키 등록·sudo 권한 등 VM 초기 세팅 이슈 다수 해결.
- `compose.yaml`의 `app` 서비스에 healthcheck 추가(`curl -f .../actuator/health`) — 기존엔 db·cache만 healthy로 뜨고 app은 Up만 떠서 구분이 안 되던 문제 해결.
- AWS 설계 착수. 용량 산정 체인 확정: DAU 2,400 → 피크집중률 12% → 방문자 288명 → (Little's Law) → 동시접속자 29명 → Think Time 12초 → 피크 RPS 2.4 → 급증배수 3배 → **급증 RPS 7.2**. 네 가지 가정(피크집중률·평균체류시간·Think Time·급증배수)만 세우고 나머지는 계산으로 도출하는 방식으로 정리.
- 컴퓨팅 티어 ADR: EC2 Auto Scaling vs ECS Fargate 비교 후 EC2 선택 — 팀 전원 VM/Docker 운영 경험은 있으나 ECS 경험은 없다는 점을 핵심 근거로 사용(09-28 교안 예시 ADR과 동일 논리).
- 비용 최적화 ADR 3건 추가: NAT Gateway 2개→1개(Zonal, AWS 권장사항과 다르게 간 트레이드오프 명시), ElastiCache Replica 미적용(랭킹은 DB에서 재생성 가능한 비영속 데이터), Auto Scaling 최소 대수 2(AZ 이중화, 처리량 아님).
- NAT Gateway 조사 중 **Regional NAT Gateway**(신규 옵션, AZ 단위 과금이라 Zonal 2개와 비용 동일) 확인 — ADR-03 "검토한 대안"에 3번째로 추가.
- AWS Pricing Calculator 최종 견적: **월 $215.95**(EC2 $18.61·ALB $21.10·RDS Multi-AZ $127.27·NAT $48.97·Data Transfer $0). RDS Multi-AZ가 전체의 59% 차지 — "작은 서비스인데 왜 이렇게 비싸냐"는 질문에 "요구사항(RTO 30분)의 가격표"라는 논리로 대응하기로 정리.
- 필수 질문 5개(공식, 배점 20%) + 팀 자체 질문 7개 + 보너스 4개 정리. 자체 질문 중 "Redis 캐시 적중률 개선" 문구가 실제 구현(랭킹·락 용도로만 사용, 조회 캐시 없음)과 안 맞아 정정.
- Figma로 1→2→3단계 아키텍처 다이어그램 작성(draw.io 대신 기존 와이어프레임 툴 재사용), PNG로 내보내 `docs/11_발표자료/`에 저장.
- ADR 5건, VPC·보안그룹·상태외부화 문서 전부 최종본으로 정리해 push(PR #51).

## 2026-10-01

- 팀 리뷰 중 발견된 오류 2건 수정: ① Auto Scaling 최소/희망 용량이 실수로 2→1로 바뀌어 AZ 이중화 근거(ADR-05)와 모순되던 것 복구, ② t4g.small 2대 비용이 문서마다 다르게($12/$25) 적혀있던 것을 실제 Pricing Calculator 값($18.61)으로 통일.
- 발표자료 Notion 페이지 작성 완료 — 설계 전제(Single-AZ로도 트래픽은 충분하지만 학습 목적상 Multi-AZ 선택) 명시, 프로젝트 개요·활용 장비·수행 절차·수행 결과·트러블슈팅·멘토 피드백까지 전체 구성.
- 시연 영상 시나리오 확정(핵심 기능 흐름 → 동시 수락 락 증명 → 랭킹 실시간 변화 → VM 컨테이너 재현 과정), API curl로 동시성 로직 사전 검증(두 요청 동시 수락 시 하나는 200, 다른 하나는 409 LOCK_CONFLICT "다른 요청이 먼저 처리됨" 확인) 후 녹화 진행.
- 최종 제출 준비 — Keka로 압축(맥 기본 압축의 한글 파일명 깨짐 문제 회피), `8기클라우드_프로젝트1_1팀.zip` 규칙대로 제출.
