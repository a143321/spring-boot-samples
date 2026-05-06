package com.example.factoryapp.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProductRequest(

    @NotBlank(message = "製品コードは必須です")
    @Size(max = 20, message = "製品コードは20文字以内にしてください")
    String productCode,

    @NotBlank(message = "製品名は必須です")
    @Size(max = 100, message = "製品名は100文字以内にしてください")
    String productName,

    @NotBlank(message = "単位は必須です")
    String unit
) {}
