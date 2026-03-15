#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
ENV_FILE="${DEPLOY_ENV_FILE:-$ROOT_DIR/ops/deploy/.deploy.env}"

if [[ ! -f "$ENV_FILE" ]]; then
  echo "Missing deploy env file: $ENV_FILE" >&2
  exit 1
fi

# shellcheck disable=SC1090
source "$ENV_FILE"

git -C "$ROOT_DIR" fetch origin "${DEPLOY_GIT_BRANCH:-main}"
git -C "$ROOT_DIR" pull --ff-only origin "${DEPLOY_GIT_BRANCH:-main}"

"$ROOT_DIR/ops/deploy/deploy.sh"
