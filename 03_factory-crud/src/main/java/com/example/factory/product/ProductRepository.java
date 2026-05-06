package com.example.factory.product;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepository {

    private final JdbcClient jdbcClient;

    public ProductRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    // 全件取得
    public List<Product> findAll() {
        return jdbcClient
            .sql("SELECT product_code, product_name, unit FROM products ORDER BY product_code")
            .query(Product.class)
            .list();
    }

    // 1件取得
    public Optional<Product> findByCode(String productCode) {
        return jdbcClient
            .sql("SELECT product_code, product_name, unit FROM products WHERE product_code = :code")
            .param("code", productCode)
            .query(Product.class)
            .optional();
    }

    // 登録
    public int insert(Product p) {
        return jdbcClient
            .sql("INSERT INTO products (product_code, product_name, unit) VALUES (:productCode, :productName, :unit)")
            .param("productCode", p.productCode())
            .param("productName", p.productName())
            .param("unit",        p.unit())
            .update();
    }

    // 更新
    public int update(Product p) {
        return jdbcClient
            .sql("UPDATE products SET product_name=:productName, unit=:unit WHERE product_code=:productCode")
            .param("productCode", p.productCode())
            .param("productName", p.productName())
            .param("unit",        p.unit())
            .update();
    }

    // 削除
    public int delete(String productCode) {
        return jdbcClient
            .sql("DELETE FROM products WHERE product_code = :code")
            .param("code", productCode)
            .update();
    }
}
