# spring-jdbc

Spring Boot + Spring JDBC + H2（インメモリDB）で  
生産実績データの REST API を作るサンプル。

SQL を直接書いてDBと連携する基本パターンを学ぶプロジェクト。

## 概要

| 項目 | 内容 |
|------|------|
| フレームワーク | Spring Boot 3.4.5 |
| 言語 | Java 21 |
| DB アクセス | Spring JDBC（JdbcClient） |
| DB | H2（インメモリ）|
| ビルドツール | Maven |

## エンドポイント

| メソッド | パス | 説明 |
|---------|------|------|
| GET | `/productions` | 生産実績 一覧取得 |
| GET | `/productions/{id}` | 生産実績 1件取得 |
| POST | `/productions` | 生産実績 登録 |
| PUT | `/productions/{id}` | 生産実績 更新 |
| DELETE | `/productions/{id}` | 生産実績 削除 |
| GET | `/h2-console` | H2 DB コンソール（ブラウザ） |

## 起動方法

```bash
cd spring-jdbc
mvn spring-boot:run
```

## 動作確認

### 一覧取得
```bash
curl http://localhost:8080/productions
```

### 1件取得
```bash
curl http://localhost:8080/productions/1
```

### 登録（JSON を POST）
```bash
curl -X POST http://localhost:8080/productions \
  -H "Content-Type: application/json" \
  -d '{"lineCode":"LINE-C","productCode":"PART-005","productionQty":100,"defectQty":2,"resultDate":"2026-05-04"}'
```

### 更新（JSON を PUT）
```bash
curl -X PUT http://localhost:8080/productions/1 \
  -H "Content-Type: application/json" \
  -d '{"lineCode":"LINE-A","productCode":"PART-001","productionQty":600,"defectQty":1,"resultDate":"2026-05-01"}'
```

### 削除（DELETE）
```bash
curl -X DELETE http://localhost:8080/productions/2
```

## H2 コンソール（ブラウザでDBを確認）

起動後に → http://localhost:8080/h2-console

| 項目 | 値 |
|------|-----|
| JDBC URL | `jdbc:h2:mem:productiondb` |
| ユーザー名 | `sa` |
| パスワード | （空欄） |

## プロジェクト構成

```
spring-jdbc/
├── pom.xml
└── src/main/
    ├── java/com/example/jdbc/
    │   ├── SpringJdbcApplication.java      # 起動クラス
    │   ├── ProductionResult.java           # データクラス（1行分の入れ物）
    │   ├── ProductionResultRepository.java # DB アクセス層（SQL を書く場所）
    │   └── ProductionResultController.java # REST API 定義
    └── resources/
        ├── application.properties          # DB 設定
        ├── schema.sql                      # テーブル定義（起動時自動実行）
        └── data.sql                        # 初期データ（起動時自動投入）
```

## コードの層構造（3層アーキテクチャ）

```
リクエスト
    ↓
Controller（API の入り口）
    ↓
Repository（SQL を書いて DB と話す）
    ↓
H2 Database
```

## 前のプロジェクト / 次のステップ

- ← [hello-spring](../hello-spring/README.md)：Spring Boot の最小構成
- → 次：MyBatis を使ってより本格的な SQL マッピングを学ぶ
