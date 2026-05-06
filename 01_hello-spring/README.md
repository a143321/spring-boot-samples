# hello-spring

Spring Boot の入門プロジェクト。  
Java + Spring Boot で REST API を作る最小構成サンプル。

## 概要

| 項目 | 内容 |
|------|------|
| フレームワーク | Spring Boot 3.4.5 |
| 言語 | Java 21 |
| ビルドツール | Maven 3.8.x |
| サーバー | 組み込み Tomcat（ポート 8080） |

## エンドポイント

| メソッド | パス | 説明 |
|---------|------|------|
| GET | `/` | 起動確認 |
| GET | `/hello` | 挨拶メッセージ（デフォルト: World） |
| GET | `/hello?name=田中` | 名前を指定した挨拶 |

## 起動方法

```bash
# プロジェクトルートで実行
mvn spring-boot:run
```

## 動作確認

```bash
curl http://localhost:8080/
curl http://localhost:8080/hello
curl "http://localhost:8080/hello?name=田中"
```

## プロジェクト構成

```
hello-spring/
├── pom.xml                          # Maven 設定（依存ライブラリ管理）
└── src/
    └── main/
        ├── java/com/example/hello/
        │   ├── HelloSpringApplication.java  # アプリ起動クラス（エントリーポイント）
        │   └── HelloController.java         # REST API コントローラー
        └── resources/
            └── application.properties       # アプリ設定ファイル
```

## コードのポイント

### アノテーション（`@` で始まるもの）

```java
@SpringBootApplication  // これがアプリの起点
@RestController         // Web API のクラスに付ける
@GetMapping("/hello")   // GETリクエストのルーティング
```

Python の Flask と比較すると：

```python
# Flask
@app.route('/hello')
def hello(): ...

# Spring Boot（同じ発想）
@GetMapping("/hello")
public String hello() { ... }
```

## 次のステップ

→ [spring-jdbc](../spring-jdbc/README.md)：H2 インメモリ DB に接続して SQL を実行するサンプル
