#!/usr/bin/env bash
set -e

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$REPO_ROOT"

echo "=== 최신 main 받기 + 정리된 브랜치 지우기 ==="
echo ""

CURRENT_BRANCH=$(git branch --show-current)
if [ "$CURRENT_BRANCH" != "main" ]; then
  echo "지금 '$CURRENT_BRANCH' 브랜치입니다. 저장 안 한 변경사항이 있으면 git이 알려줄 거예요."
fi

git checkout main
git pull origin main
git fetch --prune

echo ""
echo "--- 이미 merge된 로컬 브랜치 정리 ---"
MERGED=$(git branch --merged main | grep -v "^\*\| main$" || true)
if [ -z "$MERGED" ]; then
  echo "지울 브랜치가 없습니다."
else
  echo "$MERGED" | xargs -n 1 git branch -d
fi

echo ""
echo "완료! 최신 main 기준으로 새 작업 시작하면 됩니다."
