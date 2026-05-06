-- =============================================
-- 工場系テーブル設計サンプル
-- 製品マスタ → 生産実績 の親子関係
-- =============================================

-- 製品マスタ
CREATE TABLE products (
    product_code  VARCHAR(20) PRIMARY KEY,          -- 製品コード（主キー）
    product_name  VARCHAR(100) NOT NULL,            -- 製品名
    unit          VARCHAR(10)  NOT NULL DEFAULT '個', -- 単位
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 工程マスタ
CREATE TABLE processes (
    process_code  VARCHAR(10) PRIMARY KEY,          -- 工程コード
    process_name  VARCHAR(100) NOT NULL,            -- 工程名
    sort_order    INT          NOT NULL DEFAULT 0   -- 表示順
);

-- 生産実績（製品マスタ・工程マスタを参照）
CREATE TABLE production_results (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    result_date     DATE        NOT NULL,            -- 実績日
    line_code       VARCHAR(10) NOT NULL,            -- ラインコード
    product_code    VARCHAR(20) NOT NULL,            -- 製品コード（FK）
    process_code    VARCHAR(10) NOT NULL,            -- 工程コード（FK）
    production_qty  INT         NOT NULL,            -- 生産数量
    defect_qty      INT         NOT NULL DEFAULT 0,  -- 不良数量
    operator_name   VARCHAR(50),                     -- 作業者名
    FOREIGN KEY (product_code) REFERENCES products(product_code),
    FOREIGN KEY (process_code) REFERENCES processes(process_code)
);
