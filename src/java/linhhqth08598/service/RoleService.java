package linhhqth08598.service;

import fourSome.model.Permissions;
import fourSome.model.Roles;
import linhhqth08598.dao.PermissionsDAO;
import linhhqth08598.dao.RolePermissionsDAO;
import linhhqth08598.dao.RolesDAO;
import java.util.List;
import java.util.Set;

public class RoleService {
    private RolesDAO rolesDAO = new RolesDAO();
    private PermissionsDAO permissionsDAO = new PermissionsDAO();
    private RolePermissionsDAO rolePermissionsDAO = new RolePermissionsDAO();

    public List<Roles> getAllRoles() {
        return rolesDAO.getAll();
    }

    public Roles getRoleById(int roleId) {
        return rolesDAO.findById(roleId);
    }

    public List<Permissions> getAllPermissions() {
        return permissionsDAO.getAll();
    }

    public List<Integer> getPermissionIdsByRole(int roleId) {
        return rolePermissionsDAO.getPermissionIdsByRoleId(roleId);
    }

    public Set<String> getPermissionCodesByUser(int userId) {
        return rolePermissionsDAO.getPermissionCodesByUserId(userId);
    }

    public boolean updatePermissions(int roleId, String[] permissionIds) {
        return rolePermissionsDAO.updateRolePermissions(roleId, permissionIds);
    }
}
