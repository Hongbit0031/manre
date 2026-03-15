#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
ENV_FILE="${DEPLOY_ENV_FILE:-$ROOT_DIR/ops/deploy/.deploy.env}"

if [[ ! -f "$ENV_FILE" ]]; then
  echo "Missing deploy env file: $ENV_FILE"
  echo "Copy ops/deploy/.deploy.env.example to ops/deploy/.deploy.env and fill in the values first."
  exit 1
fi

# shellcheck disable=SC1090
source "$ENV_FILE"

timestamp() {
  date '+%Y-%m-%d %H:%M:%S'
}

log() {
  printf '[%s] %s\n' "$(timestamp)" "$*"
}

require_cmd() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "Required command not found: $1" >&2
    exit 1
  fi
}

ensure_dir() {
  mkdir -p "$1"
}

copy_dir_contents() {
  local src="$1"
  local dst="$2"
  ensure_dir "$dst"
  if command -v rsync >/dev/null 2>&1; then
    rsync -a --delete "$src"/ "$dst"/
  else
    rm -rf "$dst"
    mkdir -p "$dst"
    cp -R "$src"/. "$dst"/
  fi
}

write_backend_env() {
  local file_path="$1"
  cat >"$file_path" <<EOF
SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-prod}
SERVER_PORT=${SERVER_PORT:-${BACKEND_PORT:-8080}}
MYSQL_URL=${MYSQL_URL}
MYSQL_USERNAME=${MYSQL_USERNAME}
MYSQL_PASSWORD=${MYSQL_PASSWORD}
REDIS_OPEN=${REDIS_OPEN:-true}
REDIS_HOST=${REDIS_HOST:-127.0.0.1}
REDIS_PORT=${REDIS_PORT:-6379}
REDIS_PASSWORD=${REDIS_PASSWORD:-}
REDIS_DATABASE=${REDIS_DATABASE:-0}
MAIL_HOST=${MAIL_HOST:-smtp.qq.com}
MAIL_USERNAME=${MAIL_USERNAME:-}
MAIL_PASSWORD=${MAIL_PASSWORD:-}
MAIL_SEND_FROM=${MAIL_SEND_FROM:-}
MINIO_ENDPOINT=${MINIO_ENDPOINT:-http://127.0.0.1:9005}
MINIO_ACCESS_KEY=${MINIO_ACCESS_KEY:-}
MINIO_SECRET_KEY=${MINIO_SECRET_KEY:-}
MINIO_BUCKET_NAME=${MINIO_BUCKET_NAME:-linfeng}
LINFENG_IMAGE_PATH=${LINFENG_IMAGE_PATH:-/data/linfeng/image/}
LINFENG_VIDEO_PATH=${LINFENG_VIDEO_PATH:-/data/linfeng/video/}
LINFENG_FILE_PATH=${LINFENG_FILE_PATH:-/data/linfeng/file/}
THIRDLOGIN_OPEN=${THIRDLOGIN_OPEN:-false}
THIRDLOGIN_SECRET=${THIRDLOGIN_SECRET:-}
KNIFE4J_ENABLE=${KNIFE4J_ENABLE:-false}
KNIFE4J_BASIC_ENABLE=${KNIFE4J_BASIC_ENABLE:-false}
KNIFE4J_USERNAME=${KNIFE4J_USERNAME:-linfengcommunity}
KNIFE4J_PASSWORD=${KNIFE4J_PASSWORD:-linfengcommunity}
SWAGGER_BASIC_ENABLE=${SWAGGER_BASIC_ENABLE:-true}
SWAGGER_BASIC_USERNAME=${SWAGGER_BASIC_USERNAME:-linfengcommunity}
SWAGGER_BASIC_PASSWORD=${SWAGGER_BASIC_PASSWORD:-linfengcommunity}
EOF
}

write_admin_runtime_config() {
  local file_path="$1"
  cat >"$file_path" <<EOF
window.__MANRE_ADMIN_CONFIG__ = {
  devApi: "${ADMIN_PUBLIC_DEV_API:-http://127.0.0.1:8080}",
  prodApi: "${ADMIN_PUBLIC_PROD_API:-https://api.example.com}"
};
EOF
}

write_uniapp_runtime_config() {
  local file_path="$1"
  cat >"$file_path" <<EOF
globalThis.__MANRE_UNIAPP_CONFIG__ = {
  baseUrl: "${UNI_H5_BASE_URL:-api.example.com}",
  apiProtocol: "${UNI_H5_API_PROTOCOL:-https}",
  wsProtocol: "${UNI_H5_WS_PROTOCOL:-wss}",
  shareH5Url: "${UNI_H5_SHARE_URL:-https://h5.example.com/#/}",
  emojiUrl: "${UNI_H5_EMOJI_URL:-https://h5.example.com/static/emoji/}",
  imgResource: "${UNI_H5_IMG_RESOURCE:-https://h5.example.com/resource}",
  wxMpAppId: "${UNI_H5_WX_MP_APP_ID:-}",
  wxh5Login: ${UNI_H5_WX_H5_LOGIN:-false}
};
EOF
}

write_systemd_service() {
  local file_path="$1"
  local backend_dir="$2"
  local env_file="$3"
  cat >"$file_path" <<EOF
[Unit]
Description=Manre backend service
After=network.target

[Service]
Type=simple
WorkingDirectory=$backend_dir
EnvironmentFile=$env_file
ExecStart=/usr/bin/java -jar $backend_dir/${BACKEND_JAR_NAME:-linfeng-community-backend.jar} --spring.profiles.active=\${SPRING_PROFILES_ACTIVE}
SuccessExitStatus=143
Restart=always
RestartSec=5

[Install]
WantedBy=multi-user.target
EOF
}

write_nginx_conf() {
  local file_path="$1"
  cat >"$file_path" <<EOF
server {
    listen 80;
    server_name ${NGINX_ADMIN_SERVER_NAME:-admin.example.com};
    root ${NGINX_ADMIN_ROOT:-/var/www/manre-admin};
    index index.html;

    location / {
        try_files \$uri \$uri/ /index.html;
    }
}

server {
    listen 80;
    server_name ${NGINX_SERVER_NAME:-example.com};
    root ${NGINX_UNI_H5_ROOT:-/var/www/manre-h5};
    index index.html;

    location / {
        try_files \$uri \$uri/ /index.html;
    }

    location /app/ {
        proxy_pass ${NGINX_API_UPSTREAM:-http://127.0.0.1:8080}/app/;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }

    location /doc.html {
        proxy_pass ${NGINX_API_UPSTREAM:-http://127.0.0.1:8080}/doc.html;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }

    location /webjars/ {
        proxy_pass ${NGINX_API_UPSTREAM:-http://127.0.0.1:8080}/webjars/;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }

    location /swagger-resources/ {
        proxy_pass ${NGINX_API_UPSTREAM:-http://127.0.0.1:8080}/swagger-resources/;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }

    location /v3/api-docs/ {
        proxy_pass ${NGINX_API_UPSTREAM:-http://127.0.0.1:8080}/v3/api-docs/;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }

    location /resource/ {
        proxy_pass ${NGINX_API_UPSTREAM:-http://127.0.0.1:8080}/resource/;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }

    location /druid/ {
        proxy_pass ${NGINX_API_UPSTREAM:-http://127.0.0.1:8080}/druid/;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }

    location /app/socket/ {
        proxy_http_version 1.1;
        proxy_set_header Upgrade \$http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_pass ${NGINX_API_UPSTREAM:-http://127.0.0.1:8080}/app/socket/;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }
}
EOF
}

BACKEND_DEPLOY_DIR="${DEPLOY_BASE_DIR:-/srv/manre}/backend"
BACKEND_ENV_FILE="${BACKEND_DEPLOY_DIR}/backend.env"
BACKEND_SERVICE_FILE="${DEPLOY_BASE_DIR:-/srv/manre}/out/${BACKEND_SERVICE_NAME:-manre-backend}.service"
NGINX_RENDERED_CONF="${DEPLOY_BASE_DIR:-/srv/manre}/out/manre.nginx.conf"
BACKEND_BUILD_DIR="$ROOT_DIR/linfeng-community-backend"
ADMIN_BUILD_DIR="$ROOT_DIR/linfeng-community-vue"
UNIAPP_BUILD_DIR="$ROOT_DIR/linfeng-community-uniapp"
SYSTEMD_TARGET_FILE="/etc/systemd/system/${BACKEND_SERVICE_NAME:-manre-backend}.service"

ensure_dir "${DEPLOY_BASE_DIR:-/srv/manre}/out"
ensure_dir "$BACKEND_DEPLOY_DIR"
ensure_dir "${LINFENG_IMAGE_PATH:-/data/linfeng/image/}"
ensure_dir "${LINFENG_VIDEO_PATH:-/data/linfeng/video/}"
ensure_dir "${LINFENG_FILE_PATH:-/data/linfeng/file/}"

log "Building backend jar"
require_cmd mvn
require_cmd java
mvn -f "$BACKEND_BUILD_DIR/pom.xml" -DskipTests clean package
cp "$BACKEND_BUILD_DIR/target/${BACKEND_JAR_NAME:-linfeng-community-backend.jar}" "$BACKEND_DEPLOY_DIR/${BACKEND_JAR_NAME:-linfeng-community-backend.jar}"
write_backend_env "$BACKEND_ENV_FILE"
write_systemd_service "$BACKEND_SERVICE_FILE" "$BACKEND_DEPLOY_DIR" "$BACKEND_ENV_FILE"

log "Building admin site"
require_cmd npm
(
  cd "$ADMIN_BUILD_DIR"
  ${ADMIN_NODE_INSTALL_CMD:-npm ci}
  npm run build
)
copy_dir_contents "$ADMIN_BUILD_DIR/dist" "${ADMIN_SITE_DIR:-/var/www/manre-admin}"
write_admin_runtime_config "${ADMIN_SITE_DIR:-/var/www/manre-admin}/runtime-config.js"

if [[ "${UNI_H5_ENABLED:-1}" == "1" ]]; then
  log "Building uni-app H5 site"
  if [[ -n "${UNI_H5_BUILD_CMD:-}" ]]; then
    (
      cd "$UNIAPP_BUILD_DIR"
      eval "${UNI_H5_BUILD_CMD}"
    )
  fi
  if [[ ! -d "${UNI_H5_OUTPUT_DIR:-$UNIAPP_BUILD_DIR/unpackage/dist/build/h5}" ]]; then
    echo "Uni-app H5 output directory not found: ${UNI_H5_OUTPUT_DIR:-$UNIAPP_BUILD_DIR/unpackage/dist/build/h5}" >&2
    echo "Set UNI_H5_BUILD_CMD or UNI_H5_OUTPUT_DIR in $ENV_FILE" >&2
    exit 1
  fi
  copy_dir_contents "${UNI_H5_OUTPUT_DIR:-$UNIAPP_BUILD_DIR/unpackage/dist/build/h5}" "${UNI_H5_SITE_DIR:-/var/www/manre-h5}"
  ensure_dir "${UNI_H5_SITE_DIR:-/var/www/manre-h5}/static"
  write_uniapp_runtime_config "${UNI_H5_SITE_DIR:-/var/www/manre-h5}/static/runtime-config.js"
fi

log "Rendering nginx config"
write_nginx_conf "$NGINX_RENDERED_CONF"
if [[ "${INSTALL_NGINX:-1}" == "1" ]]; then
  require_cmd nginx
  install -m 644 "$NGINX_RENDERED_CONF" "${NGINX_CONF_PATH:-/etc/nginx/conf.d/manre.conf}"
  nginx -t
  systemctl reload nginx
fi

if [[ "${INSTALL_SYSTEMD:-1}" == "1" ]]; then
  install -m 644 "$BACKEND_SERVICE_FILE" "$SYSTEMD_TARGET_FILE"
  systemctl daemon-reload
  systemctl enable "${BACKEND_SERVICE_NAME:-manre-backend}"
  systemctl restart "${BACKEND_SERVICE_NAME:-manre-backend}"
fi

log "Deployment finished"
