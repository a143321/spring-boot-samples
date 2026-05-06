CREATE TABLE production_results (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    result_date     DATE        NOT NULL,
    line_code       VARCHAR(10) NOT NULL,
    product_code    VARCHAR(20) NOT NULL,
    production_qty  INT         NOT NULL,
    defect_qty      INT         NOT NULL DEFAULT 0
);
