package com.example.factoryapp.production;

// JOIN + GROUP BY の集計結果
public record ProductionSummary(
    String productCode,
    String productName,
    int totalQty,
    int totalDefects,
    double defectRate
) {}
