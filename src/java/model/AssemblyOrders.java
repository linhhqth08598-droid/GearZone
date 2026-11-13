package model;

import java.util.Date;

public class AssemblyOrders {

    private Integer assemblyId;
    private Integer userId;
    private Integer orderId;
    private Integer buildId;
    private Integer technicianId;
    private String assemblyType;
    private Double assemblyFee;
    private String status;
    private Date scheduledDate;
    private Date handedOverAt;
    private Integer customerRating;
    private String customerFeedback;
    private String note;
    private Date createdAt;

    public AssemblyOrders() {}

    public Integer getAssemblyId() { return assemblyId; }
    public void setAssemblyId(Integer assemblyId) { this.assemblyId = assemblyId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }

    public Integer getBuildId() { return buildId; }
    public void setBuildId(Integer buildId) { this.buildId = buildId; }

    public Integer getTechnicianId() { return technicianId; }
    public void setTechnicianId(Integer technicianId) { this.technicianId = technicianId; }

    public String getAssemblyType() { return assemblyType; }
    public void setAssemblyType(String assemblyType) { this.assemblyType = assemblyType; }

    public Double getAssemblyFee() { return assemblyFee; }
    public void setAssemblyFee(Double assemblyFee) { this.assemblyFee = assemblyFee; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(Date scheduledDate) { this.scheduledDate = scheduledDate; }

    public Date getHandedOverAt() { return handedOverAt; }
    public void setHandedOverAt(Date handedOverAt) { this.handedOverAt = handedOverAt; }

    public Integer getCustomerRating() { return customerRating; }
    public void setCustomerRating(Integer customerRating) { this.customerRating = customerRating; }

    public String getCustomerFeedback() { return customerFeedback; }
    public void setCustomerFeedback(String customerFeedback) { this.customerFeedback = customerFeedback; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

}
