package com.example.factory.production;

// 製品別・不良率集計の結果クラス
public record ProductionSummary(
    String productCode,
    String productName,          // products テーブルから JOIN
    int totalQty,                // 総生産数
    int totalDefects,            // 総不良数
    double defectRate            // 不良率（%）
) {}
