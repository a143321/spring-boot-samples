package com.example.factory.production;

import java.time.LocalDate;

// 生産実績 1件分のデータクラス
public record ProductionResult(
    Long id,
    LocalDate resultDate,
    String lineCode,
    String productCode,
    String processCode,
    int productionQty,
    int defectQty,
    String operatorName
) {}
