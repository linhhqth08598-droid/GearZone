package Models;

import java.util.Date;

public class StaffAuditLogs {

    private Integer logId;
    private Integer staffId;
    private String action;
    private String targetTable;
    private Integer targetId;
    private String description;
    private Date createdAt;
    private String status;

    public StaffAuditLogs() {
    }

    public StaffAuditLogs(Integer logId, Integer staffId, String action, String targetTable, Integer targetId, String description, Date createdAt, String status) {
        this.logId = logId;
        this.staffId = staffId;
        this.action = action;
        this.targetTable = targetTable;
        this.targetId = targetId;
        this.description = description;
        this.createdAt = createdAt;
        this.status = status;
    }

    public Integer getLogId() {
        return logId;
    }

    public void setLogId(Integer logId) {
        this.logId = logId;
    }

    public Integer getStaffId() {
        return staffId;
    }

    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTargetTable() {
        return targetTable;
    }

    public void setTargetTable(String targetTable) {
        this.targetTable = targetTable;
    }

    public Integer getTargetId() {
        return targetId;
    }

    public void setTargetId(Integer targetId) {
        this.targetId = targetId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
