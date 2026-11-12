package Models;

import java.util.Date;

public class Parts {

    private Integer partId;
    private Integer categoryId;
    private Integer brandId;
    private Integer seriesId;
    private String productCode;
    private String partName;
    private Double basePrice;
    private Double salePrice;
    private Boolean isRgb;
    private Integer gamingPerformanceScore;
    private String description;
    private Integer warrantyMonths;
    private Integer viewCount;
    private Integer soldCount;
    private Double avgCostPrice;
    private String status;
    private Date createdAt;

    public Parts() {
    }

    public Parts(Integer partId, Integer categoryId, Integer brandId, Integer seriesId, String productCode, String partName, Double basePrice, Double salePrice, Boolean isRgb, Integer gamingPerformanceScore, String description, Integer warrantyMonths, Integer viewCount, Integer soldCount, Double avgCostPrice, String status, Date createdAt) {
        this.partId = partId;
        this.categoryId = categoryId;
        this.brandId = brandId;
        this.seriesId = seriesId;
        this.productCode = productCode;
        this.partName = partName;
        this.basePrice = basePrice;
        this.salePrice = salePrice;
        this.isRgb = isRgb;
        this.gamingPerformanceScore = gamingPerformanceScore;
        this.description = description;
        this.warrantyMonths = warrantyMonths;
        this.viewCount = viewCount;
        this.soldCount = soldCount;
        this.avgCostPrice = avgCostPrice;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Integer getPartId() {
        return partId;
    }

    public void setPartId(Integer partId) {
        this.partId = partId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public Integer getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(Integer seriesId) {
        this.seriesId = seriesId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public Double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Double basePrice) {
        this.basePrice = basePrice;
    }

    public Double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Double salePrice) {
        this.salePrice = salePrice;
    }

    public Boolean getIsRgb() {
        return isRgb;
    }

    public void setIsRgb(Boolean isRgb) {
        this.isRgb = isRgb;
    }

    public Integer getGamingPerformanceScore() {
        return gamingPerformanceScore;
    }

    public void setGamingPerformanceScore(Integer gamingPerformanceScore) {
        this.gamingPerformanceScore = gamingPerformanceScore;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getWarrantyMonths() {
        return warrantyMonths;
    }

    public void setWarrantyMonths(Integer warrantyMonths) {
        this.warrantyMonths = warrantyMonths;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getSoldCount() {
        return soldCount;
    }

    public void setSoldCount(Integer soldCount) {
        this.soldCount = soldCount;
    }

    public Double getAvgCostPrice() {
        return avgCostPrice;
    }

    public void setAvgCostPrice(Double avgCostPrice) {
        this.avgCostPrice = avgCostPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

}
