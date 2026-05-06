package com.example.factoryapp.production;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductionRepository {

    private final JdbcClient jdbcClient;

    public ProductionRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<ProductionResult> findAll() {
        return jdbcClient
            .sql("SELECT * FROM production_results ORDER BY result_date DESC, id DESC")
            .query(ProductionResult.class)
            .list();
    }

    public Optional<ProductionResult> findById(Long id) {
        return jdbcClient
            .sql("SELECT * FROM production_results WHERE id = :id")
            .param("id", id)
            .query(ProductionResult.class)
            .optional();
    }

    public void insert(ProductionRequest r) {
        jdbcClient
            .sql("""
                INSERT INTO production_results
                    (result_date, line_code, product_code, process_code, production_qty, defect_qty, operator_name)
                VALUES
                    (:resultDate, :lineCode, :productCode, :processCode, :productionQty, :defectQty, :operatorName)
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

    public int delete(Long id) {
        return jdbcClient
            .sql("DELETE FROM production_results WHERE id = :id")
            .param("id", id)
            .update();
    }

    // ★ 製品別不良率集計（JOIN + GROUP BY）
    public List<ProductionSummary> summarizeByProduct() {
        return jdbcClient
            .sql("""
                SELECT
                    r.product_code,
                    p.product_name,
                    SUM(r.production_qty)  AS total_qty,
                    SUM(r.defect_qty)      AS total_defects,
                    ROUND(SUM(r.defect_qty) * 100.0 / SUM(r.production_qty), 2) AS defect_rate
                FROM production_results r
                JOIN products p ON r.product_code = p.product_code
                GROUP BY r.product_code, p.product_name
                ORDER BY defect_rate DESC
                """)
            .query(ProductionSummary.class)
            .list();
    }
}
