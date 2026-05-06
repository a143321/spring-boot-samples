# 08_cloud-run

07_docker-compose を **Google Cloud Run + Supabase** に対応させた本番デプロイサンプル。  
`deploy.sh` の1コマンドで Cloud Run にデプロイできる。

## 07 との違い

| 観点 | 07_docker-compose | 08_cloud-run |
|------|-------------------|-------------|
| 実行環境 | ローカル Docker | Google Cloud Run |
| DB | Docker 内 PostgreSQL | Supabase（外部・無料） |
| 起動 | `docker compose up` | `./deploy.sh` |
| URL | `localhost:8080` | Cloud Run が発行する URL |
| スケール | 手動 | 自動（リクエスト数に応じて） |

---

## 前提条件

```bash
# Google Cloud CLI がインストール・認証済みであること
gcloud auth login
gcloud config set project elegant-karma-492109-n2

# Docker がインストールされていること
docker --version
```

---

## Supabase のセットアップ

1. https://supabase.com でアカウント作成
2. 新プロジェクトを作成（リージョン: Tokyo）
3. Settings → Database → **Connection String（JDBC）** をコピー

```
## Supabase の接続方式

Supabase には3種類の接続方式がある。Cloud Run には **Transaction pooler** を使う。

| 接続方式 | ポート | IPv4 | 用途 |
|---------|--------|------|------|
| Direct connection | 5432 | ❌（IPv6のみ） | VM・長期接続向け |
| **Transaction pooler** | **6543** | ✅ | **Cloud Run（サーバーレス）向け** |
| Session pooler | 5432 | ✅ | IPv4必要な場合の代替 |

### なぜ Direct connection が使えないのか

Supabase の Direct connection はデフォルトで IPv6 アドレスを使用する。  
しかし **Google Cloud Run はアウトバウンド通信が IPv4 のみ対応**（2024年時点）。

```
Cloud Run → ❌ → IPv6 の PostgreSQL（Direct connection）
Cloud Run → ✅ → IPv4 の Transaction pooler → PostgreSQL
```

### なぜ Transaction pooler が Cloud Run に最適なのか

```
Cloud Run の特性
  └── リクエストのたびに起動・停止（接続が短命・大量）

Transaction pooler の特性
  ├── IPv4 対応（Cloud Run から接続できる）
  └── 短命な接続を内部でプールして再利用（負荷を軽減）
```

Cloud Run のようなサーバーレス環境では、DB 接続を毎回張り直すコストが高い。  
Transaction pooler がその接続を管理・使い回してくれるため、パフォーマンスも向上する。

### 接続文字列の構造

```
jdbc:postgresql://aws-1-ap-northeast-1.pooler.supabase.com:6543/postgres
                  ↑ プーラーのホスト名                        ↑ポート6543

?user=postgres.[プロジェクトID]&password=[パスワード]
```

→ `.env` では `user` と `password` を URL と分けて管理する：

```properties
DB_URL=jdbc:postgresql://aws-1-ap-northeast-1.pooler.supabase.com:6543/postgres
DB_USER=postgres.mfoyqmnyfzcdowbrohcj   # ← Supabase のユーザー名
DB_PASS=your-database-password
```

---

## デプロイ方法

### シンプル版（スクリプト実行）

```bash
cd 08_cloud-run
chmod +x deploy.sh

./deploy.sh \
  "elegant-karma-492109-n2" \
  "jdbc:postgresql://db.xxxx.supabase.co:5432/postgres" \
  "postgres" \
  "your-supabase-password"
```

デプロイ完了後、URL が表示されます：

```
✅ デプロイ完了！
URL: https://factory-app-xxxx-an.a.run.app/api/productions/summary
```

### 手動版（ステップ別に実行したい場合）

```bash
# 1. Artifact Registry リポジトリ作成
gcloud artifacts repositories create factory \
  --repository-format=docker \
  --location=asia-northeast1 \
  --project=elegant-karma-492109-n2

# 2. Docker 認証
gcloud auth configure-docker asia-northeast1-docker.pkg.dev

# 3. イメージビルド & push
docker build -t asia-northeast1-docker.pkg.dev/elegant-karma-492109-n2/factory/factory-app:latest .
docker push asia-northeast1-docker.pkg.dev/elegant-karma-492109-n2/factory/factory-app:latest

# 4. Cloud Run にデプロイ
gcloud run deploy factory-app \
  --image=asia-northeast1-docker.pkg.dev/elegant-karma-492109-n2/factory/factory-app:latest \
  --region=asia-northeast1 \
  --platform=managed \
  --allow-unauthenticated \
  --set-env-vars="DB_URL=jdbc:postgresql://db.xxxx.supabase.co:5432/postgres,DB_USER=postgres,DB_PASS=your-password" \
  --project=elegant-karma-492109-n2
```

---

## Cloud Build による CI/CD（自動デプロイ）

GitHub に push するたびに自動でデプロイしたい場合は `cloudbuild.yaml` を使用。

```bash
# Cloud Build トリガー作成
gcloud builds triggers create github \
  --repo-name=spring-boot-samples \
  --repo-owner=a143321 \
  --branch-pattern=main \
  --build-config=08_cloud-run/cloudbuild.yaml \
  --substitutions="_DB_URL=jdbc:postgresql://...,_DB_USER=postgres,_DB_PASS=your-password" \
  --project=elegant-karma-492109-n2
```

---

## 動作確認

デプロイ後に発行される URL で確認：

```bash
BASE_URL="https://factory-app-xxxx-an.a.run.app"

# 製品一覧
curl ${BASE_URL}/api/products

# 不良率集計
curl ${BASE_URL}/api/productions/summary

# CSV バッチ出力
curl -X POST ${BASE_URL}/api/batch/run
```

---

## 本番移行時（Cloud SQL を使う場合）

DB の接続先を変えるだけで本番環境に移行できる。

```bash
# Cloud SQL 接続文字列に変更
--set-env-vars="DB_URL=jdbc:postgresql://[CLOUD_SQL_IP]:5432/factorydb,..."
```

| 環境 | DB | 費用 |
|------|-----|------|
| デモ・学習 | Supabase（無料） | 0円 |
| 本番 | Cloud SQL（PostgreSQL） | 月$7〜 |

**接続文字列を変えるだけ。コードは変更不要。**

---

## アーキテクチャ

```
GitHub
  │ push
  ▼
Cloud Build（自動ビルド）
  │
  ├→ Artifact Registry（Docker イメージ保存）
  │
  └→ Cloud Run（自動スケール・HTTPS対応）
          │
          └→ Supabase（PostgreSQL・無料）
```

---

## 07 との比較（SQL 変更なし）

07_docker-compose と 08_cloud-run のアプリコードは同一。  
`application.properties` の環境変数を切り替えるだけで動作する。

```properties
# どの環境でも同じ設定ファイル
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/factorydb}
spring.datasource.username=${DB_USER:factory}
spring.datasource.password=${DB_PASS:factory123}
```
