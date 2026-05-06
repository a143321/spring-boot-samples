# ── ステージ1: ビルド ──────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# pom.xml だけ先にコピーして依存をキャッシュ
COPY pom.xml .
RUN mvn dependency:go-offline -q

# ソースをコピーしてビルド
COPY src ./src
RUN mvn package -DskipTests -q

# ── ステージ2: 実行 ──────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# ビルド済み JAR だけコピー
COPY --from=builder /app/target/*.jar app.jar

# CSV 出力フォルダ
RUN mkdir -p output

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
