-- PostgreSQL 構文（H2 との違い: AUTO_INCREMENT → SERIAL、型の違いなど）

CREATE TABLE IF NOT EXISTS products (
    product_code  VARCHAR(20)  NOT NULL PRIMARY KEY,
    product_name  VARCHAR(100) NOT NULL,
    unit          VARCHAR(10)  NOT NULL,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS processes (
    process_code  VARCHAR(20)  NOT NULL PRIMARY KEY,
    process_name  VARCHAR(100) NOT NULL,
    sort_order    INT          NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS production_results (
    id              SERIAL       PRIMARY KEY,
    result_date     DATE         NOT NULL,
    line_code       VARCHAR(10)  NOT NULL,
    product_code    VARCHAR(20)  NOT NULL REFERENCES products(product_code),
    process_code    VARCHAR(20)  NOT NULL REFERENCES processes(process_code),
    production_qty  INT          NOT NULL,
    defect_qty      INT          NOT NULL DEFAULT 0,
    operator_name   VARCHAR(50)
);
