# push 스크립트

git 명령어가 익숙하지 않아도 파일 저장하고 이것만 실행하면 커밋·push·PR 생성까지 자동으로 됩니다.

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

## 사전 준비 (한 번만)

- [GitHub CLI](https://cli.github.com) 설치
- 터미널에서 `gh auth login` 실행 후 안내대로 로그인
