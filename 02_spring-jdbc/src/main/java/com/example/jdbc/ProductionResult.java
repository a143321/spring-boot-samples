package com.example.jdbc;

import java.time.LocalDate;

// レコード（Java 16+）= Python の dataclass / TypedDict に相当
// DB の1行分のデータを表す「入れ物」
public record ProductionResult(
    Integer id,
    String lineCode,
    String productCode,
    int productionQty,
    int defectQty,
    LocalDate resultDate
) {}
