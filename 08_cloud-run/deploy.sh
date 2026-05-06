#!/bin/bash
# Cloud Run デプロイスクリプト
# 使い方: ./deploy.sh
#
# 事前に .env ファイルに接続情報を記入してください。
# cp .env.example .env && vi .env

set -e

# ── .env ファイルを読み込む ──────────────────────────────
SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
ENV_FILE="${SCRIPT_DIR}/.env"

if [ -f "${ENV_FILE}" ]; then
  echo "▶ .env ファイルを読み込み..."
  export $(grep -v '^#' "${ENV_FILE}" | xargs)
else
  echo "❌ .env ファイルが見つかりません。"
  echo "   cp .env.example .env して接続情報を記入してください。"
  exit 1
fi

# ── 設定 ──────────────────────────────────────────────────
IMAGE="${REGION}-docker.pkg.dev/${PROJECT_ID}/${REPO}/${SERVICE_NAME}"

echo "=========================================="
echo "  Cloud Run デプロイ開始"
echo "  Project : ${PROJECT_ID}"
echo "  Region  : ${REGION}"
echo "  Image   : ${IMAGE}"
echo "=========================================="

# ── Artifact Registry リポジトリ作成（初回のみ） ───────────
echo "▶ Artifact Registry を確認..."
gcloud artifacts repositories describe ${REPO} \
  --location=${REGION} \
  --project=${PROJECT_ID} 2>/dev/null || \
gcloud artifacts repositories create ${REPO} \
  --repository-format=docker \
  --location=${REGION} \
  --project=${PROJECT_ID} \
  --description="Factory App Docker Images"

# ── Docker 認証設定 ─────────────────────────────────────
echo "▶ Docker 認証設定..."
gcloud auth configure-docker ${REGION}-docker.pkg.dev --quiet

# ── イメージをビルド & push ──────────────────────────────
echo "▶ Docker イメージをビルド..."
docker build -t "${IMAGE}:latest" .

echo "▶ Artifact Registry に push..."
docker push "${IMAGE}:latest"

# ── Cloud Run にデプロイ ─────────────────────────────────
echo "▶ Cloud Run にデプロイ..."
gcloud run deploy ${SERVICE_NAME} \
  --image="${IMAGE}:latest" \
  --region=${REGION} \
  --platform=managed \
  --allow-unauthenticated \
  --set-env-vars="DB_URL=${DB_URL},DB_USER=${DB_USER},DB_PASS=${DB_PASS}" \
  --project=${PROJECT_ID}

# ── デプロイ結果 ─────────────────────────────────────────
echo ""
echo "=========================================="
echo "  ✅ デプロイ完了！"
URL=$(gcloud run services describe ${SERVICE_NAME} \
  --region=${REGION} \
  --project=${PROJECT_ID} \
  --format="value(status.url)")
echo "  URL: ${URL}/api/productions/summary"
echo "=========================================="
