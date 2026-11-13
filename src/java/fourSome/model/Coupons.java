package fourSome.model;

import java.util.Date;

public class Coupons {

    private Integer couponId;
    private String code;
    private String couponName;
    private String discountType;
    private Double discountValue;
    private Double minOrderValue;
    private Date startDate;
    private Date endDate;
    private Integer usageLimit;
    private Integer perUserLimit;
    private Integer usedCount;
    private Double maxDiscountAmount;
    private Integer applicableCategoryId;
    private Integer applicableBrandId;
    private String status;

    public Coupons() {}

    public Integer getCouponId() { return couponId; }
    public void setCouponId(Integer couponId) { this.couponId = couponId; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getCouponName() { return couponName; }
    public void setCouponName(String couponName) { this.couponName = couponName; }

    public String getDiscountType() { return discountType; }
    public void setDiscountType(String discountType) { this.discountType = discountType; }

    public Double getDiscountValue() { return discountValue; }
    public void setDiscountValue(Double discountValue) { this.discountValue = discountValue; }

    public Double getMinOrderValue() { return minOrderValue; }
    public void setMinOrderValue(Double minOrderValue) { this.minOrderValue = minOrderValue; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public Integer getUsageLimit() { return usageLimit; }
    public void setUsageLimit(Integer usageLimit) { this.usageLimit = usageLimit; }

    public Integer getPerUserLimit() { return perUserLimit; }
    public void setPerUserLimit(Integer perUserLimit) { this.perUserLimit = perUserLimit; }

    public Integer getUsedCount() { return usedCount; }
    public void setUsedCount(Integer usedCount) { this.usedCount = usedCount; }

    public Double getMaxDiscountAmount() { return maxDiscountAmount; }
    public void setMaxDiscountAmount(Double maxDiscountAmount) { this.maxDiscountAmount = maxDiscountAmount; }

    public Integer getApplicableCategoryId() { return applicableCategoryId; }
    public void setApplicableCategoryId(Integer applicableCategoryId) { this.applicableCategoryId = applicableCategoryId; }

    public Integer getApplicableBrandId() { return applicableBrandId; }
    public void setApplicableBrandId(Integer applicableBrandId) { this.applicableBrandId = applicableBrandId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

}
