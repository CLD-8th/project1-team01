# 스크립트 모음

git 명령어가 익숙하지 않아도 이 스크립트들만 실행하면 됩니다.

## sync — 최신 main 받기 + 브랜치 정리

작업 시작 전에 한 번씩 실행하면 좋음. `main`으로 이동 → 최신 내용 받기 → 이미 merge된 로컬 브랜치 자동 삭제.

- **Mac**: `scripts/sync.command` 더블클릭
- **Windows**: `scripts/sync.bat` 더블클릭
- **터미널**: `./scripts/sync.sh`

## push — 커밋·push·PR 생성

파일 저장하고 이것만 실행하면 커밋·push·PR 생성까지 자동으로 됩니다.

## 사용법

- **Mac**: `scripts/push.command` 더블클릭
  - 처음 한 번은 "보안 때문에 실행할 수 없습니다" 뜰 수 있음 → 파인더에서 우클릭 → 열기
- **Windows**: `scripts/push.bat` 더블클릭 (Git for Windows 설치 필요 — bash가 포함되어 있음)
- **터미널**: `./scripts/push.sh`

## 하는 일 (순서대로)

1. `main` 브랜치에 있으면 새 브랜치 이름을 물어보고 만들어줌
2. 커밋 메시지 입력받음 (예: `feat: 도서 등록 API 추가`) — AI 도구 관련 문구 있으면 거부
3. 코드 스타일 자동 정리(`spotlessApply`)
4. 컴파일 확인 — 오류 있으면 여기서 멈춤(push 안 됨)
5. add → commit → push
6. PR이 없으면 자동으로 만들고, 이미 있으면 그 PR에 커밋만 추가

## 하지 않는 일

- **merge는 자동으로 안 함** — CI 통과 확인하고 GitHub에서 직접 눌러야 함
- 처음 한 번은 `gh auth login`으로 GitHub 로그인이 되어 있어야 함(안내 메시지 뜸)

## 개발 중 빠르게 돌리는 법

매번 `docker compose up --build`로 전체를 새로 빌드하면 느립니다. 개발할 땐 DB·캐시만 도커로 띄우고 앱은 IDE(또는 `./gradlew bootRun`)로 직접 실행하는 게 훨씬 빠릅니다.

```bash
docker compose up -d db cache   # db(localhost:3307), cache(localhost:6380)만 띄움
./gradlew bootRun               # 앱은 로컬에서 직접 실행(재시작 빠름)
```

`application.yml`이 이미 이 포트로 맞춰져 있어서 별도 설정 없이 바로 됩니다. 전체를 도커 이미지로 최종 확인할 때만 `docker compose up -d --build` 사용.

## 세팅 완료 확인

아래 3가지가 이 문구 그대로 나오면 세팅 성공. 팀 채팅에 캡처 공유할 때 이 기준으로 확인하면 됨.

**1. `docker compose ps`**

```
NAME          STATUS
app-db-1      Up ... (healthy)
app-cache-1   Up ... (healthy)
```

`(healthy)`가 둘 다 떠야 함. `(health: starting)`이면 몇 초 더 기다리기.

**2. 앱 실행 로그** (터미널 또는 IDE 콘솔)

```
Started StudyAppApplication in X.XXX seconds (process running for X.XXX)
```

이 줄이 뜨면 DB·Redis 연결 성공. 안 뜨고 `BUILD FAILED`, `Access denied`, `Connection refused` 같은 게 뜨면 실패.

**3. 헬스체크**

```bash
curl http://localhost:8090/actuator/health
```

```json
{"status":"UP"}
```

브라우저로 `http://localhost:8090/actuator/health` 열어봐도 됨. `DOWN`이거나 응답이 없으면 실패.

**(선택) 테이블 자동 생성 확인**

```bash
docker exec -it app-db-1 mysql -uhunchaekbang -phunchaekbang1234 hunchaekbang -e "SHOW TABLES;"
```

```
book
exchange_request
member
```

이것까지 나오면 엔티티(`Book`/`ExchangeRequest`)도 제대로 반영된 것까지 확인 끝.

## 사전 준비 (한 번만)

- [GitHub CLI](https://cli.github.com) 설치
- 터미널에서 `gh auth login` 실행 후 안내대로 로그인
