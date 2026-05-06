# 03_factory-crud

工場系システムを想定した本格的な REST API サンプル。  
複数テーブルの設計・外部キー制約・JOIN による集計クエリを実装している。

## このサンプルで学べること

- 複数テーブルの設計（マスタ ↔ トランザクション）
- 外部キー制約（`FOREIGN KEY`）を使った整合性管理
- `JOIN` + `GROUP BY` による集計クエリ
- パッケージ（フォルダ）をドメインで分ける設計
- 不良率などの計算結果を API で返す方法

---

## API エンドポイント一覧

### 製品マスタ (`/products`)

| メソッド | パス | 説明 |
|---------|------|------|
| GET | `/products` | 製品一覧取得 |
| GET | `/products/{code}` | 製品1件取得 |
| POST | `/products` | 製品登録 |
| PUT | `/products/{code}` | 製品更新 |
| DELETE | `/products/{code}` | 製品削除 |

### 生産実績 (`/productions`)

| メソッド | パス | 説明 |
|---------|------|------|
| GET | `/productions` | 生産実績一覧取得 |
| GET | `/productions/{id}` | 生産実績1件取得 |
| GET | `/productions/summary` | ★ 製品別不良率集計 |
| POST | `/productions` | 生産実績登録 |
| DELETE | `/productions/{id}` | 生産実績削除 |

---

## テーブル設計

```
products（製品マスタ）          processes（工程マスタ）
┌─────────────────┐            ┌──────────────────┐
│ product_code PK │            │ process_code PK  │
│ product_name    │            │ process_name     │
│ unit            │            │ sort_order       │
└────────┬────────┘            └────────┬─────────┘
         │ FK                           │ FK
         ▼                             ▼
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

## プロジェクト構成

```
03_factory-crud/
├── pom.xml
└── src/main/
    ├── java/com/example/factory/
    │   ├── FactoryApplication.java        # アプリ起動クラス
    │   ├── product/                       # 製品マスタ 機能
    │   │   ├── Product.java               # データクラス（1件分）
    │   │   ├── ProductRepository.java     # DB アクセス（SQL を書く場所）
    │   │   └── ProductController.java     # REST API 定義
    │   └── production/                    # 生産実績 機能
    │       ├── ProductionResult.java      # データクラス（1件分）
    │       ├── ProductionSummary.java     # 集計結果用データクラス
    │       ├── ProductionResultRepository.java  # DB アクセス（集計SQL含む）
    │       └── ProductionResultController.java  # REST API 定義
    └── resources/
        ├── application.properties         # DB接続・H2コンソール設定
        ├── schema.sql                     # テーブル定義（起動時に自動実行）
        └── data.sql                       # 初期データ（起動時に自動実行）
```

---

## 各ファイルの説明

### `FactoryApplication.java`
アプリのエントリーポイント。`main()` メソッドがここにある。  
`@SpringBootApplication` を付けるだけで Spring が全クラスを自動検出する。

---

### `product/Product.java`
製品マスタ 1件分のデータを格納する **record クラス**。  
`productCode`（製品コード）/ `productName`（製品名）/ `unit`（単位）を持つ。  
Java 16 以降の `record` を使うことで getter・equals・toString が自動生成される。

---

### `product/ProductRepository.java`
製品マスタの DB アクセスを担当。`JdbcClient` を使って SQL を実行する。  
CRUD（全件取得 / 1件取得 / 登録 / 更新 / 削除）をすべて実装している。  
`:productCode` のようなバインド変数で SQLインジェクションを防いでいる。

---

### `product/ProductController.java`
製品マスタの REST API を定義するクラス。  
`@RestController` + `@RequestMapping("/products")` で `/products` 配下のURLを担当。  
`ProductRepository` をコンストラクタ経由で受け取る（依存性注入）。

---

### `production/ProductionResult.java`
生産実績 1件分のデータクラス。  
`id` / `resultDate` / `lineCode` / `productCode` / `processCode` /  
`productionQty`（生産数）/ `defectQty`（不良数）/ `operatorName`（作業者）を持つ。

---

### `production/ProductionSummary.java`
製品別の集計結果を格納するデータクラス。  
`productCode` / `productName` / `totalQty`（総生産数）/  
`totalDefects`（総不良数）/ `defectRate`（不良率 %）を持つ。  
`production_results` と `products` を JOIN した結果を格納するために使う。

---

### `production/ProductionResultRepository.java`
生産実績の DB アクセスを担当。このクラスの目玉は **`summarizeByProduct()`**。

```sql
-- JOIN + GROUP BY + 計算を組み合わせた集計クエリ
SELECT
    r.product_code,
    p.product_name,
    SUM(r.production_qty)  AS total_qty,
    SUM(r.defect_qty)      AS total_defects,
    ROUND(SUM(r.defect_qty) * 100.0 / SUM(r.production_qty), 2) AS defect_rate
FROM production_results r
JOIN products p ON r.product_code = p.product_code
GROUP BY r.product_code, p.product_name
ORDER BY defect_rate DESC
```

`production_results` と `products` を `JOIN` して製品名を取得し、  
`GROUP BY` で製品ごとに集計、不良率を計算して返す。

---

### `production/ProductionResultController.java`
生産実績の REST API を定義するクラス。  
`/productions/summary` エンドポイントが集計結果を返す目玉機能。

---

### `resources/schema.sql`
アプリ起動時に自動実行されるテーブル定義 SQL。  
`FOREIGN KEY` 制約で `production_results` → `products` / `processes` を紐付け。  
存在しない製品コード・工程コードは登録できないようにしている。

---

### `resources/data.sql`
起動時に自動投入されるサンプルデータ。  
製品マスタ 3件・工程マスタ 4件・生産実績 8件を投入する。

---

### `resources/application.properties`
アプリの設定ファイル。H2 インメモリ DB の接続設定と、  
起動時 SQL 自動実行（`spring.sql.init.mode=always`）を指定している。

---

## 起動方法

```bash
cd 03_factory-crud
mvn spring-boot:run
```

## 動作確認

```bash
# 製品マスタ一覧
curl http://localhost:8080/products

# 生産実績一覧
curl http://localhost:8080/productions

# ★ 製品別不良率集計
curl http://localhost:8080/productions/summary

# 製品登録
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{"productCode":"PART-004","productName":"ゴムパッキン","unit":"個"}'
```

## H2 コンソール（ブラウザでDBを確認）

起動後 → http://localhost:8080/h2-console

| 項目 | 値 |
|------|-----|
| JDBC URL | `jdbc:h2:mem:factorydb` |
| ユーザー名 | `sa` |
| パスワード | （空欄） |

---

## 02_spring-jdbc との違い

| 観点 | 02_spring-jdbc | 03_factory-crud |
|------|--------------|-----------------|
| テーブル数 | 1 | 3（マスタ + トランザクション） |
| 外部キー | なし | あり |
| SQL | 単純な CRUD | JOIN + GROUP BY 集計あり |
| パッケージ構成 | フラット | ドメイン別（product / production） |
| 集計 API | なし | `/productions/summary` |
