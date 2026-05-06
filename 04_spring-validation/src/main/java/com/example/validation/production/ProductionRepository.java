package com.example.validation.production;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductionRepository {

    private final JdbcClient jdbcClient;

    public ProductionRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    // バリデーション通過後に DB へ保存
    public void insert(ProductionRequest r) {
        jdbcClient
            .sql("""
                INSERT INTO production_results
                    (result_date, line_code, product_code, production_qty, defect_qty)
                VALUES
                    (:resultDate, :lineCode, :productCode, :productionQty, :defectQty)
                """)
            .param("resultDate",    r.resultDate())
            .param("lineCode",      r.lineCode())
            .param("productCode",   r.productCode())
            .param("productionQty", r.productionQty())
            .param("defectQty",     r.defectQty())
            .update();
    }

    // 保存済みデータを全件取得
    public List<ProductionResult> findAll() {
        return jdbcClient
            .sql("SELECT * FROM production_results ORDER BY id DESC")
            .query(ProductionResult.class)
            .list();
    }
}
