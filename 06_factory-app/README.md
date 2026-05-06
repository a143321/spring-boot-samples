# 06_factory-app

01〜05 のサンプルを統合した**工場系生産管理 REST API の総合アプリ**。  
製品マスタ管理・生産実績 CRUD・集計 API・CSV バッチ出力をすべて備える。

## このサンプルで学べること

- 複数パッケージ（ドメイン）に機能を整理した実務的な設計
- バリデーション付き CRUD（`@Valid` + `GlobalExceptionHandler`）
- 外部キー制約を持つ複数テーブルの JOIN 集計
- Spring Batch による CSV 出力バッチの REST API 起動
- 01〜05 の技術を1つのアプリに統合する方法

---

## パッケージ構成

```
com.example.factoryapp/
├── FactoryAppApplication.java     # 起動クラス
├── common/
│   └── GlobalExceptionHandler.java  # 全体エラーハンドリング
├── product/                          # 製品マスタ機能
│   ├── Product.java
│   ├── ProductRequest.java
│   ├── ProductRepository.java
│   └── ProductController.java
├── production/                       # 生産実績機能
│   ├── ProductionResult.java
│   ├── ProductionRequest.java
│   ├── ProductionSummary.java
│   ├── ProductionRepository.java
│   └── ProductionController.java
└── batch/                            # CSV 出力バッチ
    ├── DailySummaryItem.java
    ├── BatchConfig.java
    └── BatchController.java
```

---

## テーブル設計（ER 図）

```
products（製品マスタ）          processes（工程マスタ）
┌──────────────────┐            ┌──────────────────┐
│ product_code PK  │            │ process_code PK  │
│ product_name     │            │ process_name     │
│ unit             │            │ sort_order       │
└────────┬─────────┘            └────────┬─────────┘
         │ FK                            │ FK
         ▼                              ▼
         production_results（生産実績）
         ┌──────────────────────────────┐
         │ id            PK AUTO       │
         │ result_date                 │
         │ line_code                   │
         │ product_code  → products    │
         │ process_code  → processes   │
         │ production_qty              │
         │ defect_qty                  │
         │ operator_name               │
         └──────────────────────────────┘
```

---

## API エンドポイント一覧

### 製品マスタ（`/api/products`）

| メソッド | パス | 説明 |
|---------|------|------|
| GET | `/api/products` | 製品一覧取得 |
| GET | `/api/products/{code}` | 製品1件取得 |
| POST | `/api/products` | 製品登録（バリデーション付き） |
| PUT | `/api/products/{code}` | 製品更新（バリデーション付き） |
| DELETE | `/api/products/{code}` | 製品削除 |

### 生産実績（`/api/productions`）

| メソッド | パス | 説明 |
|---------|------|------|
| GET | `/api/productions` | 生産実績一覧 |
| GET | `/api/productions/{id}` | 生産実績1件 |
| GET | `/api/productions/summary` | ★ 製品別不良率集計 |
| POST | `/api/productions` | 生産実績登録（バリデーション付き） |
| DELETE | `/api/productions/{id}` | 生産実績削除 |

### バッチ（`/api/batch`）

| メソッド | パス | 説明 |
|---------|------|------|
| POST | `/api/batch/run` | ★ 日次集計 CSV を出力 |

---

## 起動方法

```bash
cd 06_factory-app
mvn spring-boot:run
```

---

## 動作確認

### 製品マスタ

```bash
# 一覧取得
curl http://localhost:8080/api/products

# 1件取得
curl http://localhost:8080/api/products/PART-001

# 登録
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"productCode":"PART-004","productName":"ゴムパッキン","unit":"個"}'

# 更新
curl -X PUT http://localhost:8080/api/products/PART-004 \
  -H "Content-Type: application/json" \
  -d '{"productCode":"PART-004","productName":"ゴムパッキンA","unit":"個"}'

# 削除
curl -X DELETE http://localhost:8080/api/products/PART-004
```

### 生産実績

```bash
# 一覧取得
curl http://localhost:8080/api/productions

# ★ 製品別不良率集計（JOIN + GROUP BY）
curl http://localhost:8080/api/productions/summary

# 登録
curl -X POST http://localhost:8080/api/productions \
  -H "Content-Type: application/json" \
  -d '{"resultDate":"2026-05-06","lineCode":"LINE-A","productCode":"PART-001","processCode":"PROC-01","productionQty":500,"defectQty":2,"operatorName":"田中"}'

# 削除
curl -X DELETE http://localhost:8080/api/productions/1
```

### バリデーションエラー確認

```bash
# ❌ 必須項目が空 → 400 Bad Request
curl -X POST http://localhost:8080/api/productions \
  -H "Content-Type: application/json" \
  -d '{"resultDate":null,"lineCode":"","productCode":"PART-001","processCode":"PROC-01","productionQty":0,"defectQty":0}'
# → {"status":400,"error":"Validation Failed","messages":["resultDate: 日付は必須です",...]}
```

### CSV バッチ出力

```bash
# バッチ実行
curl -X POST http://localhost:8080/api/batch/run

# 出力ファイル確認
ls output/
cat output/daily_summary_*.csv
```

**出力 CSV の例：**
```
日付,製品コード,総生産数,不良数,不良率(%)
2026-05-01,PART-001,700,4,0.57
2026-05-01,PART-002,300,5,1.67
2026-05-02,PART-003,200,10,5.0
```

---

## H2 コンソール（DB 確認）

起動後 → http://localhost:8080/h2-console

| 項目 | 値 |
|------|-----|
| JDBC URL | `jdbc:h2:mem:factoryappdb` |
| ユーザー名 | `sa` |
| パスワード | （空欄） |

```sql
-- 製品マスタ確認
SELECT * FROM PRODUCTS;

-- 生産実績確認
SELECT * FROM PRODUCTION_RESULTS;

-- 不良率集計（集計 API と同じクエリ）
SELECT r.product_code, p.product_name,
       SUM(r.production_qty) AS total_qty,
       SUM(r.defect_qty) AS total_defects,
       ROUND(SUM(r.defect_qty) * 100.0 / SUM(r.production_qty), 2) AS defect_rate
FROM production_results r
JOIN products p ON r.product_code = p.product_code
GROUP BY r.product_code, p.product_name
ORDER BY defect_rate DESC;
```

---

## 01〜05 との対応関係

| 機能 | 参照元サンプル |
|------|------------|
| REST API の基本構造 | 01_hello-spring |
| JdbcClient による CRUD | 02_spring-jdbc |
| 複数テーブル設計・JOIN 集計 | 03_factory-crud |
| バリデーション・エラーハンドリング | 04_spring-validation |
| Spring Batch・CSV 出力 | 05_spring-batch |
