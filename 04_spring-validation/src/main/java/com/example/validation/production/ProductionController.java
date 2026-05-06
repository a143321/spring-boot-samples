package com.example.validation.production;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/productions")
@Validated
public class ProductionController {

    private final ProductionRepository repository;

    public ProductionController(ProductionRepository repository) {
        this.repository = repository;
    }

    // GET /productions → 保存済み一覧
    @GetMapping
    public List<ProductionResult> findAll() {
        return repository.findAll();
    }

    // POST /productions → バリデーション → DB保存
    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid ProductionRequest request) {
        repository.insert(request);
        return ResponseEntity.ok(Map.of("message", "登録しました"));
    }
}

