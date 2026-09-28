# Git 규칙 (펩시미만잡)

## push 스크립트

git 명령어 치기 번거롭거나 익숙하지 않으면 `scripts/push.command`(Mac) / `scripts/push.bat`(Windows) 더블클릭 — 브랜치 생성부터 스타일 정리·컴파일 확인·commit·push·PR 생성까지 자동으로 해줌. 자세한 건 [scripts/README.md](../scripts/README.md).

## 브랜치

- `main` + `feature/<번호>-<기능>` ([역할분담표](06_역할분담표.md) 번호 기준, 예: `feature/4-거래요청`)
- 문서만 고칠 땐 `docs/<내용>`, 그 외 잡일은 `chore/<내용>`
- develop 브랜치는 생략

## 커밋 메시지

- `feat:` `fix:` `docs:` `refactor:` `chore:` 접두어 + 한 줄 요약
- 장황한 설명 없이 실제 바뀐 것만 간단히
- AI 도구 이름·Co-Authored-By 같은 문구 넣지 않기 — CI가 자동으로 막음(`check` 워크플로우)

## PR

- `main` 직접 push 금지, 무조건 PR로만 merge
- 팀원 approve는 **강제 아님** — CI(`check`) 통과만 하면 본인 판단으로 merge 가능(실제 GitHub 브랜치 보호 설정 기준)
- 그래도 급한 거 아니면 merge 전에 diff 한 번은 팀원한테 보여주고 진행하는 걸 권장
- PR 올리기 전에 `main` pull 받아서 최신 상태로 맞추기 — 브랜치 오래 묵히지 않기

## 문서 충돌 방지

- `docs/` 안의 같은 파일(API목록·ERD 등)을 여러 명이 동시에 고칠 것 같으면 미리 채팅으로 얘기하고 순서대로 push
- 남이 먼저 push했으면 pull 받고 이어서 수정(덮어쓰기 금지)

## 코드 스타일

- Google Java Format(Spotless 플러그인)로 통일 — 사람마다 IDE 설정 달라서 생기는 줄바꿈·들여쓰기 충돌 방지
- 커밋 전 `./gradlew spotlessApply`로 자동 정렬, PR에서 `spotlessCheck`가 CI로 확인함

## 비밀 정보

- `.env`는 `.gitignore` 처리, `.env.example`만 커밋
