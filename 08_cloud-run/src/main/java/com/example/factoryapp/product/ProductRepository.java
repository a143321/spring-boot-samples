package com.example.factoryapp.product;

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

    public List<Product> findAll() {
        return jdbcClient
            .sql("SELECT * FROM products ORDER BY product_code")
            .query(Product.class)
            .list();
    }

    public Optional<Product> findByCode(String productCode) {
        return jdbcClient
            .sql("SELECT * FROM products WHERE product_code = :productCode")
            .param("productCode", productCode)
            .query(Product.class)
            .optional();
    }

    public void insert(ProductRequest r) {
        jdbcClient
            .sql("INSERT INTO products (product_code, product_name, unit) VALUES (:productCode, :productName, :unit)")
            .param("productCode", r.productCode())
            .param("productName", r.productName())
            .param("unit",        r.unit())
            .update();
    }

    public int update(String productCode, ProductRequest r) {
        return jdbcClient
            .sql("UPDATE products SET product_name = :productName, unit = :unit WHERE product_code = :productCode")
            .param("productName", r.productName())
            .param("unit",        r.unit())
            .param("productCode", productCode)
            .update();
    }

    public int delete(String productCode) {
        return jdbcClient
            .sql("DELETE FROM products WHERE product_code = :productCode")
            .param("productCode", productCode)
            .update();
    }
}
