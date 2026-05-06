package com.example.factoryapp.production;

import java.time.LocalDate;

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
