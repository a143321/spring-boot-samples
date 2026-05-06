package com.example.factoryapp.production;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record ProductionRequest(

    @NotNull(message = "日付は必須です")
    LocalDate resultDate,

    @NotBlank(message = "ラインコードは必須です")
    String lineCode,

    @NotBlank(message = "製品コードは必須です")
    String productCode,

    @NotBlank(message = "工程コードは必須です")
    String processCode,

    @Min(value = 1, message = "生産数は1以上にしてください")
    int productionQty,

    @Min(value = 0, message = "不良数は0以上にしてください")
    int defectQty,

    String operatorName
) {}
