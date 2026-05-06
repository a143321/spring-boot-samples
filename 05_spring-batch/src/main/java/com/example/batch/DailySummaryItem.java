package com.example.batch;

// バッチで集計する1日・1製品分のデータクラス
public class DailySummaryItem {

    private String resultDate;
    private String productCode;
    private int totalQty;
    private int totalDefects;
    private double defectRate;

    public DailySummaryItem() {}

    public DailySummaryItem(String resultDate, String productCode,
                             int totalQty, int totalDefects, double defectRate) {
        this.resultDate = resultDate;
        this.productCode = productCode;
        this.totalQty = totalQty;
        this.totalDefects = totalDefects;
        this.defectRate = defectRate;
    }

    public String getResultDate()  { return resultDate; }
    public String getProductCode() { return productCode; }
    public int getTotalQty()       { return totalQty; }
    public int getTotalDefects()   { return totalDefects; }
    public double getDefectRate()  { return defectRate; }

    public void setResultDate(String resultDate)   { this.resultDate = resultDate; }
    public void setProductCode(String productCode) { this.productCode = productCode; }
    public void setTotalQty(int totalQty)          { this.totalQty = totalQty; }
    public void setTotalDefects(int totalDefects)  { this.totalDefects = totalDefects; }
    public void setDefectRate(double defectRate)   { this.defectRate = defectRate; }

    @Override
    public String toString() {
        return "DailySummaryItem{date=%s, product=%s, qty=%d, defects=%d, rate=%.2f%%}"
            .formatted(resultDate, productCode, totalQty, totalDefects, defectRate);
    }
}
