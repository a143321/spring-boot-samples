# 05_spring-batch

日次生産実績を集計して CSV ファイルに出力するバッチ処理サンプル。  
Spring Batch の基本構造（Job → Step → Reader/Writer）を工場系ユースケースで実装している。

## このサンプルで学べること

- Spring Batch の基本構造（Job / Step / ItemReader / ItemWriter）
- `JdbcCursorItemReader` で DB からデータを読み込む方法
- `FlatFileItemWriter` で CSV ファイルを出力する方法
- バッチを REST API（`POST /batch/run`）で手動起動する方法
- SQL の `GROUP BY` による日次集計クエリ

---

## API エンドポイント

| メソッド | パス | 説明 |
|---------|------|------|
| POST | `/batch/run` | バッチを手動実行・CSV を出力 |

---

## バッチ処理の流れ

```
POST /batch/run
    ↓
JobLauncher がジョブを起動
    ↓
Step: dailySummaryStep
    ↓                    ↓
ItemReader           ItemWriter
DB から集計 SQL      CSV ファイルへ書き出し
（GROUP BY）          output/daily_summary_*.csv
```

---

## 集計 SQL（重要ポイント）

```sql
SELECT
    FORMATDATETIME(result_date, 'yyyy-MM-dd') AS result_date,
    product_code,
    SUM(production_qty)  AS total_qty,
    SUM(defect_qty)      AS total_defects,
    ROUND(SUM(defect_qty) * 100.0 / SUM(production_qty), 2) AS defect_rate
FROM production_results
GROUP BY result_date, product_code
ORDER BY result_date, product_code
```

`result_date × product_code` の組み合わせで集計し、不良率を計算する。

---

## プロジェクト構成

```
05_spring-batch/
├── pom.xml
├── output/                                    # CSV 出力先（自動作成）
└── src/main/
    ├── java/com/example/batch/
    │   ├── BatchApplication.java              # アプリ起動クラス
    │   ├── BatchConfig.java                   # ★ Job / Step / Reader / Writer 定義
    │   ├── BatchController.java               # POST /batch/run エンドポイント
    │   └── DailySummaryItem.java              # 集計結果を格納するデータクラス
    └── resources/
        ├── application.properties             # DB・バッチ設定
        ├── schema.sql                         # テーブル定義
        └── data.sql                           # サンプルデータ（3日分）
```

---

## 各ファイルの説明

### `BatchConfig.java`
このサンプルの核となるクラス。Job / Step / Reader / Writer をすべて定義する。

**ItemReader**（`productionReader`）：DB から集計結果を1行ずつ読み込む。
```java
new JdbcCursorItemReaderBuilder<DailySummaryItem>()
    .sql("SELECT ... GROUP BY result_date, product_code ...")
    .rowMapper(...)
```

**ItemWriter**（`csvWriter`）：CSV ファイルへ書き出す。ヘッダー行も自動付与。
```java
new FlatFileItemWriterBuilder<DailySummaryItem>()
    .delimited()
    .delimiter(",")
    .names("resultDate", "productCode", "totalQty", "totalDefects", "defectRate")
    .headerCallback(writer -> writer.write("日付,製品コード,総生産数,不良数,不良率(%)"))
```

**Step**：Reader と Writer を `chunk(10)` でつなぐ。10件単位でコミット。

**Job**：Step を実行するジョブ定義。

---

### `BatchController.java`
`POST /batch/run` を受け取って `JobLauncher` でジョブを起動するコントローラー。  
毎回ユニークな `run.id` パラメータを付与することで同じジョブを再実行できる。

---

### `DailySummaryItem.java`
DB から読み込んだ集計結果 1行分を格納するデータクラス。  
`FlatFileItemWriter` が getter を使って値を取り出すため、record ではなく通常クラスで実装している。

---

## テーブル設計

```
production_results（生産実績）     daily_summary（日次集計結果）
┌──────────────────────────┐      ┌──────────────────────────┐
│ id            PK         │      │ id            PK         │
│ result_date              │  →   │ result_date              │
│ line_code                │ 集計  │ product_code             │
│ product_code             │      │ total_qty                │
│ production_qty           │      │ total_defects            │
│ defect_qty               │      │ defect_rate              │
└──────────────────────────┘      └──────────────────────────┘
```

※ `daily_summary` テーブルは今回のバッチでは使用せず、CSV に直接出力している。

---

## 起動方法

```bash
cd 05_spring-batch
mvn spring-boot:run
```

## 動作確認

```bash
# バッチ実行（CSV が output/ に出力される）
curl -X POST http://localhost:8080/batch/run

# 出力ファイルを確認
ls output/
cat output/daily_summary_*.csv
```

**出力 CSV の例：**

```
日付,製品コード,総生産数,不良数,不良率(%)
2026-05-01,PART-001,700,4,0.57
2026-05-01,PART-002,300,5,1.67
2026-05-02,PART-001,480,2,0.42
2026-05-02,PART-002,320,0,0.0
2026-05-02,PART-003,200,10,5.0
2026-05-03,PART-001,510,4,0.78
2026-05-03,PART-003,190,2,1.05
```

## H2 コンソール（入力データを確認）

起動後 → http://localhost:8080/h2-console

| 項目 | 値 |
|------|-----|
| JDBC URL | `jdbc:h2:mem:batchdb` |
| ユーザー名 | `sa` |
| パスワード | （空欄） |

```sql
-- 生産実績を確認
SELECT * FROM PRODUCTION_RESULTS;

-- バッチ実行後の集計イメージを SQL で確認
SELECT result_date, product_code,
       SUM(production_qty), SUM(defect_qty)
FROM PRODUCTION_RESULTS
GROUP BY result_date, product_code;
```

---

## 04_spring-validation との違い

| 観点 | 04_spring-validation | 05_spring-batch |
|------|---------------------|----------------|
| 処理のきっかけ | HTTP リクエスト（常時受付） | バッチ起動（定期・手動） |
| 出力先 | JSON レスポンス | CSV ファイル |
| 処理量 | 1件ずつ | まとめて大量処理 |
| 用途 | リアルタイム登録 | 集計・帳票出力 |
