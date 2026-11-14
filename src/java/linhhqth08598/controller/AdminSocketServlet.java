package linhhqth08598.controller;

import fourSome.model.SocketTypes;
import fourSome.model.Users;
import linhhqth08598.service.SocketService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "AdminSocketServlet", urlPatterns = {"/admin/socket"})
public class AdminSocketServlet extends HttpServlet {

    private SocketService socketService = new SocketService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("sockets", socketService.getAllSockets());
        request.getRequestDispatcher("/views/linhhqth08598/admin/socket.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        Users adminUser = (Users) session.getAttribute("loggedUser");

        if ("saveSocket".equals(action)) {
            try {
                int socketId = Integer.parseInt(request.getParameter("socketId"));
                String socketName = request.getParameter("socketName");
                boolean isEdit = socketId > 0;

                SocketTypes socket = new SocketTypes();
                socket.setSocketId(socketId);
                socket.setSocketName(socketName);
                socket.setStatus("ACTIVE");

                String error = socketService.saveSocket(socket, isEdit, adminUser);
                if (error == null) {
                    session.setAttribute("message", (isEdit ? "Cập nhật" : "Thêm mới") + " Socket thành công!");
                } else {
                    session.setAttribute("error", error);
                }
            } catch (Exception e) {
                session.setAttribute("error", "Dữ liệu không hợp lệ!");
            }
            response.sendRedirect(request.getContextPath() + "/admin/socket");

        } else if ("deleteSocket".equals(action)) {
            try {
                int socketId = Integer.parseInt(request.getParameter("socketId"));
                String error = socketService.deleteSocket(socketId, adminUser);
                if (error == null) {
                    session.setAttribute("message", "Đã ẩn Socket!");
                } else {
                    session.setAttribute("error", error);
                }
            } catch (Exception e) {
                session.setAttribute("error", "Mã không hợp lệ!");
            }
            response.sendRedirect(request.getContextPath() + "/admin/socket");
        }
    }
}