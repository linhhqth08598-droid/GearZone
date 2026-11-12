package Models;

import java.util.Date;

public class Promotions {

    private Integer promotionId;
    private String promotionName;
    private String promotionType;
    private String discountType;
    private Double discountValue;
    private Double minOrderValue;
    private Integer targetPartId;
    private Integer targetCategoryId;
    private Integer targetBrandId;
    private Date startDate;
    private Date endDate;
    private Boolean isActive;
    private Integer createdBy;
    private Date createdAt;

    public Promotions() {
    }

    public Promotions(Integer promotionId, String promotionName, String promotionType, String discountType, Double discountValue, Double minOrderValue, Integer targetPartId, Integer targetCategoryId, Integer targetBrandId, Date startDate, Date endDate, Boolean isActive, Integer createdBy, Date createdAt) {
        this.promotionId = promotionId;
        this.promotionName = promotionName;
        this.promotionType = promotionType;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minOrderValue = minOrderValue;
        this.targetPartId = targetPartId;
        this.targetCategoryId = targetCategoryId;
        this.targetBrandId = targetBrandId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isActive = isActive;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public Integer getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Integer promotionId) {
        this.promotionId = promotionId;
    }

    public String getPromotionName() {
        return promotionName;
    }

    public void setPromotionName(String promotionName) {
        this.promotionName = promotionName;
    }

    public String getPromotionType() {
        return promotionType;
    }

    public void setPromotionType(String promotionType) {
        this.promotionType = promotionType;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public Double getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(Double discountValue) {
        this.discountValue = discountValue;
    }

    public Double getMinOrderValue() {
        return minOrderValue;
    }

    public void setMinOrderValue(Double minOrderValue) {
        this.minOrderValue = minOrderValue;
    }

    public Integer getTargetPartId() {
        return targetPartId;
    }

    public void setTargetPartId(Integer targetPartId) {
        this.targetPartId = targetPartId;
    }

    public Integer getTargetCategoryId() {
        return targetCategoryId;
    }

    public void setTargetCategoryId(Integer targetCategoryId) {
        this.targetCategoryId = targetCategoryId;
    }

    public Integer getTargetBrandId() {
        return targetBrandId;
    }

    public void setTargetBrandId(Integer targetBrandId) {
        this.targetBrandId = targetBrandId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

}
