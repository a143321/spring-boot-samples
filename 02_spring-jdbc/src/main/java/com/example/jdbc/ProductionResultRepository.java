package com.example.jdbc;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// @Repository = 「DB アクセスを担当するクラス」という宣言
// Flask でいう「モデル層」に相当
@Repository
public class ProductionResultRepository {

    // JdbcClient = Spring が用意した SQL 実行ツール（シンプルで直感的）
    private final JdbcClient jdbcClient;

    // コンストラクタインジェクション（Spring が自動で渡してくれる）
    public ProductionResultRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    // 全件取得
    public List<ProductionResult> findAll() {
        return jdbcClient
            .sql("SELECT id, line_code, product_code, production_qty, defect_qty, result_date FROM production_results ORDER BY id")
            .query(ProductionResult.class)  // DB の行を ProductionResult に自動マッピング
            .list();
    }

    // 1件取得
    public Optional<ProductionResult> findById(int id) {
        return jdbcClient
            .sql("SELECT id, line_code, product_code, production_qty, defect_qty, result_date FROM production_results WHERE id = :id")
            .param("id", id)  // :id に値をバインド（SQLインジェクション対策）
            .query(ProductionResult.class)
            .optional();
    }

    // 登録
    public int insert(ProductionResult r) {
        return jdbcClient
            .sql("INSERT INTO production_results (line_code, product_code, production_qty, defect_qty, result_date) VALUES (:lineCode, :productCode, :productionQty, :defectQty, :resultDate)")
            .param("lineCode",      r.lineCode())
            .param("productCode",   r.productCode())
            .param("productionQty", r.productionQty())
            .param("defectQty",     r.defectQty())
            .param("resultDate",    r.resultDate())
            .update();
    }

    // 更新
    public int update(int id, ProductionResult r) {
        return jdbcClient
            .sql("UPDATE production_results SET line_code=:lineCode, product_code=:productCode, production_qty=:productionQty, defect_qty=:defectQty, result_date=:resultDate WHERE id=:id")
            .param("id",            id)
            .param("lineCode",      r.lineCode())
            .param("productCode",   r.productCode())
            .param("productionQty", r.productionQty())
            .param("defectQty",     r.defectQty())
            .param("resultDate",    r.resultDate())
            .update();
    }

    // 削除
    public int delete(int id) {
        return jdbcClient
            .sql("DELETE FROM production_results WHERE id = :id")
            .param("id", id)
            .update();
    }
}
