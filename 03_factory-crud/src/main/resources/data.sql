-- 製品マスタ
INSERT INTO products (product_code, product_name, unit) VALUES
    ('PART-001', 'アルミフレーム A型', '本'),
    ('PART-002', 'スチールブラケット', '個'),
    ('PART-003', 'プラスチックカバー', '枚');

-- 工程マスタ
INSERT INTO processes (process_code, process_name, sort_order) VALUES
    ('PROC-01', '材料投入', 1),
    ('PROC-02', '加工',     2),
    ('PROC-03', '検査',     3),
    ('PROC-04', '梱包',     4);

-- 生産実績
INSERT INTO production_results (result_date, line_code, product_code, process_code, production_qty, defect_qty, operator_name) VALUES
    ('2026-05-01', 'LINE-A', 'PART-001', 'PROC-02', 500, 3,  '山田'),
    ('2026-05-01', 'LINE-A', 'PART-001', 'PROC-03', 497, 1,  '佐藤'),
    ('2026-05-01', 'LINE-B', 'PART-002', 'PROC-02', 300, 5,  '田中'),
    ('2026-05-02', 'LINE-A', 'PART-001', 'PROC-02', 480, 2,  '山田'),
    ('2026-05-02', 'LINE-B', 'PART-002', 'PROC-02', 320, 0,  '田中'),
    ('2026-05-02', 'LINE-C', 'PART-003', 'PROC-02', 200, 10, '鈴木'),
    ('2026-05-03', 'LINE-A', 'PART-001', 'PROC-03', 478, 4,  '佐藤'),
    ('2026-05-03', 'LINE-C', 'PART-003', 'PROC-03', 190, 2,  '鈴木');
