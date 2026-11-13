package model;

import java.util.Date;

public class SupportTickets {

    private Integer ticketId;
    private String ticketCode;
    private Integer userId;
    private String ticketType;
    private String subject;
    private String content;
    private Integer partId;
    private Integer orderId;
    private Integer buildId;
    private Integer currentPcId;
    private Integer requirementId;
    private Integer assignedStaffId;
    private String priority;
    private String status;
    private Date createdAt;
    private Date resolvedAt;

    public SupportTickets() {}

    public Integer getTicketId() { return ticketId; }
    public void setTicketId(Integer ticketId) { this.ticketId = ticketId; }

    public String getTicketCode() { return ticketCode; }
    public void setTicketCode(String ticketCode) { this.ticketCode = ticketCode; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getTicketType() { return ticketType; }
    public void setTicketType(String ticketType) { this.ticketType = ticketType; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getPartId() { return partId; }
    public void setPartId(Integer partId) { this.partId = partId; }

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }

    public Integer getBuildId() { return buildId; }
    public void setBuildId(Integer buildId) { this.buildId = buildId; }

    public Integer getCurrentPcId() { return currentPcId; }
    public void setCurrentPcId(Integer currentPcId) { this.currentPcId = currentPcId; }

    public Integer getRequirementId() { return requirementId; }
    public void setRequirementId(Integer requirementId) { this.requirementId = requirementId; }

    public Integer getAssignedStaffId() { return assignedStaffId; }
    public void setAssignedStaffId(Integer assignedStaffId) { this.assignedStaffId = assignedStaffId; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(Date resolvedAt) { this.resolvedAt = resolvedAt; }

}
