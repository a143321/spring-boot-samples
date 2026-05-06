-- 製品マスタ
CREATE TABLE products (
    product_code  VARCHAR(20)  NOT NULL PRIMARY KEY,
    product_name  VARCHAR(100) NOT NULL,
    unit          VARCHAR(10)  NOT NULL,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 工程マスタ
CREATE TABLE processes (
    process_code  VARCHAR(20)  NOT NULL PRIMARY KEY,
    process_name  VARCHAR(100) NOT NULL,
    sort_order    INT          NOT NULL DEFAULT 0
);

-- 生産実績（製品・工程に対する外部キー制約あり）
CREATE TABLE production_results (
    id              BIGINT       AUTO_INCREMENT PRIMARY KEY,
    result_date     DATE         NOT NULL,
    line_code       VARCHAR(10)  NOT NULL,
    product_code    VARCHAR(20)  NOT NULL,
    process_code    VARCHAR(20)  NOT NULL,
    production_qty  INT          NOT NULL,
    defect_qty      INT          NOT NULL DEFAULT 0,
    operator_name   VARCHAR(50),
    FOREIGN KEY (product_code) REFERENCES products(product_code),
    FOREIGN KEY (process_code) REFERENCES processes(process_code)
);
