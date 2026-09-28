#!/usr/bin/env bash
set -e

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$REPO_ROOT"

echo "=== 헌책방 push 스크립트 ==="
echo ""

if ! command -v gh &> /dev/null; then
  echo "GitHub CLI(gh)가 설치되어 있지 않습니다."
  echo "https://cli.github.com 에서 설치한 뒤 다시 실행하세요."
  exit 1
fi

if ! gh auth status &> /dev/null; then
  echo "GitHub 로그인이 안 되어 있습니다."
  echo "터미널에서 'gh auth login'을 먼저 실행하세요."
  exit 1
fi

CURRENT_BRANCH=$(git branch --show-current)

if [ "$CURRENT_BRANCH" = "main" ]; then
  echo "지금 main 브랜치입니다. main에는 직접 push할 수 없습니다."
  read -p "새 브랜치 이름을 입력하세요 (예: feature/4-거래요청): " NEW_BRANCH
  if [ -z "$NEW_BRANCH" ]; then
    echo "브랜치 이름이 없어서 취소합니다."
    exit 1
  fi
  git pull origin main
  git checkout -b "$NEW_BRANCH"
  CURRENT_BRANCH="$NEW_BRANCH"
fi

if git diff --quiet && git diff --cached --quiet && [ -z "$(git status --porcelain)" ]; then
  echo "변경된 파일이 없습니다. 할 일이 없어요."
  exit 0
fi

echo ""
echo "커밋 메시지 예시: feat: 도서 등록 API 추가"
read -p "커밋 메시지를 입력하세요: " COMMIT_MSG
if [ -z "$COMMIT_MSG" ]; then
  echo "커밋 메시지가 없어서 취소합니다."
  exit 1
fi

if echo "$COMMIT_MSG" | grep -iE 'claude|anthropic|co-authored-by|gpt|chatgpt|openai|copilot|gemini|bard' > /dev/null; then
  echo "커밋 메시지에 AI 도구 관련 문구가 있습니다. 다른 메시지로 다시 시도하세요."
  exit 1
fi

echo ""
echo "--- 코드 스타일 자동 정리 (spotlessApply) ---"
./gradlew spotlessApply --no-daemon

echo ""
echo "--- 컴파일 확인 ---"
if ! ./gradlew compileJava --no-daemon; then
  echo ""
  echo "컴파일 오류가 있습니다. 위 메시지를 보고 고친 뒤 다시 실행하세요."
  exit 1
fi

git add -A

if git diff --cached --quiet; then
  echo "커밋할 변경사항이 없습니다(포맷 정리 후 달라진 게 없음)."
  exit 0
fi

git commit -m "$COMMIT_MSG"
git push -u origin "$CURRENT_BRANCH"

echo ""
if gh pr view "$CURRENT_BRANCH" &> /dev/null; then
  echo "이미 열려있는 PR에 커밋을 추가했습니다."
  gh pr view "$CURRENT_BRANCH" --json url --jq .url
else
  PR_URL=$(gh pr create --title "$COMMIT_MSG" --body "push 스크립트로 자동 생성된 PR" --base main --head "$CURRENT_BRANCH")
  echo "PR을 만들었습니다:"
  echo "$PR_URL"
fi

echo ""
echo "완료! CI 통과 확인하고 GitHub에서 merge 버튼을 눌러주세요(merge는 자동으로 안 됩니다)."
