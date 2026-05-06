# トラブルシューティング

## ポート 8080 が既に使用されている

`mvn spring-boot:run` を実行したとき、以下のエラーが出る場合：

```
Web server failed to start. Port 8080 was already in use.
```

**原因：** 前回のサーバーが Ctrl+C で止まらずにバックグラウンドで残っている。  
ターミナルを閉じた場合もプロセスは内部で動き続ける。

**解決方法：**

```bash
# ① 占有しているプロセスを確認
lsof -i :8080

# ② PID が表示されたら強制終了（例: PID が 12345 の場合）
kill -9 12345

# または一発で解放（確認なしで強制終了）
fuser -k 8080/tcp
```

---

## `404 Not Found` が返る

**原因：** 別のプロジェクトのサーバーが起動したまま、違うエンドポイントを叩いている。

**確認方法：**

```bash
# 今どのプロセスが 8080 を使っているか確認
lsof -i :8080
```

**解決方法：** 目的のプロジェクトを起動し直す（必要なら先に `fuser -k 8080/tcp` でリセット）。

---

## `mvn` コマンドが見つからない

```
mvn: command not found
```

Maven がインストールされていない。[setup.md](./setup.md) を参照してインストールする。

---

## H2 コンソールが表示されない

`application.properties` に以下が設定されているか確認する：

```properties
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.sql.init.mode=always
```

設定済みの場合は → http://localhost:8080/h2-console にアクセス。
