-- 生産実績テーブル
CREATE TABLE production_results (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    result_date     DATE        NOT NULL,
    line_code       VARCHAR(10) NOT NULL,
    product_code    VARCHAR(20) NOT NULL,
    production_qty  INT         NOT NULL,
    defect_qty      INT         NOT NULL DEFAULT 0
);

-- バッチ集計結果テーブル（日次サマリー）
CREATE TABLE daily_summary (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    result_date     DATE        NOT NULL,
    product_code    VARCHAR(20) NOT NULL,
    total_qty       INT         NOT NULL,
    total_defects   INT         NOT NULL,
    defect_rate     DOUBLE      NOT NULL,
    created_at      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);
