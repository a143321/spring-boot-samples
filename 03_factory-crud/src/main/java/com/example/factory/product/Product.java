package com.example.factory.product;

// 製品マスタ 1件分のデータクラス
public record Product(
    String productCode,
    String productName,
    String unit
) {}
