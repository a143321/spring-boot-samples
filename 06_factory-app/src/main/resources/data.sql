-- 製品マスタ
INSERT INTO products (product_code, product_name, unit) VALUES
    ('PART-001', 'アルミフレーム',       '個'),
    ('PART-002', 'スチールブラケット',   '個'),
    ('PART-003', 'プラスチックカバー',   '個');

-- 工程マスタ
INSERT INTO processes (process_code, process_name, sort_order) VALUES
    ('PROC-01', '組立',  1),
    ('PROC-02', '加工',  2),
    ('PROC-03', '検査',  3),
    ('PROC-04', '梱包',  4);

-- 生産実績
INSERT INTO production_results (result_date, line_code, product_code, process_code, production_qty, defect_qty, operator_name) VALUES
    ('2026-05-01', 'LINE-A', 'PART-001', 'PROC-01', 500, 3,  '田中'),
    ('2026-05-01', 'LINE-A', 'PART-001', 'PROC-02', 200, 1,  '田中'),
    ('2026-05-01', 'LINE-B', 'PART-002', 'PROC-01', 300, 5,  '鈴木'),
    ('2026-05-02', 'LINE-A', 'PART-001', 'PROC-01', 480, 2,  '田中'),
    ('2026-05-02', 'LINE-B', 'PART-002', 'PROC-02', 320, 0,  '佐藤'),
    ('2026-05-02', 'LINE-C', 'PART-003', 'PROC-01', 200, 10, '鈴木'),
    ('2026-05-03', 'LINE-A', 'PART-001', 'PROC-03', 510, 4,  '田中'),
    ('2026-05-03', 'LINE-C', 'PART-003', 'PROC-02', 190, 2,  '佐藤');
