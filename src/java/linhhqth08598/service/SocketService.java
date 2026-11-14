package linhhqth08598.service;

import fourSome.model.SocketTypes;
import fourSome.model.Users;
import java.util.List;
import linhhqth08598.dao.SocketTypesDAO;

public class SocketService {
    private SocketTypesDAO socketDAO = new SocketTypesDAO();

    public List<SocketTypes> getAllSockets() {
        return socketDAO.getAll();
    }

    public String saveSocket(SocketTypes socket, boolean isEdit, Users adminUser) {
        if (socket.getSocketName() == null || socket.getSocketName().trim().isEmpty()) {
            return "Tên Socket không được để trống!";
        }
        int excludeId = isEdit ? socket.getSocketId() : -1;
        if (socketDAO.checkNameExist(socket.getSocketName(), excludeId)) {
            return "Tên Socket đã tồn tại!";
        }

        boolean success = isEdit ? socketDAO.update(socket) : socketDAO.insert(socket);
        if (success) {
            AuditLogService auditLog = new AuditLogService();
            String action = isEdit ? "UPDATE_SOCKET" : "INSERT_SOCKET";
            auditLog.logAction(adminUser.getUserId(), action, "socket_types", socket.getSocketId(), 
                "Admin đã " + (isEdit ? "cập nhật" : "thêm mới") + " Socket: " + socket.getSocketName());
            return null;
        }
        return "Lỗi hệ thống khi lưu Socket!";
    }

    public String deleteSocket(int socketId, Users adminUser) {
        if (socketDAO.softDelete(socketId)) {
            AuditLogService auditLog = new AuditLogService();
            auditLog.logAction(adminUser.getUserId(), "DELETE_SOCKET", "socket_types", socketId, "Admin đã ẩn Socket ID: " + socketId);
            return null;
        }
        return "Lỗi hệ thống khi xóa Socket!";
    }
}