package com.example.factory.production;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productions")
public class ProductionResultController {

    private final ProductionResultRepository repository;

    public ProductionResultController(ProductionResultRepository repository) {
        this.repository = repository;
    }

    // GET /productions → 一覧
    @GetMapping
    public List<ProductionResult> findAll() {
        return repository.findAll();
    }

    // GET /productions/{id} → 1件
    @GetMapping("/{id}")
    public ResponseEntity<ProductionResult> findById(@PathVariable long id) {
        return repository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // GET /productions/summary → 製品別不良率集計
    @GetMapping("/summary")
    public List<ProductionSummary> summary() {
        return repository.summarizeByProduct();
    }

    // POST /productions → 登録
    @PostMapping
    public ResponseEntity<String> create(@RequestBody ProductionResult result) {
        repository.insert(result);
        return ResponseEntity.ok("登録しました");
    }

    // DELETE /productions/{id} → 削除
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable long id) {
        int rows = repository.delete(id);
        if (rows == 0) return ResponseEntity.notFound().build();
        return ResponseEntity.ok("削除しました");
    }
}
