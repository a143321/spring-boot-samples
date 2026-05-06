package com.example.factoryapp.product;

import java.time.LocalDateTime;

public record Product(
    String productCode,
    String productName,
    String unit,
    LocalDateTime createdAt
) {}
