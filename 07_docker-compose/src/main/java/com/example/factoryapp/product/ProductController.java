package com.example.factoryapp.product;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
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
    public ResponseEntity<?> create(@RequestBody @Valid ProductRequest request) {
        repository.insert(request);
        return ResponseEntity.ok(Map.of("message", "製品を登録しました", "productCode", request.productCode()));
    }

    @PutMapping("/{code}")
    public ResponseEntity<?> update(@PathVariable String code, @RequestBody @Valid ProductRequest request) {
        int updated = repository.update(code, request);
        if (updated == 0) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(Map.of("message", "製品を更新しました"));
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<?> delete(@PathVariable String code) {
        int deleted = repository.delete(code);
        if (deleted == 0) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(Map.of("message", "製品を削除しました"));
    }
}
