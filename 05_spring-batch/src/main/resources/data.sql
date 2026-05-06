-- 生産実績 サンプルデータ（3日分）
INSERT INTO production_results (result_date, line_code, product_code, production_qty, defect_qty) VALUES
    ('2026-05-01', 'LINE-A', 'PART-001', 500, 3),
    ('2026-05-01', 'LINE-A', 'PART-001', 200, 1),
    ('2026-05-01', 'LINE-B', 'PART-002', 300, 5),
    ('2026-05-02', 'LINE-A', 'PART-001', 480, 2),
    ('2026-05-02', 'LINE-B', 'PART-002', 320, 0),
    ('2026-05-02', 'LINE-C', 'PART-003', 200, 10),
    ('2026-05-03', 'LINE-A', 'PART-001', 510, 4),
    ('2026-05-03', 'LINE-C', 'PART-003', 190, 2);
