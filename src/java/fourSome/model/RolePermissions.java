package fourSome.model;


public class RolePermissions {

    private Integer roleId;
    private Integer permissionId;
    private String status;

    public RolePermissions() {}

    public Integer getRoleId() { return roleId; }
    public void setRoleId(Integer roleId) { this.roleId = roleId; }

    public Integer getPermissionId() { return permissionId; }
    public void setPermissionId(Integer permissionId) { this.permissionId = permissionId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

}
