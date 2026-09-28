# Git 규칙 (펩시미만잡)

## 브랜치

- `main` + `feature/<번호>-<기능>` (역할분담표 번호 기준, 예: `feature/4-거래요청`)
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

## 비밀 정보

- `.env`는 `.gitignore` 처리, `.env.example`만 커밋
