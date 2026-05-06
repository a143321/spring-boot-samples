-- 生産実績テーブル
CREATE TABLE production_results (
    id          INT AUTO_INCREMENT PRIMARY KEY,  -- 自動採番
    line_code   VARCHAR(10)  NOT NULL,           -- ライン番号（例: LINE-A）
    product_code VARCHAR(20) NOT NULL,           -- 製品コード
    production_qty INT       NOT NULL,           -- 生産数量
    defect_qty  INT          NOT NULL DEFAULT 0, -- 不良数量
    result_date DATE         NOT NULL            -- 実績日
);
