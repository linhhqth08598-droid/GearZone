package Models;

import java.util.Date;

public class UserRoles {

    private Integer userId;
    private Integer roleId;
    private Date assignedAt;
    private String status;

    public UserRoles() {
    }

    public UserRoles(Integer userId, Integer roleId, Date assignedAt, String status) {
        this.userId = userId;
        this.roleId = roleId;
        this.assignedAt = assignedAt;
        this.status = status;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Date getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(Date assignedAt) {
        this.assignedAt = assignedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
