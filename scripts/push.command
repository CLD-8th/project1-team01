#!/bin/bash
cd "$(dirname "$0")/.."
bash scripts/push.sh
echo ""
read -p "엔터를 누르면 창이 닫힙니다..."
