# 環境構築ガイド（WSL2 / Ubuntu）

## 1. Java 21 インストール

```bash
sudo apt update
sudo apt install -y openjdk-21-jdk

# バージョン確認
java --version
# → openjdk 21.0.x ...
```

## 2. Maven インストール

```bash
sudo apt install -y maven

# バージョン確認
mvn --version
# → Apache Maven 3.8.x ...
```

## 3. 動作確認

```bash
java --version
mvn --version
```

両方表示されれば環境構築完了。

---

## 各プロジェクトの起動方法

```bash
# プロジェクトディレクトリに移動して起動
cd 02_spring-jdbc
mvn spring-boot:run

# アプリ停止
Ctrl + C
```

> ⚠️ `mvn` コマンドは必ず各プロジェクトの `pom.xml` があるディレクトリで実行すること。

---

## 対象とする業務領域

工場・製造業の基幹システムでよく出てくる以下のユースケースをカバーする。

- 生産実績の登録・検索・集計
- 工程・製品・ライン別の不良率レポート
- 日次・月次バッチ集計
- CSV / 帳票出力
