# 04_spring-validation

リクエストのバリデーション（入力値検証）とエラーハンドリングのサンプル。  
不正なリクエストを弾いて、統一フォーマットのエラー JSON を返す。

## このサンプルで学べること

- `@NotNull` / `@NotBlank` / `@Min` / `@Max` でリクエストを検証する方法
- バリデーションエラー時に **400 Bad Request** + JSON を返す方法
- `@RestControllerAdvice` でエラーハンドリングを一か所にまとめる方法

---

## API エンドポイント

| メソッド | パス | 説明 |
|---------|------|------|
| POST | `/productions` | 生産実績登録（バリデーション付き） |

---

## バリデーションルール

| フィールド | ルール | エラーメッセージ |
|-----------|--------|----------------|
| `resultDate` | 必須（null 不可） | 日付は必須です |
| `lineCode` | 必須（空文字不可） | ラインコードは必須です |
| `productCode` | 必須（空文字不可） | 製品コードは必須です |
| `productionQty` | 1以上 | 生産数は1以上にしてください |
| `defectQty` | 0以上・99999以下 | 不良数は0以上にしてください |

---

## プロジェクト構成

```
04_spring-validation/
├── pom.xml
└── src/main/java/com/example/validation/
    ├── ValidationApplication.java          # アプリ起動クラス
    ├── GlobalExceptionHandler.java         # ★ エラーハンドリング（全体）
    └── production/
        ├── ProductionRequest.java          # ★ バリデーション付きリクエストクラス
        └── ProductionController.java       # REST API 定義
```

---

## 各ファイルの説明

### `ProductionRequest.java`
リクエスト JSON を受け取るクラス。  
各フィールドにバリデーションアノテーションを付けるだけで検証が自動で行われる。

```java
@NotNull(message = "日付は必須です")
LocalDate resultDate,

@Min(value = 1, message = "生産数は1以上にしてください")
int productionQty,
```

---

### `ProductionController.java`
`@Valid` を付けるだけでバリデーションが実行される。  
エラー時は `GlobalExceptionHandler` が自動で処理する。

```java
public ResponseEntity<?> create(@RequestBody @Valid ProductionRequest request)
//                                               ↑ これだけでOK
```

---

### `GlobalExceptionHandler.java`
**アプリ全体のエラーを一か所で管理するクラス。**  
`@RestControllerAdvice` を付けることで、どのコントローラーのエラーもここで捕まえられる。

バリデーションエラー時のレスポンス例：
```json
{
  "status": 400,
  "error": "Validation Failed",
  "messages": [
    "productionQty: 生産数は1以上にしてください",
    "lineCode: ラインコードは必須です"
  ]
}
```

---

## 起動方法

```bash
cd 04_spring-validation
mvn spring-boot:run
```

## 動作確認：正常系（DB に保存される）

```bash
# ✅ 正常①
curl -X POST http://localhost:8080/productions \
  -H "Content-Type: application/json" \
  -d '{"resultDate":"2026-05-01","lineCode":"LINE-A","productCode":"PART-001","productionQty":500,"defectQty":3}'
# → {"message":"登録しました"}

# ✅ 正常②
curl -X POST http://localhost:8080/productions \
  -H "Content-Type: application/json" \
  -d '{"resultDate":"2026-05-02","lineCode":"LINE-B","productCode":"PART-002","productionQty":300,"defectQty":5}'
# → {"message":"登録しました"}
```

---

## 動作確認：異常系（DB に保存されない）

バリデーションエラーになったリクエストは **DB への INSERT が実行されない**。

```bash
# ❌ 異常①: 生産数が 0
curl -X POST http://localhost:8080/productions \
  -H "Content-Type: application/json" \
  -d '{"resultDate":"2026-05-01","lineCode":"LINE-A","productCode":"PART-001","productionQty":0,"defectQty":3}'
# → {"status":400,"error":"Validation Failed","messages":["productionQty: 生産数は1以上にしてください"]}

# ❌ 異常②: 必須項目が空（複数エラーが同時に返る）
curl -X POST http://localhost:8080/productions \
  -H "Content-Type: application/json" \
  -d '{"resultDate":null,"lineCode":"","productCode":"PART-001","productionQty":500,"defectQty":3}'
# → {"status":400,"error":"Validation Failed","messages":["resultDate: 日付は必須です","lineCode: ラインコードは必須です"]}

# ❌ 異常③: 不良数がマイナス
curl -X POST http://localhost:8080/productions \
  -H "Content-Type: application/json" \
  -d '{"resultDate":"2026-05-01","lineCode":"LINE-A","productCode":"PART-001","productionQty":100,"defectQty":-1}'
# → {"status":400,"error":"Validation Failed","messages":["defectQty: 不良数は0以上にしてください"]}
```

---

## 保存結果の確認方法

### 方法① curl で一覧取得

```bash
curl http://localhost:8080/productions
```

正常系のみ保存されていることを確認できる。

### 方法② H2 コンソール（ブラウザで SQL を確認）

起動後 → http://localhost:8080/h2-console

| 項目 | 値 |
|------|-----|
| JDBC URL | `jdbc:h2:mem:validationdb` |
| ユーザー名 | `sa` |
| パスワード | （空欄） |

ログイン後に実行：
```sql
SELECT * FROM PRODUCTION_RESULTS;
```

→ バリデーションエラーになったデータは **一切含まれていない**ことが確認できる。

### 方法③ VS Code REST Client（`requests.http`）

```
requests.http を開く → 「Send Request」をクリックするだけ
```

---

## なぜバリデーションエラー時に保存されないか

```
POST /productions
    ↓
@Valid がリクエストを検証
    ↓ NG → GlobalExceptionHandler が 400 を返す（ここで終わり）
    ↓ OK のときだけ続く
repository.insert() → DB に保存
```

コントローラーの `insert()` はバリデーション通過後にしか呼ばれないため、  
エラーになったデータが DB に混入することはない。

---

## 03_factory-crud との違い

| 観点 | 03_factory-crud | 04_spring-validation |
|------|----------------|----------------------|
| バリデーション | なし | `@NotNull` / `@Min` 等で検証 |
| エラー処理 | Spring デフォルト | `GlobalExceptionHandler` で統一 |
| エラーレスポンス | バラバラ | JSON で統一フォーマット |
| DB 保存 | 常に実行 | バリデーション通過後のみ |

