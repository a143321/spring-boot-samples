# API 確認方法

REST API（POST/PUT/DELETE）はブラウザの URL バーだけでは確認できない。  
以下のツールを用途に応じて使い分ける。

---

## ツール比較

| ツール | 種別 | 特徴 |
|--------|------|------|
| **curl** | CLI | 導入不要。各 README のコマンドをコピペで即実行 |
| **Bruno** | GUI | コレクション保存可能。Postman に近い操作感。無料 |
| **Postman** | GUI | 業界標準の API テストツール |
| **VS Code REST Client** | 拡張 | `.http` ファイルを書くだけでリクエストできる |

> 💡 フロントを作らないバックエンド開発では **curl** または **VS Code REST Client** が手軽。

---

## curl の使い方

```bash
# GET（一覧取得）
curl http://localhost:8080/productions

# POST（登録）
curl -X POST http://localhost:8080/productions \
  -H "Content-Type: application/json" \
  -d '{"resultDate":"2026-05-01","lineCode":"LINE-A","productCode":"PART-001","productionQty":500,"defectQty":3}'

# PUT（更新）
curl -X PUT http://localhost:8080/productions/1 \
  -H "Content-Type: application/json" \
  -d '{"resultDate":"2026-05-01","lineCode":"LINE-A","productCode":"PART-001","productionQty":600,"defectQty":1}'

# DELETE（削除）
curl -X DELETE http://localhost:8080/productions/1
```

### curl オプション早見表

| オプション | 意味 |
|-----------|------|
| `-X POST` | HTTP メソッドを指定（省略時は GET） |
| `-H "..."` | HTTP ヘッダーを追加（`Content-Type: application/json` など） |
| `-d '...'` | リクエストボディ（送るデータ）を指定 |

---

## VS Code REST Client の使い方

1. 拡張機能（`Ctrl+Shift+X`）で **REST Client** を検索・インストール
2. 拡張子 `.http` のファイルを作成
3. 各プロジェクトの `requests.http` を開く
4. リクエストの上に表示される **「Send Request」** をクリックするだけ

```http
### GET 一覧
GET http://localhost:8080/productions

### POST 登録
POST http://localhost:8080/productions
Content-Type: application/json

{
  "resultDate": "2026-05-01",
  "lineCode": "LINE-A",
  "productCode": "PART-001",
  "productionQty": 500,
  "defectQty": 3
}
```

---

## H2 コンソール（DB の中身を確認）

H2 対応プロジェクト（02 / 03 / 04 / 05）では起動後にブラウザで DB を確認できる。

```
http://localhost:8080/h2-console
```

| プロジェクト | JDBC URL |
|-------------|---------|
| 02_spring-jdbc | `jdbc:h2:mem:productiondb` |
| 03_factory-crud | `jdbc:h2:mem:factorydb` |
| 04_spring-validation | `jdbc:h2:mem:validationdb` |
| 05_spring-batch | `jdbc:h2:mem:batchdb` |

ユーザー名: `sa` / パスワード: （空欄）
