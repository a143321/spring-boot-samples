# ── ステージ1: ビルド ──────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -q

COPY src ./src
RUN mvn package -DskipTests -q

# ── ステージ2: 実行 ──────────────────────────────────────
# Cloud Run は PORT 環境変数でポートを指定する
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN mkdir -p output

COPY --from=builder /app/target/*.jar app.jar

# Cloud Run が使用するポート（デフォルト 8080）
EXPOSE 8080

# Cloud Run の PORT 環境変数に対応
ENTRYPOINT ["java", "-jar", "-Dserver.port=${PORT:-8080}", "app.jar"]
