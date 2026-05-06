package com.example.factoryapp.production;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productions")
public class ProductionController {

    private final ProductionRepository repository;

    public ProductionController(ProductionRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<ProductionResult> findAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductionResult> findById(@PathVariable Long id) {
        return repository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // ★ 製品別不良率集計
    @GetMapping("/summary")
    public List<ProductionSummary> summary() {
        return repository.summarizeByProduct();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid ProductionRequest request) {
        repository.insert(request);
        return ResponseEntity.ok(Map.of("message", "生産実績を登録しました"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        int deleted = repository.delete(id);
        if (deleted == 0) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(Map.of("message", "生産実績を削除しました"));
    }
}
