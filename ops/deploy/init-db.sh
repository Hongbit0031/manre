#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
ENV_FILE="${DEPLOY_ENV_FILE:-$ROOT_DIR/ops/deploy/.deploy.env}"
SQL_FILE="${SQL_FILE:-$ROOT_DIR/sql/linfeng-community-v3.1.0-all.sql}"

if [[ ! -f "$ENV_FILE" ]]; then
  echo "Missing deploy env file: $ENV_FILE" >&2
  exit 1
fi

if [[ "${CONFIRM_DB_INIT:-}" != "YES" ]]; then
  echo "Refusing to initialize the database without CONFIRM_DB_INIT=YES" >&2
  exit 1
fi

# shellcheck disable=SC1090
source "$ENV_FILE"

if [[ ! -f "$SQL_FILE" ]]; then
  echo "SQL file not found: $SQL_FILE" >&2
  exit 1
fi

MYSQL_HOST="${MYSQL_HOST:-127.0.0.1}"
MYSQL_PORT="${MYSQL_PORT:-3306}"
MYSQL_DATABASE="${MYSQL_DATABASE:-linfengcommunity}"

mysql \
  --host="$MYSQL_HOST" \
  --port="$MYSQL_PORT" \
  --user="$MYSQL_USERNAME" \
  --password="$MYSQL_PASSWORD" \
  "$MYSQL_DATABASE" <"$SQL_FILE"

echo "Database initialized from $SQL_FILE"
