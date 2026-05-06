package com.example.factory.product;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository repository;

    public ProductController(ProductRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Product> findAll() {
        return repository.findAll();
    }

    @GetMapping("/{code}")
    public ResponseEntity<Product> findByCode(@PathVariable String code) {
        return repository.findByCode(code)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<String> create(@RequestBody Product product) {
        repository.insert(product);
        return ResponseEntity.ok("登録しました: " + product.productCode());
    }

    @PutMapping("/{code}")
    public ResponseEntity<String> update(@PathVariable String code, @RequestBody Product product) {
        int rows = repository.update(new Product(code, product.productName(), product.unit()));
        if (rows == 0) return ResponseEntity.notFound().build();
        return ResponseEntity.ok("更新しました");
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<String> delete(@PathVariable String code) {
        int rows = repository.delete(code);
        if (rows == 0) return ResponseEntity.notFound().build();
        return ResponseEntity.ok("削除しました");
    }
}
