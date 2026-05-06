# 07_docker-compose

06_factory-app を **Docker + PostgreSQL** 構成に対応させた本番想定サンプル。  
`docker compose up` の1コマンドで アプリ + DB が同時に起動する。

## 06 との違い

| 観点 | 06_factory-app | 07_docker-compose |
|------|---------------|-------------------|
| DB | H2（インメモリ・消える） | PostgreSQL（永続化） |
| 起動 | `mvn spring-boot:run` | `docker compose up` |
| 環境 | ローカル Java 必須 | Docker があれば動く |
| データ永続化 | ❌ 再起動で消える | ✅ Volume に保存 |

---

## なぜ Docker を使うのか

Docker の最大のメリットは **「誰がやっても同じ環境を再現できる」** こと。

### Before Docker（よくある問題）

```
「自分の PC では動くのに、他の人の PC では動かない」
「Java のバージョンが違う」
「PostgreSQL の設定が違う」
「OS が違う（Windows / Mac / Linux）」
```

### After Docker

```
docker compose up --build

→ どの PC でも同じ環境が動く。
```

---

## 構成ファイル

```
07_docker-compose/
├── Dockerfile          # Spring Boot → Docker イメージ化（マルチステージビルド）
├── compose.yml         # app + PostgreSQL をまとめて定義
├── .dockerignore       # ビルドコンテキストから除外するファイル
├── pom.xml
└── src/
    └── main/
        ├── java/com/example/factoryapp/   # 06 と同じパッケージ構成
        └── resources/
            ├── application.properties     # PostgreSQL 接続設定
            ├── schema.sql                 # PostgreSQL 構文のテーブル定義
            └── data.sql                   # 初期データ
```

---

## Docker 構成の解説

### `Dockerfile`（マルチステージビルド）

```dockerfile
# ステージ1: Maven でビルド
FROM maven:3.9-eclipse-temurin-21-alpine AS builder
RUN mvn package -DskipTests

# ステージ2: JRE で実行（JDK より軽量）
FROM eclipse-temurin:21-jre-alpine
COPY --from=builder /app/target/*.jar app.jar
```

2段階に分けることでイメージサイズを削減している。

---

### `compose.yml`

```yaml
services:
  db:                          # PostgreSQL
    image: postgres:16-alpine
    healthcheck: ...           # 準備できたか確認

  app:                         # Spring Boot
    build: .                   # このディレクトリの Dockerfile でビルド
    depends_on:
      db:
        condition: service_healthy  # DB が起動してから app を起動
```

`depends_on` + `healthcheck` で DB が準備できてからアプリが起動する。

---

### `application.properties`（環境変数で切り替え可能）

```properties
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/factorydb}
```

`${環境変数:デフォルト値}` の形式で、Docker 外でもローカル実行できる。

---

## API エンドポイント

06_factory-app と同じ（パスは `/api/` プレフィックス）

| パス | 説明 |
|------|------|
| `GET /api/products` | 製品一覧 |
| `GET /api/productions/summary` | ★ 不良率集計 |
| `POST /api/batch/run` | CSV 出力バッチ |

---

## 起動方法

### 前提

```bash
docker --version   # Docker がインストールされていること
docker compose version
```

### 起動

```bash
cd 07_docker-compose
docker compose up --build
```

初回は PostgreSQL イメージのダウンロードと Spring Boot のビルドがあるため数分かかる。

### バックグラウンド起動

```bash
docker compose up --build -d
```

### 停止

```bash
docker compose down          # コンテナ停止（データは残る）
docker compose down -v       # コンテナ + データも削除
```

---

## 動作確認

起動後 → http://localhost:8080/api/productions/summary

```bash
# 製品一覧
curl http://localhost:8080/api/products

# 不良率集計
curl http://localhost:8080/api/productions/summary

# 生産実績登録
curl -X POST http://localhost:8080/api/productions \
  -H "Content-Type: application/json" \
  -d '{"resultDate":"2026-05-06","lineCode":"LINE-A","productCode":"PART-001","processCode":"PROC-01","productionQty":500,"defectQty":2,"operatorName":"田中"}'

# CSV バッチ出力（output/ フォルダに保存される）
curl -X POST http://localhost:8080/api/batch/run
ls output/
```

---

## PostgreSQL に直接接続して確認

```bash
# コンテナに入る
docker exec -it factory-db psql -U factory -d factorydb

# SQL 実行
SELECT * FROM products;
SELECT * FROM production_results;
\q   # 終了
```

---

## H2 との SQL 構文の違い

| 機能 | H2 | PostgreSQL |
|------|-----|------------|
| 自動採番 | `BIGINT AUTO_INCREMENT` | `SERIAL` |
| 日付フォーマット | `FORMATDATETIME(...)` | `TO_CHAR(...)` |
| 重複防止 | ─ | `ON CONFLICT DO NOTHING` |
| 存在確認 | ─ | `IF NOT EXISTS` |
