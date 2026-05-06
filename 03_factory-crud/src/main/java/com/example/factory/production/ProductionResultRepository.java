package com.example.factory.production;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductionResultRepository {

    private final JdbcClient jdbcClient;

    public ProductionResultRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    // 全件取得
    public List<ProductionResult> findAll() {
        return jdbcClient
            .sql("""
                SELECT id, result_date, line_code, product_code, process_code,
                       production_qty, defect_qty, operator_name
                FROM production_results
                ORDER BY result_date DESC, id DESC
                """)
            .query(ProductionResult.class)
            .list();
    }

    // 1件取得
    public Optional<ProductionResult> findById(long id) {
        return jdbcClient
            .sql("""
                SELECT id, result_date, line_code, product_code, process_code,
                       production_qty, defect_qty, operator_name
                FROM production_results WHERE id = :id
                """)
            .param("id", id)
            .query(ProductionResult.class)
            .optional();
    }

    // ★ 製品別・不良率集計（JOIN + GROUP BY + 計算）
    // 工場系で必ずと言っていいほど出てくる集計クエリ
    public List<ProductionSummary> summarizeByProduct() {
        return jdbcClient
            .sql("""
                SELECT
                    r.product_code,
                    p.product_name,
                    SUM(r.production_qty)  AS total_qty,
                    SUM(r.defect_qty)      AS total_defects,
                    ROUND(
                        SUM(r.defect_qty) * 100.0 / SUM(r.production_qty),
                        2
                    )                      AS defect_rate
                FROM production_results r
                JOIN products p ON r.product_code = p.product_code
                GROUP BY r.product_code, p.product_name
                ORDER BY defect_rate DESC
                """)
            .query(ProductionSummary.class)
            .list();
    }

    // 登録
    public int insert(ProductionResult r) {
        return jdbcClient
            .sql("""
                INSERT INTO production_results
                    (result_date, line_code, product_code, process_code,
                     production_qty, defect_qty, operator_name)
                VALUES
                    (:resultDate, :lineCode, :productCode, :processCode,
                     :productionQty, :defectQty, :operatorName)
                """)
            .param("resultDate",    r.resultDate())
            .param("lineCode",      r.lineCode())
            .param("productCode",   r.productCode())
            .param("processCode",   r.processCode())
            .param("productionQty", r.productionQty())
            .param("defectQty",     r.defectQty())
            .param("operatorName",  r.operatorName())
            .update();
    }

    // 削除
    public int delete(long id) {
        return jdbcClient
            .sql("DELETE FROM production_results WHERE id = :id")
            .param("id", id)
            .update();
    }
}
