# クラウドデプロイガイド

このリポジトリのアプリをクラウドにデプロイする方法をまとめる。  
対象は `07_docker-compose`（Docker + PostgreSQL 構成）。  
次の `08_cloud-run` では Cloud Run + Supabase の実装を行う予定。

---

## デプロイ構成の選択肢

| 構成 | 特徴 | 費用目安 |
|------|------|---------|
| **Cloud Run + Supabase** | 手軽・無料枠あり | ほぼ0円 |
| **Cloud Run + Cloud SQL** | 本番向け・フルマネージド | 月$7〜 |
| **GCE（e2-micro）+ Docker Compose** | 今の構成そのまま | 無料枠あり |
| Railway | compose.yml ほぼそのまま | 無料枠あり |

---

## Cloud Run + Supabase（推奨・無料）

### 1. Supabase でDBを作成

1. https://supabase.com にアクセス・アカウント作成
2. 新プロジェクトを作成
3. Settings → Database → Connection String（JDBC）をコピー

```
jdbc:postgresql://db.xxxx.supabase.co:5432/postgres
```

### 2. Spring Boot イメージを Artifact Registry に push

```bash
# イメージをビルド
cd 07_docker-compose
docker build -t factory-app .

# Artifact Registry にタグ付け
docker tag factory-app asia-northeast1-docker.pkg.dev/[PROJECT_ID]/factory/app:latest

# push
docker push asia-northeast1-docker.pkg.dev/[PROJECT_ID]/factory/app:latest
```

### 3. Cloud Run にデプロイ

```bash
gcloud run deploy factory-app \
  --image asia-northeast1-docker.pkg.dev/[PROJECT_ID]/factory/app:latest \
  --region asia-northeast1 \
  --platform managed \
  --allow-unauthenticated \
  --set-env-vars "DB_URL=jdbc:postgresql://db.xxxx.supabase.co:5432/postgres" \
  --set-env-vars "DB_USER=postgres" \
  --set-env-vars "DB_PASS=your-password"
```

デプロイ完了後、URL が発行される：
```
https://factory-app-xxxx-an.a.run.app/api/productions/summary
```

---

## Cloud Run + Cloud SQL（本番向け）

Cloud SQL（PostgreSQL）は Google のフルマネージドサービス。  
バックアップ・パッチ適用・HA が自動管理される。

```bash
# Cloud SQL インスタンス作成
gcloud sql instances create factory-db \
  --database-version=POSTGRES_16 \
  --tier=db-f1-micro \
  --region=asia-northeast1

# DB・ユーザー作成
gcloud sql databases create factorydb --instance=factory-db
gcloud sql users create factory --instance=factory-db --password=factory123
```

Cloud Run からは Cloud SQL Auth Proxy 経由で接続する。  
→ 08_cloud-run で実装予定。

---

## GCE（e2-micro）に Docker Compose をデプロイ

compose.yml をそのまま使いたい場合。e2-micro は GCP の Always Free 対象。

```bash
# VM 作成（us-central1 のみ無料枠対象）
gcloud compute instances create factory-vm \
  --zone=us-central1-a \
  --machine-type=e2-micro \
  --image-family=debian-12 \
  --image-project=debian-cloud

# VM に SSH 接続
gcloud compute ssh factory-vm --zone=us-central1-a

# Docker インストール後
git clone https://github.com/[USER]/spring-boot-samples.git
cd spring-boot-samples/07_docker-compose
docker compose up --build -d
```

---

## 環境変数の考え方

`application.properties` は `${環境変数:デフォルト値}` 形式で記述済み。

```properties
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/factorydb}
spring.datasource.username=${DB_USER:factory}
spring.datasource.password=${DB_PASS:factory123}
```

| 環境 | 設定方法 |
|------|---------|
| ローカル（Docker） | compose.yml の `environment:` |
| Cloud Run | `--set-env-vars` または Cloud Run コンソール |
| GCE | `.env` ファイルまたは OS 環境変数 |

**コードは変えない。接続先の環境変数だけ変える。**
