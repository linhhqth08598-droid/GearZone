package Models;


public class Roles {

    private Integer roleId;
    private String roleCode;
    private String roleName;
    private String description;
    private String status;

    public Roles() {
    }

    public Roles(Integer roleId, String roleCode, String roleName, String description, String status) {
        this.roleId = roleId;
        this.roleCode = roleCode;
        this.roleName = roleName;
        this.description = description;
        this.status = status;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
