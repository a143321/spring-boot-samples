# spring-boot-samples

Spring Boot を使った工場・生産系システム開発のサンプル集。  
REST API の基礎から DB 接続・バリデーション・バッチ処理・総合アプリまでを段階的に構築する。

## プロジェクト一覧

| # | プロジェクト | 内容 | 主な技術 | 状態 |
|---|------------|------|---------|------|
| 01 | [hello-spring](./01_hello-spring/) | Spring Boot 最小構成・REST API の基本 | Spring Boot | ✅ 完成 |
| 02 | [spring-jdbc](./02_spring-jdbc/) | Spring JDBC + H2 でフル CRUD API | Spring JDBC, H2 | ✅ 完成 |
| 03 | [factory-crud](./03_factory-crud/) | 工場系テーブル設計 + JOIN集計 + 本格 CRUD | Spring JDBC, H2, ER図 | ✅ 完成 |
| 04 | [spring-validation](./04_spring-validation/) | バリデーション・エラーハンドリング | Bean Validation | ✅ 完成 |
| 05 | [spring-batch](./05_spring-batch/) | 日次生産集計バッチ・CSV 出力 | Spring Batch | ✅ 完成 |
| 06 | [factory-app](./06_factory-app/) | 生産管理 総合 Web アプリ | Spring Boot, Spring Batch, H2 | ✅ 完成 |
| 07 | [docker-compose](./07_docker-compose/) | Docker + PostgreSQL 本番構成 | Docker, PostgreSQL | ✅ 完成 |

## 使用技術

| カテゴリ | 技術 |
|---------|------|
| 言語 | Java 21 |
| フレームワーク | Spring Boot 3.4.x |
| DB アクセス | Spring JDBC |
| DB | H2（開発・学習用） / PostgreSQL（本番想定） |
| ビルドツール | Apache Maven |
| バッチ | Spring Batch |
| コンテナ | Docker / Docker Compose |
| 実行環境 | Linux / WSL2 |

## クイックスタート

```bash
# 例: 02_spring-jdbc を起動する場合
cd 02_spring-jdbc
mvn spring-boot:run

# 動作確認
curl http://localhost:8080/productions

# 停止
Ctrl + C
```

```bash
# 例: 07_docker-compose（Docker 構成）を起動する場合
cd 07_docker-compose
docker compose up --build

# 動作確認
curl http://localhost:8080/api/productions/summary

# 停止
docker compose down
```

## 構成の流れ

```
01_hello-spring       → Spring Boot と REST API の基礎
        ↓
02_spring-jdbc        → SQL を書いて DB と連携する
        ↓
03_factory-crud       → 工場系のテーブル設計と SQL 集計
        ↓
04_spring-validation  → バリデーションとエラーハンドリング
        ↓
05_spring-batch       → バッチ処理・日次集計・CSV 出力
        ↓
06_factory-app        → 上記を組み合わせた総合アプリ
        ↓
07_docker-compose     → Docker + PostgreSQL による本番構成
```

## ドキュメント

| ドキュメント | 内容 |
|------------|------|
| [環境構築](./docs/setup.md) | Java / Maven のインストール手順 |
| [API 確認方法](./docs/api-testing.md) | curl / REST Client / H2コンソールの使い方 |
| [トラブルシューティング](./docs/troubleshooting.md) | ポート占有・404 エラーなどの解決方法 |
| [クラウドデプロイ](./docs/deployment.md) | Cloud Run / GCE / Supabase へのデプロイ手順 |

> 📝 各プロジェクトの詳細は個別の README を参照してください。
