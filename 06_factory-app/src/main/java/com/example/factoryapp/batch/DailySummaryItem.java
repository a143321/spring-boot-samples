package com.example.factoryapp.batch;

// バッチ集計結果を格納するクラス（FlatFileItemWriter のために通常クラスで実装）
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

    public void setResultDate(String v)  { this.resultDate = v; }
    public void setProductCode(String v) { this.productCode = v; }
    public void setTotalQty(int v)       { this.totalQty = v; }
    public void setTotalDefects(int v)   { this.totalDefects = v; }
    public void setDefectRate(double v)  { this.defectRate = v; }
}
