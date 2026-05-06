package com.example.jdbc;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// REST API の入り口
@RestController
@RequestMapping("/productions")  // すべてのパスが /productions から始まる
public class ProductionResultController {

    private final ProductionResultRepository repository;

    public ProductionResultController(ProductionResultRepository repository) {
        this.repository = repository;
    }

    // GET /productions → 一覧取得
    @GetMapping
    public List<ProductionResult> findAll() {
        return repository.findAll();
    }

    // GET /productions/{id} → 1件取得
    @GetMapping("/{id}")
    public ResponseEntity<ProductionResult> findById(@PathVariable int id) {
        return repository.findById(id)
            .map(ResponseEntity::ok)                      // 見つかった → 200 OK
            .orElse(ResponseEntity.notFound().build());   // なければ → 404 Not Found
    }

    // POST /productions → 登録
    @PostMapping
    public ResponseEntity<String> create(@RequestBody ProductionResult result) {
        int rows = repository.insert(result);
        return ResponseEntity.ok(rows + " 件登録しました");
    }

    // PUT /productions/{id} → 更新
    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable int id, @RequestBody ProductionResult result) {
        int rows = repository.update(id, result);
        if (rows == 0) {
            return ResponseEntity.notFound().build();  // 対象なし → 404
        }
        return ResponseEntity.ok(rows + " 件更新しました");
    }

    // DELETE /productions/{id} → 削除
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable int id) {
        int rows = repository.delete(id);
        if (rows == 0) {
            return ResponseEntity.notFound().build();  // 対象なし → 404
        }
        return ResponseEntity.ok(rows + " 件削除しました");
    }
}
