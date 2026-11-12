package Models;

import java.util.Date;

public class ComboPromotions {

    private Integer comboId;
    private String comboName;
    private Double discountPercentage;
    private Integer minPartsCount;
    private String comboType;
    private String status;
    private Date startDate;
    private Date endDate;

    public ComboPromotions() {
    }

    public ComboPromotions(Integer comboId, String comboName, Double discountPercentage, Integer minPartsCount, String comboType, String status, Date startDate, Date endDate) {
        this.comboId = comboId;
        this.comboName = comboName;
        this.discountPercentage = discountPercentage;
        this.minPartsCount = minPartsCount;
        this.comboType = comboType;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Integer getComboId() {
        return comboId;
    }

    public void setComboId(Integer comboId) {
        this.comboId = comboId;
    }

    public String getComboName() {
        return comboName;
    }

    public void setComboName(String comboName) {
        this.comboName = comboName;
    }

    public Double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(Double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public Integer getMinPartsCount() {
        return minPartsCount;
    }

    public void setMinPartsCount(Integer minPartsCount) {
        this.minPartsCount = minPartsCount;
    }

    public String getComboType() {
        return comboType;
    }

    public void setComboType(String comboType) {
        this.comboType = comboType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

}
