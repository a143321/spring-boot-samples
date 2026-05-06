package com.example.validation.production;

import java.time.LocalDate;

// DB から取得した1行分のデータ
public record ProductionResult(
    Long id,
    LocalDate resultDate,
    String lineCode,
    String productCode,
    int productionQty,
    int defectQty
) {}
